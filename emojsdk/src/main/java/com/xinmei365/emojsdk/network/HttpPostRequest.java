package com.xinmei365.emojsdk.network;

import android.text.TextUtils;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.xinmei365.emojsdk.utils.Logger;

import java.io.File;
import java.util.Map;

/**
 * Created by xinmei on 15/11/18.
 */
public class HttpPostRequest<T> extends HttpReuqest {

    private static final String TAG = HttpPostRequest.class.getSimpleName();
    private String content;
    private byte[] bytes;
    private File file;
    private MediaType mediaType;

    private int type = 0;
    private static final int TYPE_PARAMS = 1;
    private static final int TYPE_STRING = 2;
    private static final int TYPE_BYTES = 3;
    private static final int TYPE_FILE = 4;


    private final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream;charset=utf-8");

    private final MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain;charset=utf-8");
    private final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/jpeg");


    public HttpPostRequest(String url, String tag, Map<String, T> params, Map<String, String> headers, MediaType mediaType, String content, byte[] bytes, File file) {
        super(url, tag, params, headers);
        this.mediaType = mediaType;
        this.content = content;
        this.bytes = bytes;
        this.file = file;
    }

    protected void validParams() {
        int count = 0;
        if (params != null && !params.isEmpty()) {
            type = TYPE_PARAMS;
            count++;
        }
        if (content != null) {
            type = TYPE_STRING;
            count++;
        }
        if (bytes != null) {
            type = TYPE_BYTES;
            count++;
        }
        if (file != null) {
            type = TYPE_FILE;
            count++;
        }

        if (count <= 0) {
            throw new IllegalArgumentException("the params , content , file , bytes must has one and only one .");
        }
    }

    @Override
    public Request buildRequest() {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url can not be empty!");
        }
        Request.Builder builder = new Request.Builder();
        appendHeaders(builder, headers);
        requestBody = buildRequestBody();
        builder.url(url).tag(tag).post(requestBody);
        return builder.build();
    }

    @Override
    public RequestBody buildRequestBody() {
        validParams();
        RequestBody requestBody = null;

        switch (type) {
            case TYPE_PARAMS:

//                FormEncodingBuilder builder = new FormEncodingBuilder();
//                addParams(builder, params);
//                requestBody = builder.build();
                requestBody = getRequestBody(params);
                Logger.d(TAG, requestBody.toString());
                break;
            case TYPE_BYTES:
                requestBody = RequestBody.create(mediaType != null ? mediaType : MEDIA_TYPE_STREAM, bytes);
                break;
            case TYPE_FILE:
                requestBody = RequestBody.create(mediaType != null ? mediaType : MEDIA_TYPE_STREAM, file);
                break;
            case TYPE_STRING:
                requestBody = RequestBody.create(mediaType != null ? mediaType : MEDIA_TYPE_STRING, content);
                break;
        }
        return requestBody;
    }


    private RequestBody getRequestBody(Map<String, T> formParams) {
        MultipartBuilder multiBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (Map.Entry<String,T> entry : formParams.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();
            if (value instanceof String) {
                multiBuilder.addFormDataPart(key, String.valueOf(value));
            }else if (value instanceof File){
                multiBuilder.addPart(
                        Headers.of("Content-Disposition", "form-data;name=\"emoji\"; filename=\"" + key + "\""),
                        RequestBody.create(MEDIA_TYPE_STREAM, (File) value));
            }
        }
        return multiBuilder.build();
    }

}

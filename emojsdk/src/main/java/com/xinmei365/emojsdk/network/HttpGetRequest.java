package com.xinmei365.emojsdk.network;

import android.text.TextUtils;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.Map;

/**
 * Created by xinmei on 15/11/18.
 */
public class HttpGetRequest<T> extends HttpReuqest {
    public HttpGetRequest(String url, String tag, Map<String,T> params, Map<String, String> headers) {
        super(url, tag, params, headers);
    }


    @Override
    public Request buildRequest() {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url can not be empty!");
        }
        //append params , if necessary
        url = appendParams(url, params);
        Request.Builder builder = new Request.Builder();
        //add headers , if necessary
        appendHeaders(builder, headers);
        builder.url(url).tag(tag);
        return builder.build();
    }

    @Override
    public RequestBody buildRequestBody() {
        return null;
    }


    private String appendParams(String url, Map<String, T> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url + "?");
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                sb.append(key).append("=").append((T)params.get(key)).append("&");
            }
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


}

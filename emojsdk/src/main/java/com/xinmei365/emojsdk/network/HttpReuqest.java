package com.xinmei365.emojsdk.network;

import android.util.Pair;
import android.widget.ImageView;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by xinmei on 15/11/18.
 */
public abstract class HttpReuqest<T> {

    protected RequestBody requestBody;
    protected Request request;

    protected String url;
    protected String tag;
    protected Map<String, T> params;
    protected Map<String, String> headers;

    protected boolean isImageReq = false;

    public HttpReuqest(String url, String tag,
                          Map<String, T> params, Map<String, String> headers) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
    }

    public abstract Request buildRequest();

    public abstract RequestBody buildRequestBody();


    protected void appendHeaders(Request.Builder builder, Map<String, String> headers) {
        if (builder == null) {
            throw new IllegalArgumentException("builder can not be empty!");
        }

        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }


    public static class Builder {
        private String url;
        private String tag;
        private Map<String, String> headers;
        private Map<String, String> params;
        private Pair<String, File>[] files;
        private MediaType mediaType;

        private String destFileDir;
        private String destFileName;

        private ImageView imageView;
        private int errorResId = -1;

        //for post
        private String content;
        private byte[] bytes;
        private File file;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder tag(String tag)
        {
            this.tag = tag;
            return this;
        }

        public Builder params(Map<String, String> params)
        {
            this.params = params;
            return this;
        }

        public Builder addParams(String key, String val)
        {
            if (this.params == null)
            {
                params = new IdentityHashMap<String,String>();
            }
            params.put(key, val);
            return this;
        }

        public Builder headers(Map<String, String> headers)
        {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String key, String val)
        {
            if (this.headers == null)
            {
                headers = new IdentityHashMap<String,String>();
            }
            headers.put(key, val);
            return this;
        }


        public Builder files(Pair<String, File>... files)
        {
            this.files = files;
            return this;
        }

        public Builder destFileName(String destFileName)
        {
            this.destFileName = destFileName;
            return this;
        }

        public Builder destFileDir(String destFileDir)
        {
            this.destFileDir = destFileDir;
            return this;
        }


        public Builder imageView(ImageView imageView)
        {
            this.imageView = imageView;
            return this;
        }

        public Builder errResId(int errorResId)
        {
            this.errorResId = errorResId;
            return this;
        }

        public Builder content(String content)
        {
            this.content = content;
            return this;
        }

        public Builder mediaType(MediaType mediaType)
        {
            this.mediaType = mediaType;
            return this;
        }
    }

}

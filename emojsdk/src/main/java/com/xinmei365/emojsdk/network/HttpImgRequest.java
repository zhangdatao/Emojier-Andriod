package com.xinmei365.emojsdk.network;

import android.widget.ImageView;

import java.util.Map;

/**
 * Created by xinmei on 15/11/23.
 */
public class HttpImgRequest extends HttpGetRequest {



    private ImageView mImageview;
    private int mErrorResId;

    public HttpImgRequest(String url, String tag, Map<String, String> params,
                          Map<String, String> headers) {
        this(url,tag,params,headers,null,-1);
    }

    public HttpImgRequest(String url, String tag, Map<String, String> params,
                          Map<String, String> headers,ImageView imageView, int errorResId) {
        super(url, tag, params, headers);
        mImageview = imageView;
        mErrorResId = errorResId;
        isImageReq = true;
    }


}

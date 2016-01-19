package com.xinmei365.emojsdk.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xinmei365.emojsdk.domain.Constant;
import com.xinmei365.emojsdk.utils.CommUtil;
import com.xinmei365.emojsdk.utils.CompressUtil;
import com.xinmei365.emojsdk.utils.MD5Util;
import com.xinmei365.emojsdk.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Created by xinmei on 15/11/18.
 */
public class HttpManager {


    private static HttpManager mInstance = null;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;

    private HttpManager() {
        this.mOkHttpClient = new OkHttpClient();
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));

        mDelivery = new Handler(Looper.getMainLooper());
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk >= 23) {
            GsonBuilder gsonBuilder = new GsonBuilder()
                    .excludeFieldsWithModifiers(
                            Modifier.FINAL,
                            Modifier.TRANSIENT,
                            Modifier.STATIC);
            mGson = gsonBuilder.create();
        } else {
            mGson = new Gson();
        }

    }

    public static HttpManager getInstance() {
        if (mInstance == null) {
            synchronized (HttpManager.class) {
                if (mInstance == null) {
                    mInstance = new HttpManager();
                }
            }
        }
        return mInstance;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Handler getDelivery() {
        return mDelivery;
    }


    public void execute(final HttpReuqest httReq, ResponseCallback respCallback) {
        if (respCallback == null) {
            respCallback = ResponseCallback.DEFAULT_RESPONSE_CALLBACK;
        }
        if (respCallback.onBefore(httReq)){ //return true, find in local buffer
            return;
        }
        final ResponseCallback finalRespCallback = respCallback;
        mOkHttpClient.newCall(httReq.buildRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                //TODO when cancel , should do?
                sendFailResultCallback(httReq, e, finalRespCallback);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    if (response.code() >= 400 && response.code() <= 599) {
                        sendFailResultCallback(httReq, new RuntimeException(response.body().string()), finalRespCallback);
                    } else {
                        if (httReq.isImageReq){
                            //handle image download request
                            processImgReq(httReq,response,finalRespCallback);
                        }else {
                            if (finalRespCallback.mType == String.class) {
                                String string = response.body().string();
                                sendSuccessResultCallback(string, finalRespCallback);
                            } else if (finalRespCallback.mType == Bitmap.class){
                                byte[] bytes = response.body().bytes();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                sendSuccessResultCallback(bitmap,finalRespCallback);
                            } else{
//                                Object o = mGson.fromJson(string, finalRespCallback.mType);
                                sendSuccessResultCallback(response, finalRespCallback);
                            }
                        }
                    }
                } catch (Exception e) {
                    sendFailResultCallback(httReq, e, finalRespCallback);
                }
            }
        });
    }

    /**
     * handle image downlaod request, cache emoji to local
     * if the content beyond buffer size, remove the old emoji
     */
    private void processImgReq(HttpReuqest httReq, Response response, ResponseCallback respCallback) {
        InputStream inStream = response.body().byteStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inStream);

        String filName = MD5Util.getMD5String(httReq.url);
        File file = new File(Environment.getExternalStorageDirectory()+ Constant.IMAGE_CACHE_DIR + "/" + filName + ".png");
        File restFile = CompressUtil.compressAndSaveBitmap(bitmap, file);
        file.setLastModified(System.currentTimeMillis());
        String destPath = restFile.getPath();

        if (!StringUtil.isNullOrEmpty(destPath)){
            sendSuccessResultCallback(destPath,respCallback);
        }

        CommUtil.deleteRedundancyImgs();
    }


    public void sendFailResultCallback(final HttpReuqest request, final Exception e, final ResponseCallback callback) {
        if (callback == null) return;

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(request, e);
                callback.onAfter();
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final ResponseCallback callback) {
        if (callback == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onSucess(object);
                callback.onAfter();
            }
        });
    }

    public void cancel(Object tag) {
        if (null != tag)
            mOkHttpClient.cancel(tag);
    }


}

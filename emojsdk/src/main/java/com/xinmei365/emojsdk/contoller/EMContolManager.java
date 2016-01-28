package com.xinmei365.emojsdk.contoller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.xinmei365.emojsdk.domain.CharEntity;
import com.xinmei365.emojsdk.domain.Constant;
import com.xinmei365.emojsdk.domain.EMCandiateEntity;
import com.xinmei365.emojsdk.domain.EMCandiateProperty;
import com.xinmei365.emojsdk.domain.EMCharacterEntity;
import com.xinmei365.emojsdk.domain.EMKeyEntity;
import com.xinmei365.emojsdk.domain.EMReceiveTxtEntity;
import com.xinmei365.emojsdk.domain.UserInputEntity;
import com.xinmei365.emojsdk.network.HttpGetRequest;
import com.xinmei365.emojsdk.network.HttpImgRequest;
import com.xinmei365.emojsdk.network.HttpManager;
import com.xinmei365.emojsdk.network.HttpReuqest;
import com.xinmei365.emojsdk.network.HttpUrlConfig;
import com.xinmei365.emojsdk.network.ResponseCallback;
import com.xinmei365.emojsdk.orm.EMDBMagager;
import com.xinmei365.emojsdk.utils.CommUtil;
import com.xinmei365.emojsdk.utils.Logger;
import com.xinmei365.emojsdk.utils.MD5Util;
import com.xinmei365.emojsdk.utils.SharedPrenceUtil;
import com.xinmei365.emojsdk.utils.StringUtil;
import com.xinmei365.emojsdk.view.OnEMResponseListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by xinmei on 15/11/20.
 */
public class EMContolManager {

    private final String TAG = EMContolManager.class.getSimpleName();
    private static EMContolManager mInstance;
    private EMContolManager() {

    }


    public static EMContolManager getInstance() {
        if (mInstance == null) {
            synchronized (EMContolManager.class) {
                if (mInstance == null) {
                    mInstance = new EMContolManager();
                }
            }
        }
        return mInstance;
    }

    public void requestForEmoj(final UserInputEntity reqEmojEntity, final OnEMResponseListener emojResponseSpanListener, final boolean needPutMsgQueue) {
        Map<String, String> emojReqParam = new HashMap<String, String>();

        String emojTag = reqEmojEntity.mEmojTag;
        if (reqEmojEntity.mEmojTag.contains("\\")) {
            reqEmojEntity.mEmojTag = reqEmojEntity.mEmojTag.substring(1, reqEmojEntity.mEmojTag.length());
        }

        if (reqEmojEntity.mEmojTag.contains(" ")) {
            emojTag = Uri.encode(reqEmojEntity.mEmojTag);
        }

        emojReqParam.put("q", emojTag);
        emojReqParam.put("app_key", SharedPrenceUtil.getInstance(CommUtil.getContext()).getString(Constant.KEY_APP_KEY));
        emojReqParam.put("text_size", CommUtil.getReqImgSize());
        String userKey = SharedPrenceUtil.getInstance(CommUtil.getContext()).getString(Constant.KEY_USER_KEY);
        if (!StringUtil.isNullOrEmpty(userKey)) {
            emojReqParam.put("p_key", userKey);
        }
        int count = SharedPrenceUtil.getInstance(CommUtil.getContext()).getInt(Constant.KEY_EMOJI_CAND_COUNT);
        emojReqParam.put("count", String.valueOf(count));
        HttpGetRequest emojHttpReq = new HttpGetRequest(HttpUrlConfig.CANDIATE_EMOJ_URL, TAG, emojReqParam, null);
        HttpManager.getInstance().execute(emojHttpReq, new ResponseCallback<String>() {

            @Override
            public boolean onBefore(HttpReuqest request) {

                try {
                    String emojConent = EMDBMagager.getInstance().queryEmojByTag(reqEmojEntity.mEmojTag);
                    if (StringUtil.isNullOrEmpty(emojConent)) {
                        return false;
                    } else {
                        EMCandiateEntity candiateEmojEntity = new EMCandiateEntity(reqEmojEntity.mEmojTag, reqEmojEntity.mEmojStartIndex);
                        candiateEmojEntity.parseRespJson(new JSONObject(emojConent));
                        requestForBitmap(candiateEmojEntity, emojResponseSpanListener, needPutMsgQueue);
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void onError(HttpReuqest request, Exception exp) {
                Log.d(TAG, "request emoj by tag error,exp =" + exp.getLocalizedMessage());
                if (emojResponseSpanListener != null) {
                    emojResponseSpanListener.onEMRespnSpanError(exp);
                }
                //put already tranlated emojikey means there already exist data
                Logger.d("xxxxxx", "request emoj error putEmojKey");
                if (needPutMsgQueue) {
                    MessageQueueManager.getInstance().putTransedEmojKey(new EMCharacterEntity(reqEmojEntity.mEmojStartIndex, reqEmojEntity.mEmojTag));
                }
            }

            @Override
            public void onSucess(String response) {
                try {
                    Log.d(TAG, "request emoj by tag success,response =" + response);
                    EMCandiateEntity candiateEmojEntity = new EMCandiateEntity(reqEmojEntity.mEmojTag, reqEmojEntity.mEmojStartIndex);
                    candiateEmojEntity.parseRespJson(new JSONObject(response));
                    if (candiateEmojEntity.mStatusCode == 200 && candiateEmojEntity.mCandCount > 0) {
                        EMDBMagager.getInstance().cacheEmojTag(reqEmojEntity.mEmojTag, response);
                        requestForBitmap(candiateEmojEntity, emojResponseSpanListener, needPutMsgQueue);
                    } else {
                        if (needPutMsgQueue) {
                            MessageQueueManager.getInstance().putTransedEmojKey(new EMCharacterEntity(reqEmojEntity.mEmojStartIndex, reqEmojEntity.mEmojTag));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     *
     * @param candiateEmojEntity
     * @param emojResponseSpanListener
     * @param needPutMsgQueue
     */
    private void requestForBitmap(final EMCandiateEntity candiateEmojEntity, final OnEMResponseListener emojResponseSpanListener, final boolean needPutMsgQueue) {
        final ArrayList<Bitmap> hasDownloadImg = new ArrayList<Bitmap>();

        ArrayList<EMCandiateProperty> emojTagEntities = candiateEmojEntity.mEmojEntities;
        if (emojTagEntities != null && emojTagEntities.size() > 0) {
            for (EMCandiateProperty tagEntity : emojTagEntities) {
                final ArrayList<EMCandiateProperty.EMImgProperty> emImgProperties = tagEntity.mEmImgProperties;
                if (emImgProperties != null && emImgProperties.size() > 0) {
                    for (final EMCandiateProperty.EMImgProperty emImgProperty : emImgProperties) {
                        String emojUrl = emImgProperty.mEmojUrl;
                        final HttpImgRequest emojHttpReq = new HttpImgRequest(emojUrl, TAG, null, null);
                        HttpManager.getInstance().execute(emojHttpReq, new ResponseCallback<String>() {

                            @Override
                            public boolean onBefore(HttpReuqest request) {
                                super.onBefore(request);
                                try {
                                    //check if there responding cached emoji
                                    String filName = MD5Util.getMD5String(emojHttpReq.buildRequest().urlString());
                                    File file = new File(Constant.IMAGE_CACHE_DIR + "/" + filName + ".png");
                                    if (file.exists()) {
                                        file.setLastModified(System.currentTimeMillis());
                                        emImgProperty.mEmojPath = file.getPath();

                                        emImgProperty.mEmojBmap = BitmapFactory.decodeStream(new FileInputStream(file));
                                        if (!hasDownloadImg.contains(emImgProperty.mEmojBmap)) {
                                            hasDownloadImg.add(emImgProperty.mEmojBmap);
                                        }
                                        if (hasDownloadImg.size() == emImgProperties.size()) {

                                            if (emojResponseSpanListener != null) {
                                                emojResponseSpanListener.onEMRespSpanSb(candiateEmojEntity);
                                            }
                                            if (needPutMsgQueue) {
                                                MessageQueueManager.getInstance().putTransedEmojKey(new EMCharacterEntity(candiateEmojEntity.mEMStart, candiateEmojEntity.mEMKey));
                                            }
                                        }

                                        //delete reduncany emojis
                                        CommUtil.deleteRedundancyImgs();
                                        return true;
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }

                            @Override
                            public void onError(HttpReuqest request, Exception exp) {
                                if (emojResponseSpanListener != null) {
                                    emojResponseSpanListener.onEMRespnSpanError(exp);
                                }

                                if (needPutMsgQueue) {
                                    MessageQueueManager.getInstance().putTransedEmojKey(new EMCharacterEntity(candiateEmojEntity.mEMStart, candiateEmojEntity.mEMKey));
                                }
                            }

                            @Override
                            public void onSucess(String respEmojPath) {
                                if (!StringUtil.isNullOrEmpty(respEmojPath)) {
                                    emImgProperty.mEmojPath = respEmojPath;
                                    emImgProperty.mEmojBmap = BitmapFactory.decodeFile(respEmojPath);
                                    if (!hasDownloadImg.contains(emImgProperty.mEmojBmap)) {
                                        hasDownloadImg.add(emImgProperty.mEmojBmap);
                                    }
                                    if (hasDownloadImg.size() == emImgProperties.size()) {
                                        if (emojResponseSpanListener != null) {
                                            emojResponseSpanListener.onEMRespSpanSb(candiateEmojEntity);
                                        }
                                        if (needPutMsgQueue) {
                                            MessageQueueManager.getInstance().putTransedEmojKey(new EMCharacterEntity(candiateEmojEntity.mEMStart, candiateEmojEntity.mEMKey));
                                        }
                                    }

                                }
                            }
                        });
                    }
                }
            }
        }
    }


    /**
     * download all the emoji images
     *
     * @param receTxtEntity
     * @param emojResponseSpanListener
     */

    public void downLoadAllPopertyEmoj(final EMReceiveTxtEntity receTxtEntity, final OnEMResponseListener emojResponseSpanListener) {
        for (EMCandiateProperty EMCandiateProperty : receTxtEntity.mNoBitMapEmojTagEntities) {
            for (final EMCandiateProperty.EMImgProperty emImgProperty : EMCandiateProperty.mEmImgProperties) {
                String emojUrl = emImgProperty.mEmojUrl;
                final HttpImgRequest emojHttpReq = new HttpImgRequest(emojUrl, TAG, null, null);
                HttpManager.getInstance().execute(emojHttpReq, new ResponseCallback<String>() {

                    @Override
                    public void onError(HttpReuqest request, Exception e) {

                    }

                    @Override
                    public void onSucess(String respEmojPath) {
                        if (!StringUtil.isNullOrEmpty(respEmojPath)) {
                            emImgProperty.mEmojPath = respEmojPath;
                            emImgProperty.mEmojBmap = BitmapFactory.decodeFile(respEmojPath);
                            //TODO:
                            if (!receTxtEntity.mHasDownBitMap.contains(respEmojPath)) {
                                receTxtEntity.mHasDownBitMap.add(respEmojPath);
                            }
                            int downBitmap = receTxtEntity.mHasDownBitMap.size();
                            int noBitmapEmojEntitySize = 0;
                            ArrayList<EMCandiateProperty> mNoBitMapEmojTagEntities = receTxtEntity.mNoBitMapEmojTagEntities;
                            for (EMCandiateProperty tagEntity : mNoBitMapEmojTagEntities) {
                                noBitmapEmojEntitySize += tagEntity.mEmImgProperties.size();
                            }
                            if (downBitmap == noBitmapEmojEntitySize) {
                                //all the emojis have downloaed,notify update ui
                                emojResponseSpanListener.onEMRespProperty(receTxtEntity);
                            }

                            //delete reduncany emojis
                            CommUtil.deleteRedundancyImgs();
                        }
                    }
                });
            }
        }

    }

    public void requestForEmojById(final EMReceiveTxtEntity receTxtEntity, final OnEMResponseListener emojResponseSpanListener) {
        for (final CharEntity charEntity : receTxtEntity.mEmojIds) {
            Map<String, String> emojReqParam = new HashMap<String, String>();

            String prefix = charEntity.mEmojID.substring(0, charEntity.mEmojID.length() - 7);
            String suffix = charEntity.mEmojID.substring(charEntity.mEmojID.length() - 4);
            charEntity.mEmojID = prefix + CommUtil.getReqImgSize() + suffix;

            emojReqParam.put("id", charEntity.mEmojID);
            emojReqParam.put("app_key", SharedPrenceUtil.getInstance(CommUtil.getContext()).getString(Constant.KEY_APP_KEY));
            HttpGetRequest emojHttpReq = new HttpGetRequest(HttpUrlConfig.GET_EMOJ_PROPERTY, TAG, emojReqParam, null);
            HttpManager.getInstance().execute(emojHttpReq, new ResponseCallback<String>() {

                @Override
                public void onError(HttpReuqest request, Exception exp) {
                    if (emojResponseSpanListener != null) {
                        emojResponseSpanListener.onEMRespnSpanError(exp);
                    }
                }

                @Override
                public void onSucess(String response) {
                    try {

                        EMDBMagager.getInstance().cacheEmojIdProperty(charEntity.mEmojID, response);

                        EMCandiateProperty emojEntity = new EMCandiateProperty(charEntity.mEmojID);
                        emojEntity.mSpanStarIndex = charEntity.start;
                        emojEntity.mSpanEndIndex = charEntity.end;
                        emojEntity.parseReqJson(new JSONObject(response));

                        receTxtEntity.mNoBitMapEmojTagEntities.add(emojEntity);
                        receTxtEntity.mEmojIds.remove(charEntity);
                        if (receTxtEntity.mEmojIds.size() == 0 && receTxtEntity.mNoBitMapEmojTagEntities.size() > 0) {
                            //all cached id converted to object, it means start downloading image
                            downLoadAllPopertyEmoj(receTxtEntity, emojResponseSpanListener);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * @param timestamp
     */

    public void getAllHotEmojTag(long timestamp) {
        Map<String, String> reqParmas = new HashMap<>();
        reqParmas.put("timestamp", String.valueOf(timestamp));
        reqParmas.put("app_key", SharedPrenceUtil.getInstance(CommUtil.getContext()).getString(Constant.KEY_APP_KEY));
        String userKey = SharedPrenceUtil.getInstance(CommUtil.getContext()).getString(Constant.KEY_USER_KEY);
        if (!StringUtil.isNullOrEmpty(userKey)) {
            reqParmas.put("p_key", Uri.encode(userKey));
            long priTimestamp = SharedPrenceUtil.getInstance(CommUtil.getContext()).getLong(SharedPrenceUtil.LastPriEmojTagTimestamp, 0);
            reqParmas.put("pritimestamp", String.valueOf(priTimestamp));

        }
        HttpGetRequest httpGetReq = new HttpGetRequest(HttpUrlConfig.EMOJ_HOT_TAG_LIET, TAG, reqParmas, null);
        HttpManager.getInstance().execute(httpGetReq, new ResponseCallback<String>() {
            @Override
            public void onError(HttpReuqest request, Exception e) {
                Log.d(TAG, "request for hot emoj tag error");
            }

            @Override
            public void onSucess(String response) {
                EMKeyEntity tagEntitiy = new EMKeyEntity();
                tagEntitiy.parseJson(response);
                cacheEmojTag(tagEntitiy);
            }
        });

    }

    private void cacheEmojTag(EMKeyEntity emojTagEntity) {
        SharedPrenceUtil.getInstance(CommUtil.getContext()).setLong(SharedPrenceUtil.LastPubEmojTagTimestamp, emojTagEntity.mPubTimestamp);
        SharedPrenceUtil.getInstance(CommUtil.getContext()).setLong(SharedPrenceUtil.LastPriEmojTagTimestamp, emojTagEntity.mPriTimestamp);
        final ArrayList<String> emojTags = emojTagEntity.mEmojTags;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (String emojTag : emojTags) {
                    EMDBMagager.getInstance().cacheEmojTag(emojTag, null);
                }
            }
        };
        new Thread(runnable).start();

    }

    public void startSendMsg() {
        Vector<EMCharacterEntity> needTransEmojKeys = null;

        needTransEmojKeys = MessageQueueManager.getInstance().getNeedTransEmojKeys();
        while (needTransEmojKeys.size() != 0) {
            EMCharacterEntity entry = MessageQueueManager.getInstance().getNextSendMessage();
            Logger.d(TAG, "one key need transfer word=" + entry.mWord + " start=" + entry.mWordStart);
            reqestEmojProperty(new UserInputEntity(entry.mWord.toString(), entry.mWordStart));
            needTransEmojKeys = MessageQueueManager.getInstance().getNeedTransEmojKeys();
        }

    }

    private void reqestEmojProperty(UserInputEntity userInputEntity) {
        requestForEmoj(userInputEntity, null, true);
    }
}

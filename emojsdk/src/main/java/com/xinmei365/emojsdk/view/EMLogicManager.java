package com.xinmei365.emojsdk.view;

import android.content.Context;
import android.os.Environment;

import com.xinmei365.emojsdk.contoller.EMContolManager;
import com.xinmei365.emojsdk.contoller.EMRecentManger;
import com.xinmei365.emojsdk.domain.Constant;
import com.xinmei365.emojsdk.domain.EMCandiateEntity;
import com.xinmei365.emojsdk.domain.EMReceiveTxtEntity;
import com.xinmei365.emojsdk.domain.UserInputEntity;
import com.xinmei365.emojsdk.orm.DbOpenHelper;
import com.xinmei365.emojsdk.utils.CommUtil;
import com.xinmei365.emojsdk.utils.DeviceUtil;
import com.xinmei365.emojsdk.utils.SharedPrenceUtil;

import java.io.File;

/**
 * Created by xinmei on 15/11/20.
 */
public class EMLogicManager implements OnEMResponseListener {

    private static EMLogicManager mInstance;
    private OnEMResponseListener mEMRespSpanListener;
    private EMContolManager mEmojControlMgr;
    private Context mAppContext;

    private EMLogicManager() {
        mEmojControlMgr = EMContolManager.getInstance();
    }

    public Context getAppContext(){
        return mAppContext;
    }

    public static EMLogicManager getInstance() {
        if (mInstance == null) {
            synchronized (EMLogicManager.class) {
                if (mInstance == null) {
                    mInstance = new EMLogicManager();
                }
            }
        }
        return mInstance;
    }


    public void requestForEmoj(String emKey, int emStart) {
        UserInputEntity reqEmojEntity = new UserInputEntity(emKey, emStart);
        mEmojControlMgr.requestForEmoj(reqEmojEntity, this, false);
    }


    public void requestForEmojById(EMReceiveTxtEntity receTxtEntity) {
//        if (mEMRespSpanListener == null) {
//            mEMRespSpanListener = new DefEmojSpan(userInputStr);
//        }
        mEmojControlMgr.requestForEmojById(receTxtEntity, this);
    }

    public void downLoadAllEmojBitmap(EMReceiveTxtEntity receTxtEntity) {
        mEmojControlMgr.downLoadAllPopertyEmoj(receTxtEntity, this);
    }

    public void getAllEmKeys(){
        long emojTagTimestamp = SharedPrenceUtil.getInstance(CommUtil.getContext()).getLong(SharedPrenceUtil.LastPubEmojTagTimestamp,1000);
        mEmojControlMgr.getAllHotEmojTag(emojTagTimestamp);
    }

    public void setEMRespSpanListener(OnEMResponseListener emojResponseSpanListener) {
        this.mEMRespSpanListener = emojResponseSpanListener;
    }



    @Override
    public void onEMRespnSpanError(Exception exp) {
        if (mEMRespSpanListener != null) {
            mEMRespSpanListener.onEMRespnSpanError(exp);
        }
    }

    @Override
    public void onEMRespProperty(EMReceiveTxtEntity EMReceiveTxtEntity) {
        if (mEMRespSpanListener != null) {
            mEMRespSpanListener.onEMRespProperty(EMReceiveTxtEntity);
        }
    }

    @Override
    public  void onEMRespSpanSb(EMCandiateEntity emojCandiateEntity) {
        if (mEMRespSpanListener != null) {
            mEMRespSpanListener.onEMRespSpanSb(emojCandiateEntity);
        }
    }

    public void setAppKey(String appKey) {
        SharedPrenceUtil.getInstance(CommUtil.getContext()).setString(Constant.KEY_APP_KEY, appKey);
    }

    public void setUserKey(String userkey) {
        SharedPrenceUtil.getInstance(CommUtil.getContext()).setString(Constant.KEY_USER_KEY, userkey);
    }

    public void setEmojiSize(int emojiSize){
        SharedPrenceUtil.getInstance(CommUtil.getContext()).setInt(Constant.KEY_CUSTOM_EMOJI_SIZE, emojiSize);
    }

    //set the candidate count for each word, if not set or less than 0, 1 candidate as default
    public void setCandidateCount(int count){
        if (count<=0){
            count = 1;
        }
        SharedPrenceUtil.getInstance(CommUtil.getContext()).setInt(Constant.KEY_EMOJI_CAND_COUNT,count);
    }

    /**
     * set the max value of the emoji buffer,
     * @param bufferSize which takes a value in M
     */
    public void setMaxEmojiBuffer(int bufferSize){
        SharedPrenceUtil.getInstance(CommUtil.getContext()).setInt(Constant.KEY_MAX_EMOJI_BUFFER_SIZE, bufferSize);
    }


    public void init(Context appContext){
        mAppContext = appContext;
        getAllEmKeys();
        processImgCacheDir();
        EMRecentManger.getInstance().init();
        int emojiSize = SharedPrenceUtil.getInstance(CommUtil.getContext()).getInt(Constant.KEY_CUSTOM_EMOJI_SIZE);
        if (emojiSize <=0){
            SharedPrenceUtil.getInstance(CommUtil.getContext()).setInt(Constant.KEY_CUSTOM_EMOJI_SIZE, DeviceUtil.getSpanEmojSize());
        }
        int candCount = SharedPrenceUtil.getInstance(CommUtil.getContext()).getInt(Constant.KEY_EMOJI_CAND_COUNT);
        if (candCount<=0) {
            SharedPrenceUtil.getInstance(CommUtil.getContext()).setInt(Constant.KEY_EMOJI_CAND_COUNT, 1);
        }
    }

    private void processImgCacheDir() {
        //check image dir
        try {
            File file = new File(Constant.IMAGE_CACHE_DIR);
            if (!file.exists()) {
               file.mkdirs();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

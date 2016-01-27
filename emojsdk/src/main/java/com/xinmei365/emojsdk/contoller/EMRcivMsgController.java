package com.xinmei365.emojsdk.contoller;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.xinmei365.emojsdk.domain.CharEntity;
import com.xinmei365.emojsdk.domain.Constant;
import com.xinmei365.emojsdk.domain.EMCandiateProperty;
import com.xinmei365.emojsdk.domain.EMHolderEntity;
import com.xinmei365.emojsdk.domain.EMReceiveTxtEntity;
import com.xinmei365.emojsdk.domain.TempEntity;
import com.xinmei365.emojsdk.notify.NotifyEntity;
import com.xinmei365.emojsdk.notify.NotifyKeys;
import com.xinmei365.emojsdk.notify.NotifyManager;
import com.xinmei365.emojsdk.orm.EMDBMagager;
import com.xinmei365.emojsdk.utils.Logger;
import com.xinmei365.emojsdk.utils.MD5Util;
import com.xinmei365.emojsdk.utils.StringUtil;
import com.xinmei365.emojsdk.view.DefEmojSpan;
import com.xinmei365.emojsdk.view.DefaultEMResponse;
import com.xinmei365.emojsdk.view.EMLogicManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xinmei on 15/12/4.
 */
public class EMRcivMsgController {

    private static final String TAG = EMRcivMsgController.class.getSimpleName();
    private static EMRcivMsgController mInstance = null;
    private static IReceiveMsgTranslateListener msgTranslateListener;

    private EMRcivMsgController() {

    }

    public static EMRcivMsgController getInstance() {
        if (mInstance == null) {
            synchronized (EMRcivMsgController.class) {
                if (mInstance == null) {
                    mInstance = new EMRcivMsgController();
                }
            }
        }
        return mInstance;
    }


    public void processReceiveContent(String msgStr) {

        Logger.d(TAG, "receive content=" + msgStr);

        EMReceiveTxtEntity receTxtEnty = new EMReceiveTxtEntity(msgStr);

        Map<Integer, CharEntity> map = new TreeMap<>();

        String regex =  "(#\\|)[\\w:_ ]{1,}\\|";//"#\\[[\\w]{4}_[a-zA-Z_0-9]{1,}\\]|(#\\|)[\\w:_ ]{1,}\\|";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msgStr);
        ArrayList<TempEntity> arrs = new ArrayList<>();
        while (matcher.find()) {
            //find emoji content
            String emojTag = matcher.group();

            arrs.add(new TempEntity(matcher.start(), matcher.end()));
            int currStart = matcher.start();
//            if (emojTag.startsWith("#[")) {
//                CharEntity charEntity = new CharEntity(emojTag, currStart, CharEntity.CharType.LocalEMOJ);
//                charEntity.mEmojUnicode = emojTag.substring(2, emojTag.length() - 1);
//                map.put(currStart, charEntity);
//            } else if (emojTag.startsWith("#|")) {
            if (emojTag.startsWith("#|")) {
                CharEntity charEntity = new CharEntity(emojTag, currStart, CharEntity.CharType.OnlineEmoj);
                charEntity.setEmojKeyID(emojTag.substring(2, emojTag.length() - 1));
                map.put(currStart, charEntity);
            }
        }

        if (arrs.size() == 0) {
            receTxtEnty.mFinalSpanSB.append(msgStr);
            msgTranslateListener.onTranslateReceiveMsgSuccess(receTxtEnty);
            return;
        }

        //handle char conjunction case, split normal content
        int minCurIndex = 0;
        for (int m = 0; m < arrs.size(); m++) {
            TempEntity tempEnty = arrs.get(m);
            if (tempEnty.start > minCurIndex) {
                String str = msgStr.substring(minCurIndex, tempEnty.start);
                int curStart = minCurIndex;
                CharEntity charEntity = new CharEntity(str, curStart, CharEntity.CharType.Normal);
                map.put(curStart, charEntity);
            }
            minCurIndex = tempEnty.end;

            if (m == arrs.size() - 1 && tempEnty.end < msgStr.length()) {
                String str = msgStr.substring(tempEnty.end, msgStr.length());
                int curStart = tempEnty.end;
                CharEntity charEntity = new CharEntity(str, curStart, CharEntity.CharType.Normal);
                map.put(curStart, charEntity);
            }
        }

        receTxtEnty.allContent = map;


        assembleBuilder(receTxtEnty);
        processEmojIds(receTxtEnty);
        return;

    }

    private void assembleBuilder(EMReceiveTxtEntity receTxtEnty) {
        Set<Map.Entry<Integer, CharEntity>> entrySet = receTxtEnty.allContent.entrySet();
        for (Map.Entry<Integer, CharEntity> entry : entrySet) {
            CharEntity charEnty = entry.getValue();
            switch (charEnty.mCharType) {
                case Normal:
//                    receTxtEnty.mFinalSpanSB.setSpan(charEnty.mOriginalStr, charEnty.start, charEnty.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    receTxtEnty.mFinalSpanSB.append(charEnty.mOriginalStr);
                    break;
//                case LocalEMOJ:
//                    processLocalEmoj(ctx, receTxtEnty, charEnty);
//                    break;
                case OnlineEmoj:
                    processOnlineEmoj(receTxtEnty, charEnty);
                    break;
            }
        }
    }

//    /**
//     * process local emoj
//     *
//     * @param receTxtEnty
//     */
//    private void processLocalEmoj(Context ctx, EMReceiveTxtEntity receTxtEnty, CharEntity charEntity) {
//
//        if (!StringUtil.isNullOrEmpty(charEntity.mEmojUnicode)) {
//            String emojRes = charEntity.mEmojUnicode;
//            float density = ctx.getResources().getDisplayMetrics().density;
//            int emojSize = (int) density * 20;
//            int id = ctx.getResources().getIdentifier(emojRes, "drawable", ctx.getPackageName());
//            if (id != 0) {
//                Drawable drawable = ctx.getResources().getDrawable(id);
//                drawable.setBounds(0, 0, emojSize, emojSize);
//                ImageSpan imageSpan = new ImageSpan(drawable);
//                receTxtEnty.mFinalSpanSB.append(EMHolderEntity.FINAL_HOLDER);
//                int length = receTxtEnty.mFinalSpanSB.toString().length();
//                receTxtEnty.mFinalSpanSB.setSpan(imageSpan, length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }
//    }


    private void processOnlineEmoj(EMReceiveTxtEntity receTxtEnty, CharEntity charEnty) {
        String emojProperty = EMDBMagager.getInstance().getEmojPropertyById(charEnty.mEmojID);
        if (StringUtil.isNullOrEmpty(emojProperty)) {
            receTxtEnty.mEmojIds.add(charEnty);
            //there ate no emoji property cache content, show the related key directly
            receTxtEnty.mFinalSpanSB.append(charEnty.mEmojKey);
        } else {
            processOnlinEmojProperty(receTxtEnty, charEnty, emojProperty);

        }

    }

    private void processOnlinEmojProperty(EMReceiveTxtEntity receTxtEnty, CharEntity charEntity, String emojProperty) {
        try {
            EMCandiateProperty emojEntity = new EMCandiateProperty(charEntity.mEmojID);
            emojEntity.mSpanStarIndex = charEntity.start;
            emojEntity.mSpanEndIndex = charEntity.end;
            emojEntity.parseReqJson(new JSONObject(emojProperty));
            if (emojEntity.mEmImgProperties.size() > 0) {
                boolean haveEmptyBitmap = false;
                for (EMCandiateProperty.EMImgProperty emImgProperty : emojEntity.mEmImgProperties) {
                    String filName = MD5Util.getMD5String(emImgProperty.mEmojUrl);
                    File file = new File(Constant.IMAGE_CACHE_DIR + "/" + filName + ".png");
                    if (file.exists()) {
                        emImgProperty.mEmojPath = file.getPath();
                        emImgProperty.mEmojBmap = BitmapFactory.decodeFile(file.getPath());
                    }
                    if (emImgProperty.mEmojBmap == null) {
                        haveEmptyBitmap = true;
                        if (!receTxtEnty.mNoBitMapEmojTagEntities.contains(emojEntity)) {
                            receTxtEnty.mNoBitMapEmojTagEntities.add(emojEntity);
                            break;
                        }
                    }
                }
                if (!haveEmptyBitmap) {
                    //we can find related images, convert to span image content
                    DefEmojSpan emojSpan = new DefEmojSpan(emojEntity);


                    receTxtEnty.mFinalSpanSB.append(EMHolderEntity.FINAL_HOLDER);
                    int length = receTxtEnty.mFinalSpanSB.toString().length();
                    receTxtEnty.mFinalSpanSB.setSpan(emojSpan, length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    //emoji property json exist in local but there are no related image, show content directly
                    receTxtEnty.mFinalSpanSB.append(charEntity.mEmojKey);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processEmojIds(EMReceiveTxtEntity receTxtEntity) {
        //request property according to id
        if (receTxtEntity == null) return;
        if (receTxtEntity.mEmojIds.size() == 0 && receTxtEntity.mNoBitMapEmojTagEntities.size() == 0){
            msgTranslateListener.onTranslateReceiveMsgSuccess(receTxtEntity);
            return;
        }


        if (receTxtEntity.mEmojIds.size() > 0) {
            notifyCallBack();
            EMLogicManager.getInstance().requestForEmojById(receTxtEntity);
        } else if (receTxtEntity.mEmojIds.size() == 0 && receTxtEntity.mNoBitMapEmojTagEntities.size() > 0) {
            //request to download image
            notifyCallBack();
            EMLogicManager.getInstance().downLoadAllEmojBitmap(receTxtEntity);
        }
    }

    private static void notifyCallBack() {
        EMLogicManager.getInstance().setEMRespSpanListener(new DefaultEMResponse() {
            @Override
            public void onEMRespProperty(EMReceiveTxtEntity txtEntity) {
                if (msgTranslateListener != null){
                    msgTranslateListener.onTranslateReceiveMsgSuccess(txtEntity);
                }
            }
        });
    }


    public void setOnReceiveMsgTranslateListener(IReceiveMsgTranslateListener msgTranslateListener) {
        this.msgTranslateListener = msgTranslateListener;
    }

    public interface IReceiveMsgTranslateListener{
        void onTranslateReceiveMsgSuccess(EMReceiveTxtEntity txtEntity);
    }
}

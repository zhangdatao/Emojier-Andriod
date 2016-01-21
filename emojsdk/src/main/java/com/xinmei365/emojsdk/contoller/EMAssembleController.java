package com.xinmei365.emojsdk.contoller;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ReplacementSpan;

import com.xinmei365.emojsdk.domain.EMCandiateEntity;
import com.xinmei365.emojsdk.domain.EMCandiateProperty;
import com.xinmei365.emojsdk.domain.EMCharacterEntity;
import com.xinmei365.emojsdk.domain.EMTranslatEntity;
import com.xinmei365.emojsdk.domain.EmojEntity;
import com.xinmei365.emojsdk.notify.NotifyEntity;
import com.xinmei365.emojsdk.notify.NotifyKeys;
import com.xinmei365.emojsdk.notify.NotifyManager;
import com.xinmei365.emojsdk.orm.EMDBMagager;
import com.xinmei365.emojsdk.utils.StringUtil;
import com.xinmei365.emojsdk.view.DefEmojSpan;
import com.xinmei365.emojsdk.view.EMImageSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by xinmei on 15/12/28.
 */
public class EMAssembleController {

    private static final char HOLDER_CHAR = 0xfffc;

    private static EMAssembleController mInstance = null;

    private EMAssembleController() {

    }

    public static EMAssembleController getInstance() {
        if (mInstance == null) {
            synchronized (EMAssembleController.class) {
                if (mInstance == null) {
                    mInstance = new EMAssembleController();
                }
            }
        }
        return mInstance;
    }


    public EMTranslatEntity assembleSpan(ArrayList<EMCharacterEntity> joinArr) throws JSONException {

        if (joinArr == null || joinArr.size() == 0) return null;

        SpannableStringBuilder spanSb = new SpannableStringBuilder();
        Vector<EMCandiateEntity> allStressEMKeys = new Vector<>();

        for (int i = 0; i < joinArr.size(); i++) {
            EMCharacterEntity entry = joinArr.get(i);
            switch (entry.mCharType) {
                case Normal:
                case Other:
                case Space:
                    entry.mWordStart = spanSb.length();
                    spanSb.append(entry.mWord);
                    break;
                case Emoj:
                    SpannableStringBuilder tempSpan = (SpannableStringBuilder) entry.mWord;
                    ReplacementSpan[] spans = tempSpan.getSpans(i, i + 1, ReplacementSpan.class);
                    if (spans.length > 0) {
                        ReplacementSpan emojSpan = null;
                        if (spans[0] instanceof EMImageSpan) {
                            emojSpan = (EMImageSpan) spans[0];
                        } else if (spans[0] instanceof DefEmojSpan) {
                            emojSpan = (DefEmojSpan) spans[0];
                        }
                        spanSb.append(HOLDER_CHAR);
                        spanSb.setSpan(emojSpan, spanSb.length() - 1, spanSb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case Transfer:
                    String emojProperty = EMDBMagager.getInstance().queryEmojByTag(entry.mWord.toString());
                    if (StringUtil.isNullOrEmpty(emojProperty)) {
                        spanSb.append(entry.mWord);
                    } else {
                        EMCandiateEntity candiateEmojEntity = new EMCandiateEntity(entry.mWord.toString(), entry.mWordStart);
                        candiateEmojEntity.parseRespJson(new JSONObject(emojProperty));
                        ArrayList<EMCandiateProperty> emojTagEntities = candiateEmojEntity.mEmojEntities;
                        if (emojTagEntities != null && emojTagEntities.size() == 1) {
                            String emojTagId = "#|" + candiateEmojEntity.mEMKey + "_" + emojTagEntities.get(0).mUniqueId + "|";
                            candiateEmojEntity.mEMStart = spanSb.length();
                            spanSb.append(HOLDER_CHAR);
                            DefEmojSpan emojResponseSpan = new DefEmojSpan(emojTagEntities.get(0));
                            emojResponseSpan.mOriginTxt = candiateEmojEntity.mEMKey;
                            emojResponseSpan.mTransferTxt = emojTagId;

                            spanSb.setSpan(emojResponseSpan, spanSb.toString().length() - 1, spanSb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            // handle here if cache key and emoji property
                            cacheCustomEmojToDB(emojTagId);
                        }else {
                            candiateEmojEntity.mEMStart = spanSb.length();
                            allStressEMKeys.add(candiateEmojEntity);
                            spanSb.append(entry.mWord);
                        }
                    }
                    break;
            }
        }

        EMTranslatEntity translatEntity = new EMTranslatEntity(spanSb, allStressEMKeys);
        return translatEntity;
    }

    protected void cacheCustomEmojToDB(String emojId) {
        EmojEntity emojEntity = new EmojEntity(emojId, 1);
        emojEntity.mRecentUseTimestamp = System.currentTimeMillis();
        NotifyEntity notifyEntity = new NotifyEntity(NotifyKeys.CACHE_EMOJ_TO_LOCAL_DB, emojEntity);
        NotifyManager.getInstance().sendNotifyCallback(NotifyKeys.CACHE_EMOJ_TO_LOCAL_DB, notifyEntity);
    }
}

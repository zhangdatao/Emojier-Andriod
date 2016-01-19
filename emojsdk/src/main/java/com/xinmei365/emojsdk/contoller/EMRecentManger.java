package com.xinmei365.emojsdk.contoller;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.xinmei365.emojsdk.domain.EMCandiateProperty;
import com.xinmei365.emojsdk.domain.EMHolderEntity;
import com.xinmei365.emojsdk.domain.EmojEntity;
import com.xinmei365.emojsdk.notify.INotifyCallback;
import com.xinmei365.emojsdk.notify.NotifyEntity;
import com.xinmei365.emojsdk.notify.NotifyKeys;
import com.xinmei365.emojsdk.notify.NotifyManager;
import com.xinmei365.emojsdk.orm.EMDBMagager;
import com.xinmei365.emojsdk.utils.CommUtil;
import com.xinmei365.emojsdk.utils.Logger;
import com.xinmei365.emojsdk.utils.SpanableUtil;
import com.xinmei365.emojsdk.view.DefEmojSpan;
import com.xinmei365.emojsdk.view.EMImageSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by xinmei on 15/11/19.
 */
public class EMRecentManger implements INotifyCallback {

    private static final String TAG = EMRecentManger.class.getSimpleName();
    private static EMRecentManger mInstance = null;

    private EMRecentManger() {
        init();
    }

    public static EMRecentManger getInstance() {
        if (mInstance == null) {
            synchronized (EMRecentManger.class) {
                if (mInstance == null) {
                    mInstance = new EMRecentManger();
                }
            }
        }
        return mInstance;
    }

    public void init() {
        NotifyManager.getInstance().registerNotifyCallback(NotifyKeys.CACHE_EMOJ_TO_LOCAL_DB, this);
    }

    public void destory() {
        NotifyManager.getInstance().removeNotifyCallback(NotifyKeys.CACHE_EMOJ_TO_LOCAL_DB, this);
    }


    public Vector<EmojEntity> getRecentEmojs() {
        return EMDBMagager.getInstance().queryRecentEomjs();
    }

    @Override
    public void notifyCallback(NotifyEntity entity) {
        if (entity.getKey().equals(NotifyKeys.CACHE_EMOJ_TO_LOCAL_DB)) {
            //cache emoj to local db
            EMDBMagager.getInstance().cacheEmojToLocalDB((EmojEntity) entity.getObject());
        }
    }

    public Vector<Spannable> getEMRecents() {
        Vector<Spannable> recentEmChars = new Vector<>();
        try {
            Vector<EmojEntity> recentEmojs = getRecentEmojs();
            if (recentEmojs != null && recentEmojs.size() > 0) {
                for (EmojEntity emEntity : recentEmojs) {
                    SpannableStringBuilder spanSb = new SpannableStringBuilder(EMHolderEntity.FINAL_HOLDER);
                    if (emEntity.mEmojType == 1) {
                        String keyId = emEntity.mEmojUnicode.substring(2, emEntity.mEmojUnicode.length() - 1);
                        String[] emojKeyAndID = keyId.split("_");
                        String emojKey = emojKeyAndID[0];
                        String emojId = emojKeyAndID[1];
                        String emojProperty = EMDBMagager.getInstance().getEmojPropertyById(emojId);
                        EMCandiateProperty emEntry = new EMCandiateProperty(emojId);
                        emEntry.parseReqJson(new JSONObject(emojProperty));

                        DefEmojSpan emojSpan = new DefEmojSpan(emEntry);
                        emojSpan.mOriginTxt = emojKey;
                        emojSpan.mTransferTxt = emEntity.mEmojUnicode;
                        spanSb.setSpan(emojSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (emEntity.mEmojType == 0) {
                        String emojUnicode = emEntity.mEmojUnicode.substring(2, emEntity.mEmojUnicode.length() - 1);
                        EMImageSpan emojSpan = SpanableUtil.getEmojSpan(CommUtil.getContext(), emojUnicode);
                        spanSb.setSpan(emojSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    recentEmChars.add(spanSb);
                }
            }
        }catch (JSONException exp) {
            Logger.d(TAG, exp.getMessage());
        }

        return recentEmChars;
    }


}

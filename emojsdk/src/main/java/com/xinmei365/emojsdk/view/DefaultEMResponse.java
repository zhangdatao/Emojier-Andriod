package com.xinmei365.emojsdk.view;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

import com.xinmei365.emojsdk.domain.EMCandiateEntity;
import com.xinmei365.emojsdk.domain.EMCandiateProperty;
import com.xinmei365.emojsdk.domain.EMHolderEntity;
import com.xinmei365.emojsdk.domain.EMReceiveTxtEntity;

import java.util.Vector;

/**
 * Created by xinmei on 15/12/2.
 */
public class DefaultEMResponse implements OnEMResponseListener {


    @Override
    public void onEMRespnSpanError(Exception exp) {

    }

    @Override
    public void onEMRespProperty(EMReceiveTxtEntity receTxtEnty) {

    }

    @Override
    public void onEMRespSpanSb(EMCandiateEntity emCandiateEntity) {
        Vector<SpannableStringBuilder> tempSpans = new Vector<>();
        for (final EMCandiateProperty emCandiateProperty : emCandiateEntity.mEmojEntities) {
            String transferTxt = "#|" + emCandiateEntity.mEMKey + " _" + emCandiateProperty.mUniqueId + "|";

            SpannableStringBuilder spanSb = new SpannableStringBuilder(EMHolderEntity.FINAL_HOLDER);
            DefEmojSpan emojResponseSpan = new DefEmojSpan(emCandiateProperty,emCandiateEntity.mEMKey,transferTxt);
            if (emojResponseSpan.haveEmptyBitmap()) {
                continue;
            }
            spanSb.setSpan(emojResponseSpan, 0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tempSpans.add(spanSb);
        }
        emCandiateEntity.mEMSpans = tempSpans;
    }

}

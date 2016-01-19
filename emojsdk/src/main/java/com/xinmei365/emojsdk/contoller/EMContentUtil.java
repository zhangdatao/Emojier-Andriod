package com.xinmei365.emojsdk.contoller;

import android.text.SpannableStringBuilder;
import android.text.style.ReplacementSpan;

import com.xinmei365.emojsdk.domain.EMHolderEntity;
import com.xinmei365.emojsdk.view.DefEmojSpan;
import com.xinmei365.emojsdk.view.EMImageSpan;

/**
 * Created by xinmei on 15/12/30.
 */
public class EMContentUtil {



    public static CharSequence getTranslateTxt(CharSequence content) {
        StringBuilder sBuilder = new StringBuilder();
        if (content instanceof SpannableStringBuilder) {
            SpannableStringBuilder spanSb = (SpannableStringBuilder) content;
            if (spanSb.toString().contains(EMHolderEntity.FINAL_HOLDER)) {
                for (int i = 0; i < spanSb.length(); i++) {
                    ReplacementSpan[] spans = spanSb.getSpans(i, i + 1, ReplacementSpan.class);
                    if (spans.length > 0) {
                        if (spans[0] instanceof EMImageSpan) {
                            EMImageSpan imgSpan = (EMImageSpan) spans[0];
                            sBuilder.append(imgSpan.mTransferTxt);
                        } else if (spans[0] instanceof DefEmojSpan) {
                            DefEmojSpan defSpan = (DefEmojSpan) spans[0];
                            sBuilder.append(defSpan.mTransferTxt);
                        }
                    } else {
                        sBuilder.append(spanSb.subSequence(i, i + 1));
                    }
                }
            } else {
                sBuilder.append(content);
            }
        }
        return sBuilder;
    }
}

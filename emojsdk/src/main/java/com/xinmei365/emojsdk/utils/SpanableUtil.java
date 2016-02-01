package com.xinmei365.emojsdk.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ReplacementSpan;

import com.xinmei365.emojsdk.domain.EMHolderEntity;
import com.xinmei365.emojsdk.view.DefEmojSpan;
import com.xinmei365.emojsdk.view.EMImageSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xinmei on 15/11/19.
 */
public class SpanableUtil {



    public static EMImageSpan getEmojSpan(Context context,String emojKey){
        int resId = context.getResources().getIdentifier(emojKey, "drawable", context.getPackageName());
        Drawable emojDrwable = context.getResources().getDrawable(resId);
        emojDrwable.setBounds(0, 0, DeviceUtil.getSpanEmojSize(), DeviceUtil.getSpanEmojSize());
        EMImageSpan imgSpan = new EMImageSpan(emojDrwable);
        imgSpan.mTransferTxt = "#[" + emojKey + "]";
        return imgSpan;
    }

    /**
     * get real translate charsequence
     * @param content
     * @return
     */
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
    public static String recoverTranslatedTxt(String content) {
        StringBuffer sb = new StringBuffer(content);
        String regex =  "(#\\|)[\\w:_ ]{1,}\\|";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        int offset = 0;
        while (matcher.find()) {
            String emojTag = matcher.group();
            String tmpStrAr[] = emojTag.substring(2, emojTag.length() - 1).split("_");
            sb = sb.replace(matcher.start()-offset,matcher.end()-offset,tmpStrAr[0]);
            offset+=emojTag.length()-tmpStrAr[0].length();
        }
        return sb.toString();
    }
}

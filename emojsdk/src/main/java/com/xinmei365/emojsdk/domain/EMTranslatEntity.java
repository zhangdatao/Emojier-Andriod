package com.xinmei365.emojsdk.domain;

import android.text.SpannableStringBuilder;

import java.util.Vector;

/**
 * Created by xinmei on 15/12/30.
 */
public class EMTranslatEntity {

    public SpannableStringBuilder mSpanSb;
    public Vector<EMCandiateEntity> mAllStressEMKeys;

    public EMTranslatEntity(SpannableStringBuilder mSpanSb, Vector<EMCandiateEntity> mAllStressEMKeys) {
        this.mSpanSb = mSpanSb;
        this.mAllStressEMKeys = mAllStressEMKeys;
    }
}

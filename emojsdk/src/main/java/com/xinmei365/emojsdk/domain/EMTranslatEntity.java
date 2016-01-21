package com.xinmei365.emojsdk.domain;

import android.text.SpannableStringBuilder;

import java.util.Vector;

/**
 * Created by xinmei on 15/12/30.
 */
public class EMTranslatEntity {

    public SpannableStringBuilder mSpanSb;
    public Vector<EMCandiateEntity> mCands;

    public EMTranslatEntity(SpannableStringBuilder spanSb, Vector<EMCandiateEntity> cands) {
        this.mSpanSb = spanSb;
        this.mCands = cands;
    }
}

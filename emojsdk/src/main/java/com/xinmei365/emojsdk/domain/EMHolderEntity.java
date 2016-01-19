package com.xinmei365.emojsdk.domain;

/**
 * Created by xinmei on 15/12/8.
 */
public class EMHolderEntity {

    public static final String FINAL_HOLDER = "☃";

    public int mEmojStart;

    public int mEmojEnd;

    public EMHolderEntity(int emojStart) {
        this.mEmojStart = emojStart;
        mEmojEnd = this.mEmojStart + FINAL_HOLDER.length();
    }


    public void decreaseEmojStart() {
        mEmojStart = mEmojStart - 1;
        mEmojEnd = mEmojEnd -1;
    }

    public void increaEmojStart() {
        mEmojStart = mEmojStart + 1;
        mEmojEnd = mEmojEnd + 1;
    }

    public void decreaseEmojStart(String emojKey) {
        mEmojStart = (mEmojStart - emojKey.length()) + 1;
        mEmojEnd = (mEmojEnd - emojKey.length()) + 1;
    }
}

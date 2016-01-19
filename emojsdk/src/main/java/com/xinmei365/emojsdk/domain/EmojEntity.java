package com.xinmei365.emojsdk.domain;

/**
 * Created by xinmei on 15/11/18.
 */
public class EmojEntity {

    public long mRecentUseTimestamp;
    public String mEmojCategoryName;
    public String mEmojCategoryId;

    public String mEmojUnicode;
    public String mEmojId;
    public String mEmojName;
    public int mEmojType;  //0 means local emoji, 1 means other emoji


    public EmojEntity(String mEmojUnicode,int emojType) {
        this.mEmojUnicode = mEmojUnicode;
        this.mEmojType = emojType;
    }


}


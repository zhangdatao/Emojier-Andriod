package com.xinmei365.emojsdk.domain;

/**
 * Created by xinmei on 15/11/20.
 */
public class UserInputEntity extends BaseEntity{


    public  String mEmojTag;
    public   int mEmojStartIndex;

    public UserInputEntity(String emojTag, int emojStartIndex) {
        this.mEmojTag = emojTag;
        this.mEmojStartIndex = emojStartIndex;
    }

}

package com.xinmei365.emojsdk.domain;

/**
 * Created by xinmei on 15/12/4.
 */
public class CharEntity {

    public int start; //char start position
    public int end;  //char end position, contain whitespace

    public CharType mCharType; //char type, normal, local emoji, online emoji
    public String mOriginalStr; //original string #|\smile_1:candf1newcar064001|

    private String mEmojKeyID;  //online emoji's key combine with id James_1:candf1newcar064001:
    public String[] mEmojKeyAndID;  //store online emoji's key and id
    public String mEmojKey; //online emoji's key
    public String mEmojID; //online emoji's id

    public String mEmojUnicode; //local emoji's unicode value


    public enum CharType{
        Normal,LocalEMOJ,OnlineEmoj
    }

    public CharEntity(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public CharEntity(String originStr,int start,CharType type) {
        this.mOriginalStr = originStr;
        this.start = start;
        this.mCharType = type;
        this.end = start + originStr.length();
    }

    public String getEmojKeyID() {
        return mEmojKeyID;
    }

    public void setEmojKeyID(String emojKeyID) {
        this.mEmojKeyID = emojKeyID;
        mEmojKeyAndID = emojKeyID.split("_");
        mEmojKey = mEmojKeyAndID[0];
        mEmojID = mEmojKeyAndID[1];
    }
}

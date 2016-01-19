package com.xinmei365.emojsdk.domain;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by xinmei on 15/12/4.
 */
public class EMReceiveTxtEntity {

    public String originReceiveTxt;

    public SpannableStringBuilder mSpanStrBuild;

    public SpannableStringBuilder mFinalSpanSB;

    public String[] mSplitTxts;

    public Map<Integer,CharEntity> allContent;
    public ArrayList<EMCandiateProperty> mNoBitMapEmojTagEntities;//The EmojiTagEntity of set is online, emoji in local buffer with property's json content, no image
    public ArrayList<CharEntity> mEmojIds; //The CharEntity of set is online emoji's content, only id, no property and local image
    public ArrayList<String> mHasDownBitMap;

    public EMReceiveTxtEntity(String originReceiveTxt) {
        this.originReceiveTxt = originReceiveTxt;
        mSpanStrBuild = new SpannableStringBuilder(originReceiveTxt);
        mFinalSpanSB = new SpannableStringBuilder();
        mNoBitMapEmojTagEntities = new ArrayList<EMCandiateProperty>();
        mEmojIds = new ArrayList<CharEntity>();
        mHasDownBitMap = new ArrayList<String>();
    }
}

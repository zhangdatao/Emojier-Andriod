package com.xinmei365.emojsdk.view;

import android.graphics.BitmapFactory;

import com.xinmei365.emojsdk.domain.EMCandiateProperty;
import com.xinmei365.emojsdk.utils.StringUtil;

import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by xinmei on 15/11/20.
 */
public class DefEmojSpan extends AbsEmojSpan {


    public String mOriginTxt;


    public String mTransferTxt;


    private ArrayList<EMCandiateProperty.EMImgProperty> mNoBitmapEmojs = new ArrayList<>();

    public DefEmojSpan(EMCandiateProperty mEmojEntity,String originTxt,String transferTxt) {
        super(mEmojEntity);
        this.mOriginTxt = originTxt;
        this.mTransferTxt = transferTxt;
    }

    public DefEmojSpan(EMCandiateProperty mEmojEntity,String originTxt,String transferTxt,int emojSize) {
        super(mEmojEntity,emojSize);
        this.mOriginTxt = originTxt;
        this.mTransferTxt = transferTxt;
    }



    public DefEmojSpan(EMCandiateProperty mEmojEntity) {
        super(mEmojEntity);
    }

    public DefEmojSpan(EMCandiateProperty emCandiateProperty, int emojSize) {
        super(emCandiateProperty,emojSize);
    }

    @Override
    public ArrayList<EMCandiateProperty.EMImgProperty> getEmojDescrips() {

        try {
            for (EMCandiateProperty.EMImgProperty emImgProperty : mEmojEntity.mEmImgProperties) {
                if (emImgProperty.mEmojBmap == null && !StringUtil.isNullOrEmpty(emImgProperty.mEmojPath)) {
                    emImgProperty.mEmojBmap = BitmapFactory.decodeStream(new FileInputStream(emImgProperty.mEmojPath));
                } else if (emImgProperty.mEmojBmap == null && StringUtil.isNullOrEmpty(emImgProperty.mEmojPath)){
                    mNoBitmapEmojs.add(emImgProperty);
                }
            }
        } catch (Exception e) {

        }
        return mEmojEntity.mEmImgProperties;
    }

    public boolean haveEmptyBitmap() {
        if (mNoBitmapEmojs.size() == 0) {
            return false;
        }
        return true;
    }

}

package com.xinmei365.emojsdk.domain;

import android.text.SpannableStringBuilder;

import com.xinmei365.emojsdk.orm.EMDBMagager;
import com.xinmei365.emojsdk.utils.StringUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by xinmei on 15/11/26.
 */
public class EMCandiateEntity {

    public String mEMKey;
    public  int mEMStart;


    public int mStatusCode;
    public int mCandCount;

    public ArrayList<EMCandiateProperty> mEmojEntities;

    public boolean isClickCan = false; //flag if toolbar had been clicked, select emoji candidate to replace highlight word
    public Vector<SpannableStringBuilder> mEMSpans;

    public EMCandiateEntity(String userinputEmojStr, int emojStartIndex) {
        mEMKey = userinputEmojStr;
        mEMStart = emojStartIndex;
        mEmojEntities = new ArrayList<EMCandiateProperty>();
    }

    public EMCandiateEntity parseRespJson(JSONObject jsonObj) {
        this.mCandCount = jsonObj.optInt("cand_count");
        this.mStatusCode = jsonObj.optInt("status_code");
        for (int i = 1;i <= mCandCount;i++) {

            JSONObject obj = jsonObj.optJSONObject(String.valueOf(i));
            EMCandiateProperty emojEntity = new EMCandiateProperty(obj.optString("id"));
            emojEntity.parseReqJson(obj);
            mEmojEntities.add(emojEntity);

            //update id and content of EmojIdProperty
            EMDBMagager.getInstance().cacheEmojIdProperty(emojEntity.mUniqueId,obj.toString());
        }
        return this;
    }


    public boolean haveCanEmojBitmap() {
        boolean haveEmoj = false;
        for (EMCandiateProperty tagEntity : mEmojEntities) {
            for (EMCandiateProperty.EMImgProperty descrip : tagEntity.mEmImgProperties) {
                if (!StringUtil.isNullOrEmpty(descrip.mEmojPath)){
                    haveEmoj = true;
                    return haveEmoj;
                }
            }
        }
        return haveEmoj;
    }
}

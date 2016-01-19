package com.xinmei365.emojsdk.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xinmei on 15/12/7.
 */
public class EMKeyEntity extends BaseEntity {

    public long mPubTimestamp;
    public long mPriTimestamp;

    public int mStatusCode;
    public int mDataCount;

    public ArrayList<String> mEmojTags;


    public EMKeyEntity() {
        mEmojTags = new ArrayList<String>();
    }

    public void parseJson(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            this.mStatusCode = jsonObj.optInt("status_code");
            this.mPubTimestamp = jsonObj.optLong("pubtimestamp");
            this.mPriTimestamp = jsonObj.optLong("pritimestamp");
            this.mDataCount = jsonObj.optInt("data_count");
            JSONArray array = jsonObj.optJSONArray("data");
            for (int i=0;i<array.length();i++) {
                mEmojTags.add(array.get(i).toString());
            }
        }catch (Exception e){

        }
    }
}

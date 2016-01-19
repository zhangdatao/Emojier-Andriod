
package com.xinmei365.emojsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;


public final class SharedPrenceUtil {


    private static final String sharedPreferencesInfo = "xinmeichat.shareInfo";

    public static final String LastPubEmojTagTimestamp = "LastPubEmojTagTimestamp";
    public static final String LastPriEmojTagTimestamp = "LastPriEmojTagTimestamp";



    private static Context myContext;

    private static SharedPreferences mPreferences;

    private static Editor mEditor;

    private static SharedPrenceUtil mSharedInstance = new SharedPrenceUtil();

    private SharedPrenceUtil() {

    }

    public static SharedPrenceUtil getInstance(Context context) {
        myContext = context;
        if (mPreferences == null && myContext != null) {
            mPreferences = myContext.getSharedPreferences(
                    sharedPreferencesInfo, Context.MODE_PRIVATE);
            mEditor = mPreferences.edit();
        }
        return mSharedInstance;
    }


    public boolean isContainKey(String key) {
        return mPreferences.contains(key);
    }

    public boolean clearItem(String key) {
        mEditor.remove(key);
        return mEditor.commit();
    }


    @SuppressWarnings("unchecked")
    public HashMap<String, ?> getAll() {
        if (mPreferences.getAll() instanceof HashMap) {
            return (HashMap<String, ?>) mPreferences.getAll();
        }
        return null;
    }


    public boolean setString(String key, String value) {
        if (mPreferences.contains(key)) {
            mEditor.remove(key);
        }
        mEditor.putString(key, value);
        return mEditor.commit();
    }


    public String getString(String key) {
        return mPreferences.getString(key, "");
    }

    /**
     *
     * @param key
     * @param defValue default value
     */
    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }


    public boolean setInt(String key, int value) {
        if (mPreferences.contains(key)) {
            mEditor.remove(key);
        }
        mEditor.putInt(key, value);
        return mEditor.commit();
    }


    public int getInt(String key) {
        return mPreferences.getInt(key, 0);
    }


    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public boolean setFloat(String key, float value) {
        if (mPreferences.contains(key)) {
            mEditor.remove(key);
        }
        mEditor.putFloat(key, value);
        return mEditor.commit();
    }

    public float getFloat(String key) {
        return mPreferences.getFloat(key, 0);
    }

    public float getFloat(String key, float defValue) {
        return mPreferences.getFloat(key, defValue);
    }

    public boolean setBoolean(String key, boolean value) {
        if (mPreferences.contains(key)) {
            mEditor.remove(key);
        }
        mEditor.putBoolean(key, value);
        return mEditor.commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public boolean setLong(String key, long value) {
        if (mPreferences.contains(key)) {
            mEditor.remove(key);
        }
        mEditor.putLong(key, value);
        return mEditor.commit();
    }

    public long getLong(String key) {
        return mPreferences.getLong(key, 0);
    }

    public long getLong(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }



}
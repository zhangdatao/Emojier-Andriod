package com.xinmei365.emojsdk.utils;

/**
 * Created by xinmei on 15/11/24.
 */
public class StringUtil {

    /**
     * check if string is null or ""
     *
     * @param str
     * @return boolean
     */
    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.length() == 0);
    }
}

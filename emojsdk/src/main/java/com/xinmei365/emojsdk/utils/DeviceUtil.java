package com.xinmei365.emojsdk.utils;

import android.content.Context;

/**
 * Created by xinmei on 15/11/19.
 */
public class DeviceUtil {



    public static float getDensity(Context ctx){
        return ctx.getResources().getDisplayMetrics().density;
    }

    public static int getSpanEmojSize() {

        return (int) getDensity(CommUtil.getContext()) * 20;
    }
}

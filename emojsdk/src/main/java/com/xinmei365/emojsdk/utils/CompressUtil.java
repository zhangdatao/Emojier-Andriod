package com.xinmei365.emojsdk.utils;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by xinmei on 15/11/24.
 */
public class CompressUtil {

    /**
     * Compress bitmap to specified path
     *
     * @param bitmap
     * @param file
     * @return
     */
    public static File compressAndSaveBitmap(Bitmap bitmap, File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG,90, bos);
            bos.flush();
            bos.close();
            //noinspection ConstantConditions
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}

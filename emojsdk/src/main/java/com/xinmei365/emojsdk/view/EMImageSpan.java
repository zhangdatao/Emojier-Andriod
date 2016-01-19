package com.xinmei365.emojsdk.view;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by xinmei on 15/12/29.
 */
public class EMImageSpan extends ImageSpan{


    public String mTransferTxt;

    public EMImageSpan(Bitmap b) {
        super(b);
    }

    public EMImageSpan(Drawable emojDrwable, int alignBaseline) {
        super(emojDrwable, alignBaseline);
    }

    public EMImageSpan(Drawable emojDrwable) {
        super(emojDrwable);
    }
}

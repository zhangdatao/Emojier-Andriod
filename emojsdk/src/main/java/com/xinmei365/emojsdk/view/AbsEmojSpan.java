package com.xinmei365.emojsdk.view;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.view.View;

import com.xinmei365.emojsdk.domain.Constant;
import com.xinmei365.emojsdk.domain.EMCandiateProperty;
import com.xinmei365.emojsdk.utils.CommUtil;
import com.xinmei365.emojsdk.utils.DeviceUtil;
import com.xinmei365.emojsdk.utils.SharedPrenceUtil;
import com.xinmei365.emojsdk.utils.StringUtil;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by xinmei on 15/11/20.
 */
public abstract class AbsEmojSpan extends ReplacementSpan {

    private static final String TAG = "DynamicDrawableSpan";

    protected EMCandiateProperty mEmojEntity;

    private WeakReference<ArrayList<EMCandiateProperty.EMImgProperty>> mEmojDescriopRef;

    protected  int mEmojSize = -1;

    protected  int mSpanEmojSize;

    public AbsEmojSpan(EMCandiateProperty mEmojEntity,int emojSize) {
        this.mEmojEntity = mEmojEntity;
        this.mEmojSize = SharedPrenceUtil.getInstance(CommUtil.getContext()).getInt(Constant.KEY_CUSTOM_EMOJI_SIZE);
        mSpanEmojSize = mEmojSize <= 0 ? DeviceUtil.getSpanEmojSize() : mEmojSize;
    }

    public AbsEmojSpan(EMCandiateProperty mEmojEntity) {
        this(mEmojEntity,-1);
    }

    public AbsEmojSpan() {
    }


    @Override
    public int getSize(Paint paint, CharSequence text,
                       int start, int end,
                       Paint.FontMetricsInt fm) {


        ArrayList<EMCandiateProperty.EMImgProperty> emojDescips = getCachedDrawable();

        Drawable drawable;
        Rect rect = new Rect();
        for (EMCandiateProperty.EMImgProperty emImgProperty : emojDescips) {
            if(StringUtil.isNullOrEmpty(emImgProperty.mEmojPath)){
                continue;
            }

            drawable = Drawable.createFromPath(emImgProperty.mEmojPath);
            drawable.setBounds(0, 0, mSpanEmojSize,
                    mSpanEmojSize);
            rect.left += drawable.getBounds().left;
            rect.top += drawable.getBounds().top;
            rect.right += drawable.getBounds().right;
            rect.bottom += drawable.getBounds().bottom;
        }
        if (fm != null) {
            fm.ascent = -rect.bottom;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }

        return rect.right;
    }


    @Override
    public void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, Paint paint) {

        ArrayList<EMCandiateProperty.EMImgProperty> emojDescips = getCachedDrawable();

        float emojLeft;
        BitmapDrawable drawable;

        for (int i=0;i<emojDescips.size();i++){
            canvas.save();

            if (emojDescips.get(i).mEmojBmap == null){
                canvas.drawText(text.subSequence(start, end), start, end, x, y, paint);
                break;
            }

            drawable = new BitmapDrawable(emojDescips.get(i).mEmojBmap);

            drawable.setBounds(0, 0, mSpanEmojSize, mSpanEmojSize);

            int transY = bottom - drawable.getBounds().bottom;

            int transX = (int)x;
            if (i>0){
                Drawable lastDrawable =  new BitmapDrawable(emojDescips.get(i-1).mEmojBmap);
                lastDrawable.setBounds(0, 0, mSpanEmojSize,
                        mSpanEmojSize);
                transX += i * lastDrawable.getBounds().right;
            }
            canvas.translate(transX, transY);


            drawable.draw(canvas);

            if (!StringUtil.isNullOrEmpty(mEmojEntity.mLinkUrl)){
                paint.setColor(Color.RED);
                canvas.drawCircle(drawable.getBounds().right-12,drawable.getBounds().top+8,8,paint);
            }

            canvas.restore();
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
    }

    private ArrayList<EMCandiateProperty.EMImgProperty> getCachedDrawable() {
        WeakReference<ArrayList<EMCandiateProperty.EMImgProperty>> wr = mEmojDescriopRef;
        ArrayList<EMCandiateProperty.EMImgProperty> drawables = null;

        if (wr != null)
            drawables = wr.get();

        if (drawables == null) {
            drawables = getEmojDescrips();
            mEmojDescriopRef = new WeakReference<ArrayList<EMCandiateProperty.EMImgProperty>>(drawables);
        }

        return drawables;
    }





    public abstract ArrayList<EMCandiateProperty.EMImgProperty> getEmojDescrips();

    public void onClick(View widget) {
        if (!StringUtil.isNullOrEmpty(mEmojEntity.mLinkUrl )){
            enterWebView(mEmojEntity.mLinkUrl);
        }
    }

    private void enterWebView(String linkUrl) {
        Intent intent;
        try {
            intent = Intent.parseUri(linkUrl, 0);
            intent.setClass(CommUtil.getContext(), WebViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            CommUtil.getContext().startActivity(intent);
        }catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setEmojSize(int emojSzie) {
        this.mEmojSize = emojSzie;
    }
    public void setSpanEmojSize(int emojSpanSzie){
        this.mSpanEmojSize = emojSpanSzie;
    }
}

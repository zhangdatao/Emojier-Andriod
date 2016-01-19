package com.xinmei365.emojsdk.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class ForegroundClickSpan extends ClickableSpan {


        private SpanClickListener mSpanClickListener;

        @Override
        public void updateDrawState(TextPaint ds) {
            if (mSpanClickListener != null) {
                mSpanClickListener.updateDrawState(ds);
            }
        }

        public void onClick(View widget) {
            if (mSpanClickListener != null) {
                mSpanClickListener.onSpanClick(widget);
            }
        }

        public void setSpanClickListener(SpanClickListener spanClickListener) {
            this.mSpanClickListener = spanClickListener;
        }



     public interface SpanClickListener {
        void onSpanClick(View widget);

        void updateDrawState(TextPaint ds);
    }
}
package com.xinmei.app.emojidemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinmei365.emojsdk.view.EMLinkMovementMethod;

import java.util.ArrayList;

/**
 * Created by xinmei on 16/1/5.
 */
public class ReceiveMsagAdapter extends CommBaseAdapter<CharSequence> {


    public ReceiveMsagAdapter(Context mCtx, ArrayList<CharSequence> dataArray) {
        super(mCtx, dataArray);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.mItemTV = (TextView) convertView.findViewById(R.id.mItemTV);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CharSequence str = mDataArray.get(position);
        holder.mItemTV.setText(str, TextView.BufferType.SPANNABLE);

        holder.mItemTV.setMovementMethod(EMLinkMovementMethod.getInstance());
        return convertView;
    }

    private static class ViewHolder{
        TextView mItemTV;
    }
}


package com.xinmei.app.emojidemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;


public abstract class CommBaseAdapter<T> extends BaseAdapter {


    protected Context mContext;

    protected ArrayList<T> mDataArray = new ArrayList<T>();


    protected LayoutInflater mLayInflater;


    public CommBaseAdapter(Context mCtx,ArrayList<T> dataArray) {
        mContext = mCtx;
        mDataArray = dataArray;
        mLayInflater = LayoutInflater.from(mContext);
    }

    /**
     * @return
     */
    @Override
    public int getCount() {
        return mDataArray.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return mDataArray.get(position);
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * data update
     *
     * @param isClearOrigin  if clear all original data
     */
    public void updateData(ArrayList<T> dataArr, boolean isClearOrigin) {
        if (isClearOrigin) {
            mDataArray.clear();
        }
        mDataArray.addAll(dataArr);
        this.notifyDataSetChanged();
    }

    public void addData(T str) {
        mDataArray.add(str);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItemView(position, convertView, parent);
    }

    public abstract View getItemView(int position, View convertView, ViewGroup parent);



    public ArrayList<T> getDataArray() {
        return mDataArray;
    }

    public void setDataArray(ArrayList<T> mDataArray) {
        this.mDataArray.addAll(mDataArray);
    }
}

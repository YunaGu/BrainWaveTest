package com.geekb.brainwavetest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WaveAdapter extends BaseAdapter {
    private Context mContext;
    private List<Map<String, Object>> mData;
    private LayoutInflater mInflater;
    private boolean[] showW = {true, true, true, true, true, true, true, true, true};

    public WaveAdapter(Context context, List<Map<String, Object>> data) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        if (data != null) {
            mData = data;
        } else {
            mData = new ArrayList<>();
        }

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void changeSelected(int position){
        showW[position] = !showW[position];
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.test_item,null);
            holder.tvType = convertView.findViewById(R.id.tv_type);
            holder.tvShown = convertView.findViewById(R.id.tv_shown);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvType.setText((String) mData.get(position).get("textView"));
        for (int i = 0; i < 9; i++) {
            if (showW[position]){
                holder.tvShown.setText("显示");
                holder.tvShown.setTextColor(Color.parseColor("#ed7c4f"));
            }else {
                holder.tvShown.setText("隐藏");
                holder.tvShown.setTextColor(Color.parseColor("#b3b3b3"));
            }
        }
        return convertView;
    }
    class ViewHolder{
        public TextView tvShown;
        public TextView tvType;
    }
}

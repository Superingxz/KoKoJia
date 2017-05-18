package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.LocalVideo;

import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
public class LocalPlayAdapter extends BaseAdapter {

    private List<LocalVideo> videoList;
    private Context context;

    public LocalPlayAdapter(List<LocalVideo> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.local_play_item,parent,false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tv_local_play_item);
        tv.setText(videoList.get(position).getName());
        if(videoList.get(position).isCheck()){
            tv.setTextColor(0xff00aeef);
        }else{
            tv.setTextColor(0xff494949);
        }
        return convertView;
    }
}

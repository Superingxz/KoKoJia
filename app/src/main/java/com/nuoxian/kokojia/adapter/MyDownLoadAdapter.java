package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.MyDownLoadCourse;
import com.nuoxian.kokojia.utils.CommonMethod;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/20.
 */
public class MyDownLoadAdapter extends BaseAdapter {

    private List<MyDownLoadCourse> videoList;
    private Context context;

    public MyDownLoadAdapter(List<MyDownLoadCourse> videoList, Context context) {
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
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.my_download_item,parent,false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.tv_my_download_lesson);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.cb_my_download_delete);
        ImageView iv = (ImageView) convertView.findViewById(R.id.iv_my_download_lesson);
        name.setText(videoList.get(position).getName());
        Bitmap bitmap = CommonMethod.getLoacalBitmap(videoList.get(position).getImage());
        if(bitmap!=null){
            iv.setImageBitmap(bitmap);
        }
        if(videoList.get(position).isCheck()){
            cb.setChecked(true);
        }else{
            cb.setChecked(false);
        }
        if(videoList.get(position).isVisible()){
            cb.setVisibility(View.VISIBLE);
        }else{
            cb.setVisibility(View.GONE);
        }

        return convertView;
    }
}

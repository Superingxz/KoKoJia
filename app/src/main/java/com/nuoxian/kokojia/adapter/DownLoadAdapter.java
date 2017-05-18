package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.CourseLessonDownload;

import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 */
public class DownLoadAdapter extends BaseAdapter {
    private Context context;
    private List<CourseLessonDownload> list;

    public void setmProgressListener(ProgressListener mProgressListener) {
        this.mProgressListener = mProgressListener;
    }

    private ProgressListener mProgressListener;

    public DownLoadAdapter(Context context, List<CourseLessonDownload> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.download_item,parent,false);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_download_time);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.cb_download);
        TextView tvProgress = (TextView) convertView.findViewById(R.id.tv_download_progress);
        final Button pause = (Button) convertView.findViewById(R.id.pause);
        final Button restart = (Button) convertView.findViewById(R.id.restart);
        tvTime.setText(list.get(position).getVideo_time());
        tvProgress.setTag(position);
        checkBox.setText(list.get(position).getTitle());
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgressListener != null) {
                    mProgressListener.VideoDownLoadProgress(position,true);
                }
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgressListener != null) {
                    mProgressListener.VideoDownLoadProgress(position,false);
                }
            }
        });
        if(list.get(position).getPlayer_status()==1){
            //可以下载，设置checkBox可选择
            convertView.setClickable(false);
            checkBox.setEnabled(true);
        }else{
            //不可以下载，设置checkBox不可选择
            convertView.setClickable(true);
            checkBox.setEnabled(false);
        }
        if(!list.isEmpty()){
            //设置checkBox的选中状态
            if(CourseLessonDownload.CHECKED==list.get(position).getCheckedStatus()){
                //如果被选中了
                checkBox.setChecked(true);
                convertView.setBackgroundColor(Color.WHITE);
            }else if(CourseLessonDownload.UNCHECKED==list.get(position).getCheckedStatus()){
                //如果没被选中
                checkBox.setChecked(false);
                convertView.setBackgroundColor(Color.WHITE);
            }else if(CourseLessonDownload.CAN_NOT_CHECKED==list.get(position).getCheckedStatus()){
                //不能被选中
                checkBox.setChecked(true);
                checkBox.setClickable(false);
                convertView.setBackgroundColor(0xffeaeaea);
                convertView.setClickable(false);
            }
            if(CourseLessonDownload.DOWNLOADDED==list.get(position).getDownLoadStatus()){
                pause.setVisibility(View.GONE);
                restart.setVisibility(View.GONE);
                tvProgress.setText("已下载");
                tvProgress.setVisibility(View.VISIBLE);
            }else if(CourseLessonDownload.PRAPER_DOWNLOAD==list.get(position).getDownLoadStatus()){
                pause.setVisibility(View.GONE);
                restart.setVisibility(View.GONE);
                checkBox.setChecked(true);
                checkBox.setClickable(false);
                tvProgress.setText("等待下载");
                tvProgress.setVisibility(View.VISIBLE);
            }else if(CourseLessonDownload.DOWNLOADING==list.get(position).getDownLoadStatus()){
                //正在下载
                restart.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
                checkBox.setClickable(false);
                convertView.setBackgroundColor(0xffeaeaea);
                convertView.setClickable(true);
            }else if(CourseLessonDownload.UNFINISH==list.get(position).getDownLoadStatus()){
                //未完成
                restart.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
                checkBox.setClickable(false);
                convertView.setBackgroundColor(0xffeaeaea);
                convertView.setClickable(true);
            }else if(CourseLessonDownload.PAUSE==list.get(position).getDownLoadStatus()){
                //暂停
                restart.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
                checkBox.setChecked(true);
                checkBox.setClickable(false);
                convertView.setBackgroundColor(0xffeaeaea);
                convertView.setClickable(true);
            }else{
                tvProgress.setVisibility(View.GONE);
            }
        }

        return convertView;
    }


    public interface ProgressListener{
        void VideoDownLoadProgress(int position, boolean isPause);
    }
}

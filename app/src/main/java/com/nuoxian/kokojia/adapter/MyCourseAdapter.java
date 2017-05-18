package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.MyCourse;

import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
public class MyCourseAdapter extends BaseAdapter {

    private List<MyCourse> list;
    private Context context;

    public MyCourseAdapter(List<MyCourse> list, Context context) {
        this.list = list;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.my_course_item,parent,false);
            ViewHoder vh = new ViewHoder();
            vh.iv = (ImageView) convertView.findViewById(R.id.iv_my_course);
            vh.title = (TextView) convertView.findViewById(R.id.tv_my_course_title);
            vh.price = (TextView) convertView.findViewById(R.id.tv_my_course_price);
            vh.learn = (TextView) convertView.findViewById(R.id.tv_my_course_learn);
            convertView.setTag(vh);
        }
        ViewHoder vh = (ViewHoder) convertView.getTag();
        vh.title.setText(list.get(position).getTitle());
        vh.price.setText(list.get(position).getPrice());
        vh.learn.setText("学习进度:已学习"+list.get(position).getLearn()+"课时");
        MyApplication.imageLoader.displayImage(list.get(position).getImage_url(),vh.iv,MyApplication.options);

        return convertView;
    }

    class ViewHoder{
        private ImageView iv;
        private TextView title,price,learn;
    }
}

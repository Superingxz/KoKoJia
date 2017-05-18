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
import com.nuoxian.kokojia.enterty.FragmentMyCourse;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
public class MyCourseFragmentAdapter extends BaseAdapter{

    private List<FragmentMyCourse.DataBean> list;
    private Context context;

    public MyCourseFragmentAdapter(List<FragmentMyCourse.DataBean> list, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_course_fragment,parent,false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_my_course_fragment);
        TextView title = (TextView) convertView.findViewById(R.id.tv_my_course_fragment_title);
        title.setText(list.get(position).getTitle());
        TextView classNum = (TextView) convertView.findViewById(R.id.tv_my_course_fragment_class_num);
        classNum.setText("共" + list.get(position).getClass_num() + "课时");
        TextView price = (TextView) convertView.findViewById(R.id.tv_my_course_fragment_price);
        price.setText(list.get(position).getPrice());
        TextView statusName = (TextView) convertView.findViewById(R.id.tv_my_course_fragment_status_name);
        statusName.setText(list.get(position).getStatus_name());
        TextView time  = (TextView) convertView.findViewById(R.id.tv_my_course_fragment_time);
        time.setText("结束时间:"+list.get(position).getEndtime());
        MyApplication.imageLoader.displayImage(list.get(position).getImage_url(), imageView, MyApplication.options);
        return convertView;
    }
}

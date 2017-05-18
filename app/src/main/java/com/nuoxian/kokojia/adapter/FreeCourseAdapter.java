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
import com.nuoxian.kokojia.enterty.FreeCourse;
import com.nuoxian.kokojia.utils.CommonMethod;
import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */
public class FreeCourseAdapter extends BaseAdapter {

    private List<FreeCourse.CourseListBean> mList;
    private Context context;

    public FreeCourseAdapter(List<FreeCourse.CourseListBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_free_course,parent,false);
        }
        CommonMethod.setFontAwesome(convertView.findViewById(R.id.layout_item_free_course), context);

        TextView title = (TextView) convertView.findViewById(R.id.tv_free_course_title);//课程名
        title.setText(mList.get(position).getTitle());
        TextView price = (TextView) convertView.findViewById(R.id.tv_free_course_price);//价格
        price.setText(mList.get(position).getPrice());
        TextView courseNum = (TextView) convertView.findViewById(R.id.tv_free_course_courseNum);//课时
        courseNum.setText(mList.get(position).getClass_num()+"个课时");
        TextView learn = (TextView) convertView.findViewById(R.id.tv_free_course_learn);
        learn.setText(mList.get(position).getTrial_num()+"人在学");

        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_free_course);
        MyApplication.imageLoader.displayImage(mList.get(position).getImage_url(),imageView,MyApplication.options);

        return convertView;
    }
}

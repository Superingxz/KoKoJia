package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CoursePackage;

import java.util.List;

/**
 * Created by Administrator on 2016/10/17.
 */
public class CourseDetailsPackageAdapter extends BaseAdapter {

    private List<CoursePackage.PackageListBean> mList;
    private Context context;

    public CourseDetailsPackageAdapter(List<CoursePackage.PackageListBean> mList, Context context) {
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
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_course_details_package,parent,false);
        }
        TextView course = (TextView) convertView.findViewById(R.id.tv_course_package_title);//课程名
        course.setText(mList.get(position).getTitle());
        TextView originalPrice = (TextView) convertView.findViewById(R.id.tv_course_package_original_price);//原价
        originalPrice.setText("原价:"+mList.get(position).getOriginal_price());
        originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        TextView packagePrice = (TextView) convertView.findViewById(R.id.tv_course_package_package_price);//套餐价
        packagePrice.setText("套餐价:"+mList.get(position).getPrice());
        TextView courseNum = (TextView) convertView.findViewById(R.id.tv_course_package_courseNum);
        courseNum.setText("共"+mList.get(position).getCourse_count()+"套课程");
        TextView classNum = (TextView) convertView.findViewById(R.id.tv_course_package_classNum);
        classNum.setText(mList.get(position).getClass_num()+"节课时");
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_course_package);
        MyApplication.imageLoader.displayImage(mList.get(position).getImage_url(),imageView,MyApplication.options);
        return convertView;
    }
}

package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Index;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.view.SodukuGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class IndexFragmentAdapter extends BaseAdapter {

    private List<Index.CourseListBean> courseList; //课程列表
    private Context context;

    public IndexFragmentAdapter(List<Index.CourseListBean> courseList, Context context) {
        this.courseList = courseList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int position) {
        return courseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_index_list,parent,false);
        }
        Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(convertView.findViewById(R.id.layout_index_list),typeface);

        TextView title = (TextView) convertView.findViewById(R.id.tv_index_list_title);
        title.setText(courseList.get(position).getTitle());//标题
        TextView price = (TextView) convertView.findViewById(R.id.tv_index_list_price);
        TextView discuntPrice = (TextView) convertView.findViewById(R.id.tv_index_list_discount_price);
        TextView countdown = (TextView) convertView.findViewById(R.id.tv_index_list_countdown);
        if(courseList.get(position).getIs_paid()==null|| TextUtils.isEmpty(courseList.get(position).getIs_paid())){
            //没有打折,隐藏优惠价和结束时间
            discuntPrice.setVisibility(View.GONE);
            countdown.setVisibility(View.GONE);
            price.setText(courseList.get(position).getPrice());
        }else{
            //打折了
            discuntPrice.setVisibility(View.VISIBLE);
            countdown.setVisibility(View.VISIBLE);
            price.setText(courseList.get(position).getDiscount_price());
            discuntPrice.setText(courseList.get(position).getPrice());
            //添加中划线
            discuntPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            countdown.setText(courseList.get(position).getCountdown());
        }
        TextView classNum = (TextView) convertView.findViewById(R.id.tv_index_list_courseNum);//课时
        classNum.setText("共"+courseList.get(position).getClass_num()+"课时");
        TextView trailNum = (TextView) convertView.findViewById(R.id.tv_index_list_trailNum);
        trailNum.setText(courseList.get(position).getTrial_num()+"人在学");
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_index_list);//图片
        MyApplication.imageLoader.displayImage(courseList.get(position).getImage_url(),imageView,MyApplication.options);
        return convertView;
    }

}

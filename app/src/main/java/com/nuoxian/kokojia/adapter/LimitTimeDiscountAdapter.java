package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.LimitTimeDiscount;
import com.nuoxian.kokojia.utils.CommonMethod;

import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */
public class LimitTimeDiscountAdapter extends BaseAdapter {

    private List<LimitTimeDiscount.DataBean> mList;
    private Context context;

    public LimitTimeDiscountAdapter(List<LimitTimeDiscount.DataBean> mList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_limit_time_discount,parent,false);
        }
        CommonMethod.setFontAwesome(convertView.findViewById(R.id.layout_item_limit_time_discount), context);

        TextView course = (TextView) convertView.findViewById(R.id.tv_discount_course);//课程
        course.setText(mList.get(position).getTitle());
        TextView price = (TextView) convertView.findViewById(R.id.tv_discount_price);//优惠价
        price.setText(mList.get(position).getPrice());
        TextView original = (TextView) convertView.findViewById(R.id.tv_discount_original_price);//原价
        original.setText(mList.get(position).getDiscount_price());
        //设置中划线
        original.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        TextView learn = (TextView) convertView.findViewById(R.id.tv_discount_learn);
        learn.setText(mList.get(position).getTrial_num() + "人在学");

        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_discount);
        MyApplication.imageLoader.displayImage(mList.get(position).getImage_url(), imageView, MyApplication.options);
        TextView status = (TextView) convertView.findViewById(R.id.tv_discount_status);//状态
        switch (mList.get(position).getCourse_type()){
            case "1"://推荐
                status.setText("推荐");
                status.setBackgroundColor(Color.RED);
                break;
            case "2"://独家
                status.setText("独家");
                status.setBackgroundColor(Color.RED);
                break;
            case "3"://首发
                status.setText("首发");
                status.setBackgroundColor(Color.parseColor("#f95c0d"));
                break;
        }
        return convertView;
    }
}

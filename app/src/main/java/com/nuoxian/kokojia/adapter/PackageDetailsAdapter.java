package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Course;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;

import java.util.List;

/**
 * Created by Administrator on 2016/9/9.
 */
public class PackageDetailsAdapter extends BaseAdapter {

    private Context context;
    private List<Course> list;

    public PackageDetailsAdapter(Context context, List<Course> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_package_details,parent,false);
        }
        //加载FONTAWESOME
        Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(convertView.findViewById(R.id.layout_package_details),typeface);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_package_details);//图片
        TextView title = (TextView) convertView.findViewById(R.id.tv_package_item_title);//标题
        TextView price = (TextView) convertView.findViewById(R.id.tv_package_item_price);//价格
        TextView discountPrice = (TextView) convertView.findViewById(R.id.tv_package_item_discount_price);//优惠价
        TextView countDown = (TextView) convertView.findViewById(R.id.tv_package_item_countdown);//剩余天数
        TextView courseNum = (TextView) convertView.findViewById(R.id.tv_package_item_course_num);//课时
        TextView userNum = (TextView) convertView.findViewById(R.id.tv_package_item_user_num);//多少人在学
        title.setText(list.get(position).getTitle());
        courseNum.setText("课时共"+list.get(position).getClass_num()+"节");
        userNum.setText(list.get(position).getTrial_num()+"人在学");
        if(!"".equals(list.get(position).getIs_paid())){//有折扣
            //显示优惠价
            discountPrice.setVisibility(View.VISIBLE);
            discountPrice.setText(list.get(position).getDiscount_price());
            //设置价格颜色为黑色，并设置中划线
            price.setTextColor(Color.BLACK);
            price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            //显示剩余天数
            countDown.setVisibility(View.VISIBLE);
            countDown.setText(list.get(position).getCountdown());
        }else{//没有折扣
            //隐藏优惠价和剩余天数
            discountPrice.setVisibility(View.GONE);
            countDown.setVisibility(View.GONE);
            //设置价格颜色为橙色，并取消中划线
            price.setTextColor(0xffff7112);
            price.getPaint().setFlags(0);
        }
        price.setText(list.get(position).getPrice());
        MyApplication.imageLoader.displayImage(list.get(position).getImage_url(), imageView, MyApplication.options);
        return convertView;
    }
}

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
import com.nuoxian.kokojia.enterty.AllPackage;
import com.nuoxian.kokojia.utils.CommonMethod;

import java.util.List;

/**
 * Created by Administrator on 2016/10/13.
 */
public class AllPackageAdapter extends BaseAdapter {

    private List<AllPackage.CourseListBean> mList;
    private Context context;

    public AllPackageAdapter(List<AllPackage.CourseListBean> mList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_all_package,parent,false);
        }
        CommonMethod.setFontAwesome(convertView.findViewById(R.id.layout_item_all_package), context);
        TextView title = (TextView) convertView.findViewById(R.id.tv_all_package_title);//课程名
        title.setText(mList.get(position).getTitle());
        TextView discountPrice = (TextView) convertView.findViewById(R.id.tv_all_package_discount_price);//套餐价
        discountPrice.setText("套餐价:"+mList.get(position).getPrice());
        TextView originalPrice = (TextView) convertView.findViewById(R.id.tv_all_package_original_price);//原价
        originalPrice.setText("(原价:"+mList.get(position).getOriginal_price()+")");
        //设置中划线
        originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        TextView courseCount = (TextView) convertView.findViewById(R.id.tv_all_package_course_count);//课程数量
        courseCount.setText(mList.get(position).getCourse_count()+"套课程");
        TextView courseNum = (TextView) convertView.findViewById(R.id.tv_all_package_course_num);//课时
        courseNum.setText(mList.get(position).getClass_num()+"节课时");
        //加载图片
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_all_package);
        MyApplication.imageLoader.displayImage(mList.get(position).getImage_url(),imageView,MyApplication.options);
        return convertView;
    }
}

package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.MyPackage;

import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 */
public class MyPackageAdapter extends BaseAdapter {

    private List<MyPackage.DataBean> list;
    private Context context;

    public MyPackageAdapter(List<MyPackage.DataBean> list, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_manage_package,parent,false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.tv_manage_package_title);
        title.setText(list.get(position).getTitle());//标题
        TextView price = (TextView) convertView.findViewById(R.id.tv_manage_package_price);
        price.setText(list.get(position).getOriginal_price());//原价
        price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//添加中划线
        TextView packagePrice = (TextView) convertView.findViewById(R.id.tv_manage_package_packageprice);
        packagePrice.setText(list.get(position).getPrice());//套餐价
        TextView classNum = (TextView) convertView.findViewById(R.id.tv_manage_package_class_num);
        classNum.setText("共"+list.get(position).getCourse_count()+"个课程");//课程总数
        TextView status = (TextView) convertView.findViewById(R.id.tv_manage_package_status_name);
        status.setText(list.get(position).getStatus_name());
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_manage_package_fragment);
        MyApplication.imageLoader.displayImage(list.get(position).getImage_url(),imageView,MyApplication.options);
        return convertView;
    }
}

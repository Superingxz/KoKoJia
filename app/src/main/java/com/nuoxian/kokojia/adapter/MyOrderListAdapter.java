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
import com.nuoxian.kokojia.enterty.MyOrder;

import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class MyOrderListAdapter extends BaseAdapter {

    private Context context;
    private List<MyOrder> list;

    public MyOrderListAdapter(Context context, List<MyOrder> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.my_order_item,parent,false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.tv_my_order_title);
        TextView price = (TextView) convertView.findViewById(R.id.tv_my_order_price);
        TextView time = (TextView) convertView.findViewById(R.id.tv_my_order_time);
        TextView status = (TextView) convertView.findViewById(R.id.tv_my_order_status);
        ImageView iv = (ImageView) convertView.findViewById(R.id.iv_my_order);
        title.setText(list.get(position).getTitle());
        price.setText(list.get(position).getPrice());
        time.setText(list.get(position).getCreate_time());
        if("未付款".equals(list.get(position).getStatus())){
            //如果是未付款，将字体变红
            status.setTextColor(0xffff7112);
        }else{
            //否则字体为灰色
            status.setTextColor(0xff999999);
        }
        status.setText(list.get(position).getStatus());
        MyApplication.imageLoader.displayImage(list.get(position).getImage_url(),iv,MyApplication.options);
        return convertView;
    }
}

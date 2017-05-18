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
import com.nuoxian.kokojia.enterty.SellOrder;

import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class SellOrderAdapter extends BaseAdapter {

    private Context context;
    private List<SellOrder.DataBean> data;

    public SellOrderAdapter(Context context, List<SellOrder.DataBean> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sell_order,parent,false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.tv_sell_order_title);
        title.setText(data.get(position).getCourse_title());
        TextView price = (TextView) convertView.findViewById(R.id.tv_sell_order_price);
        price.setText(data.get(position).getPrice());
        TextView status = (TextView) convertView.findViewById(R.id.tv_sell_order_status);
        status.setText(data.get(position).getStatus());
        TextView time = (TextView) convertView.findViewById(R.id.tv_sell_order_time);
        time.setText("下单时间:"+data.get(position).getTime());
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_sell_order);
        MyApplication.imageLoader.displayImage(data.get(position).getImage_url(),imageView,MyApplication.options);

        return convertView;
    }
}

package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.MyCoupon;

import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 */
public class CanUseCouponAdapter extends BaseAdapter {

    private List<MyCoupon.DataBean> list;
    private Context context;

    public CanUseCouponAdapter(List<MyCoupon.DataBean> list, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_can_use_coupon,parent,false);
        }
        TextView way = (TextView) convertView.findViewById(R.id.tv_coupon_way);//优惠方式
        way.setText("- "+list.get(position).getCoupon_sale()+" "+list.get(position).getSale_price());
        TextView code = (TextView) convertView.findViewById(R.id.tv_coupon_code);
        code.setText(list.get(position).getCoupon_code());
        return convertView;
    }
}

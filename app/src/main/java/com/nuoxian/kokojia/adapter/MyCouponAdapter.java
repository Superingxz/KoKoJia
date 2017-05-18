package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.MyCoupon;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */
public class MyCouponAdapter extends BaseAdapter {

    private List<MyCoupon.DataBean> list;
    private Context context;

    public MyCouponAdapter(List<MyCoupon.DataBean> list, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_coupon,parent,false);
        }
        TextView code = (TextView) convertView.findViewById(R.id.tv_mycoupon_code);//优惠码
        code.setText(list.get(position).getCoupon_code());
        TextView status = (TextView) convertView.findViewById(R.id.tv_mycoupon_status);//状态
        status.setText(list.get(position).getStatus_name());
        TextView way = (TextView) convertView.findViewById(R.id.tv_mycoupon_discuss_way);//优惠方式
        way.setText(list.get(position).getCoupon_sale());
        TextView range = (TextView) convertView.findViewById(R.id.tv_mycoupon_range);//使用范围
        range.setText(list.get(position).getUser_range_name());
        TextView validTime = (TextView) convertView.findViewById(R.id.tv_mycoupon_valid_time);//有效时间
        validTime.setText(list.get(position).getDate());
        TextView useTime = (TextView) convertView.findViewById(R.id.tv_mycoupon_use_time);//使用时间
        if(TextUtils.isEmpty(list.get(position).getUsed_date())){
            useTime.setText("暂未使用");
        }else{
            useTime.setText(list.get(position).getUsed_date());
        }
        return convertView;
    }
}

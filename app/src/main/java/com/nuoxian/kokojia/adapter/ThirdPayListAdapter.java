package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.PayWay;

import java.util.List;

/**
 * Created by Administrator on 2016/8/2.
 */
public class ThirdPayListAdapter extends BaseAdapter {
    private Context context;
    private List<PayWay> list;

    public ThirdPayListAdapter(Context context, List<PayWay> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.third_pay_list_item,parent,false);
        }
        ImageView payWay = (ImageView) convertView.findViewById(R.id.third_item_pay_way);
        ImageView paySelect = (ImageView) convertView.findViewById(R.id.third_item_pay_select);
        payWay.setImageResource(list.get(position).getPayWay());
        if(list.get(position).isSelect()){
            paySelect.setImageResource(R.mipmap.select_quan2_20_20);
        }else{
            paySelect.setImageResource(R.mipmap.select_quan1_20_20);
        }
        return convertView;
    }
}

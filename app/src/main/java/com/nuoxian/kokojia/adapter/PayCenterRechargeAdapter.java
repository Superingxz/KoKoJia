package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.RechargeRecord;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */
public class PayCenterRechargeAdapter extends BaseAdapter {

    private Context context;
    private List<RechargeRecord.DataBean> mList;

    public PayCenterRechargeAdapter(Context context, List<RechargeRecord.DataBean> mList) {
        this.context = context;
        this.mList = mList;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pay_center_recharge,parent,false);
        }
        convertView.setClickable(true);

        TextView price = (TextView) convertView.findViewById(R.id.tv_pay_center_recharge_price);//充值金额
        price.setText("￥"+mList.get(position).getMoney());
        TextView time = (TextView) convertView.findViewById(R.id.tv_pay_center_recharge_time);//充值
        time.setText(mList.get(position).getCreate_time());

        return convertView;
    }
}

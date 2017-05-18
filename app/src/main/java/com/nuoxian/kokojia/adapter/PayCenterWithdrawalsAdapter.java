package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.WithdrawalsRecord;

import java.util.List;

/**
 * Created by Administrator on 2016/10/13.
 */
public class PayCenterWithdrawalsAdapter extends BaseAdapter {

    private List<WithdrawalsRecord.DataBean> mList;
    private Context context;

    public PayCenterWithdrawalsAdapter(List<WithdrawalsRecord.DataBean> mList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pay_center_withdrawals,parent,false);
        }
        //设置item不可点击
        convertView.setClickable(true);

        TextView price = (TextView) convertView.findViewById(R.id.tv_pay_center_withdrawals_price);//提现金额
        price.setText("￥"+mList.get(position).getMoney());
        TextView status = (TextView) convertView.findViewById(R.id.tv_pay_center_withdrawals_status);//提现状态
        status.setText(mList.get(position).getStatus());
        TextView time = (TextView) convertView.findViewById(R.id.tv_pay_center_withdrawals_time);//请求时间
        time.setText(mList.get(position).getCreate_time());
        return convertView;
    }
}

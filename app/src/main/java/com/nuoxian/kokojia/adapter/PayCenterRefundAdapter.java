package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.RefundRecord;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.List;

/**
 * Created by Administrator on 2016/10/13.
 */
public class PayCenterRefundAdapter extends BaseAdapter {

    private List<RefundRecord.DataBean> mList;
    private Context context;

    public PayCenterRefundAdapter(List<RefundRecord.DataBean> mList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pay_center_refund,parent,false);
        }
        TextView course = (TextView) convertView.findViewById(R.id.tv_pay_center_refund_course);//课程名
        course.setText(mList.get(position).getTitle());
        TextView price = (TextView) convertView.findViewById(R.id.tv_pay_center_refund_price);//退款金额
        price.setText("￥"+mList.get(position).getPrice());
        TextView status = (TextView) convertView.findViewById(R.id.tv_pay_center_refund_status);//退款状态
        switch (mList.get(position).getStatus()){
            case "1":
                status.setText("待处理");
                break;
            case "2":
                status.setText("已退款");
                break;
            case "3":
                status.setText("已拒绝");
                break;
            case "4":
                status.setText("平台介入");
                break;
            case "5":
                status.setText("已关闭");
                break;
        }
        TextView time = (TextView) convertView.findViewById(R.id.tv_pay_center_refund_time);//申请时间
        time.setText(mList.get(position).getTime());

        return convertView;
    }
}

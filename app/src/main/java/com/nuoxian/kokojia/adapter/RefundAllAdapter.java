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
import com.nuoxian.kokojia.enterty.Refund;

import java.util.List;

/**
 * Created by Administrator on 2016/9/29.
 */
public class RefundAllAdapter extends BaseAdapter {

    private List<Refund.DataBean> mList;
    private Context context;

    public RefundAllAdapter(List<Refund.DataBean> mList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_refund,parent,false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.tv_item_refund_title);
        title.setText(mList.get(position).getTitle());//标题
        TextView price = (TextView) convertView.findViewById(R.id.tv_item_refund_price);
        price.setText(mList.get(position).getPrice());//价格
        TextView reason = (TextView) convertView.findViewById(R.id.tv_item_refund_reason);
        reason.setText("退款原因:"+mList.get(position).getReason());
        TextView time = (TextView) convertView.findViewById(R.id.tv_item_refund_time);
        time.setText("申请时间:"+mList.get(position).getTime());//时间
        TextView status = (TextView) convertView.findViewById(R.id.tv_item_refund_status);
        status.setText(mList.get(position).getStatus_name());//状态
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_item_refund);
        MyApplication.imageLoader.displayImage(mList.get(position).getImage_url(),imageView,MyApplication.options);
        return convertView;
    }
}

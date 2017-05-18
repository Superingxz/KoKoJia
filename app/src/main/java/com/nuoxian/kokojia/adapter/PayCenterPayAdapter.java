package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.PayRecord;
import com.nuoxian.kokojia.utils.FontManager;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */
public class PayCenterPayAdapter extends BaseAdapter {

    private List<PayRecord.DataBean> mList;
    private Context context;

    public PayCenterPayAdapter(List<PayRecord.DataBean> mList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pay_center_pay,parent,false);
        }
        Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(convertView.findViewById(R.id.layout_pay_center_pay), typeface);

        TextView title = (TextView) convertView.findViewById(R.id.tv_pay_center_pay_title);//课程名
        title.setText(mList.get(position).getCourse_title());
        TextView price = (TextView) convertView.findViewById(R.id.tv_pay_center_pay_price);//价格
        price.setText("￥"+mList.get(position).getPrice());
        TextView time = (TextView) convertView.findViewById(R.id.tv_pay_center_pay_time);//支付时间
        time.setText(mList.get(position).getPay_time());
        TextView status = (TextView) convertView.findViewById(R.id.tv_pay_center_pay_status);//交易状态
        status.setText(mList.get(position).getStatus());

        return convertView;
    }
}

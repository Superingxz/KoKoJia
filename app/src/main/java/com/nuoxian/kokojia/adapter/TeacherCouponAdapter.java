package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.TeacherCoupon;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */
public class TeacherCouponAdapter extends BaseAdapter {

    private List<TeacherCoupon.DataBean> mList;
    private Context context;

    public TeacherCouponAdapter(List<TeacherCoupon.DataBean> mList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_teacher_coupon,parent,false);
        }
        //设置不可点击
        convertView.setClickable(true);

        TextView way = (TextView) convertView.findViewById(R.id.tv_discuss_way);//优惠方式
        way.setText(mList.get(position).getCoupon_sale());
        TextView range = (TextView) convertView.findViewById(R.id.tv_use_range);//使用范围
        range.setText(mList.get(position).getUser_range_name());
        TextView count = (TextView) convertView.findViewById(R.id.tv_count);//数量
        count.setText(mList.get(position).getCoupon_used_num()+"/"+mList.get(position).getCoupon_receive_num()+"/"+mList.get(position).getCoupon_num());
        TextView status = (TextView) convertView.findViewById(R.id.tv_status);//状态
        status.setText(mList.get(position).getStatus());
        TextView createTime = (TextView) convertView.findViewById(R.id.tv_create_time);//创建时间
        createTime.setText(mList.get(position).getCreate_time());
        TextView getTime = (TextView) convertView.findViewById(R.id.tv_get_time);//领取时间
        getTime.setText(mList.get(position).getEnd_date());
        TextView useTime = (TextView) convertView.findViewById(R.id.tv_use_time);//使用时限
        useTime.setText(mList.get(position).getCoupon_date());
        return convertView;
    }
}

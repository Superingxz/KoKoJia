package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.MyCollect;

import java.util.List;

/**
 * Created by Administrator on 2016/9/28.
 */
public class MyCollectAdapter extends BaseAdapter {

    private List<MyCollect> mList;
    private Context context;

    public MyCollectAdapter(List<MyCollect> mList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_collect,parent,false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.tv_my_collect_title);
        title.setText(mList.get(position).getTitle());//标题
        TextView discuss = (TextView) convertView.findViewById(R.id.tv_my_collect_discuss);
        TextView countDown = (TextView) convertView.findViewById(R.id.tv_my_collect_countdown);
        TextView price = (TextView) convertView.findViewById(R.id.tv_my_collect_price);
        if(TextUtils.isEmpty(mList.get(position).getDiscount_price())){//没有打折
            price.setText(mList.get(position).getPrice());//价格
            discuss.setVisibility(View.GONE);
            countDown.setVisibility(View.GONE);
        }else{//打折
            price.setText(mList.get(position).getDiscount_price());//优惠价
            discuss.setVisibility(View.VISIBLE);
            discuss.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
            discuss.setText(mList.get(position).getPrice());//原价
            countDown.setVisibility(View.VISIBLE);
            countDown.setText(mList.get(position).getCountdown());//结束时间
        }
        TextView learn = (TextView) convertView.findViewById(R.id.tv_my_collect_learn);
        learn.setText("学习进度:已学习"+mList.get(position).getLearn()+"课时");//学习进度
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_my_collect);
        MyApplication.imageLoader.displayImage(mList.get(position).getImage_url(),imageView,MyApplication.options);
        return convertView;
    }
}

package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/30.
 */
public class HomeGridViewAdapter extends BaseAdapter {

    private List<Map<String,Object>> menuList;
    private Context context;
    private ImageView mImageView;
    private TextView mTextView;
    public HomeGridViewAdapter(Context context,List<Map<String,Object>> menuList) {
        this.menuList = menuList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.home_gridview_item,parent,false);
        }
        mImageView = (ImageView) convertView.findViewById(R.id.iv_gridview_image);
        mTextView = (TextView) convertView.findViewById(R.id.tv_gridview_title);

        mImageView.setImageResource((int)menuList.get(position).get("image"));
        mTextView.setText(menuList.get(position).get("title")+"");

        return convertView;
    }
}

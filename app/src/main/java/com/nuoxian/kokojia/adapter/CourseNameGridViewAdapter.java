package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.CourseCatalog;

import java.util.List;

/**
 * Created by 陈思龙 on 2016/7/1.
 */
public class CourseNameGridViewAdapter extends BaseAdapter {

    private List<CourseCatalog.TypeTwoBean> list;
    private Context context;

    public CourseNameGridViewAdapter(Context context,List<CourseCatalog.TypeTwoBean> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.coursename_gridview_item,parent,false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_coursename_name);
        tvName.setText(list.get(position).getName());

        return convertView;
    }
}

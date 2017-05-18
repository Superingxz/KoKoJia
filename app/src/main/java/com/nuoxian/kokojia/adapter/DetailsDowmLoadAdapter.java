package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.CourseFile;

import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
public class DetailsDowmLoadAdapter extends BaseAdapter {

    private List<CourseFile> list;
    private Context context;

    public DetailsDowmLoadAdapter(List<CourseFile> list, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.details_download_item,parent,false);
        }
        TextView tvFile = (TextView) convertView.findViewById(R.id.tv_details_dowmload_file);
        tvFile.setText(list.get(position).getTitle());

        return convertView;
    }
}

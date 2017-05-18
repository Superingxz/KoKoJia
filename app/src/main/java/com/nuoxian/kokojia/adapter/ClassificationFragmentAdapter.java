package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseCatalog;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.view.SodukuGridView;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 */
public class ClassificationFragmentAdapter extends BaseAdapter {

    private List<CourseCatalog> mList;
    private Context context;

    public ClassificationFragmentAdapter(List<CourseCatalog> mList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_classify_fragment,parent,false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.tv_item_classify_name);//名称
        name.setText(mList.get(position).getCourse_type_name());
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_classify_fragment);//图标
        MyApplication.imageLoader.displayImage(
                "http://www.kokojia.com/Public/course_type_icon/"+mList.get(position).getCourse_type_id()+".png",
                imageView,
                MyApplication.options);
        return convertView;
    }
}

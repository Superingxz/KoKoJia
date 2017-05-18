package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Index;
import com.nuoxian.kokojia.utils.FontManager;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 */
public class IndexSelectAdapter extends BaseAdapter {

    private List<Index.CourseJpBean> selectList;
    private Context context;

    public IndexSelectAdapter(List<Index.CourseJpBean> selectList, Context context) {
        this.selectList = selectList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return selectList.size();
    }

    @Override
    public Object getItem(int position) {
        return selectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_index_select_gridview,parent,false);
        }
        Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(convertView.findViewById(R.id.layout_index_select), typeface);

        TextView title = (TextView) convertView.findViewById(R.id.tv_index_select_title);//标题
        title.setText(selectList.get(position).getTitle());
        TextView classNum = (TextView) convertView.findViewById(R.id.tv_index_select_classNum);
        classNum.setText("共"+selectList.get(position).getClass_num()+"课时");
        TextView trailNum = (TextView) convertView.findViewById(R.id.tv_index_select_trailNum);
        trailNum.setText(selectList.get(position).getTrial_num()+"人在学");
        TextView type = (TextView) convertView.findViewById(R.id.tv_index_select_type);
        if("1".equals(selectList.get(position).getCourse_type())){
            //推荐
            type.setVisibility(View.VISIBLE);
            type.setText("推荐");
        }else if("2".equals(selectList.get(position).getCourse_type())){
            //独家
            type.setVisibility(View.VISIBLE);
            type.setText("独家");
        }else if("3".equals(selectList.get(position).getCourse_type())){
            //首发
            type.setVisibility(View.VISIBLE);
            type.setText("首发");
        }else{
            type.setVisibility(View.GONE);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_index_select);//图片
        MyApplication.imageLoader.displayImage(selectList.get(position).getImg(),imageView,MyApplication.options);
        return convertView;
    }
}

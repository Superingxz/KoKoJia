package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.CourseListActivity;
import com.nuoxian.kokojia.activity.HomeActivity;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseCatalog;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.view.SodukuGridView;

import java.util.List;

/**
 * Created by 陈思龙 on 2016/7/1.
 * 课程名listview适配器
 */
public class CourseNameAdapter extends BaseAdapter {

    private List<CourseCatalog> courseName;
    private Context context;
    private FragmentActivity fa;
    private TextView mTitle;

    public CourseNameAdapter(Context context,List<CourseCatalog> courseName,FragmentActivity fa,TextView mTitle) {
        this.courseName = courseName;
        this.context = context;
        this.fa = fa;
        this.mTitle = mTitle;
    }

    @Override
    public int getCount() {
        return courseName.size();
    }

    @Override
    public Object getItem(int position) {
        return courseName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.coursename_listview_item,parent,false);
            ViewHolder vh = new ViewHolder();
            vh.courseTypeName = (TextView) convertView.findViewById(R.id.tv_coursename_coursetypename);
            vh.total = (TextView) convertView.findViewById(R.id.tv_coursename_total);
            vh.mGridView = (SodukuGridView) convertView.findViewById(R.id.gv_coursename_name);
            convertView.setTag(vh);
        }
        final ViewHolder vh = (ViewHolder) convertView.getTag();

        //设置tag为当前item的position
        vh.mGridView.setTag(position);
        vh.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击的gridview在listview的哪个item上
                int itemPosition = (int) vh.mGridView.getTag();
                int urlId = Integer.parseInt(courseName.get(itemPosition).getType_two().get(position).getId());
                //加载点击的页面
                String title = courseName.get(itemPosition).getType_two().get(position).getName();
                mTitle.setText(title);
                Intent intent = new Intent(context, CourseListActivity.class);
                intent.putExtra("uilId",urlId);
                intent.putExtra("urlPage",1);
                intent.putExtra("title",title);
                context.startActivity(intent);
            }
        });

        vh.courseTypeName.setText(courseName.get(position).getCourse_type_name());
        vh.total.setText("("+courseName.get(position).getCourse_total()+")");

        //加载数据
        List<CourseCatalog.TypeTwoBean> list = courseName.get(position).getType_two();
        CourseNameGridViewAdapter adapter = new CourseNameGridViewAdapter(context,list);
        vh.mGridView.setAdapter(adapter);

        return convertView;
    }

    class ViewHolder{
        private TextView  courseTypeName,total;
        private SodukuGridView mGridView;
    }

}

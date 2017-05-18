package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.CourseLesson;
import com.nuoxian.kokojia.utils.CommonMethod;

import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
public class DetailsLessonAdapter extends BaseAdapter {

    private List<CourseLesson> list;
    private Context context;
    private static int TYPE_COUNT = 2;
    private static int ZHANG = 0;//章
    private static int JIE = 1;//节

    public DetailsLessonAdapter(List<CourseLesson> list, Context context) {
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
    public int getItemViewType(int position) {
        if("0".equals(list.get(position).getLevel())){
            return ZHANG;
        }else if("1".equals(list.get(position).getLevel())){
            return JIE;
        }else{
            return -1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(ZHANG==getItemViewType(position)){//章
            if(convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.details_lesson_item1,parent,false);
            }
            CommonMethod.setFontAwesome(convertView.findViewById(R.id.layout_details_lesson_item1), context);
            TextView tv = (TextView) convertView.findViewById(R.id.tv_lesson_item1_title);
            tv.setText(list.get(position).getTitle());
        }else if(JIE==getItemViewType(position)){//节
            if(convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.details_lesson_item2,parent,false);
            }
            CommonMethod.setFontAwesome(convertView.findViewById(R.id.layout_details_lesson_item2), context);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_lesson_item2_title);
            tvTitle.setText(list.get(position).getTitle());
            TextView tvTime = (TextView) convertView.findViewById(R.id.tv_lesson_item2_time);
            tvTime.setText(list.get(position).getVideo_time());
            TextView textView = (TextView) convertView.findViewById(R.id.tv_lesson_item2);
            if(list.get(position).getPlayer_status()==1){//可以学习
                textView.setText(R.string.mycourse);
                textView.setTextColor(Color.parseColor("#00aeef"));
            }else{//需要购买才可以学习
                textView.setText(R.string.lock);
                textView.setTextColor(Color.parseColor("#D7D7D7"));
            }
        }
        return convertView;
    }
}

package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.PlayLesson;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;

import java.util.List;

/**
 * Created by Administrator on 2016/7/12.
 */
public class PlayCourseLessonAdapter extends BaseAdapter {

    private List<PlayLesson> list;
    private Context context;
    private static int ZHANG = 0;
    private static int JIE = 1;
    private String lid;

    public PlayCourseLessonAdapter(List<PlayLesson> list, Context context,String lid) {
        this.list = list;
        this.context = context;
        this.lid = lid;
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
    public int getViewTypeCount() {
        return 2;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(getItemViewType(position)==ZHANG){//显示章
            if(convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.play_listview_item1,parent,false);
            }
            CommonMethod.setFontAwesome(convertView.findViewById(R.id.layout_play_listview_item1),context);
            TextView tv = (TextView) convertView.findViewById(R.id.tv_play_title_zhang);
            tv.setText(list.get(position).getTitle());
        }else if(getItemViewType(position)==JIE){//显示节
            if(convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.play_listview_item2,parent,false);
            }
            TextView title = (TextView) convertView.findViewById(R.id.tv_play_title_jie);
            TextView time = (TextView) convertView.findViewById(R.id.tv_play_time);
            title.setText(list.get(position).getTitle());
            if(list.get(position).isCheck()){
                title.setTextColor(0xff00aeef);
            }else{
                title.setTextColor(0xff494949);
            }
            time.setText(list.get(position).getVideo_time());
            TextView iv = (TextView) convertView.findViewById(R.id.iv_play);
            if ("1".equals(list.get(position).getPlayer_status())) {//可以播放
                iv.setText(R.string.mycourse);
                iv.setTextColor(Color.parseColor("#00aeef"));
            }else{//不能播放
                iv.setText(R.string.lock);
                iv.setTextColor(Color.parseColor("#D7D7D7"));
            }
            Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
            iv.setTypeface(typeface);
        }
        return convertView;
    }
}

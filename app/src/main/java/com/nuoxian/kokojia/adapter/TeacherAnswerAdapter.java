package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.enterty.TeacherAnswer;
import com.nuoxian.kokojia.utils.FontManager;

import java.util.List;

/**
 * Created by Administrator on 2016/9/19.
 */
public class TeacherAnswerAdapter extends BaseAdapter {

    private List<TeacherAnswer.DataBean> list;
    private Context context;

    public TeacherAnswerAdapter(List<TeacherAnswer.DataBean> list, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_teacher_answer,parent,false);
        }
        Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(convertView.findViewById(R.id.layout_item_teacher_answer),typeface);
        TextView title = (TextView) convertView.findViewById(R.id.tv_item_answer_title);
        TextView content = (TextView) convertView.findViewById(R.id.tv_item_answer_content);
        TextView user = (TextView) convertView.findViewById(R.id.tv_item_answer_user);
        TextView time = (TextView) convertView.findViewById(R.id.tv_item_answer_time);
        TextView status = (TextView) convertView.findViewById(R.id.tv_item_answer_status);
        title.setText(list.get(position).getTitle());
        content.setText(list.get(position).getContent());
        user.setText(list.get(position).getNickname());
        time.setText(list.get(position).getTime());
        if("1".equals(list.get(position).getTeacher_reply())){
            status.setText("待回复");
        }else{
            status.setText("已回复");
        }
        return convertView;
    }
}

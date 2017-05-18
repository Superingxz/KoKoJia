package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseDiscuss;
import com.nuoxian.kokojia.enterty.MyQuestion;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/26.
 */
public class MyQuestionAdapter extends BaseAdapter {

    private List<MyQuestion.DataBean> mList;
    private Context context;

    public MyQuestionAdapter(List<MyQuestion.DataBean> mList, Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_question,parent,false);
        }
        Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(convertView.findViewById(R.id.layout_item_question), typeface);
        TextView course = (TextView) convertView.findViewById(R.id.tv_item_question_course);//课程
        course.setText(mList.get(position).getCourse_title());
        TextView zhang = (TextView) convertView.findViewById(R.id.tv_item_question_zhang);//章节
        if(TextUtils.isEmpty(mList.get(position).getLesson_title())){
            zhang.setText("未指定");
        }else{
            zhang.setText(mList.get(position).getLesson_title());
        }
        TextView content = (TextView) convertView.findViewById(R.id.tv_item_question_content);//内容
        content.setText(mList.get(position).getContent());
        TextView zan = (TextView) convertView.findViewById(R.id.tv_item_question_zan);//赞
        zan.setText("赞("+mList.get(position).getVote_num()+")");
        TextView reply = (TextView) convertView.findViewById(R.id.tv_item_question_reply);//回复
        reply.setText("回复("+mList.get(position).getEvaluate_num()+")");
        TextView time = (TextView) convertView.findViewById(R.id.tv_item_question_time);//时间
        time.setText(mList.get(position).getTime());

        return convertView;
    }

}

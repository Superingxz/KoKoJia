package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseComment;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.view.RoundImageView;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
public class DetailsCommentAdapter extends BaseAdapter {

    private List<CourseComment> commentList;
    private Context context;

    public DetailsCommentAdapter(List<CourseComment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.course_comment_item,parent,false);
            ViewHoder vh = new ViewHoder();
            vh.ivHead = (RoundImageView) convertView.findViewById(R.id.iv_comment_userhead);
            vh.tvContent = (TextView) convertView.findViewById(R.id.tv_comment_content);
            vh.tvUserName = (TextView) convertView.findViewById(R.id.tv_comment_username);
            vh.tvTime = (TextView) convertView.findViewById(R.id.tv_comment_time);
//            vh.tvCount = (TextView) convertView.findViewById(R.id.tv_comment_star_count);
            convertView.setTag(vh);
        }
        //设置item不可点击
        convertView.setClickable(true);

        ViewHoder vh = (ViewHoder) convertView.getTag();
        vh.tvContent.setText(commentList.get(position).getContent());//评价内容
        vh.tvUserName.setText(commentList.get(position).getStar_name());//用户名称
        vh.tvTime.setText(commentList.get(position).getTime());//时间
        AutoLinearLayout llStar = (AutoLinearLayout) convertView.findViewById(R.id.ll_comment_star);
        llStar.removeAllViews();
        for(int i=0;i<5;i++){//星星
            Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
            TextView star =  new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            star.setLayoutParams(params);
            if(i<Integer.parseInt(commentList.get(position).getRate())){//评价的星星数
                //显示实心的星星
                star.setText(R.string.solid_star);
            }else{
                //显示空心的星星
                star.setText(R.string.star);
            }
            star.setTextColor(Color.parseColor("#00A0CC"));
            star.setTypeface(typeface);
            llStar.addView(star);
        }

        MyApplication.imageLoader.displayImage(commentList.get(position).getAvatar(),vh.ivHead,MyApplication.options);

        return convertView;
    }

    class ViewHoder{
        private RoundImageView ivHead;
        private TextView tvUserName,tvTime,tvContent,tvCount;
    }
}

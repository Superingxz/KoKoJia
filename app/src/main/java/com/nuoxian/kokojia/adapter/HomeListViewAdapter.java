package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Course;
import com.nuoxian.kokojia.utils.FontManager;

import java.util.List;

/**
 * Created by 陈思龙 on 2016/6/29.
 * 首页listView适配器
 */
public class HomeListViewAdapter extends BaseAdapter {

    private Context context;
    private List<Course> courseContent;
    public HomeListViewAdapter(Context context,List<Course> courseContent) {
        this.context = context;
        this.courseContent = courseContent;
    }

    @Override
    public int getCount() {
        return courseContent.size();
    }

    @Override
    public Object getItem(int position) {
        return courseContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.home_listview_item,parent,false);
            ViewHolder vh = new ViewHolder();
            vh.class_num = (TextView) convertView.findViewById(R.id.tv_home_listview_class_num);
            vh.countdown = (TextView) convertView.findViewById(R.id.tv_home_listview_countdown);
            vh.discount_price = (TextView) convertView.findViewById(R.id.tv_home_listview_discount_price);
            vh.price = (TextView) convertView.findViewById(R.id.tv_home_listview_price);
            vh.title = (TextView) convertView.findViewById(R.id.tv_home_listview_title);
            vh.trial_num = (TextView) convertView.findViewById(R.id.tv_home_listview_trial_num);
            vh.image = (ImageView) convertView.findViewById(R.id.iv_home_listview_course);
            convertView.setTag(vh);
        }
        Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(convertView.findViewById(R.id.layout_home_list),typeface);

        ViewHolder vh = (ViewHolder) convertView.getTag();
        if(!"".equals(courseContent.get(position).getDiscount_price())){//价格有折扣
            vh.price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);//添加中划线
            vh.price.setTextColor(Color.BLACK);
        }else{//价格没有折扣
            vh.price.getPaint().setFlags(0);//取消中划线
            vh.price.setTextColor(0xffff7112);
        }
        vh.trial_num.setText(courseContent.get(position).getTrial_num()+"人在学");
        vh.title.setText(courseContent.get(position).getTitle());
        vh.price.setText(courseContent.get(position).getPrice());
        vh.discount_price.setText(courseContent.get(position).getDiscount_price());
        vh.class_num.setText("课时共"+courseContent.get(position).getClass_num()+"节");
        vh.countdown.setText(courseContent.get(position).getCountdown());
        MyApplication.imageLoader.displayImage(courseContent.get(position).getImage_url(),vh.image,MyApplication.options);
        return convertView;
    }

    class ViewHolder{
        private TextView title,discount_price,price,countdown,class_num,trial_num;
        private ImageView image;
    }
}

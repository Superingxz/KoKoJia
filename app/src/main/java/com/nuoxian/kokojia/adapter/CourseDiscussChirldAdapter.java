package com.nuoxian.kokojia.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLinearLayout;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/14.
 */
public class CourseDiscussChirldAdapter extends BaseAdapter {

    private Context context;
    private List<CourseDiscuss.CourseDiscussBean.ReplyBean> list;
    private String content;

    public CourseDiscussChirldAdapter(Context context, List<CourseDiscuss.CourseDiscussBean.ReplyBean> list) {
        this.context = context;
        this.list = list;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_course_discuss_chirld,parent,false);
            ViewHolder vh = new ViewHolder();
            vh.imageView = (ImageView) convertView.findViewById(R.id.iv_discuss_chilrd);
            vh.title = (TextView) convertView.findViewById(R.id.tv_discuss_chirld_title);
            vh.reply = (TextView) convertView.findViewById(R.id.tv_discuss_chirld_reply);
            vh.time = (TextView) convertView.findViewById(R.id.tv_discuss_chirld_time);
            convertView.setTag(vh);
        }
        Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(convertView.findViewById(R.id.layout_discuss_chirld),typeface);

        ViewHolder vh = (ViewHolder) convertView.getTag();

        String title = list.get(position).getNickname()+"回复"+list.get(position).getReply_name();
        String nickName = list.get(position).getNickname();
        String replyName = list.get(position).getReply_name();
        //设置标题部分字体颜色
        SpannableStringBuilder span = new SpannableStringBuilder(title);
        span.setSpan(new ForegroundColorSpan(0xff00aeff),0,nickName.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        span.setSpan(new ForegroundColorSpan(0xff00aeff), nickName.length() + 2, nickName.length() + 2 + replyName.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        vh.title.setText(span);//标题
        vh.reply.setText(list.get(position).getContent());//回复内容
        vh.time.setText(list.get(position).getTime());//时间
        MyApplication.imageLoader.displayImage(list.get(position).getStudent_avatar(), vh.imageView, MyApplication.options);//头像
        //设置回复监听
        AutoLinearLayout reply = (AutoLinearLayout) convertView.findViewById(R.id.ll_discuss_chirld_reply);
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回复
                if(CommonMethod.getUid(context).equals(list.get(position).getReply_id())){
                    CommonMethod.showAlerDialog("回复失败","不能回复自己",context);
                }else{
                    reply(position);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder{
        public ImageView imageView;
        public TextView title,reply,time;
    }

    /**
     * 回复
     */
    private void reply(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //自定义的对话框
        View view  = View.inflate(context,R.layout.edittext_alertdialog,null);
        EditText reply = (EditText) view.findViewById(R.id.et_reply);
        Button cancle = (Button) view.findViewById(R.id.btn_reply_cancle);
        Button commit = (Button) view.findViewById(R.id.btn_reply_commit);
        final AlertDialog dialog = builder.create();
        dialog.setView(view);
        dialog.show();
        reply.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                //获取用户输入的内容
                content = s.toString();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消
                dialog.dismiss();
            }
        });
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(content)) {
                    //提交
                    commit(Urls.COURSE_DISCUSS_REPLY,position);
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 提交
     * @param url
     * @param position
     */
    private void commit(String url, final int position){
        final List<CourseDiscuss.CourseDiscussBean.ReplyBean> tempList = new ArrayList<>();
        tempList.addAll(list);
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //回复成功
                                JSONObject j2 = j1.getJSONObject("data");
                                CourseDiscuss.CourseDiscussBean.ReplyBean reply = new CourseDiscuss.CourseDiscussBean.ReplyBean();
                                reply.setNickname(j2.getString("student_nickname"));
                                reply.setStudent_avatar(j2.getString("student_avatar"));
                                reply.setTime(j2.getString("time"));
                                reply.setReply_name(j2.getString("reply_name"));
                                reply.setId(j2.getString("id"));
                                reply.setReply_id(CommonMethod.getUid(context));
                                reply.setDiscuss_id(j2.getString("discuss_id"));
                                reply.setContent(j2.getString("content"));
                                //将回复的内容添加进来
                                list.add(reply);
                                notifyDataSetChanged();
                            }else{
                                //回复失败
                                CommonMethod.showAlerDialog("回复失败", j1.getString("msg"), context);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(context);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",list.get(position).getId());
                params.put("uid",CommonMethod.getUid(context));
                params.put("type","2");//回复的回复，类型固定为1
                params.put("content",content);
                params.put("user_id",list.get(position).getReply_id());
                return params;
            }
        };
        mQueue.add(request);
    }
}

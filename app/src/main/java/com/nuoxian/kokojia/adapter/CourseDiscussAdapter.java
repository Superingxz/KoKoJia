package com.nuoxian.kokojia.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
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
import com.nuoxian.kokojia.activity.ImageActivity;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseDiscuss;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.nuoxian.kokojia.view.NoScrollListview;
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
public class CourseDiscussAdapter extends BaseAdapter {

    private Context context;
    private List<CourseDiscuss.CourseDiscussBean> list;
    private String content;
    private NoScrollListview mListView;

    public CourseDiscussAdapter(Context context, List<CourseDiscuss.CourseDiscussBean> list) {
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_course_discuss_group, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.imageView = (ImageView) convertView.findViewById(R.id.iv_discuss_group);
            vh.ivQuestion = (ImageView) convertView.findViewById(R.id.iv_discuss_group_question);
            vh.title = (TextView) convertView.findViewById(R.id.tv_discuss_group_title);
            vh.question = (TextView) convertView.findViewById(R.id.tv_discuss_group_question);
            vh.time = (TextView) convertView.findViewById(R.id.tv_discuss_group_time);
            vh.zan = (TextView) convertView.findViewById(R.id.tv_discuss_group_zan);
            convertView.setTag(vh);
        }
        Typeface typeface = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(convertView.findViewById(R.id.layout_discuss_group), typeface);

        final ViewHolder vh = (ViewHolder) convertView.getTag();
        String nickName = list.get(position).getNickname();
        String lessionTitle = list.get(position).getLesson_title();
        //设置标题
        if (TextUtils.isEmpty(lessionTitle)) {
            String title = nickName + "发表讨论";
            //设置标题部分字体颜色
            SpannableStringBuilder span = new SpannableStringBuilder(title);
            span.setSpan(new ForegroundColorSpan(0xff00aeff), 0, nickName.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            vh.title.setText(span);
        } else {
            String title = nickName + "在课时" + lessionTitle + "发表讨论";
            //设置标题部分字体颜色
            SpannableStringBuilder span = new SpannableStringBuilder(title);
            span.setSpan(new ForegroundColorSpan(0xff00aeff), 0, nickName.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            span.setSpan(new ForegroundColorSpan(0xff00aeff), nickName.length() + 3, nickName.length() + 3 + lessionTitle.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            vh.title.setText(span);
        }
        //问题
        vh.question.setText(list.get(position).getContent());
        //时间
        vh.time.setText(list.get(position).getTime());
        //赞的次数
        vh.zan.setText("赞(" + list.get(position).getVote_num() + ")");
        if (TextUtils.isEmpty(list.get(position).getImage_url())) {//提问内容中没有图片
            vh.ivQuestion.setVisibility(View.GONE);
        } else {//提问内容中有图片
            vh.ivQuestion.setVisibility(View.VISIBLE);
            MyApplication.imageLoader.displayImage(list.get(position).getImage_url(), vh.ivQuestion, MyApplication.options);
        }
        //内容图片的点击事件
        vh.ivQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra("url", list.get(position).getImage_url());
                context.startActivity(intent);
            }
        });
        //头像
        MyApplication.imageLoader.displayImage(list.get(position).getStudent_avatar(), vh.imageView, MyApplication.options);
        //点赞的点击监听事件
        AutoLinearLayout llZan = (AutoLinearLayout) convertView.findViewById(R.id.ll_discuss_group_zan);
        llZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点赞
                zan(Urls.COURSE_DISCUSS_ZAN, position);
            }
        });
        //回复
        AutoLinearLayout llReply = (AutoLinearLayout) convertView.findViewById(R.id.ll_discuss_group_reply);
        llReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回复
                reply(position);
            }
        });
        //子listview
        mListView = (NoScrollListview) convertView.findViewById(R.id.lv_discuss_chirld);
//        List<CourseDiscuss.CourseDiscussBean.ReplyBean> chirldList = new ArrayList<>();
//        if(!"0".equals(list.get(position).getEvaluate_num())) {//有回复
//            //给list赋值
//            chirldList.addAll(list.get(position).getReply());
//            //如果没有回复则集合是空的
//        }
//        list.get(position).setReply(chirldList);
        CourseDiscussChirldAdapter adapter = new CourseDiscussChirldAdapter(context, list.get(position).getReply());
        mListView.setAdapter(adapter);

        return convertView;
    }

    private class ViewHolder {
        public ImageView imageView, ivQuestion;
        public TextView title, question, time, zan;
    }

    /**
     * 点赞
     */
    private void zan(String url, final int position) {
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j1 = new JSONObject(response);
                    if ("1".equals(j1.getString("status"))) {
                        //将新的点赞次数添加到list集合
                        CourseDiscuss.CourseDiscussBean course = list.get(position);
                        course.setVote_num(j1.getString("count"));
                        list.set(position, course);
                        notifyDataSetChanged();
                    } else {
                        CommonMethod.toast(context, "点赞失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.toast(context, "点赞失败请检查网络");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", list.get(position).getId());
                params.put("uid", CommonMethod.getUid(context));
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 回复
     */
    private void reply(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //自定义的对话框
        View view = View.inflate(context, R.layout.edittext_alertdialog, null);
        EditText reply = (EditText) view.findViewById(R.id.et_reply);
        Button cancle = (Button) view.findViewById(R.id.btn_reply_cancle);
        Button commit = (Button) view.findViewById(R.id.btn_reply_commit);
        final AlertDialog dialog = builder.create();
        dialog.setView(view);
        dialog.show();
        reply.addTextChangedListener(new TextChangeListener() {
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
                if (!TextUtils.isEmpty(content)) {
                    //提交
                    commit(Urls.COURSE_DISCUSS_REPLY, position);
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 提交
     *
     * @param url
     * @param position
     */
    private void commit(String url, final int position) {
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("1".equals(j1.getString("status"))) {
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
                                list.get(position).getReply().add(reply);
                                notifyDataSetChanged();
                            } else {
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", list.get(position).getId());
                params.put("uid", CommonMethod.getUid(context));
                params.put("type", "1");//回复，类型固定为1
                params.put("content", content);
                params.put("user_id", list.get(position).getUser_id());
                return params;
            }
        };
        mQueue.add(request);
    }
}

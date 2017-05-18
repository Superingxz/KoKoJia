package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.TeacherAnswerDetailsAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.AnswerDetails;
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
 * 课程详情里面的讨论页面
 */
public class TeacherAnswerDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mImageView,ivContent;
    private TextView nickName,content,time,zan;
    private AutoLinearLayout llZan,llReply;
    private ListView mListView;
    private List<AnswerDetails.CommentlistBean> list = new ArrayList<>();
    private String id,uid,userId,replyContent;
    private RequestQueue mQueue;
    private TeacherAnswerDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_teacher_answer_details);
        CommonMethod.showLoadingDialog("正在加载...",this);
        //设置标题
        setTitle("答疑详情");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherAnswerDetailsActivity.this.finish();
            }
        });

        mQueue = MyApplication.getRequestQueue();
        id = getIntent().getStringExtra("id");
        uid = CommonMethod.getUid(this);
        //初始化视图
        initView();
        //获取数据
        getData(Urls.teacherAnswerDetails(id,uid));

        adapter = new TeacherAnswerDetailsAdapter(list,this,id);
        mListView.setAdapter(adapter);
    }

    private void initView(){
        Typeface typeface = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(this.findViewById(R.id.layout_teacher_answer_details),typeface);
        mImageView = (ImageView) findViewById(R.id.iv_answer_details_user);
        ivContent = (ImageView) findViewById(R.id.iv_answer_details_content);
        nickName = (TextView) findViewById(R.id.tv_answer_details_nickName);
        content = (TextView) findViewById(R.id.tv_answer_details_content);
        time = (TextView) findViewById(R.id.tv_answer_details_time);
        zan = (TextView) findViewById(R.id.tv_answer_details_zan);
        mListView = (ListView) findViewById(R.id.lv_answer_details);
        llZan = (AutoLinearLayout) findViewById(R.id.ll_answer_details_zan);
        llReply = (AutoLinearLayout) findViewById(R.id.ll_answer_details_reply);
        // 设置点击监听
        llZan.setOnClickListener(this);
        llReply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_answer_details_zan://赞
                zan();
                break;
            case R.id.ll_answer_details_reply://回复
                if(uid.equals(userId)){
                    CommonMethod.showAlerDialog("回复失败","不能回复自己",this);
                }else{
                    reply();
                }
                break;
            default:
                break;
        }
    }

    private void getData(String url){
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j1 = new JSONObject(response);
                    if("1".equals(j1.getString("status"))){
                        Gson gson = new Gson();
                        AnswerDetails details = gson.fromJson(response,AnswerDetails.class);
                        userId = details.getData().getUser_id();
                        nickName.setText(details.getData().getStudent_nickname());//昵称
                        content.setText(details.getData().getContent());//提问的内容
                        time.setText(details.getData().getTime());// 时间
                        zan.setText("赞(" + details.getData().getVote_num() + ")");//赞的次数
                        if(TextUtils.isEmpty(details.getData().getImage_url())){
                            //如果是空的说明内容中没有图片
                            ivContent.setVisibility(View.GONE);
                        }else{
                            //有图片
                            ivContent.setVisibility(View.VISIBLE);
                            MyApplication.imageLoader.displayImage(details.getData().getImage_url(),ivContent,MyApplication.options);
                        }
                        MyApplication.imageLoader.displayImage(details.getData().getStudent_avatar(), mImageView, MyApplication.options);//头像
                        //获取回复的信息
                        if(details.getCommentlist()==null){
                            List<AnswerDetails.CommentlistBean> temp = new ArrayList<>();
                            list.addAll(temp);
                        }else{
                            list.addAll(details.getCommentlist());
                        }
                        adapter.notifyDataSetChanged();
                    }else{
                        CommonMethod.toast(TeacherAnswerDetailsActivity.this,j1.optString("msg"));
                    }
                    CommonMethod.dismissLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(TeacherAnswerDetailsActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        });
        mQueue.add(request);
    }

    /**
     * 赞
     */
    private void zan(){
        StringRequest request = new StringRequest(Request.Method.POST,Urls.COURSE_DISCUSS_ZAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                zan.setText("赞(" + j1.getString("count") + ")");
                            }else{
                                CommonMethod.toast(TeacherAnswerDetailsActivity.this,j1.optString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(TeacherAnswerDetailsActivity.this);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",id);
                params.put("uid",uid);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 回复
     */
    private void reply(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //自定义的对话框
        View view  = View.inflate(this,R.layout.edittext_alertdialog,null);
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
                replyContent = s.toString();
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
                if(!TextUtils.isEmpty(replyContent)) {
                    //提交
                    commit(Urls.COURSE_DISCUSS_REPLY);
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 回复
     */
    private void commit(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                JSONObject j2 = j1.getJSONObject("data");
                                AnswerDetails.CommentlistBean bean = new AnswerDetails.CommentlistBean();
                                bean.setStudent_nickname(j2.getString("student_nickname"));
                                bean.setStudent_avatar(j2.getString("student_avatar"));
                                bean.setTime(j2.getString("time"));
                                bean.setReply_name(j2.getString("reply_name"));
                                bean.setReply_id(j2.getString("reply_id"));
                                bean.setContent(j2.getString("content"));
                                list.add(bean);
                                adapter.notifyDataSetChanged();
                            }else{
                                CommonMethod.showAlerDialog("回复失败",j1.optString("msg"),TeacherAnswerDetailsActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(TeacherAnswerDetailsActivity.this);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",id);
                params.put("uid",uid);
                params.put("type","1");//回复，类型固定为1
                params.put("content",replyContent);
                params.put("user_id",userId);
                return params;
            }
        };
        mQueue.add(request);
    }

}

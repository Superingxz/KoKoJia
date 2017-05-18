package com.nuoxian.kokojia.fragment;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableExpandableListView;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.CourseDiscussAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseDiscuss;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/14.
 * 课程讨论
 */
public class CourseDiscussFragment extends Fragment {

    private PullToRefreshLayout layout;
    private PullableListView mListView;
    private String id,uid,talkingContent;
    private int page=1;
    private List<CourseDiscuss.CourseDiscussBean> list;
    private CourseDiscussAdapter adapter;
    private RequestQueue mQueue;
    private AutoLinearLayout sendTalking,llNoContent;
    private int count = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_discuss,container,false);
        //获取参数
        id = getArguments().getString("id");
        uid = CommonMethod.getUid(getContext());
        mQueue = MyApplication.getRequestQueue();
        page = 1;

        initView(view);
        //加载适配器
        list = new ArrayList<>();
        adapter = new CourseDiscussAdapter(getContext(),list);
        mListView.setAdapter(adapter);
        //获取数据
        getData(Urls.courseDiscussUrls(id, uid, page), CommonValues.NOT_TO_DO, null);

        return view;
    }

    private void initView(View v){
        sendTalking = (AutoLinearLayout) v.findViewById(R.id.ll_course_discuss_sendtalking);
        Typeface typeFace = FontManager.getTypeface(getContext(),FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(sendTalking,typeFace);
        layout = (PullToRefreshLayout) v.findViewById(R.id.layout_refresh);
        mListView = (PullableListView) v.findViewById(R.id.lv_course_discuss);
        llNoContent = (AutoLinearLayout) v.findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,getContext());
        //上下拉刷新监听
        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                list.clear();
                getData(Urls.courseDiscussUrls(id, uid, page), CommonValues.TO_REFRESH, layout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(Urls.courseDiscussUrls(id, uid, page), CommonValues.TO_LOAD, layout);
                adapter.notifyDataSetChanged();
            }
        });
        //发表讨论
        sendTalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发表讨论
                sendTalking();
            }
        });
    }

    /**
     * 获取数据
     * @param url
     */
    private void getData(String url, final int TO_DO, final PullToRefreshLayout layout){
        final List<CourseDiscuss.CourseDiscussBean> tempList = new ArrayList<>();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    llNoContent.setVisibility(View.GONE);
                    JSONObject j1 = new JSONObject(response);
                    if("1".equals(j1.getString("status"))){
                        //解析数据
                        JSONArray j2 = j1.getJSONArray("course_discuss");
                        for(int i=0;i<j2.length();i++){
                            JSONObject j3 = j2.getJSONObject(i);
                            CourseDiscuss.CourseDiscussBean course = new CourseDiscuss.CourseDiscussBean();
                            course.setId(j3.getString("id"));
                            course.setUser_id(j3.getString("user_id"));
                            course.setCourse_id(j3.getString("course_id"));
                            course.setCourse_lesson_id(j3.getString("course_lesson_id"));
                            course.setContent(j3.getString("content"));
                            course.setImage_url(j3.getString("image_url"));
                            course.setTime(j3.getString("time"));
                            course.setVote_num(j3.getString("vote_num"));
                            course.setEvaluate_num(j3.getString("evaluate_num"));
                            course.setNickname(j3.getString("nickname"));
                            course.setStudent_avatar(j3.getString("student_avatar"));
                            course.setLesson_title(j3.getString("lesson_title"));
                            JSONArray j4 = j3.optJSONArray("reply");
                            List<CourseDiscuss.CourseDiscussBean.ReplyBean> replyList = new ArrayList<>();
                            if(j4!=null){
                                for (int j=0;j<j4.length();j++){
                                    JSONObject j5 = j4.getJSONObject(j);
                                    CourseDiscuss.CourseDiscussBean.ReplyBean reply = new CourseDiscuss.CourseDiscussBean.ReplyBean();
                                    reply.setId(j5.getString("id"));
                                    reply.setUser_id(j5.getString("user_id"));
                                    reply.setDiscuss_id(j5.getString("discuss_id"));
                                    reply.setReply_id(j5.getString("reply_id"));
                                    reply.setContent(j5.getString("content"));
                                    reply.setTime(j5.getString("time"));
                                    reply.setNickname(j5.getString("nickname"));
                                    reply.setStudent_avatar(j5.getString("student_avatar"));
                                    reply.setReply_name(j5.getString("reply_name"));
                                    replyList.add(reply);
                                }
                            }
                            course.setReply(replyList);
                            tempList.add(course);
                        }
                        list.addAll(tempList);
                    }else{
                        if(TO_DO!=CommonValues.TO_LOAD){
                            llNoContent.setVisibility(View.VISIBLE);
                        }else {
                            CommonMethod.toast(getContext(),"没有了~");
                        }
                    }
                    adapter.notifyDataSetChanged();
                    CommonMethod.pullToRefreshSuccess(TO_DO,layout);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(getContext());
                CommonMethod.pullToRefreshFail(TO_DO,layout);
            }
        });
        mQueue.add(request);
    }

    /**
     * 发表讨论
     */
    private void sendTalking(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        //自定义的对话框
        View view  = View.inflate(getContext(),R.layout.edittext_alertdialog,null);
        EditText reply = (EditText) view.findViewById(R.id.et_reply);
        Button cancle = (Button) view.findViewById(R.id.btn_reply_cancle);
        final Button commit = (Button) view.findViewById(R.id.btn_reply_commit);
        final AlertDialog dialog = builder.create();
        dialog.setView(view);
        dialog.show();
        reply.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                //获取用户输入的内容
                talkingContent = s.toString();
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
                if (!TextUtils.isEmpty(talkingContent)) {
                    dialog.dismiss();
                    //提交
                    commit(Urls.SEND_TALKING);
                }
            }
        });
    }

    /**
     *
     * @param url
     */
    private void commit(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //发表成功，刷新数据
                                page = 1;
                                list.clear();
                                getData(Urls.courseDiscussUrls(id, uid, page), CommonValues.TO_REFRESH, layout);
                                adapter.notifyDataSetChanged();
                            }else{
                                //发表失败
                                CommonMethod.showAlerDialog("发表失败",j1.optString("msg"),getContext());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(getContext());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("content",talkingContent);
                params.put("course_id",id);
                params.put("type","1");
                return params;
            }
        };
        mQueue.add(request);
    }
}

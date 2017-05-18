package com.nuoxian.kokojia.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.DetailsCommentAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseComment;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
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
 * Created by Administrator on 2016/7/8.
 * 课程评价
 */
public class CourseCommentFragment extends Fragment implements View.OnClickListener {

    private View view;
    private PullableListView listView;
    private PullToRefreshLayout layout;
    private AutoLinearLayout llReload;
    private List<CourseComment> commentList;
    private DetailsCommentAdapter adapter;
    private String id,uid;
    private int page=1;
    private int loadCount=0;//加载成功的次数
    private AutoLinearLayout llNoContent,llSendComment;
    private TextView hint,star1,star2,star3,star4,star5,starHint,cancle,commit;
    private EditText content;
    private String rate;//选中的星星数
    private String commentContent;//评论的内容

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_comment, container, false);
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        uid = CommonMethod.getUid(getContext());
        //初始化视图
        initView();

        commentList = new ArrayList<>();
        adapter = new DetailsCommentAdapter(commentList,getContext());
        listView.setAdapter(adapter);
        //获取数据
        getData(Urls.getCourseCommentUrl(id, uid, page), CommonValues.NOT_TO_DO, null);
        //设置监听
        setListener();
        return view;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        listView = (PullableListView) view.findViewById(R.id.lv_details_comment);
        layout = (PullToRefreshLayout) view.findViewById(R.id.comment_layout_refresh);
        llReload = (AutoLinearLayout) view.findViewById(R.id.ll_reload);
        llNoContent = (AutoLinearLayout) view.findViewById(R.id.ll_no_content);
        llSendComment = (AutoLinearLayout) view.findViewById(R.id.ll_course_comment_sendcomment);
        CommonMethod.setFontAwesome(llNoContent, getContext());
        CommonMethod.setFontAwesome(llSendComment, getContext());
    }

    /**
     * 设置监听
     */
    private void setListener(){
        llReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新加载数据
                getData(Urls.getCourseCommentUrl(id,uid,page), CommonValues.NOT_TO_DO,null);
                //隐藏重新加载数据
                llReload.setVisibility(View.GONE);
            }
        });

        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                getData(Urls.getCourseCommentUrl(id, uid, page), CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page++;
                getData(Urls.getCourseCommentUrl(id, uid, page), CommonValues.TO_LOAD, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
        //发表评论
        llSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示评价窗口
                showCommentDialog();
            }
        });
    }

    /**
     * 显示评价窗口
     */
    private void showCommentDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_send_comment, null);
        CommonMethod.setFontAwesome(view.findViewById(R.id.layout_send_comment),getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        hint = (TextView) view.findViewById(R.id.tv_comment_hint);//错误提示
        star1 = (TextView) view.findViewById(R.id.star1);
        star1.setOnClickListener(this);
        star2 = (TextView) view.findViewById(R.id.star2);
        star2.setOnClickListener(this);
        star3 = (TextView) view.findViewById(R.id.star3);
        star3.setOnClickListener(this);
        star4 = (TextView) view.findViewById(R.id.star4);
        star4.setOnClickListener(this);
        star5 = (TextView) view.findViewById(R.id.star5);
        star5.setOnClickListener(this);
        starHint = (TextView) view.findViewById(R.id.tv_star_hint);//评分
        content = (EditText) view.findViewById(R.id.et_comment);//评论内容
        content.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                //获取用户输入的评价内容
                commentContent = s+"";
            }
        });
        cancle = (Button) view.findViewById(R.id.btn_comment_cancle);//取消
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        commit = (Button) view.findViewById(R.id.btn_comment_comit);//提交
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInputOk()){
                    hint.setVisibility(View.GONE);
                    dialog.dismiss();
                    //提交
                    commit();
                }
            }
        });
    }

    /**
     * 输入是否正确
     */
    private boolean isInputOk(){
        if(TextUtils.isEmpty(rate)){
            hint.setText("您忘记给课程评分咯!");
            hint.setVisibility(View.VISIBLE);
            return false;
        }
        if(TextUtils.isEmpty(commentContent)){
            hint.setText("请输入评价的内容!");
            hint.setVisibility(View.VISIBLE);
            return  false;
        }

        return true;
    }

    /**
     * 提交评价
     */
    private void commit(){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.SEND_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //刷新
                                commentList.clear();
                                page = 1;
                                getData(Urls.getCourseCommentUrl(id, uid, page), CommonValues.NOT_TO_DO, null);
                                adapter.notifyDataSetChanged();
                            }else{
                                CommonMethod.showAlerDialog("",j1.getString("msg"),getContext());
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
                params.put("id",id);
                params.put("uid",uid);
                params.put("rate",rate);
                params.put("content",commentContent);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 获取数据
     * @param url
     */
    private void getData(String url, final int TO_DO, final PullToRefreshLayout pullToRefreshLayout){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                llNoContent.setVisibility(View.GONE);
                //加载成功，加载次数+1
                loadCount++;
                if(TO_DO==CommonValues.TO_REFRESH){
                    //如果是刷新数据,先清空list
                    commentList.clear();
                }
                parseJson(response,TO_DO);
                CommonMethod.pullToRefreshSuccess(TO_DO,pullToRefreshLayout);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"加载失败，请检查网络!",Toast.LENGTH_SHORT).show();
                CommonMethod.pullToRefreshFail(TO_DO,pullToRefreshLayout);
                if(loadCount<1){
                    //显示重新加载
                    llReload.setVisibility(View.VISIBLE);
                }
            }
        });
        mQueue.add(request);
    }

    /**
     * 解析json数据
     * @param json
     */
    private void parseJson(String json,int TO_DO){
        List<CourseComment> tempList = new ArrayList<>();
        try {
            JSONObject j1 = new JSONObject(json);
            if("1".equals(j1.getString("status"))){
                JSONArray j2 = j1.getJSONArray("course_comment");
                for(int i=0;i<j2.length();i++){
                    CourseComment courseComment = new CourseComment();
                    JSONObject j3 = j2.getJSONObject(i);
                    courseComment.setId(j3.getString("id"));
                    courseComment.setContent(j3.getString("content"));
                    courseComment.setAvatar(j3.getString("avatar"));
                    courseComment.setRate(j3.getString("rate"));
                    courseComment.setStar_name(j3.getString("star_name"));
                    courseComment.setTime(j3.getString("time"));
                    tempList.add(courseComment);
                }
                commentList.addAll(tempList);
                adapter.notifyDataSetChanged();
            }else if("2".equals(j1.getString("status"))){
                if(TO_DO!=CommonValues.TO_LOAD){
                    llNoContent.setVisibility(View.VISIBLE);
                }else {
                    CommonMethod.toast(getContext(),"暂无评价");
                }
            }else{
                Toast.makeText(getContext(),"没有更多评价",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        unCheckAllStar();
        switch (v.getId()){
            case R.id.star1://一颗星
                star1.setText(R.string.solid_star);
                starHint.setText("较差");
                rate = "1";
                break;
            case R.id.star2://二颗星
                star1.setText(R.string.solid_star);
                star2.setText(R.string.solid_star);
                starHint.setText("一般");
                rate = "2";
                break;
            case R.id.star3://三颗星
                star1.setText(R.string.solid_star);
                star2.setText(R.string.solid_star);
                star3.setText(R.string.solid_star);
                starHint.setText("良好");
                rate = "3";
                break;
            case R.id.star4://四颗星
                star1.setText(R.string.solid_star);
                star2.setText(R.string.solid_star);
                star3.setText(R.string.solid_star);
                star4.setText(R.string.solid_star);
                starHint.setText("推荐");
                rate = "4";
                break;
            case R.id.star5://五颗星
                star1.setText(R.string.solid_star);
                star2.setText(R.string.solid_star);
                star3.setText(R.string.solid_star);
                star4.setText(R.string.solid_star);
                star5.setText(R.string.solid_star);
                starHint.setText("极佳");
                rate = "5";
                break;
        }
    }

    /**
     * 让所有星星没选中
     */
    private void unCheckAllStar(){
        star1.setText(R.string.star);
        star2.setText(R.string.star);
        star3.setText(R.string.star);
        star4.setText(R.string.star);
        star5.setText(R.string.star);
    }
}

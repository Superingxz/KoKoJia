package com.nuoxian.kokojia.fragment;

import android.app.AlertDialog;
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
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.CourseDiscussAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseDiscuss;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLinearLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/21.
 * 播放  讨论
 */
public class PlayerDiscussFragment extends Fragment {

    private PullableListView mListView;
    private PullToRefreshLayout mLayout;
    private AutoLinearLayout llSendTalking, llNoContent;
    private List<CourseDiscuss.CourseDiscussBean> mList;
    private CourseDiscussAdapter adapter;
    private String uid, id, lid;
    private int page;
    private RequestQueue mQueue;
    private String talkingContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_discuss, container, false);

        mQueue = MyApplication.getRequestQueue();
        initView(view);
        //获取参数
        getValues();

        mList = new ArrayList<>();
        adapter = new CourseDiscussAdapter(getContext(), mList);
        mListView.setAdapter(adapter);
        //获取数据
        getData(page, CommonValues.NOT_TO_DO, null);
        //设置监听
        setListener();
        return view;
    }

    private void initView(View v) {
        CommonMethod.setFontAwesome(v.findViewById(R.id.layout_fragment_player_discuss), getContext());
        mLayout = (PullToRefreshLayout) v.findViewById(R.id.layout_refresh);
        mListView = (PullableListView) v.findViewById(R.id.lv_player_discuss);
        llSendTalking = (AutoLinearLayout) v.findViewById(R.id.ll_player_sendtalking);
        llNoContent = (AutoLinearLayout) v.findViewById(R.id.ll_no_content);
    }

    /**
     * 获取参数
     */
    private void getValues() {
        Bundle bundle = getArguments();
        uid = bundle.getString("uid");
        id = bundle.getString("id");
        lid = bundle.getString("lid");
        page = 1;
    }

    private void setListener() {
        mLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                mList.clear();
                getData(page, CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(page,CommonValues.TO_LOAD,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
        //发表评论
        llSendTalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTalking();
            }
        });
    }

    /**
     * 获取数据
     *
     * @param page
     * @param TO_DO
     * @param layout
     */
    private void getData(int page, final int TO_DO, final PullToRefreshLayout layout) {
        StringRequest request = new StringRequest(Urls.playDiscuss(id, uid, page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if ("1".equals(j1.getString("status"))) {
                                Gson gson = new Gson();
                                CourseDiscuss discuss = gson.fromJson(response, CourseDiscuss.class);
                                mList.addAll(discuss.getCourse_discuss());
                                adapter.notifyDataSetChanged();
                            } else {
                                if (TO_DO != CommonValues.TO_LOAD) {
                                    llNoContent.setVisibility(View.VISIBLE);
                                } else {
                                    CommonMethod.toast(getContext(), "没有了~");
                                }
                            }
                            CommonMethod.pullToRefreshSuccess(TO_DO, layout);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(getContext());
                CommonMethod.pullToRefreshFail(TO_DO, layout);
            }
        });
        mQueue.add(request);
    }

    /**
     * 发表讨论
     */
    private void sendTalking() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        //自定义的对话框
        View view = View.inflate(getContext(), R.layout.edittext_alertdialog, null);
        EditText reply = (EditText) view.findViewById(R.id.et_reply);
        Button cancle = (Button) view.findViewById(R.id.btn_reply_cancle);
        final Button commit = (Button) view.findViewById(R.id.btn_reply_commit);
        final AlertDialog dialog = builder.create();
        dialog.setView(view);
        dialog.show();
        reply.addTextChangedListener(new TextChangeListener() {
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
                                mList.clear();
                                getData(page, CommonValues.NOT_TO_DO, null);
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
                params.put("lesson_id",lid);
                params.put("type","1");
                return params;
            }
        };
        mQueue.add(request);
    }
}

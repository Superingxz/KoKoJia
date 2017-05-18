package com.nuoxian.kokojia.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
import com.nuoxian.kokojia.activity.TeacherAnswerDetailsActivity;
import com.nuoxian.kokojia.adapter.MyQuestionAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.MyQuestion;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/26.
 * 我的提问
 */
public class MyQuestionFragment extends Fragment {

    private PullToRefreshLayout layout;
    private PullableListView mListView;
    private String uid;
    private static String TYPE = "0";
    private int page = 1;
    private List<MyQuestion.DataBean> mList;
    private MyQuestionAdapter adapter;
    private AutoLinearLayout llNoContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_question,container,false);
        uid = CommonMethod.getUid(getContext());

        initView(view);

        mList = new ArrayList<>();
        adapter = new MyQuestionAdapter(mList,getContext());
        mListView.setAdapter(adapter);

        getData(Urls.MY_QUESTION, CommonValues.NOT_TO_DO, null);
        return view;
    }

    private void initView(View v){
        llNoContent = (AutoLinearLayout) v.findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,getContext());
        layout = (PullToRefreshLayout) v.findViewById(R.id.layout_refresh);
        mListView = (PullableListView) v.findViewById(R.id.lv_my_question);

        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                mList.clear();
                getData(Urls.MY_QUESTION, CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(Urls.MY_QUESTION,CommonValues.TO_LOAD,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
        //listview item监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到答疑详情页面
                Intent intent = new Intent(getContext(), TeacherAnswerDetailsActivity.class);
                intent.putExtra("id",mList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    /**
     * 获取数据
     * @param url
     * @param TO_DO
     * @param layout
     */
    private void getData(String url, final int TO_DO, final PullToRefreshLayout layout){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethod.pullToRefreshSuccess(TO_DO,layout);
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                Gson gson = new Gson();
                                MyQuestion question = gson.fromJson(response,MyQuestion.class);
                                mList.addAll(question.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(TO_DO!=CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else {
                                    CommonMethod.toast(getContext(),"没有了~");
                                }
                            }
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("type",TYPE);
                params.put("page",page+"");
                return params;
            }
        };
        mQueue.add(request);
    }
}

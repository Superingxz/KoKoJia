package com.nuoxian.kokojia.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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
import com.nuoxian.kokojia.adapter.MyCourseFragmentAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.FragmentMyCourse;
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
 * Created by Administrator on 2016/9/2.
 * 课程管理回收站
 */
public class RecycleBinFragment extends Fragment {

    private View view;
    private PullToRefreshLayout layout;
    private PullableListView mListView;
    private AutoLinearLayout llNoContent;
    private EditText etSearch;
    private TextView tvSearch;
    private MyCourseFragmentAdapter adapter;
    private List<FragmentMyCourse.DataBean> list = new ArrayList<>();
    private String uid,word="",searchWord="";
    private int page =1;
    private static String status = "8";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recycle_bin,container,false);
        //初始化视图
        iniView();
        uid = CommonMethod.getUid(getContext());
        getData(uid,page, CommonValues.NOT_TO_DO,null);

        adapter = new MyCourseFragmentAdapter(list,getContext());
        mListView.setAdapter(adapter);
        return view;
    }

    /**
     * 初始化视图
     */
    private void iniView(){
        etSearch = (EditText) view.findViewById(R.id.et_recycle_bin_search);
        tvSearch = (TextView) view.findViewById(R.id.tv_recycle_bin_search);
        layout = (PullToRefreshLayout) view.findViewById(R.id.layout_refresh);
        mListView = (PullableListView) view.findViewById(R.id.lv_recycle_bin);
        llNoContent = (AutoLinearLayout) view.findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,getContext());
        //获取用户输入的搜索关键字
        etSearch.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                searchWord = s+"";
            }
        });
        //对软键盘中的搜索按钮进行监听
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    CommonMethod.hideInput(getActivity(), etSearch);
                    //搜索
                    search();
                }
                return false;
            }
        });
        //点击搜索按钮
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //搜索
                search();
            }
        });
        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                list.clear();
                getData(word, page, CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(word, page, CommonValues.TO_LOAD, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获取数据
     */
    private void getData(final String word, final int page, final int TO_DO, final PullToRefreshLayout layout){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.MY_SCHOOL_MY_COURSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                Gson gson = new Gson();
                                FragmentMyCourse course = gson.fromJson(response,FragmentMyCourse.class);
                                list.addAll(course.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(TO_DO!=CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
                                    CommonMethod.toast(getContext(),"没有了~");
                                }
                            }
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("status",status);
                params.put("word",word);
                params.put("page",page+"");
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 搜索
     */
    private void search(){
        list.clear();
        page = 1;
        word = searchWord;
        getData(word,page,CommonValues.NOT_TO_DO,null);
        adapter.notifyDataSetChanged();
    }
}

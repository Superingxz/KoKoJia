package com.nuoxian.kokojia.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
import com.nuoxian.kokojia.activity.PackageDetailsActivity;
import com.nuoxian.kokojia.adapter.MyPackageAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.MyPackage;
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
 * Created by Administrator on 2016/9/6.
 */
public class MyPackageRecycleBinFragment extends Fragment {

    private EditText etSearch;
    private TextView tvSearch;
    private PullToRefreshLayout layout;
    private PullableListView mListView;
    private MyPackageAdapter adapter;
    private String uid,word="",searchWord;
    private int page = 1;
    private static String status = "8";
    private List<MyPackage.DataBean> list;
    private AutoLinearLayout llNoContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_my_package_recycle_bin,container,false);

        uid = CommonMethod.getUid(getContext());

        initView(view);
        //设置监听
        setListener();

        list = new ArrayList<>();
        adapter = new MyPackageAdapter(list,getContext());
        mListView.setAdapter(adapter);
        //获取数据
        getData(uid,page, CommonValues.NOT_TO_DO);
        return view;
    }

    private void initView(View v){
        etSearch = (EditText) v.findViewById(R.id.et_recycle_bin_search);
        tvSearch = (TextView) v.findViewById(R.id.tv_recycle_bin_search);
        layout = (PullToRefreshLayout) v.findViewById(R.id.layout_refresh);
        mListView = (PullableListView) v.findViewById(R.id.lv_recycle_bin);
        llNoContent = (AutoLinearLayout) v.findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,getContext());
    }

    private void setListener(){
        //获取输入的搜索关键字
        etSearch.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                searchWord = s+"";
            }
        });
        //对软键盘的搜索监听
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //搜索
                    search();
                }
                return false;
            }
        });
        //点击搜索监听
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //搜索
                search();
            }
        });
        //listview的item点击时间
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), PackageDetailsActivity.class);
                intent.putExtra("id",list.get(position).getId());
                startActivity(intent);
            }
        });
        //上下拉刷新
        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                list.clear();
                getData(word, page, CommonValues.TO_REFRESH);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(word,page,CommonValues.TO_LOAD);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获取数据
     * @param word
     * @param page
     * @param TO_DO
     */
    private void getData(final String word, final int page, final int TO_DO){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.MY_SCHOOL_MY_PACKAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                Gson gson = new Gson();
                                MyPackage myPackage = gson.fromJson(response,MyPackage.class);
                                list.addAll(myPackage.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(TO_DO!=CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else {
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
                CommonMethod.pullToRefreshSuccess(TO_DO,layout);
                CommonMethod.loadFailureToast(getContext());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
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
        getData(word,page,CommonValues.NOT_TO_DO);
        adapter.notifyDataSetChanged();
    }
}

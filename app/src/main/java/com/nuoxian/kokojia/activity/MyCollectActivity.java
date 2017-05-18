package com.nuoxian.kokojia.activity;

/**
 * 我的收藏
 */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MyCollectAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.MyCollect;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCollectActivity extends BaseActivity {

    private List<MyCollect> mList = new ArrayList<>();
    private PullToRefreshLayout layout;
    private PullableListView mListView;
    private MyCollectAdapter adapter;
    private String uid;
    private int page=1;
    private AutoLinearLayout llNoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_my_collect);
        //设置标题
        setTitle("我的收藏");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCollectActivity.this.finish();
            }
        });

        uid = CommonMethod.getUid(this);
        //初始化视图
        initView();
        //获取数据
        getData(Urls.MY_COLLECT, CommonValues.NOT_TO_DO,null);

        adapter = new MyCollectAdapter(mList,this);
        mListView.setAdapter(adapter);
    }

    private void initView(){
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,this);
        layout = (PullToRefreshLayout) findViewById(R.id.layout_refresh);
        mListView = (PullableListView) findViewById(R.id.lv_my_collect);

        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page=1;
                mList.clear();
                getData(Urls.MY_COLLECT, CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(Urls.MY_COLLECT,CommonValues.TO_LOAD,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情页面
                Intent intent = new Intent(MyCollectActivity.this,CourseDetailsActivity.class);
                intent.putExtra("id",mList.get(position).getCourse_id());
                startActivity(intent);
            }
        });
    }

    /**
     * 获取数据
     */
    private void getData(String url, final int TO_DO, final PullToRefreshLayout layout){
        final List<MyCollect> tempList = new ArrayList<>();
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
                                JSONArray j2 = j1.getJSONArray("data");
                                for(int i=0;i<j2.length();i++){
                                    JSONObject j3 = j2.getJSONObject(i);
                                    MyCollect collect = new MyCollect();
                                    collect.setId(j3.getString("id"));
                                    collect.setCourse_id(j3.getString("course_id"));
                                    collect.setTitle(j3.getString("title"));
                                    collect.setPrice(j3.getString("price"));
                                    collect.setImage_url(j3.getString("image_url"));
                                    collect.setLearn(j3.getString("learn"));
                                    collect.setIs_paid(j3.optString("is_paid"));
                                    collect.setDiscount_price(j3.optString("discount_price"));
                                    collect.setCountdown(j3.optString("countdown"));
                                    tempList.add(collect);
                                }
                                mList.addAll(tempList);
                                adapter.notifyDataSetChanged();
                            }else{
                                if(TO_DO!=CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else {
                                    CommonMethod.toast(MyCollectActivity.this,"没有了~");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(MyCollectActivity.this);
                CommonMethod.pullToRefreshFail(TO_DO,layout);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("page",page+"");
                return params;
            }
        };
        mQueue.add(request);
    }
}

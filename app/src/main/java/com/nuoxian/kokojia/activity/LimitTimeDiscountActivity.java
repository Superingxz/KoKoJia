package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.LimitTimeDiscountAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.LimitTimeDiscount;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 限时折扣页面
 */
public class LimitTimeDiscountActivity extends BaseActivity {

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private AutoLinearLayout llNoContent;
    private List<LimitTimeDiscount.DataBean> mList;
    private LimitTimeDiscountAdapter adapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_limit_time_discount);
        CommonMethod.showLoadingDialog("正在加载...", this);
        setTitle("限时折扣");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LimitTimeDiscountActivity.this.finish();
            }
        });
        //显示搜索按钮
        showSearchButton();

        initView();
        //设置适配器
        mList = new ArrayList<>();
        adapter = new LimitTimeDiscountAdapter(mList,this);
        mListView.setAdapter(adapter);
        //获取数据
        getData(page, CommonValues.NOT_TO_DO,null);
        //设置监听
        setListener();
    }

    private void initView(){
        mLayout = (PullToRefreshLayout) findViewById(R.id.layout_refresh);
        mListView = (PullableListView) findViewById(R.id.lv_limit_time_discount);
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,this);
    }

    /**
     * 设置监听
     */
    private void setListener(){
        mLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                // 刷新
                page = 1;
                mList.clear();
                getData(page, CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                // 加载
                page++;
                getData(page,CommonValues.TO_LOAD,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情页面
                Intent intent = new Intent(LimitTimeDiscountActivity.this,CourseDetailsActivity.class);
                intent.putExtra("id",mList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    /**
     * 获取数据
     * @param page
     * @param to_do
     * @param layout
     */
    private void getData(int page, final int to_do, final PullToRefreshLayout layout){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Urls.limitTimeDiscount(page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                llNoContent.setVisibility(View.GONE);
                                //获取数据
                                Gson gson = new Gson();
                                LimitTimeDiscount discount = gson.fromJson(response,LimitTimeDiscount.class);
                                mList.addAll(discount.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(to_do!=CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
                                    CommonMethod.toast(LimitTimeDiscountActivity.this,"没有了~");
                                }
                            }
                            CommonMethod.dismissLoadingDialog();
                            CommonMethod.pullToRefreshSuccess(to_do,layout);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.dismissLoadingDialog();
                CommonMethod.pullToRefreshFail(to_do,layout);
                CommonMethod.loadFailureToast(LimitTimeDiscountActivity.this);
            }
        });
        mQueue.add(request);
    }
}

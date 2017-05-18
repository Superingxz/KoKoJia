package com.nuoxian.kokojia.activity;

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
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.PayCenterRefundAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.RefundRecord;
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
 * 退款记录
 */
public class PayCenterRefundActivity extends BaseActivity {

    private PullableListView mListView;
    private PullToRefreshLayout mLayout;
    private List<RefundRecord.DataBean> mList;
    private PayCenterRefundAdapter adapter;
    private String uid;
    private int page = 1;
    private AutoLinearLayout llNoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_pay_center_refund);

        CommonMethod.showLoadingDialog("正在加载...",this);

        setTitle("退款记录");
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayCenterRefundActivity.this.finish();
            }
        });

        uid = CommonMethod.getUid(this);
        initView();
        //加载适配器
        mList = new ArrayList<>();
        adapter = new PayCenterRefundAdapter(mList,this);
        mListView.setAdapter(adapter);
        //获取数据
        getData(CommonValues.NOT_TO_DO,null);
        //设置监听
        setListener();
    }

    private void initView(){
        mListView = (PullableListView) findViewById(R.id.lv_pay_center_refund);
        mLayout = (PullToRefreshLayout) findViewById(R.id.layout_refresh);
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,this);
    }

    private void setListener(){

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情
                Intent intent = new Intent(PayCenterRefundActivity.this,CourseDetailsActivity.class);
                intent.putExtra("id",mList.get(position).getCourse_id());
                startActivity(intent);
            }
        });

        mLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                mList.clear();
                getData(CommonValues.TO_REFRESH,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(CommonValues.TO_LOAD,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getData(final int to_do, final PullToRefreshLayout layout){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest reques = new StringRequest(Request.Method.POST, Urls.PAY_CENTER_REFUND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                llNoContent.setVisibility(View.GONE);
                                //获取数据
                                Gson gson = new Gson();
                                RefundRecord record = gson.fromJson(response,RefundRecord.class);
                                mList.addAll(record.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(to_do!=CommonValues.TO_LOAD){//显示暂无内容
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
                                    CommonMethod.toast(PayCenterRefundActivity.this,"没有了~");
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
                CommonMethod.loadFailureToast(PayCenterRefundActivity.this);
                CommonMethod.dismissLoadingDialog();
                CommonMethod.pullToRefreshFail(to_do, layout);
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
        mQueue.add(reques);
    }
}

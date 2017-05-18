package com.nuoxian.kokojia.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
import com.nuoxian.kokojia.adapter.PayCenterRechargeAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.RechargeRecord;
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

/**
 * 充值记录
 */
public class PayCenterRechargeActivity extends BaseActivity {

    private PullableListView mListView;
    private PullToRefreshLayout mLayout;
    private String uid;
    private int page=1;
    private List<RechargeRecord.DataBean> mList;
    private PayCenterRechargeAdapter adapter;
    private RequestQueue mQueue;
    private AutoLinearLayout llNoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_pay_center_recharge);

        CommonMethod.showLoadingDialog("正在加载...",this);

        setTitle("充值记录");
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayCenterRechargeActivity.this.finish();
            }
        });

        mQueue = MyApplication.getRequestQueue();
        uid = CommonMethod.getUid(this);

        initView();

        mList = new ArrayList<>();
        adapter = new PayCenterRechargeAdapter(this,mList);
        mListView.setAdapter(adapter);

        //获取数据
        getData(CommonValues.NOT_TO_DO,null);
        //设置监听
        setListener();
    }

    private void initView(){
        mLayout = (PullToRefreshLayout) findViewById(R.id.layout_refresh);
        mListView = (PullableListView) findViewById(R.id.lv_pay_center_recharge);
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,this);
    }

    private void setListener(){
        mLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page=1;
                mList.clear();
                getData(CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(CommonValues.TO_LOAD, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getData(final int to_do, final PullToRefreshLayout layout){
        StringRequest request = new StringRequest(Request.Method.POST, Urls.PAY_CENTER_RECHARGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                llNoContent.setVisibility(View.GONE);

                                Gson gson = new Gson();
                                RechargeRecord record = gson.fromJson(response,RechargeRecord.class);
                                mList.addAll(record.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(to_do!=CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
                                    CommonMethod.toast(PayCenterRechargeActivity.this,"没有了~");
                                }
                            }
                            CommonMethod.pullToRefreshSuccess(to_do,layout);
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(PayCenterRechargeActivity.this);
                CommonMethod.pullToRefreshFail(to_do, layout);
                CommonMethod.dismissLoadingDialog();
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

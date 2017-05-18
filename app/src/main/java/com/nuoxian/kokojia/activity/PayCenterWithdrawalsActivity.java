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
import com.nuoxian.kokojia.adapter.PayCenterWithdrawalsAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.WithdrawalsRecord;
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
 * 支付中心--提现记录
 */
public class PayCenterWithdrawalsActivity extends BaseActivity {

    private PullableListView mListView;
    private PullToRefreshLayout mLayout;
    private AutoLinearLayout llNoContent;
    private String uid;
    private int page = 1;
    private List<WithdrawalsRecord.DataBean> mList;
    private PayCenterWithdrawalsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_pay_center_withdrawals);

        CommonMethod.showLoadingDialog("正在加载...", this);
        setTitle("提现记录");
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayCenterWithdrawalsActivity.this.finish();
            }
        });

        uid = CommonMethod.getUid(this);
        initView();
         //加载适配器
        mList = new ArrayList<>();
        adapter = new PayCenterWithdrawalsAdapter(mList,this);
        mListView.setAdapter(adapter);
        //获取数据
        getData(CommonValues.NOT_TO_DO,null);
        //设置监听
        setListener();
    }

    private void initView(){
        mListView = (PullableListView) findViewById(R.id.lv_pay_center_withdrawals);
        mLayout = (PullToRefreshLayout) findViewById(R.id.layout_refresh);
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,this);
    }

    private void setListener(){
        mLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
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
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.PAY_CENTER_WITHDRAWALS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                llNoContent.setVisibility(View.GONE);
                                //获取数据
                                Gson gson = new Gson();
                                WithdrawalsRecord record = gson.fromJson(response,WithdrawalsRecord.class);
                                mList.addAll(record.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(to_do!=CommonValues.TO_LOAD){
                                    //显示暂无内容
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
                                    CommonMethod.toast(PayCenterWithdrawalsActivity.this,"没有了~");
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
                CommonMethod.pullToRefreshFail(to_do,layout);
                CommonMethod.dismissLoadingDialog();
                CommonMethod.loadFailureToast(PayCenterWithdrawalsActivity.this);
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

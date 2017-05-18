package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
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
import com.nuoxian.kokojia.adapter.PackageDetailsAdapter;
import com.nuoxian.kokojia.adapter.PayCenterPayAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.PayRecord;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支出记录
 */
public class PayCenterPayActivity extends BaseActivity {

    private PullableListView listView;
    private PullToRefreshLayout layout;
    private String uid;
    private int page=1;
    private RequestQueue mQueue;
    private List<PayRecord.DataBean> mList;
    private PayCenterPayAdapter adapter;
    private AutoLinearLayout llNoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_pay_center_pay);

        CommonMethod.showLoadingDialog("正在加载...",this);

        setTitle("支出记录");
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayCenterPayActivity.this.finish();
            }
        });

        uid = CommonMethod.getUid(this);
        mQueue = MyApplication.getRequestQueue();
        mList = new ArrayList<>();
        initView();

        adapter = new PayCenterPayAdapter(mList,this);
        listView.setAdapter(adapter);

        //获取数据
        getData(CommonValues.NOT_TO_DO, null);

        //设置监听
        setListener();
    }

    private void initView(){
        listView = (PullableListView) findViewById(R.id.lv_pay_center_pay);
        layout = (PullToRefreshLayout) findViewById(R.id.layout_refresh);
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        Typeface typeface = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(llNoContent,typeface);
    }

    private void setListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if("1".equals(mList.get(position).getType())){
                    //跳转到课程页面
                    Intent intent = new Intent(PayCenterPayActivity.this,CourseDetailsActivity.class);
                    intent.putExtra("id",mList.get(position).getCourse_id());
                    startActivity(intent);
                }else if("2".equals(mList.get(position).getType())){
                    //跳转到套餐详情页面
                    Intent intent = new Intent(PayCenterPayActivity.this,PackageDetailsActivity.class);
                    intent.putExtra("id",mList.get(position).getCourse_id());
                    startActivity(intent);
                }else{
                    CommonMethod.toast(PayCenterPayActivity.this,"无法访问");
                }
            }
        });

        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
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

    /**
     * 获取数据
     * @param to_do
     * @param layout
     */
    private void getData(final int to_do, final PullToRefreshLayout layout){
        StringRequest request = new StringRequest(Request.Method.POST, Urls.PAY_CENTER_PAY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                llNoContent.setVisibility(View.GONE);

                                Gson gson = new Gson();
                                PayRecord record = gson.fromJson(response,PayRecord.class);
                                mList.addAll(record.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(to_do!=CommonValues.TO_LOAD){
                                    //第一次加载和刷新的时候，显示“暂无内容”
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
                                    CommonMethod.toast(PayCenterPayActivity.this,"没有了~");
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
                CommonMethod.loadFailureToast(PayCenterPayActivity.this);
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

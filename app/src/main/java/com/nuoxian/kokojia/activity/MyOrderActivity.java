package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MyOrderListAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.MyOrder;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的订单页面
 */
public class MyOrderActivity extends AutoLayoutActivity implements View.OnClickListener {

    private TextView ivSearch,ivBack;
    private PullToRefreshLayout layout;
    private AutoLinearLayout llReload,llNoContent;
    private PullableListView listView;
    private List<MyOrder> list = new ArrayList<>();
    private MyOrderListAdapter adapter;
    private RequestQueue mQueue;
    private String uid;
    private int page=1;
    private int loadCount=0;//加载成功的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        //设置标题栏的颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        //注册EventBus
        EventBus.getDefault().register(this);
        mQueue = MyApplication.getRequestQueue();
        uid = CommonMethod.getUid(this);
        //初始化视图
        initView();
        //设置适配器
        adapter = new MyOrderListAdapter(this,list);
        listView.setAdapter(adapter);
        //获取订单数据
        getData(Urls.MY_ORDER, CommonValues.NOT_TO_DO, null);
    }

    /**
     * 初始化视图
     */
    private void initView(){
        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        ivBack = (TextView) findViewById(R.id.iv_my_order_back);
        ivBack.setTypeface(iconFont);
        ivSearch = (TextView) findViewById(R.id.iv_my_order_search);
        ivSearch.setTypeface(iconFont);
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent, this);
        layout = (PullToRefreshLayout) findViewById(R.id.layout_my_order_refresh);
        listView = (PullableListView) findViewById(R.id.lv_my_order);
        llReload = (AutoLinearLayout) findViewById(R.id.ll_reload);
        //设置监听
        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        llReload.setOnClickListener(this);
        //listView的监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情页面
                Intent intent = new Intent(MyOrderActivity.this, CourseDetailsActivity.class);
                intent.putExtra("id", list.get(position).getCourseId());
                startActivity(intent);
            }
        });
        //上下拉刷新监听
        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {//刷新
                //将页码改为1
                page = 1;
                //获取数据
                getData(Urls.MY_ORDER, CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {//加载
                //页码加1
                page++;
                //获取数据
                getData(Urls.MY_ORDER, CommonValues.TO_LOAD, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_my_order_back://返回
                finish();
                break;
            case R.id.iv_my_order_search://搜索
                CommonMethod.startActivity(this,SearchActivity.class);
                break;
            case R.id.ll_reload://重新加载
                //获取订单数据
                getData(Urls.MY_ORDER,CommonValues.NOT_TO_DO,null);
                //隐藏重新加载
                llReload.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 获取订单数据
     * @param url
     */
    private void getData(String url,final int TO_DO, final PullToRefreshLayout pullToRefreshLayout){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //加载成功，加载成功次数+1
                        loadCount++;
                        if(TO_DO==CommonValues.TO_REFRESH){
                            //如果是刷新数据,先清空数据
                            list.clear();
                        }
                        //解析数据
                        parseJson(response,TO_DO);
                        //加载成功
                        CommonMethod.pullToRefreshSuccess(TO_DO,pullToRefreshLayout);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(MyOrderActivity.this);
                //加载失败
                CommonMethod.pullToRefreshFail(TO_DO,pullToRefreshLayout);
                if(loadCount<1){
                    //加载成功的次数小于1
                    llReload.setVisibility(View.VISIBLE);
                }
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

    /**
     * 解析json数据
     * @param json
     */
    private void parseJson(String json,int to_do){
        List<MyOrder> temp = new ArrayList<>();
        try {
            llNoContent.setVisibility(View.GONE);
            JSONObject j1 = new JSONObject(json);
            if("0".equals(j1.getString("status"))){
                if(to_do!=CommonValues.TO_LOAD){
                    llNoContent.setVisibility(View.VISIBLE);
                }else {
                    CommonMethod.toast(this,"没有了~");
                }
            }else if("1".equals(j1.getString("status"))){
                JSONArray j2 = j1.getJSONArray("data");
                for (int i=0;i<j2.length();i++){
                    JSONObject j3 = j2.getJSONObject(i);
                    MyOrder order = new MyOrder();
                    order.setCourseId(j3.getString("course_id"));
                    order.setPrice(j3.getString("price"));
                    order.setCreate_time(j3.getString("create_time"));
                    order.setImage_url(j3.getString("image_url"));
                    order.setStatus(j3.getString("status"));
                    order.setTitle(j3.getString("title"));
                    temp.add(order);
                }
                list.addAll(temp);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付购买后发过来的信息
     * @param event
     */
    public void onEventMainThread(BuyResult event) {
        if("ok".equals(event.getBuyStatus())){
            //如果购买成功,刷新界面信息
            page = 1;
            list.clear();
            getData(Urls.MY_ORDER, CommonValues.NOT_TO_DO, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册
        EventBus.getDefault().unregister(this);
    }
}

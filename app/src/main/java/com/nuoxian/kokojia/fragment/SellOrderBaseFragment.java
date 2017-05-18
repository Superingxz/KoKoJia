package com.nuoxian.kokojia.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MyPackageAdapter;
import com.nuoxian.kokojia.adapter.SellOrderAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/5.
 */
public class SellOrderBaseFragment extends Fragment {

    private PullToRefreshLayout layout;
    private PullableListView mListView;
    private AutoLinearLayout llNoContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_course_all,container,false);

        layout = (PullToRefreshLayout) view.findViewById(R.id.layout_refresh);
        mListView = (PullableListView) view.findViewById(R.id.lv_mycourse_all);
        llNoContent = (AutoLinearLayout) view.findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent, getContext());
        return view;
    }

    public void setAdapter(SellOrderAdapter adapter){
        mListView.setAdapter(adapter);
    }

    public void setAdapter(MyPackageAdapter adapter){
        mListView.setAdapter(adapter);
    }

    /**
     * 上下拉刷新监听
     * @param listener
     */
    public void setRefreshListener(PullToRefreshLayout.OnRefreshListener listener){
        layout.setOnRefreshListener(listener);
    }

    /**
     * 设置listview的item的点击事件
     * @param listener
     */
    public void setOnItemClicklistener(AdapterView.OnItemClickListener listener){
        mListView.setOnItemClickListener(listener);
    }

    /**
     * 获取订单数据
     * @param uid
     * @param word
     * @param status
     * @param page
     * @param TO_DO
     * @param callBack
     */
    public void getData(final String uid, final String word, final String status, final int page, final int TO_DO,final SellOrderCallBack callBack){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.MY_SCHOOL_SELL_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethod.pullToRefreshSuccess(TO_DO,layout);
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //获取数据后的操作
                                if(callBack!=null){
                                    callBack.success(response);
                                }
                            }else{
                                if(TO_DO!= CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
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
                CommonMethod.pullToRefreshFail(TO_DO,layout);
                CommonMethod.loadFailureToast(getContext());
                callBack.faile(error);
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
     * 获取套餐数据
     * @param uid
     * @param word
     * @param status
     * @param page
     * @param TO_DO
     * @param callBack
     */
    public void getPackageData(final String uid, final String word, final String status, final int page, final int TO_DO,final SellOrderCallBack callBack){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.MY_SCHOOL_MY_PACKAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethod.pullToRefreshSuccess(TO_DO,layout);
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                               //请求网络成功后的操作
                                if(callBack!=null){
                                    callBack.success(response);
                                }
                            }else{
                                if(TO_DO!= CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
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
                CommonMethod.pullToRefreshFail(TO_DO,layout);
                callBack.faile(error);
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

    public interface SellOrderCallBack{

        void success(String json);
        void faile(VolleyError error);

    }
}

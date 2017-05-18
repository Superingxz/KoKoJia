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
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.TeacherCouponAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.TeacherCoupon;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/30.
 */
public class TeacherCouponUnOverdueFragment extends Fragment {
    private PullableListView mListView;
    private PullToRefreshLayout layout;
    private String uid;
    private int page=1;
    private static String status = "1";
    private List<TeacherCoupon.DataBean> mList;
    private TeacherCouponAdapter adapter;
    private AutoLinearLayout llNoContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_teacher_coupon,container,false);
        uid = CommonMethod.getUid(getContext());

        initView(view);

        mList = new ArrayList<>();
        adapter = new TeacherCouponAdapter(mList,getContext());
        mListView.setAdapter(adapter);
        //获取数据
        getData(CommonValues.NOT_TO_DO, null);
        return view;
    }

    private void initView(View v){
        layout = (PullToRefreshLayout) v.findViewById(R.id.layout_refresh);
        mListView = (PullableListView) v.findViewById(R.id.lv_teacher_coupon);
        llNoContent = (AutoLinearLayout) v.findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,getContext());

        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
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
                getData(CommonValues.TO_LOAD,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 获取数据
     * @param TO_DO
     * @param layout
     */
    private void getData(final int TO_DO, final PullToRefreshLayout layout){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.TEACHER_COUPON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                Gson gson = new Gson();
                                TeacherCoupon coupon = gson.fromJson(response,TeacherCoupon.class);
                                mList.addAll(coupon.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(TO_DO!=CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
                                    CommonMethod.toast(getContext(),"没有了~");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        CommonMethod.pullToRefreshSuccess(TO_DO,layout);
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
                params.put("page",page+"");
                return params;
            }
        };
        mQueue.add(request);
    }
}

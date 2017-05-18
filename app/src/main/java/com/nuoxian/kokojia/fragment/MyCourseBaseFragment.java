package com.nuoxian.kokojia.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.nuoxian.kokojia.adapter.MyCourseFragmentAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.FragmentMyCourse;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/5.
 */
public class MyCourseBaseFragment extends Fragment {

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
        CommonMethod.setFontAwesome(llNoContent,getContext());

        return view;
    }

    public void setRefreshListener(PullToRefreshLayout.OnRefreshListener listener){
        layout.setOnRefreshListener(listener);
    }

    public void setAdapter(MyCourseFragmentAdapter adapter){
        mListView.setAdapter(adapter);
    }

    /**
     * 获取数据
     */
    public void getData(final String uid,final String status,final String word,final int page, final int TO_DO,final List<FragmentMyCourse.DataBean> list,final MyCourseFragmentAdapter adapter){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.MY_SCHOOL_MY_COURSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                Gson gson = new Gson();
                                FragmentMyCourse course = gson.fromJson(response,FragmentMyCourse.class);
                                list.addAll(course.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(TO_DO!= CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
                                    CommonMethod.toast(getContext(), "没有了~");
                                }
                            }
                            CommonMethod.pullToRefreshSuccess(TO_DO, layout);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("word",word);
                params.put("page",page+"");
                return params;
            }
        };
        mQueue.add(request);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        mListView.setOnItemClickListener(listener);
    }

}

package com.nuoxian.kokojia.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

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
import com.nuoxian.kokojia.adapter.TeacherAnswerAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.TeacherAnswer;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/19.
 */
public class BaseTeacherAnswerFragment extends Fragment {

    private PullableListView mListView;
    private PullToRefreshLayout layout;
    private RequestQueue mQueue;
    private AutoLinearLayout llNoContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_teacher_answer,container,false);

        layout = (PullToRefreshLayout) view.findViewById(R.id.layout_refresh);
        mListView = (PullableListView) view.findViewById(R.id.lv_teacher_answer);
        mQueue = MyApplication.getRequestQueue();
        llNoContent = (AutoLinearLayout) view.findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,getContext());

        return view;
    }

    /**
     * 设置上下拉刷新监听
     * @param listener
     */
    public void setOnRefreshListener(PullToRefreshLayout.OnRefreshListener listener){
        layout.setOnRefreshListener(listener);
    }

    /**
     * 设置适配器
     * @param adapter
     */
    public void setAdapter(BaseAdapter adapter){
        mListView.setAdapter(adapter);
    }

    public void setItemClickListener(AdapterView.OnItemClickListener listener){
        mListView.setOnItemClickListener(listener);
    }

    public void getData(String url, final int TO_DO,final List<TeacherAnswer.DataBean> mList,final TeacherAnswerAdapter adapter, final String status, final int page){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethod.pullToRefreshSuccess(TO_DO,layout);
                        CommonMethod.dismissLoadingDialog();
//                        if(successResponse!=null){
//                            successResponse.onSuccess(response);
//                        }
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //网络请求成功后的操作
//                                if(successResponse!=null){
//                                    successResponse.onSuccess(response);
//                                }
                                Gson gson = new Gson();
                                TeacherAnswer answer = gson.fromJson(response, TeacherAnswer.class);
                                mList.addAll(answer.getData());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(TO_DO!= CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else {
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
                CommonMethod.loadFailureToast(getContext());
                CommonMethod.pullToRefreshFail(TO_DO, layout);
                CommonMethod.dismissLoadingDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", CommonMethod.getUid(getContext()));
                params.put("status", status);
                params.put("page", page+"");
                return params;
            }
        };
        mQueue.add(request);
    }

    interface SuccessResponse{
        void onSuccess(String json);
    }

}

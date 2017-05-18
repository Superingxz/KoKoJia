package com.nuoxian.kokojia.fragment;

import android.content.Context;
import android.content.Intent;
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
import com.nuoxian.kokojia.activity.CourseDetailsActivity;
import com.nuoxian.kokojia.adapter.MyCourseFragmentAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.FragmentMyCourse;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/2.
 */
public class MyCourseAll extends MyCourseBaseFragment {

    private MyCourseFragmentAdapter adapter;
    private List<FragmentMyCourse.DataBean> list;
    private static String status = "0";
    private String word,uid;
    private int page=1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(MyCourseAll.this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        uid = CommonMethod.getUid(getContext());
        word = "";
        //加载适配器
        list = new ArrayList<>();
        adapter = new MyCourseFragmentAdapter(list,getContext());
        setAdapter(adapter);
        //获取数据
        getData(uid, status, word, page, CommonValues.NOT_TO_DO,list,adapter);
        //设置上下拉刷新监听
        setRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                list.clear();
                getData(uid, status, word, page, CommonValues.TO_REFRESH, list, adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(uid,status,word,page,CommonValues.TO_LOAD,list,adapter);
                adapter.notifyDataSetChanged();
            }
        });

        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (list.get(position).getStatus()){
                    case "2"://销售中
                        //跳转到详情页
                        Intent intent = new Intent(getContext(), CourseDetailsActivity.class);
                        intent.putExtra("id",list.get(position).getId());
                        startActivity(intent);
                        break;
                    case "1"://未发布
                        CommonMethod.showAlerDialog("", "该视频未发布,不可查看", getContext());
                        break;
                    case "8"://已删除
                        CommonMethod.showAlerDialog("","该视频已删除,不可查看",getContext());
                        break;
                    case "5"://已停售
                        CommonMethod.showAlerDialog("","该视频已停售,不可查看",getContext());
                        break;
                    case "4"://已过期
                        CommonMethod.showAlerDialog("","该视频已过期,不可查看",getContext());
                        break;
                }
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 接收EventBus传过来的数据
     *  更新数据
     * @param bundle
     */
    public void onEventMainThread(Bundle bundle) {
        if("MyCourseAll".equals(bundle.getString("fragment"))){
            if(!word.equals(bundle.getString("word"))){
                //word和传过来的搜索的关键词不同,说明要更新内容
                word = bundle.getString("word");
                page = 1;
                list.clear();
                getData(uid, status, word, page, CommonValues.NOT_TO_DO,list,adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MyCourseAll.this);
    }
}

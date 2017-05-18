package com.nuoxian.kokojia.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.nuoxian.kokojia.activity.TeacherAnswerDetailsActivity;
import com.nuoxian.kokojia.adapter.TeacherAnswerAdapter;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.TeacherAnswer;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/19.
 */
public class TeacherAnswerAll extends BaseTeacherAnswerFragment {

    private List<TeacherAnswer.DataBean> mList;
    private TeacherAnswerAdapter adapter;
    private int page = 1;
    private static String status = "0";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonMethod.showLoadingDialog("正在加载...", getContext());

        init();
        getData(Urls.TEACHER_ANSWER, CommonValues.NOT_TO_DO, mList, adapter, status, page);
    }

    private void init() {
        //设置适配器
        mList = new ArrayList<>();
        adapter = new TeacherAnswerAdapter(mList, getContext());
        setAdapter(adapter);
        //上下拉刷新
        setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                mList.clear();
                getData(Urls.TEACHER_ANSWER, CommonValues.TO_REFRESH, mList, adapter, status, page);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(Urls.TEACHER_ANSWER, CommonValues.TO_LOAD, mList, adapter, status, page);
                adapter.notifyDataSetChanged();
            }
        });

        setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到答疑详情页
                Intent intent = new Intent(getContext(), TeacherAnswerDetailsActivity.class);
                intent.putExtra("id", mList.get(position).getId());
                startActivity(intent);
            }
        });
    }

}

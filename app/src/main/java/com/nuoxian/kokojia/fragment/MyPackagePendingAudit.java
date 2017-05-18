package com.nuoxian.kokojia.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.nuoxian.kokojia.activity.PackageDetailsActivity;
import com.nuoxian.kokojia.adapter.MyPackageAdapter;
import com.nuoxian.kokojia.enterty.MyPackage;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 */
public class MyPackagePendingAudit extends SellOrderBaseFragment {
    private List<MyPackage.DataBean> list;
    private String uid, word = "";
    private static String status = "1";
    private MyPackageAdapter adapter;
    private int page = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(MyPackagePendingAudit.this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        uid = CommonMethod.getUid(getContext());

        list = new ArrayList<>();
        adapter = new MyPackageAdapter(list, getContext());
        setAdapter(adapter);

        getData(word, page, CommonValues.NOT_TO_DO);
        //上下拉刷新监听
        setRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                list.clear();
                getData(word, page, CommonValues.TO_REFRESH);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(word, page, CommonValues.TO_LOAD);
                adapter.notifyDataSetChanged();
            }
        });
        setOnItemClicklistener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), PackageDetailsActivity.class);
                intent.putExtra("id", list.get(position).getId());
                startActivity(intent);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 获取数据
     */
    private void getData(String word, int page, final int TO_DO) {
        getPackageData(uid, word, status, page, TO_DO, new SellOrderCallBack() {
            @Override
            public void success(String json) {
                Gson gson = new Gson();
                MyPackage myPackage = gson.fromJson(json, MyPackage.class);
                list.addAll(myPackage.getData());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void faile(VolleyError error) {

            }
        });
    }

    /**
     * 接收EventBus传过来的数据
     * 更新数据
     *
     * @param bundle
     */
    public void onEventMainThread(Bundle bundle) {
        if ("MyPackagePendingAudit".equals(bundle.getString("fragment"))) {
            if (!word.equals(bundle.getString("word"))) {
                //word和传过来的搜索的关键词不同,说明要更新内容
                word = bundle.getString("word");
                page = 1;
                list.clear();
                getData(word, page, CommonValues.NOT_TO_DO);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MyPackagePendingAudit.this);
    }
}

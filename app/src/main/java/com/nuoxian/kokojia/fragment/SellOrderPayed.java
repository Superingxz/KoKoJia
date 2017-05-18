package com.nuoxian.kokojia.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.nuoxian.kokojia.activity.CourseDetailsActivity;
import com.nuoxian.kokojia.adapter.SellOrderAdapter;
import com.nuoxian.kokojia.enterty.SellOrder;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class SellOrderPayed extends SellOrderBaseFragment {

    private SellOrderAdapter adapter;
    private List<SellOrder.DataBean> list;
    private String uid, word = "";
    private static String status = "20";
    private int page = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(SellOrderPayed.this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        uid = CommonMethod.getUid(getContext());

        list = new ArrayList<>();
        adapter = new SellOrderAdapter(getContext(), list);
        setAdapter(adapter);
        //获取数据
        getJsonData(word, page, CommonValues.NOT_TO_DO);

        setOnItemClicklistener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ("1".equals(list.get(position).getType())) {
                    // 跳转到课程详情
                    Intent intent = new Intent(getContext(), CourseDetailsActivity.class);
                    intent.putExtra("id", list.get(position).getCourse_id());
                    startActivity(intent);
                } else if ("3".equals(list.get(position).getType())) {
                    //素材
                } else {
                    //套餐
                }
            }
        });

        setRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                list.clear();
                getJsonData(word, page, CommonValues.TO_REFRESH);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getJsonData(word, page, CommonValues.TO_LOAD);
                adapter.notifyDataSetChanged();
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 获取数据
     */
    private void getJsonData(String word, int page, final int TO_DO) {
        getData(uid, word, status, page, TO_DO, new SellOrderCallBack() {
            @Override
            public void success(String json) {
                Gson gson = new Gson();
                SellOrder order = gson.fromJson(json, SellOrder.class);
                list.addAll(order.getData());
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
        if ("SellOrderPayed".equals(bundle.getString("fragment"))) {
            if (!word.equals(bundle.getString("word"))) {
                //word和传过来的搜索的关键词不同,说明要更新内容
                word = bundle.getString("word");
                page = 1;
                list.clear();
                getJsonData(word, page, CommonValues.NOT_TO_DO);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(SellOrderPayed.this);
    }
}

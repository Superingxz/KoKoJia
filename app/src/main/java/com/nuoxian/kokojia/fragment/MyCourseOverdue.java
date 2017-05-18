package com.nuoxian.kokojia.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.nuoxian.kokojia.adapter.MyCourseFragmentAdapter;
import com.nuoxian.kokojia.enterty.FragmentMyCourse;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.ypy.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
public class MyCourseOverdue extends MyCourseBaseFragment {

    private String uid,word="";
    private MyCourseFragmentAdapter adapter;
    private List<FragmentMyCourse.DataBean> list;
    private static String status = "4";
    private int page = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(MyCourseOverdue.this);
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
                CommonMethod.showAlerDialog("","该视频已过期,不可查看",getContext());
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
        if("MyCourseOverdue".equals(bundle.getString("fragment"))){
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
        EventBus.getDefault().unregister(MyCourseOverdue.this);
    }
}

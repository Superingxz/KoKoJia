package com.nuoxian.kokojia.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.ClassmateNoteAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Note;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 * 同学的笔记
 */
public class ClassmateNoteFragment extends Fragment {

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private AutoLinearLayout llNoContent;
    private String id,uid,lid;
    private int page;
    private List<Note.CoursenoteBean> mlist;
    private ClassmateNoteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classmate_note,container,false);

        initView(view);
        initValues();
        mlist = new ArrayList<>();
        adapter = new ClassmateNoteAdapter(mlist,getContext());
        mListView.setAdapter(adapter);
        //获取数据
        getData(page, CommonValues.NOT_TO_DO,null);
        //设置监听
        setListener();

        return view;
    }

    private void initView(View v){
        mLayout = (PullToRefreshLayout) v.findViewById(R.id.layout_refresh);
        mListView = (PullableListView) v.findViewById(R.id.lv_player_classmate_note);
        llNoContent = (AutoLinearLayout) v.findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent, getContext());
    }

    /**
     * 初始化参数
     */
    private void initValues(){
        page = 1;
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        lid = bundle.getString("lid");
        uid = bundle.getString("uid");
    }

    private void setListener(){
        mLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                mlist.clear();
                getData(page, CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(page,CommonValues.TO_LOAD,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获取数据
     */
    private void getData(int page, final int TO_DO, final PullToRefreshLayout layout){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Urls.myNote(id, uid, lid, page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                Gson gson = new Gson();
                                Note note = gson.fromJson(response,Note.class);
                                if(note.getCoursenote()!=null&!note.getCoursenote().isEmpty()){
                                    //同学笔记有数据
                                    mlist.addAll(note.getCoursenote());
                                    adapter.notifyDataSetChanged();
                                }else{
                                    //同学笔记没数据
                                    if(TO_DO!=CommonValues.TO_LOAD){
                                        llNoContent.setVisibility(View.VISIBLE);
                                    }else{
                                        CommonMethod.toast(getContext(),"没有了~");
                                    }
                                }
                            }else{
                                CommonMethod.toast(getContext(),j1.getString("msg"));
                            }
                            CommonMethod.pullToRefreshSuccess(TO_DO,layout);
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
        });
        mQueue.add(request);
    }
}

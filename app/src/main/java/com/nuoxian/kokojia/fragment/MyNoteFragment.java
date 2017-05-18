package com.nuoxian.kokojia.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import com.nuoxian.kokojia.adapter.MyNoteAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Note;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/24.
 * 我的笔记
 */
public class MyNoteFragment extends Fragment {

    private AutoLinearLayout llNoContent;
    private AutoLinearLayout llWriteNote;
    private PullableListView mListView;
    private PullToRefreshLayout mLayout;
    private RequestQueue mQueue;
    private String id,uid,lid,note;
    private int page;
    private List<Note.MynoteBean> mlist;
    private MyNoteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_note,container,false);
        mQueue = MyApplication.getRequestQueue();

        initView(view);
        initValues();

        mlist = new ArrayList<>();
        adapter = new MyNoteAdapter(mlist,getContext());
        mListView.setAdapter(adapter);
        //获取数据
        getData(page, CommonValues.NOT_TO_DO,null);
        //设置监听
        setListener();

        return view;
    }

    private void initView(View v){
        CommonMethod.setFontAwesome(v.findViewById(R.id.layout_fragment_my_note),getContext());
        llNoContent = (AutoLinearLayout) v.findViewById(R.id.ll_no_content);
        llWriteNote = (AutoLinearLayout) v.findViewById(R.id.ll_player_write_note);
        mListView = (PullableListView) v.findViewById(R.id.lv_player_my_note);
        mLayout = (PullToRefreshLayout) v.findViewById(R.id.layout_refresh);
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

    /**
     * 设置监听
     */
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
                getData(page, CommonValues.TO_LOAD, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
        //写笔记
        llWriteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNote();
            }
        });
    }

    /**
     * 写笔记
     */
    private void writeNote(){
        //自定义dialog,显示写笔记界面
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_my_note, null);
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        EditText etNote = (EditText) view.findViewById(R.id.et_note);
        //获取用户输入的笔记
        etNote.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                note = s+"";
            }
        });
        Button canle = (Button) view.findViewById(R.id.btn_cancle);
        //取消
        canle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button commit = (Button) view.findViewById(R.id.btn_commit);
        //提交
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(note)){
                    dialog.dismiss();
                    //提交笔记
                    commitNote();
                }
            }
        });
    }

    /**
     * 提交笔记
     */
    private void commitNote(){
        StringRequest request = new StringRequest(Request.Method.POST, Urls.COMMIT_NOTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //提交成功，刷新数据
                                page = 1;
                                mlist.clear();
                                getData(page, CommonValues.NOT_TO_DO, null);
                                adapter.notifyDataSetChanged();
                            }else{
                                //提交失败
                                CommonMethod.showAlerDialog("",j1.optString("msg"),getContext());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(getContext());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("content",note);
                params.put("course_id",id);
                params.put("lesson_id",lid);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 获取数据
     * @param page
     * @param TO_DO
     * @param layout
     */
    private void getData(int page, final int TO_DO, final PullToRefreshLayout layout){
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
                                if(note.getMynote()!=null&&!note.getMynote().isEmpty()){
                                    //我的笔记有数据
                                    mlist.addAll(note.getMynote());
                                    adapter.notifyDataSetChanged();
                                }else{
                                    //没有数据
                                    if(TO_DO!=CommonValues.TO_LOAD){
                                        llNoContent.setVisibility(View.VISIBLE);
                                    }else{
                                        CommonMethod.toast(getContext(),"没有了!");
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

package com.nuoxian.kokojia.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.PlayCourseLessonAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Play;
import com.nuoxian.kokojia.enterty.PlayLesson;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.UniversalVideoView;
import com.ypy.eventbus.EventBus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/21.
 * 播放目录
 */
public class PlayerCatalogFragment extends Fragment {

    private PullableListView listView;
    private PullToRefreshLayout layout;
    private List<PlayLesson> list;
    private PlayCourseLessonAdapter adapter;
    private String id,lid,uid;
    private int page;
    private RequestQueue mQueue;
    private int position;//被选中的item的坐标
    private Handler handler;
    private int mSeekPosition;
    private String playUrl;
    private boolean isFirst = true;//是否为第一次设置播放器区域大小

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_catalog,container,false);

        mQueue = MyApplication.getRequestQueue();
        //获取所需的参数
        getValues();

        initView(view);
        //加载数据
        list = new ArrayList<>();
        adapter = new PlayCourseLessonAdapter(list,getContext(),lid);
        listView.setAdapter(adapter);
        //获取数据
        getData(Urls.playUrl, CommonValues.NOT_TO_DO,null);

        setListener();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                setTextChecked(position);
            }
        };
        return view;
    }

    private void getValues(){
        Bundle bundle = getArguments();
        uid = bundle.getString("uid");
        id = bundle.getString("id");
        lid = bundle.getString("lid");
        page = bundle.getInt("page");
        position = bundle.getInt("position");
    }

    private void initView(View v){
        layout = (PullToRefreshLayout) v.findViewById(R.id.play_layout_refresh);
        listView = (PullableListView) v.findViewById(R.id.lv_play);
    }

    private void setListener(){
        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //通知刷新完成
                list.clear();
                page = 1;
                getData(Urls.playUrl,CommonValues.TO_REFRESH,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(Urls.playUrl,CommonValues.TO_LOAD,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });

        //ListView的点击监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if("1".equals(list.get(position).getLevel())){//选择了节
                    if("1".equals(list.get(position).getPlayer_status())){//可以播放
                        //设置选中的颜色
                        setTextChecked(position);
                        //播放
                        PlayerCatalogFragment.this.id = list.get(position).getCourse_id();
                        PlayerCatalogFragment.this.lid = list.get(position).getId();
                        page = 1;
                        getPlayUrl(Urls.playUrl);
                    }else if("2".equals(list.get(position).getPlayer_status())){//不能播放
                        Toast.makeText(getContext(), "需要购买才能播放!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 获取数据
     * @param url
     */
    private void getData(String url, final int TO_DO, final PullToRefreshLayout layout){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJson(response);
                        CommonMethod.pullToRefreshSuccess(TO_DO,layout);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"加载失败，请检查网络!",Toast.LENGTH_SHORT).show();
                CommonMethod.pullToRefreshFail(TO_DO,layout);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("uid",uid);
                params.put("lid",lid);
                params.put("page",page+"");
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 解析json数据
     * @param json
     */
    private void parseJson(String json){
        List<PlayLesson> tempList = new ArrayList<>();
        try {
            JSONObject j1 = new JSONObject(json);
            if("1".equals(j1.getString("status"))){
                //播放地址
                playUrl = j1.getString("video_url");
                if(isFirst){
                    //通知播放器设置播放地址
                    Play play = new Play();
                    play.setStatus("play");
                    play.setPlayUrl(playUrl);
                    EventBus.getDefault().post(play);
                    isFirst = false;
                }
                JSONArray j2 = j1.getJSONArray("lesson");
                for(int i=0;i<j2.length();i++){
                    JSONObject j3 = j2.getJSONObject(i);
                    PlayLesson lesson = new PlayLesson();
                    lesson.setId(j3.getString("id"));
                    lesson.setTitle(j3.getString("title"));
                    lesson.setCourse_id(j3.getString("course_id"));
                    lesson.setLevel(j3.getString("level"));
                    lesson.setVideo_time(j3.optString("video_time"));
                    lesson.setPlayer_status(j3.getString("player_status"));
                    lesson.setCheck(false);
                    tempList.add(lesson);
                }
                list.addAll(tempList);
                adapter.notifyDataSetChanged();
                Message msg = Message.obtain();
                msg.arg1 = position;
                handler.sendMessage(msg);

                //通知播放器开始
                Play play = new Play();
                play.setStatus("start");
                EventBus.getDefault().post(play);
            }else{
                CommonMethod.toast(getContext(),"没有了~");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取播放地址,并播放
     * @param url
     */
    private void getPlayUrl(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //播放地址
                                playUrl = j1.getString("video_url");
                                //通知播放器设置播放地址
                                Play play = new Play();
                                play.setStatus("play");
                                play.setPlayUrl(playUrl);
                                EventBus.getDefault().post(play);
                                //通知播放器开始
                                Play play1 = new Play();
                                play1.setStatus("start");
                                EventBus.getDefault().post(play1);
                            }else{
                                Toast.makeText(getContext(),j1.getString("msg"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"加载失败，请检查网络!",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",id);
                params.put("uid",uid);
                params.put("lid",lid);
                params.put("page",page+"");
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 设置item被选中
     * @param position
     */
    private void setTextChecked(int position){
        this.position = position;
        if(this.position==-1){
            //这里是从点击试听那边传过来的值，所以设为-1
            for (int i=0;i<list.size();i++){
                PlayLesson lesson = list.get(i);
                //将第一节设置为被选中的状态
                if("1".equals(lesson.getLevel())){
                    lesson.setCheck(true);
                    list.set(i,lesson);
                    break;
                }
            }
        }else{
            for (int i=0;i<list.size();i++){
                PlayLesson lesson = list.get(i);
                if(i==position){
                    lesson.setCheck(true);
                }else{
                    lesson.setCheck(false);
                }
                list.set(i,lesson);
            }
        }
        adapter.notifyDataSetChanged();
    }
}

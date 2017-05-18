package com.nuoxian.kokojia.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.PlayOnlineActivity;
import com.nuoxian.kokojia.adapter.DetailsLessonAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseLesson;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 * 课程目录
 */
public class CourseLessonFragment extends Fragment implements AdapterView.OnItemClickListener{

    private View view;
    private PullableListView listView;
    private PullToRefreshLayout layout;
    private int page=1;
    private List<CourseLesson> lessonList;
    private DetailsLessonAdapter adapter;
    private String id,uid;
    private AutoLinearLayout llReload;
    private int loadCount=0;//成功加载的次数

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        uid = CommonMethod.getUid(getContext());
        view = inflater.inflate(R.layout.fragment_course_lesson, container, false);
        initView();
        getData(Urls.getCourseLessonUrl(id, uid, page), CommonValues.NOT_TO_DO, null);

        return view;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        layout = (PullToRefreshLayout) view.findViewById(R.id.lesson_layout_refresh);
        listView = (PullableListView) view.findViewById(R.id.lv_details_lesson);
        llReload = (AutoLinearLayout) view.findViewById(R.id.ll_reload);

        lessonList = new ArrayList<>();
        adapter = new DetailsLessonAdapter(lessonList,getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        llReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新加载
                getData(Urls.getCourseLessonUrl(id, uid, page), CommonValues.NOT_TO_DO, null);
                //隐藏重新加载
                llReload.setVisibility(View.GONE);
            }
        });

        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                getData(Urls.getCourseLessonUrl(id, uid, page), CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page++;
                getData(Urls.getCourseLessonUrl(id, uid, page), CommonValues.TO_LOAD, pullToRefreshLayout);
            }
        });
    }

    //listview点击监听
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if("1".equals(lessonList.get(position).getLevel())){//点击的是节

            if(lessonList.get(position).getPlayer_status()==1){//可以学习
                final Intent intent = new Intent(getContext(), PlayOnlineActivity.class);
                intent.putExtra("id",lessonList.get(position).getCourse_id());
                intent.putExtra("lid",lessonList.get(position).getId());
                intent.putExtra("page",page);
                intent.putExtra("position",position);
                if(CommonMethod.isWifiConnected(getContext())){//连接了wifi
                    //跳转到播放页面
                    startActivity(intent);
                }else{
                    //弹出一个提示框
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("警告");
                    dialog.setMessage("监测到您没有连接WiFi，继续观看视频会消耗您的流量，是否继续?");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.setNegativeButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //跳转到播放页面
                            startActivity(intent);
                        }
                    });
                    dialog.create().show();
                }

            }else if(lessonList.get(position).getPlayer_status()==2){//需要购买
                Toast.makeText(getContext(),"需要购买才能观看!",Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * 获取数据
     * @param url
     */
    private void getData(String url, final int TO_DO, final PullToRefreshLayout pullToRefreshLayout) {
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //加载成功，加载次数+1
                loadCount++;
                if (TO_DO==CommonValues.TO_REFRESH){
                    //如果是刷新数据，先清空list
                    lessonList.clear();
                }
                parseJson(response);
                //通知加载或刷新完成
                CommonMethod.pullToRefreshSuccess(TO_DO,pullToRefreshLayout);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"加载失败，请检查网络!",Toast.LENGTH_SHORT).show();
                //通知加载或刷新失败
                CommonMethod.pullToRefreshFail(TO_DO,pullToRefreshLayout);
                if(loadCount<1){
                    //显示加载失败
                    llReload.setVisibility(View.VISIBLE);
                }
            }
        });
        mQueue.add(request);
    }

    /**
     * 解析json数据
     * @param json
     */
    private void parseJson(String json){
        List<CourseLesson> tempList = new ArrayList<>();
        try {
            JSONObject j1 = new JSONObject(json);
            if("1".equals(j1.getString("status"))){
                JSONArray j2 = j1.getJSONArray("lesson");
                for(int i=0;i<j2.length();i++){
                    CourseLesson lesson = new CourseLesson();
                    JSONObject j3 = j2.getJSONObject(i);
                    lesson.setId(j3.getString("id"));
                    lesson.setTitle(j3.getString("title"));
                    lesson.setCourse_id(j3.getString("course_id"));
                    lesson.setLevel(j3.getString("level"));
                    lesson.setPlayer_status(j3.getInt("player_status"));
                    lesson.setVideo_time(j3.optString("video_time"));
                    tempList.add(lesson);
                }
                lessonList.addAll(tempList);
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getContext(), "没有了~", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

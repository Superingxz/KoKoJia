package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.nuoxian.kokojia.adapter.MyCourseAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.MyCourse;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCourseActivity extends AutoLayoutActivity implements View.OnClickListener {

    private PullToRefreshLayout layout;
    private PullableListView listView;
    private List<MyCourse> list = new ArrayList<>();
    private MyCourseAdapter adapter;
    private String uid;
    private int page = 1;
    private TextView ivSearch,ivBack;
    private AutoLinearLayout llReload,llNoContent;
    private int loadCount = 0;//计算加载数据的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course);

        //设置标题栏颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        uid = CommonMethod.getUid(this);
        //初始化视图
        initView();
        //加载适配器
        adapter = new MyCourseAdapter(list,this);
        listView.setAdapter(adapter);
        //加载数据
        getData(Urls.myCourseUrl, CommonValues.NOT_TO_DO, null);
    }

    /**
     * 初始化视图
     */
    private void initView(){
        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        ivBack = (TextView) findViewById(R.id.iv_my_course_back);
        ivBack.setTypeface(iconFont);
        ivSearch = (TextView) findViewById(R.id.iv_my_course_search);
        ivSearch.setTypeface(iconFont);
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent, this);
        layout = (PullToRefreshLayout) findViewById(R.id.layout_my_course_refresh);
        listView = (PullableListView) findViewById(R.id.lv_my_course);
        llReload = (AutoLinearLayout) findViewById(R.id.ll_reload);
        //设置监听
        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        llReload.setOnClickListener(this);
        //上下拉刷新的监听
        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {//刷新
                page = 1;
                getData(Urls.myCourseUrl, CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {//加载
                page++;
                getData(Urls.myCourseUrl,CommonValues.TO_LOAD,pullToRefreshLayout);
            }
        });
        //listview的点击监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到详情页面
                Intent intent = new Intent(MyCourseActivity.this, CourseDetailsActivity.class);
                intent.putExtra("id", list.get(position).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_my_course_back://返回
                this.finish();
                break;
            case R.id.iv_my_course_search://搜索
                CommonMethod.startActivity(this,SearchActivity.class);
                break;
            case R.id.ll_reload://重新加载
                getData(Urls.myCourseUrl, CommonValues.NOT_TO_DO,null);
                //隐藏重新加载
                llReload.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 获取数据
     * @param url
     */
    private void getData(String url, final int TO_DO, final PullToRefreshLayout pullToRefreshLayout){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //加载成功，加载的次数+1
                        loadCount++;
                        if(TO_DO==CommonValues.TO_REFRESH){
                            //如果是刷新数据,先清空数据
                            list.clear();
                        }
                        parseJson(response,TO_DO);
                        CommonMethod.pullToRefreshSuccess(TO_DO,pullToRefreshLayout);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MyCourseActivity.this,"加载失败，请检查网络!",Toast.LENGTH_SHORT).show();
                CommonMethod.pullToRefreshFail(TO_DO,pullToRefreshLayout);
                if(loadCount<1){
                    //加载成功的次数小于1，显示加载成功
                    llReload.setVisibility(View.VISIBLE);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
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
    private void parseJson(String json,int to_do){
        List<MyCourse> tempList = new ArrayList<>();
        try {
            llNoContent.setVisibility(View.GONE);
            JSONObject j1 = new JSONObject(json);
            if("1".equals(j1.getString("status"))){
                JSONArray j2 = j1.getJSONArray("data");
                for (int i=0;i<j2.length();i++){
                    MyCourse course = new MyCourse();
                    JSONObject j3 = j2.getJSONObject(i);
                    course.setId(j3.getString("id"));
                    course.setTitle(j3.getString("title"));
                    course.setImage_url(j3.getString("image_url"));
                    course.setPrice(j3.getString("price"));
                    course.setLearn(j3.getString("learn"));
                    tempList.add(course);
                }
                list.addAll(tempList);
                adapter.notifyDataSetChanged();
            }else{
                if(to_do!=CommonValues.TO_LOAD){
                    llNoContent.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(this,"没有了~",Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.PackageDetailsAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Course;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.view.NoScrollListview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 学院首页
 */
public class SchoolIndexActivity extends BaseActivity {

    private TextView tvName,tvIntroduce;
    private NoScrollListview mListView;
    private List<Course> list = new ArrayList<>();
    private PackageDetailsAdapter adapter;
    private String id,school;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_school_index);

        CommonMethod.showLoadingDialog("正在加载...", this);
        id = getIntent().getStringExtra("id");
        school = getIntent().getStringExtra("title");
        initView();
        //加载适配器
        adapter = new PackageDetailsAdapter(this,list);
        mListView.setAdapter(adapter);
        //获取数据
        getData(Urls.schoolIndexUrl(id));
        //listview监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情页面
                Intent intent = new Intent(SchoolIndexActivity.this,CourseDetailsActivity.class);
                intent.putExtra("id",list.get(position).getId());
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView(){
        //设置标题
        setTitle(school);
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchoolIndexActivity.this.finish();
            }
        });

        tvName = (TextView) findViewById(R.id.tv_school_index_name);
        tvIntroduce = (TextView) findViewById(R.id.tv_school_index_introduce);
        mListView = (NoScrollListview) findViewById(R.id.lv_school_index);
        mScrollView = (ScrollView) findViewById(R.id.sv_school);
        //解决scrollview嵌套listview的焦点问题
        mScrollView.smoothScrollTo(0,20);
        mListView.setFocusable(false);
    }

    /**
     * 获取数据
     */
    private void getData(String url){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                JSONObject j2 = j1.getJSONObject("school");
                                tvName.setText("首页 > "+j2.getString("name"));//标题
                                tvIntroduce.setText(j2.getString("description"));//学院介绍
                                JSONArray j3 = j1.getJSONArray("course_list");
                                for (int i=0;i<j3.length();i++){
                                    JSONObject j4 = j3.getJSONObject(i);
                                    Course course = new Course();
                                    course.setId(j4.getString("id"));
                                    course.setTitle(j4.getString("title"));
                                    course.setPrice(j4.getString("price"));
                                    course.setImage_url(j4.getString("image_url"));
                                    course.setTrial_num(j4.getString("trial_num"));
                                    course.setClass_num(j4.getString("class_num"));
                                    course.setIs_paid(j4.optString("is_paid"));
                                    course.setDiscount_price(j4.optString("discount_price"));
                                    course.setCountdown(j4.optString("countdown"));
                                    list.add(course);
                                }
                                adapter.notifyDataSetChanged();
                                CommonMethod.dismissLoadingDialog();
                            }else{
                                CommonMethod.toast(SchoolIndexActivity.this, "没有数据");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(SchoolIndexActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        });
        mQueue.add(request);
    }
}

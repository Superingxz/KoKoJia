package com.nuoxian.kokojia.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.HomeListViewAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Course;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索页面
 * Created by 陈思龙 on 2016/7/4.
 */
public class SearchActivity extends AutoLayoutActivity {

    private ListView mListView;
    private EditText etSearch;
    private ImageButton ibSearch;
    private String word;
    private int page=1;
    private RequestQueue mQueue;
    private List<Course> courseList = new ArrayList<>();
    private HomeListViewAdapter adapter;
    private PullToRefreshLayout layout;
    private static final int NOT_TO_DO = 0;
    private static final int TO_REFRESH = 1;
    private static final int TO_LOADING = 2;
    private TextView mTitle,ivBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //设置标题栏的颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        mQueue = MyApplication.getRequestQueue();
        initView();
        //加载数据
        adapter = new HomeListViewAdapter(this,courseList);
        mListView.setAdapter(adapter);
    }

    private void initView(){
        Typeface tf= FontManager.getTypeface(this,FontManager.FONTAWESOME);
        mListView = (ListView) findViewById(R.id.lv_search_fragment);
        etSearch = (EditText) findViewById(R.id.et_search);
        ibSearch = (ImageButton) findViewById(R.id.ib_search);
        ivBack = (TextView) findViewById(R.id.iv_search_back);
        ivBack.setTypeface(tf);
        layout = (PullToRefreshLayout) findViewById(R.id.layout__search_refresh);
        mTitle = (TextView) findViewById(R.id.tv_search_title);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, CourseDetailsActivity.class);
                intent.putExtra("id", courseList.get(position).getId());
                startActivity(intent);
            }
        });
        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                courseList.clear();
                getData(Urls.SEARCH_URL, TO_REFRESH, layout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page++;
                getData(Urls.SEARCH_URL, TO_LOADING, layout);
            }
        });

        etSearch.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                //保存用户输入的字符
                word = s + "";
            }
        });
        //对软键盘中的搜索按钮进行监听
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索
                    search();
                }
                return false;
            }
        });
        //搜索按钮的监听
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 先隐藏键盘
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                search();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });
    }

    /**
     * 搜索
     */
    private void search(){
        if (TextUtils.isEmpty(word)) {
            Toast.makeText(SearchActivity.this, "请先输入搜索关键字!", Toast.LENGTH_SHORT).show();
        } else {
            mTitle.setText("课课家教育");
            //清空之前的数据
            courseList.clear();
            getData(Urls.SEARCH_URL, NOT_TO_DO, null);
        }
    }

    /**
     * 获取数据
     */
    private void getData(String url,final int TO_DO, final PullToRefreshLayout pullToRefreshLayout){
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJson(response);
                //加载成功
                CommonMethod.pullToRefreshSuccess(TO_DO,pullToRefreshLayout);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchActivity.this,"加载失败！",Toast.LENGTH_SHORT).show();
                CommonMethod.pullToRefreshFail(TO_DO,pullToRefreshLayout);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("word", word);
                params.put("page", page + "");
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 解析数据
     * @param response
     */
    private void parseJson(String response){
        List<Course> tempList = new ArrayList<>();
        try {
            JSONObject j1 = new JSONObject(response);
            if("1".equals(j1.getString("status"))){
                JSONArray j2 = j1.getJSONArray("data");
                for (int i=0;i<j2.length();i++){
                    JSONObject j3 = j2.getJSONObject(i);
                    Course course = new Course();
                    course.setId(j3.getString("id"));
                    course.setTitle(j3.getString("title"));
                    course.setPrice(j3.getString("price"));
                    course.setImage_url(j3.getString("image_url"));
                    course.setTrial_num(j3.getString("trial_num"));
                    course.setClass_num(j3.getString("class_num"));
                    course.setIs_paid(j3.optString("is_paid"));
                    course.setDiscount_price(j3.optString("discount_price"));
                    course.setCountdown(j3.optString("countdown"));
                    tempList.add(course);
                }
                courseList.addAll(tempList);
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(SearchActivity.this,j1.getString("msg"),Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.HomeListViewAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Course;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 * 课程列表
 */
public class CourseListActivity extends BaseActivity implements View.OnClickListener {

    private PullableListView mListView;
    private HomeListViewAdapter adapter;
    private List<Course> courseContent = new ArrayList<>();
    private RequestQueue mQueue;
    private int urlId, urlPage;
    private PullToRefreshLayout layout;
    private AutoLinearLayout llComprehensive,llDifficulty,llClassify;
    private TextView tvComprehensive,tvDifficulty,tvComprehensiveCaret,tvDifficultyCaret,tvClassify;
    private static final int NOT_TO_DO = 0;
    private static final int TO_REFRESH = 1;
    private static final int TO_LOADING = 2;
    private String title,difficulty="0",order="0",sort="0",classify="";
    private PopupWindow comprehensiveWindow,difficultyWindow;
    private ImageView courserefresh;
    private TextView coursetext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.home_listview_fragment);

        CommonMethod.showLoadingDialog("正在加载...", this);
        mQueue = MyApplication.getRequestQueue();
        //获取参数
        urlId = getIntent().getIntExtra("urlId", 2);
        urlPage = getIntent().getIntExtra("urlPage", 1);
        title = getIntent().getStringExtra("title");
        classify = getIntent().getStringExtra("classify");
        //初始化视图
        initView();
        //加载数据
        adapter = new HomeListViewAdapter(this, courseContent);
        mListView.setAdapter(adapter);
        //获取数据
        getData(Urls.getHomeCourseContentUrl(urlId,difficulty,order,sort, urlPage), NOT_TO_DO, null);
        //设置监听
        setListener();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //设置标题
        setTitle(classify);
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseListActivity.this.finish();
            }
        });
        //显示搜索按钮
        showSearchButton();

        CommonMethod.setFontAwesome(findViewById(R.id.layout_home_listview_fragment), this);
        llClassify = (AutoLinearLayout) findViewById(R.id.ll_home_list_classify);
        mListView = (PullableListView) findViewById(R.id.lv_home_coursecontent);
        layout = (PullToRefreshLayout) findViewById(R.id.layout_refresh);
        llComprehensive = (AutoLinearLayout) findViewById(R.id.ll_home_list_comprehensive);
        llDifficulty = (AutoLinearLayout) findViewById(R.id.ll_home_list_difficulty);
        tvComprehensive = (TextView) findViewById(R.id.tv_home_list_comprehensive);
        tvComprehensiveCaret = (TextView) findViewById(R.id.tv_comprehensive_up_down);
        tvComprehensiveCaret.setTag("up");
        tvDifficulty = (TextView) findViewById(R.id.tv_home_list_difficulty);
        tvDifficultyCaret = (TextView) findViewById(R.id.tv_difficulty_up_down);
        tvDifficultyCaret.setTag("up");
        tvClassify = (TextView) findViewById(R.id.tv_home_list_classify);
        tvClassify.setText(title);

    }

    /**
     * 设置监听
     */
    private void setListener() {
        //上下拉刷新监听
        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新,将页码变为1
                urlPage = 1;
                getData(Urls.getHomeCourseContentUrl(urlId, difficulty, order, sort, urlPage), TO_REFRESH, layout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载,页码加1
                urlPage++;
                getData(Urls.getHomeCourseContentUrl(urlId, difficulty, order, sort, urlPage), TO_LOADING, layout);
                adapter.notifyDataSetChanged();
            }
        });
        //点击事件监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseListActivity.this, CourseDetailsActivity.class);
                intent.putExtra("id", courseContent.get(position).getId());
                startActivity(intent);
            }
        });
        //分类
        llClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseListActivity.this.finish();
            }
        });
        //综合
        llComprehensive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变三角形图标
                if("up".equals(tvComprehensiveCaret.getTag())){
                    //如果是朝上的就让他朝下
                    tvComprehensiveCaret.setText(R.string.caret_down);
                    tvComprehensiveCaret.setTag("down");
                }else{
                    //反之
                    tvComprehensiveCaret.setText(R.string.caret_up);
                    tvComprehensiveCaret.setTag("up");
                }
                //显示或关闭popupwindow
                if(comprehensiveWindow!=null){
                    if(comprehensiveWindow.isShowing()){
                        //正在显示则关闭
                        comprehensiveWindow.dismiss();
                    }else{
                        //反之
                        comprehensiveWindow.showAsDropDown(llComprehensive);
                    }
                }else{
                    //创建popupwindow
                    showComprehensiveWindow();
                }
            }
        });
        //难度
        llDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变三角形
                if ("up".equals(tvDifficultyCaret.getTag())) {
                    //如果是朝上的就让他朝下
                    tvDifficultyCaret.setText(R.string.caret_down);
                    tvDifficultyCaret.setTag("down");
                } else {
                    //反之
                    tvDifficultyCaret.setText(R.string.caret_up);
                    tvDifficultyCaret.setTag("up");
                }
                //显示或隐藏popupwindow
                if (difficultyWindow != null) {
                    if (difficultyWindow.isShowing()) {
                        //正在显示则关闭
                        difficultyWindow.dismiss();
                    } else {
                        //反之
                        difficultyWindow.showAsDropDown(llDifficulty);
                    }
                } else {
                    //创建popupwindow
                    showDifficultyWindow();
                }
            }
        });
    }

    /**
     * 显示综合排序的窗口
     */
    private void showComprehensiveWindow(){
        //自定义popupwindow
        View view = LayoutInflater.from(this).inflate(R.layout.window_comprehensive,null);
        comprehensiveWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        comprehensiveWindow.showAsDropDown(llComprehensive);
        //设置监听
        view.findViewById(R.id.tv_comprehensive_comprehensive).setOnClickListener(this);
        view.findViewById(R.id.tv_comprehensive_popularity).setOnClickListener(this);
        view.findViewById(R.id.tv_comprehensive_price).setOnClickListener(this);
        view.findViewById(R.id.tv_comprehensive_new).setOnClickListener(this);
    }

    /**
     * 显示难度分类的窗口
     */
    private void showDifficultyWindow(){
        //自定义popupwindow
        View view = LayoutInflater.from(this).inflate(R.layout.window_difficulty,null);
        difficultyWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        difficultyWindow.showAsDropDown(llDifficulty);
        //设置监听
        view.findViewById(R.id.tv_difficulty_all).setOnClickListener(this);
        view.findViewById(R.id.tv_difficulty_primary).setOnClickListener(this);
        view.findViewById(R.id.tv_difficulty_intermediate).setOnClickListener(this);
        view.findViewById(R.id.tv_difficulty_senior).setOnClickListener(this);
    }

    /**
     * 获取课程数据
     */
    private void getData(String url, final int TO_DO, final PullToRefreshLayout pullToRefreshLayout) {
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (TO_DO == CommonValues.TO_REFRESH) {
                            //如果是刷新，先将listview清空
                            courseContent.clear();
                        }
                        parseCourseJson(response);
                        CommonMethod.pullToRefreshSuccess(TO_DO, pullToRefreshLayout);
                        CommonMethod.dismissLoadingDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.pullToRefreshFail(TO_DO, pullToRefreshLayout);
                CommonMethod.loadFailureToast(CourseListActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        });
        mQueue.add(request);
    }

    /**
     * 解析课程内容json数据
     *
     * @param json
     */
    public void parseCourseJson(String json) {
        List<Course> tempCourseContent = new ArrayList<>();
        try {
            JSONObject j1 = new JSONObject(json);
            String status = j1.getString("status");
            if ("0".equals(status)) {//没有数据
                CommonMethod.toast(CourseListActivity.this, "没有了~");
            } else {//有数据
                JSONArray j2 = j1.getJSONArray("course_list");
                for (int i = 0; i < j2.length(); i++) {
                    JSONObject j3 = j2.getJSONObject(i);
                    Course course = new Course();
                    course.setId(j3.getString("id"));
                    course.setClass_num(j3.getString("class_num"));
                    course.setCountdown(j3.optString("countdown"));
                    course.setDiscount_price(j3.optString("discount_price"));
                    course.setImage_url(j3.getString("image_url"));
                    course.setIs_paid(j3.optString("is_paid"));
                    course.setPrice(j3.getString("price"));
                    course.setTitle(j3.getString("title"));
                    course.setTrial_num(j3.getString("trial_num"));
                    tempCourseContent.add(course);
                }
                courseContent.addAll(tempCourseContent);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_comprehensive_comprehensive://综合
                getComprehensiveData("0","综合");
                break;
            case R.id.tv_comprehensive_new://最新
                getComprehensiveData("10","最新");
                break;
            case R.id.tv_comprehensive_price://价格
                getComprehensiveData("20","价格");
                break;
            case R.id.tv_comprehensive_popularity://人气
                getComprehensiveData("30","人气");
                break;
            case R.id.tv_difficulty_all://全部难度
                getDifficultyData("0","全部难度");
                break;
            case R.id.tv_difficulty_primary://初级
                getDifficultyData("1","初级难度");
                break;
            case R.id.tv_difficulty_intermediate://中级
                getDifficultyData("2","中级难度");
                break;
            case R.id.tv_difficulty_senior://高级
                getDifficultyData("3","高级难度");
                break;
        }
    }

    /**
     * 获取综合选择后的数据
     */
    private void getComprehensiveData(String order,String title){
        tvComprehensive.setText(title);
        tvComprehensiveCaret.setText(R.string.caret_up);
        tvComprehensiveCaret.setTag("up");
        comprehensiveWindow.dismiss();
        //重新加载数据
        urlPage = 1;
        this.order = order;
        courseContent.clear();
        getData(Urls.getHomeCourseContentUrl(urlId, difficulty, this.order, sort, urlPage),
                CommonValues.NOT_TO_DO,null);
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取难度选择后的数据
     * @param difficulty
     * @param title
     */
    private void getDifficultyData(String difficulty,String title){
        tvDifficulty.setText(title);
        tvDifficultyCaret.setText(R.string.caret_up);
        tvDifficultyCaret.setTag("up");
        difficultyWindow.dismiss();
        //重新加载数据
        urlPage = 1;
        this.difficulty = difficulty;
        courseContent.clear();
        getData(Urls.getHomeCourseContentUrl(urlId, this.difficulty, order, sort, urlPage),
                CommonValues.NOT_TO_DO, null);
        adapter.notifyDataSetChanged();
    }
}

package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.FreeCourseAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.FreeCourse;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 免费课程页面
 */
public class FreeCourseActivity extends BaseActivity {

    private PullToRefreshLayout mLayout;
    private PullableListView mListView;
    private AutoLinearLayout llNoContent;
    private List<FreeCourse.CourseListBean> mList;
    private FreeCourseAdapter adapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_free_course);
        CommonMethod.showLoadingDialog("正在加载...",this);
        setTitle("免费课程");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreeCourseActivity.this.finish();
            }
        });
        //显示搜索按钮
        showSearchButton();

        initView();
        mList = new ArrayList<>();
        adapter = new FreeCourseAdapter(mList,this);
        mListView.setAdapter(adapter);
        //获取数据
        getData(page, CommonValues.NOT_TO_DO,null);
        //设置监听
        setListener();
    }

    private void initView(){
        mLayout = (PullToRefreshLayout) findViewById(R.id.layout_refresh);
        mListView = (PullableListView) findViewById(R.id.lv_free_course);
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent, this);
    }

    private void setListener(){
        mLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                mList.clear();
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情界面
                Intent intent = new Intent(FreeCourseActivity.this, CourseDetailsActivity.class);
                intent.putExtra("id", mList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    /**
     * 获取数据
     * @param page
     * @param to_do
     * @param layout
     */
    private void getData(int page, final int to_do, final PullToRefreshLayout layout){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Urls.getHomeCourseContentUrl(0, "0", "0", "2", page),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            llNoContent.setVisibility(View.GONE);
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                Gson gson = new Gson();
                                FreeCourse course = gson.fromJson(response,FreeCourse.class);
                                mList.addAll(course.getCourse_list());
                                adapter.notifyDataSetChanged();
                            }else{
                                if(to_do!=CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
                                    CommonMethod.toast(FreeCourseActivity.this,"没有了~");
                                }
                            }
                            CommonMethod.dismissLoadingDialog();
                            CommonMethod.pullToRefreshSuccess(to_do,layout);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.dismissLoadingDialog();
                CommonMethod.loadFailureToast(FreeCourseActivity.this);
                CommonMethod.pullToRefreshFail(to_do,layout);
            }
        });
        mQueue.add(request);
    }
}

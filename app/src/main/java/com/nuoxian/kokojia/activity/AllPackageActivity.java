package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.AllPackageAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.AllPackage;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程套餐页面
 */
public class AllPackageActivity extends BaseActivity implements View.OnClickListener {

    private PullableListView mListView;
    private PullToRefreshLayout mLayout;
    private TextView tvClassify,tvComprehensive,tvClassifyUpDown,tvComprehensiveUpDown;
    private AutoLinearLayout llNoContent;
    private List<AllPackage.CourseListBean> courseList;
    private List<AllPackage.CourseTypeBean> courseType;
    private AllPackageAdapter adapter;
    private RequestQueue mQueue;
    private int page = 1;
    private String tid = "0",order = "0";
    private int loadCount = 1;//计算数据加载的次数
    private PopupWindow classifyWindow,comprehensiveWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_all_package);
        CommonMethod.showLoadingDialog("正在加载...", this);
        setTitle("课程套餐");
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllPackageActivity.this.finish();
            }
        });
        //显示搜索按钮
        showSearchButton();

        mQueue = MyApplication.getRequestQueue();
        initView();

        courseList = new ArrayList<>();
        courseType = new ArrayList<>();
        adapter = new AllPackageAdapter(courseList,this);
        mListView.setAdapter(adapter);
        //获取数据
        getData(Urls.allpackageUrl(tid,order,page), CommonValues.NOT_TO_DO,null);
        //设置监听
        setListener();
    }

    private void initView(){
        CommonMethod.setFontAwesome(findViewById(R.id.layout_all_package),this);

        mLayout = (PullToRefreshLayout) findViewById(R.id.layout_refresh);
        mListView = (PullableListView) findViewById(R.id.lv_all_package);
        tvClassify = (TextView) findViewById(R.id.tv_package_classify);
        tvComprehensive = (TextView) findViewById(R.id.tv_package_comprehensive);
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,this);
        tvClassifyUpDown = (TextView) findViewById(R.id.tv_classify_up_dowm);//分类的三角形
        tvClassifyUpDown.setTag("up");
        tvComprehensiveUpDown = (TextView) findViewById(R.id.tv_comprehensive_up_down);//综合的三角形
        tvComprehensiveUpDown.setTag("up");
    }

    private void setListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情页面
                Intent intent = new Intent(AllPackageActivity.this, PackageDetailsActivity.class);
                intent.putExtra("id", courseList.get(position).getId());
                startActivity(intent);
            }
        });

        mLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                courseList.clear();
                getData(Urls.allpackageUrl(tid, order, page), CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(Urls.allpackageUrl(tid, order, page), CommonValues.TO_LOAD, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
        //综合
        tvComprehensive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变三角形图标
                if("up".equals(tvComprehensiveUpDown.getTag())){
                    //设置三角形为向下的
                    tvComprehensiveUpDown.setText(R.string.caret_down);
                    tvComprehensiveUpDown.setTag("down");
                }else{
                    tvComprehensiveUpDown.setText(R.string.caret_up);
                    tvComprehensiveUpDown.setTag("up");
                }

                if (comprehensiveWindow != null) {
                    if (comprehensiveWindow.isShowing()) {//正在显示
                        //关闭popupwindow
                        comprehensiveWindow.dismiss();
                    } else {
                        //显示
                        comprehensiveWindow.showAsDropDown(tvComprehensive);
                    }
                } else {
                    //显示综合排序列表
                    showComprehensiveWindow();
                }
            }
        });
        //分类
        tvClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变三角形图标
                if("up".equals(tvClassifyUpDown.getTag())){
                    //设置三角形为向下的
                    tvClassifyUpDown.setText(R.string.caret_down);
                    tvClassifyUpDown.setTag("down");
                }else{
                    tvClassifyUpDown.setText(R.string.caret_up);
                    tvClassifyUpDown.setTag("up");
                }

                if (classifyWindow != null) {
                    if (classifyWindow.isShowing()) {//正在显示popupwindow
                        //关闭
                        classifyWindow.dismiss();
                    } else {
                        classifyWindow.showAsDropDown(tvClassify);
                    }
                } else {
                    //显示分类列表
                    showClassifyWindow();
                }
            }
        });
    }

    /**
     * 综合
     */
    private void showComprehensiveWindow(){
        if(!courseType.isEmpty()){//加载出来了数据,显示popupwindow
            View view = LayoutInflater.from(this).inflate(R.layout.window_comprehensive,null);
            TextView comprehensive = (TextView) view.findViewById(R.id.tv_comprehensive_comprehensive);//综合
            comprehensive.setOnClickListener(this);
            TextView recentlyNew = (TextView) view.findViewById(R.id.tv_comprehensive_new);//最新
            recentlyNew.setOnClickListener(this);
            TextView price = (TextView) view.findViewById(R.id.tv_comprehensive_price);//价格
            price.setOnClickListener(this);
            TextView popular = (TextView) view.findViewById(R.id.tv_comprehensive_popularity);//人气
            popular.setOnClickListener(this);
            //自定义popupwindow
            comprehensiveWindow = new PopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //点击外部popupwindow消失
            comprehensiveWindow.setBackgroundDrawable(new BitmapDrawable());
            comprehensiveWindow.showAsDropDown(tvComprehensive);
        }
    }

    /**
     * 分类
     */
    private void showClassifyWindow(){
        if(!courseType.isEmpty()){//加载出来了数据,显示popupwindow
            View view = LayoutInflater.from(this).inflate(R.layout.window_classify,null);
            //自定义popupwindow
            classifyWindow = new PopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            classifyWindow.setBackgroundDrawable(new BitmapDrawable());//点击外部popupwindow消失

            AutoLinearLayout layout = (AutoLinearLayout) view.findViewById(R.id.ll_window_comprehensice);
            //添加分类
            for(int i=0;i<courseType.size();i++){
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(params);
                textView.setPadding(20, 20, 20, 20);
                textView.setText(courseType.get(i).getName());
                textView.setTextColor(Color.WHITE);
                textView.setClickable(true);
                textView.setBackgroundResource(R.drawable.search_background);
                setMyOnClickListener(textView,i);
                //设置监听
                layout.addView(textView);
            }

            classifyWindow.showAsDropDown(tvClassify);
        }
    }

    /**
     * 获取数据
     * @param url
     * @param to_do
     * @param layout
     */
    private void getData(String url, final int to_do, final PullToRefreshLayout layout){
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                llNoContent.setVisibility(View.GONE);

                                Gson gson = new Gson();
                                AllPackage allPackage = gson.fromJson(response,AllPackage.class);
                                if(loadCount == 1){
                                    //第一次加载
                                    AllPackage.CourseTypeBean  bean = new AllPackage.CourseTypeBean();
                                    bean.setId("0");
                                    bean.setName("全部套餐");
                                    courseType.add(bean);
                                    courseType.addAll(allPackage.getCourse_type());//套餐分类
                                }
                                if(allPackage.getCourse_list().isEmpty()){
                                    if(to_do!=CommonValues.TO_LOAD){
                                        llNoContent.setVisibility(View.VISIBLE);
                                    }else{
                                        CommonMethod.toast(AllPackageActivity.this,"没有了~");
                                    }
                                }else{
                                    courseList.addAll(allPackage.getCourse_list());//套餐列表
                                    adapter.notifyDataSetChanged();
                                }
                            }else{
                                if(to_do!=CommonValues.TO_LOAD){
                                    llNoContent.setVisibility(View.VISIBLE);
                                }else{
                                    CommonMethod.toast(AllPackageActivity.this,"没有了~");
                                }
                            }
                            loadCount ++ ;
                            CommonMethod.dismissLoadingDialog();
                            CommonMethod.pullToRefreshSuccess(to_do,layout);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(AllPackageActivity.this);
                CommonMethod.dismissLoadingDialog();
                CommonMethod.pullToRefreshFail(to_do,layout);
            }
        });
        mQueue.add(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_comprehensive_comprehensive://综合
                order = "0";
                tvComprehensive.setText("综合");
                break;
            case R.id.tv_comprehensive_new://最新
                order = "1";
                tvComprehensive.setText("最新");
                break;
            case R.id.tv_comprehensive_price://价格
                order = "2";
                tvComprehensive.setText("价格");
                break;
            case R.id.tv_comprehensive_popularity://人气
                order = "3";
                tvComprehensive.setText("人气");
                break;
        }
        //设置综合三角形向上
        tvComprehensiveUpDown.setText(R.string.caret_up);
        tvComprehensiveUpDown.setTag("up");
        //重新加载数据
        page = 1;
        courseList.clear();
        getData(Urls.allpackageUrl(tid, order, page), CommonValues.NOT_TO_DO, null);
        adapter.notifyDataSetChanged();
        //关闭综合popupwindow
        comprehensiveWindow.dismiss();
    }

    /**
     * 设置监听
     * @param tv
     * @param index 选中的分类中TextView的下标
     */
    private void setMyOnClickListener(TextView tv, final int index){
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvClassify.setText(courseType.get(index).getName());
                //设置分类三角形向上
                tvClassifyUpDown.setText(R.string.caret_up);
                tvClassifyUpDown.setTag("up");
                //重新加载数据
                page =1;
                courseList.clear();
                tid = courseType.get(index).getId();
                getData(Urls.allpackageUrl(tid, order, page), CommonValues.NOT_TO_DO, null);
                adapter.notifyDataSetChanged();
                //关闭分类popupwindow
                classifyWindow.dismiss();
            }
        });
    }
}

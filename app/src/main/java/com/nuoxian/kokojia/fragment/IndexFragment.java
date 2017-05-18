package com.nuoxian.kokojia.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.AllPackageActivity;
import com.nuoxian.kokojia.activity.CourseDetailsActivity;
import com.nuoxian.kokojia.activity.CourseListActivity;
import com.nuoxian.kokojia.activity.FreeCourseActivity;
import com.nuoxian.kokojia.activity.HomeActivity;
import com.nuoxian.kokojia.activity.LimitTimeDiscountActivity;
import com.nuoxian.kokojia.activity.SearchActivity;
import com.nuoxian.kokojia.adapter.IndexFragmentAdapter;
import com.nuoxian.kokojia.adapter.IndexHotAdapter;
import com.nuoxian.kokojia.adapter.IndexSelectAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Index;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.view.ImageCycleView;
import com.nuoxian.kokojia.view.SodukuGridView;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 * 精选界面
 */
public class IndexFragment extends Fragment implements View.OnClickListener {

    private ArrayList<String> imagelist = new ArrayList();
    private PullToRefreshLayout layout;
    private View loadding;
    private PullableListView mListView;
    private AutoRelativeLayout program,design,web,mobile,game,dataBase,office,other;
    private AutoLinearLayout llDiscount,llPackage,llFree;
    private RequestQueue mQueue;
    private int getDataCount = 0;//获取数据的次数
    private ImageCycleView cycleView;
    private List<Index.CourseListBean> courseList;//课程列表
    private List<Index.CourseJpBean> selectList = new ArrayList<>();//精选
    private List<Index.CourseHotBean> hotList = new ArrayList<>();//热门
    private List<Index.IndexAdBean> cycleImageList = new ArrayList<>();//轮播图
    private IndexFragmentAdapter adapter;
    private IndexSelectAdapter selectAdapter;
    private IndexHotAdapter hotAdapter;
    private AutoLinearLayout reload;
    private TextView tvSearch;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mQueue = MyApplication.getRequestQueue();
        View view = inflater.inflate(R.layout.fragment_index,container,false);
        initView(view);
        //初始化轮播图
        initCycleImage(view);
        //加载适配器
        courseList = new ArrayList<>();
        adapter = new IndexFragmentAdapter(courseList,getContext());
        mListView.setAdapter(adapter);
        getData(Urls.indexUrl(page), CommonValues.NOT_TO_DO,null);
        return view;
    }

    /**
     * 初始化视图
     * @param view
     */
    private void initView(View view){
        CommonMethod.setFontAwesome(view.findViewById(R.id.layout_index_fragment),getContext());

        tvSearch = (TextView) view.findViewById(R.id.tv_index_search);
        tvSearch.setOnClickListener(this);
        layout = (PullToRefreshLayout) view.findViewById(R.id.layout_refresh);
        mListView = (PullableListView) view.findViewById(R.id.lv_index);
        loadding = view.findViewById(R.id.loading);
        reload = (AutoLinearLayout) view.findViewById(R.id.ll_reload);
        reload.setOnClickListener(this);
        //轮播图
        View cycleImages = LayoutInflater.from(getContext()).inflate(R.layout.layout_index_head_images,null);
        //分类
        View classify = LayoutInflater.from(getContext()).inflate(R.layout.layout_index_head_classify,null);
        //加载FONTAWESOME
        Typeface typeface = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(classify.findViewById(R.id.ll_index_classify), typeface);
        program = (AutoRelativeLayout) classify.findViewById(R.id.rl_index_program_language);
        program.setOnClickListener(this);
        design = (AutoRelativeLayout) classify.findViewById(R.id.rl_index_design);
        design.setOnClickListener(this);
        web = (AutoRelativeLayout) classify.findViewById(R.id.rl_index_web);
        web.setOnClickListener(this);
        mobile = (AutoRelativeLayout) classify.findViewById(R.id.rl_index_mobile);
        mobile.setOnClickListener(this);
        game = (AutoRelativeLayout) classify.findViewById(R.id.rl_index_game);
        game.setOnClickListener(this);
        dataBase = (AutoRelativeLayout) classify.findViewById(R.id.rl_index_database);
        dataBase.setOnClickListener(this);
        office = (AutoRelativeLayout) classify.findViewById(R.id.rl_index_office);
        office.setOnClickListener(this);
        other = (AutoRelativeLayout) classify.findViewById(R.id.rl_index_other);
        other.setOnClickListener(this);
        llDiscount = (AutoLinearLayout) classify.findViewById(R.id.ll_discount);
        llDiscount.setOnClickListener(this);
        llPackage = (AutoLinearLayout) classify.findViewById(R.id.ll_package);
        llPackage.setOnClickListener(this);
        llFree = (AutoLinearLayout) classify.findViewById(R.id.ll_free);
        llFree.setOnClickListener(this);
        //精选
        View select = LayoutInflater.from(getContext()).inflate(R.layout.item_index_select,null);
        CommonMethod.setFontAwesome(select,getContext());
        SodukuGridView selectGridView = (SodukuGridView) select.findViewById(R.id.gv_item_index_select);
        selectAdapter = new IndexSelectAdapter(selectList,getContext());
        selectGridView.setAdapter(selectAdapter);
        selectGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情页面
                intentToCorseDetails(selectList.get(position).getId());
            }
        });
        //热门
        View hot = LayoutInflater.from(getContext()).inflate(R.layout.item_index_hot,null);
        CommonMethod.setFontAwesome(hot,getContext());
        SodukuGridView hotGridView = (SodukuGridView) hot.findViewById(R.id.gv_item_index_hot);
        hotAdapter = new IndexHotAdapter(hotList,getContext());
        hotGridView.setAdapter(hotAdapter);
        hotGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intentToCorseDetails(hotList.get(position).getId());
            }
        });
        //添加头部
        mListView.addHeaderView(cycleImages);
        mListView.addHeaderView(classify);
        mListView.addHeaderView(select);
        mListView.addHeaderView(hot);
        //上下拉刷新监听
        layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //刷新
                page = 1;
                courseList.clear();
                getData(Urls.indexUrl(page), CommonValues.TO_REFRESH, pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //加载
                page++;
                getData(Urls.indexUrl(page),CommonValues.TO_LOAD,pullToRefreshLayout);
                adapter.notifyDataSetChanged();
            }
        });
        //listview的点击监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情页,因为listview头部加了4个布局所以要-4
                intentToCorseDetails(courseList.get(position-4).getId());
            }
        });
    }

    /**
     * 初始化轮播图
     * @param view
     */
    private void initCycleImage(View view){
        AutoLinearLayout layout = (AutoLinearLayout) view.findViewById(R.id.layout_index_head);
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //获取屏幕宽度
        int screenWidth = dm.widthPixels;
        //创建轮播视图
        cycleView = new ImageCycleView(getContext());
        //设置宽高
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,screenWidth/2);
        cycleView.setLayoutParams(params);
        //添加轮播视图
        layout.addView(cycleView);
    }

    /**
     * 设置轮播图
     */
    private void setCycleImage(){
        ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
                /**实现点击事件*/
                intentToCorseDetails(cycleImageList.get(position).getCourse_id());
            }
            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                /**在此方法中，显示图片，可以用自己的图片加载库，也可以用本demo中的（Imageloader）*/
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                MyApplication.imageLoader.displayImage(imageURL,imageView,MyApplication.options);
            }
        };
        //加载轮播图
        cycleView.setImageResources(imagelist, mAdCycleViewListener);
        cycleView.startImageCycle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_reload://重新加载
                reload.setVisibility(View.GONE);
                CommonMethod.showLoadingDialog("正在加载...", getContext());
                getData(Urls.indexUrl(page),CommonValues.NOT_TO_DO,null);
                break;
            case R.id.tv_index_search://搜索
                CommonMethod.startActivity(getContext(), SearchActivity.class);
                break;
            case R.id.rl_index_program_language://编程语言
                intentToCourseList(3,1,"编程语言","编程语言");
                break;
            case R.id.rl_index_design://设计
                intentToCourseList(4,1,"设计","设计");
                break;
            case R.id.rl_index_web://web开发
                intentToCourseList(146,1,"web开发","web开发");
                break;
            case R.id.rl_index_mobile://移动开发
                intentToCourseList(200,1,"移动开发","移动开发");
                break;
            case R.id.rl_index_game://游戏开发
                intentToCourseList(11,1,"游戏开发","游戏开发");
                break;
            case R.id.rl_index_database://考试认证
                intentToCourseList(336,1,"考试认证","考试认证");
                break;
            case R.id.rl_index_office://office
                intentToCourseList(325,1,"Office","Office");
                break;
            case R.id.rl_index_other://更多
                RadioButton rb = (RadioButton) HomeActivity.mRadioGroup.getChildAt(1);
                rb.setChecked(true);
                break;
            case R.id.ll_discount://限时折扣
                CommonMethod.startActivity(getContext(), LimitTimeDiscountActivity.class);
                break;
            case R.id.ll_package://套餐
                CommonMethod.startActivity(getContext(), AllPackageActivity.class);
                break;
            case R.id.ll_free://免费
                CommonMethod.startActivity(getContext(), FreeCourseActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * 获取数据
     */
    private void getData(String url, final int TO_DO, final PullToRefreshLayout layout){
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j1 = new JSONObject(response);
                    if("1".equals(j1.getString("status"))){
                        Gson gson = new Gson();
                        Index index = gson.fromJson(response,Index.class);
                        if(getDataCount==0){//第一次获取数据
                            //添加精选
                            selectList.clear();
                            selectList.addAll(index.getCourse_jp());
                            selectAdapter.notifyDataSetChanged();
                            //添加热门
                            hotList.clear();
                            hotList.addAll(index.getCourse_hot());
                            hotAdapter.notifyDataSetChanged();
                        }
                        //加载轮播图
                        imagelist.clear();
                        cycleImageList.clear();
                        cycleImageList.addAll(index.getIndex_ad());
                        for(int i=0;i<index.getIndex_ad().size();i++){
                            imagelist.add(index.getIndex_ad().get(i).getImg());
                        }
                        setCycleImage();

                        getDataCount++;
                        //加载listview
                        courseList.addAll(index.getCourse_list());
                        adapter.notifyDataSetChanged();
                    }else{
                        CommonMethod.toast(getContext(), "没有了~");
                    }
                    CommonMethod.dismissLoadingDialog();
                    CommonMethod.pullToRefreshSuccess(TO_DO,layout);
                    loadding.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(getContext());
                CommonMethod.pullToRefreshFail(TO_DO, layout);
                if(getDataCount==0){//第一次加载，现在加载失败
                    reload.setVisibility(View.VISIBLE);
                }
                CommonMethod.dismissLoadingDialog();
            }
        });
        mQueue.add(request);
    }

    /**
     * 跳转到课程详情页
     */
    private void intentToCorseDetails(String id){
        Intent intent = new Intent(getContext(), CourseDetailsActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    /**
     * 跳转到课程列表页面
     */
    private void intentToCourseList(int urlId,int urlPage,String title,String classify){
        Intent intent = new Intent(getContext(), CourseListActivity.class);
        intent.putExtra("urlId",urlId);
        intent.putExtra("urlPage",urlPage);
        intent.putExtra("title",title);
        intent.putExtra("classify",classify);
        startActivity(intent);
    }
}

package com.nuoxian.kokojia.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.LoginActivity;
import com.nuoxian.kokojia.activity.PackageDetailsActivity;
import com.nuoxian.kokojia.activity.PlayOnlineActivity;
import com.nuoxian.kokojia.activity.SchoolIndexActivity;
import com.nuoxian.kokojia.adapter.CourseDetailsPackageAdapter;
import com.nuoxian.kokojia.adapter.GetCouponAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Coupon;
import com.nuoxian.kokojia.enterty.CoursePackage;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.view.NoScrollListview;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 * 课程概述
 */
public class CourseDetailsFragment extends Fragment {

    private View view;
    private TextView tvTitle,tvDiscountPrice,tvCountDown,tvPrice,tvSchoolName,tvClassNum,tvEndTime,tvTarget,tvCrowd;
    private AutoLinearLayout tvTryListen;
    private AutoLinearLayout llTarget,llCrowd,llContent,llGetCoupon;
    private String lid,uid;
    private String id,school_id,school;
    private NoScrollListview mListView;
    private AutoLinearLayout llPackage;
    private List<CoursePackage.PackageListBean> mList;
    private List<Coupon.DataBean> couponList;
    private CourseDetailsPackageAdapter adapter;
    private ScrollView mScrollView;
    private GetCouponAdapter getCouponAdapter;
    private ImageView detailsrefresh;
    private TextView detailstext;
    private AutoLinearLayout detailsall;
    private String url;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_details, container, false);

        CommonMethod.setFontAwesome(view.findViewById(R.id.layout_course_detail_fragment),getContext());

        Bundle bundle = getArguments();
        id = bundle.getString("id");
        uid = CommonMethod.getUid(getContext());
        final SharedPreferences sp = getContext().getSharedPreferences("flag", getContext().MODE_PRIVATE);

        initView();

        mList = new ArrayList<>();
        adapter = new CourseDetailsPackageAdapter(mList,getContext());
        mListView.setAdapter(adapter);
        getData(Urls.getCourseDetialsUrl(id, sp.getString("data", "0")));

        //监听重新刷新按钮
        detailsrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //有网的情况下将刷新按钮隐藏
                detailsrefresh.setVisibility(View.GONE);
                detailstext.setVisibility(View.GONE);
                detailsall.setVisibility(View.VISIBLE);
                //重新加载数据
                getData(Urls.getCourseDetialsUrl(id, sp.getString("data", "0")));
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情界面
                Intent intent = new Intent(getContext(), PackageDetailsActivity.class);
                intent.putExtra("id",mList.get(position).getId());
                startActivity(intent);
            }
        });

        return view;
    }

    //初始化
    private void initView() {
        tvTitle = (TextView) view.findViewById(R.id.tv_details_title);
        tvDiscountPrice = (TextView) view.findViewById(R.id.tv_details_discount_price);
        tvCountDown = (TextView) view.findViewById(R.id.tv_details_countdown);
        tvPrice = (TextView) view.findViewById(R.id.tv_details_price);
        tvSchoolName = (TextView) view.findViewById(R.id.tv_details_school_name);
        tvClassNum = (TextView) view.findViewById(R.id.tv_details_class_num);
        tvEndTime = (TextView) view.findViewById(R.id.tv_details_endtime);
        tvTarget = (TextView) view.findViewById(R.id.tv_details_target);
        tvCrowd = (TextView) view.findViewById(R.id.tv_details_crowd);
        tvTryListen = (AutoLinearLayout) view.findViewById(R.id.tv_details_try_listen);
        mListView = (NoScrollListview) view.findViewById(R.id.lv_course_details_package);
        llPackage = (AutoLinearLayout) view.findViewById(R.id.ll_course_package);
        mScrollView = (ScrollView) view.findViewById(R.id.sv_course_details);
        llGetCoupon = (AutoLinearLayout) view.findViewById(R.id.ll_get_coupon);

        detailsrefresh=(ImageView)view.findViewById(R.id.details_refresh);
        detailstext=(TextView)view.findViewById(R.id.details_text);
       detailsall=(AutoLinearLayout)view.findViewById(R.id.layout_course_detail_fragment);

        llTarget = (AutoLinearLayout) view.findViewById(R.id.ll_details_target);
        llCrowd = (AutoLinearLayout) view.findViewById(R.id.ll_details_crowd);
        llContent = (AutoLinearLayout) view.findViewById(R.id.ll_details_content);
        llContent.removeAllViews();

        //试听
        tvTryListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击试听跳转到播放页面
                Intent intent = new Intent(getContext(), PlayOnlineActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("lid", lid);
                intent.putExtra("position", -1);
                startActivity(intent);
            }

        });
        //学院
        tvSchoolName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到学院首页页面
                Intent intent = new Intent(getContext(),SchoolIndexActivity.class);
                intent.putExtra("id",school_id);
                intent.putExtra("title",school);
                startActivity(intent);
            }
        });
        //领取优惠券
        llGetCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uid = CommonMethod.getUid(getContext());
                if("0".equals(uid)){
                    //没有登录
                    CommonMethod.startActivity(getContext(), LoginActivity.class);
                }else{
                    //显示可领取的优惠券
                    showCoupon();
                }
            }
        });

        //解决scrollview嵌套listview的焦点问题
        mScrollView.smoothScrollTo(0,20);
        mListView.setFocusable(false);
    }

    /**
     * 显示可领取的优惠券
     */
    private void showCoupon(){
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.window_get_coupon,null);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final PopupWindow window = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new BitmapDrawable());//点击外部popupwindow消失
        window.showAtLocation(view.findViewById(R.id.layout_course_detail_fragment), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        TextView complete = (TextView) popupView.findViewById(R.id.tv_get_coupon_complete);
        //完成
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });

        ListView listView = (ListView) popupView.findViewById(R.id.lv_get_coupon);
        couponList = new ArrayList<>();
        getCouponAdapter = new GetCouponAdapter(getContext(),couponList);
        listView.setAdapter(getCouponAdapter);
        //获取优惠券数据
        getCouponData();
    }

    /**
     * 获取优惠券数据
     */
    private void getCouponData(){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Urls.getCoupon(id, uid),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("haha", "onResponse: "+response);
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //获取优惠券数据
                                Gson gson = new Gson();
                                Coupon coupon = gson.fromJson(response,Coupon.class);
                                couponList.addAll(coupon.getData());
                                getCouponAdapter.notifyDataSetChanged();
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
        });
        mQueue.add(request);
    }

    /**
     * 获取数据
     */
    private void getData(String url){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        final StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //获取相关套餐信息
                Gson gson = new Gson();
                CoursePackage packge = gson.fromJson(response,CoursePackage.class);
                if(packge.getPackage_list()!=null&&packge.getPackage_list().size()!=0){
                    //有相关套餐
                    mList.addAll(packge.getPackage_list());
                    adapter.notifyDataSetChanged();
                    llPackage.setVisibility(View.VISIBLE);
                }else{
                    llPackage.setVisibility(View.GONE);
                }
                //获取其他信息
                parseJson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //网络错误的情况下显示点击刷新的图标
                detailsall.setVisibility(View.GONE);//关闭之前的界面
                detailsrefresh.setVisibility(View.VISIBLE);
                detailstext.setVisibility(View.VISIBLE);
                CommonMethod.loadFailureToast(getContext());
            }
        });
        mQueue.add(request);
    }

    /**
     * 解析数据
     * @param json
     */
    private void parseJson(String json){
        try {
            JSONObject j1 = new JSONObject(json);
            if("1".equals(j1.getString("status"))){
                JSONObject j2 = j1.getJSONObject("course");
                //标题
                tvTitle.setText(j2.getString("title"));
                //价格
                if(TextUtils.isEmpty(j2.optString("discount_price"))){//没有打折
                    tvDiscountPrice.setText(j2.getString("price"));
                    tvCountDown.setVisibility(View.GONE);
                    tvPrice.setVisibility(View.GONE);
                }else{//打折
                    tvDiscountPrice.setText(j2.optString("discount_price"));
                    tvCountDown.setVisibility(View.VISIBLE);
                    tvCountDown.setText(j2.optString("countdown"));
                    tvPrice.setVisibility(View.VISIBLE);
                    tvPrice.setText(j2.getString("price"));
                    //添加中划线
                    tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
                //试听
                lid = j2.getString("course_trial_id");
                if(!TextUtils.isEmpty(lid)){//如果不为空说明可以试听
                    //显示试听按钮
                    tvTryListen.setVisibility(View.VISIBLE);
                }
                //课堂名
                school = j2.getString("school_name");
                tvSchoolName.setText(school);
                school_id = j2.getString("school_id");
                //课时
                tvClassNum.setText(j2.getString("class_num"));
                //有效期
                tvEndTime.setText(j2.getString("endtime"));
                //是否有优惠券
                if("1".equals(j2.getString("is_coupon"))) {//有
                    llGetCoupon.setVisibility(View.VISIBLE);
                }else{//没有
                    llGetCoupon.setVisibility(View.GONE);
                }
                //课程目标
                if(TextUtils.isEmpty(j2.optString("target"))){//没有内容
                    //隐藏
                    llTarget.setVisibility(View.GONE);
                }else{//有内容
                    llTarget.setVisibility(View.VISIBLE);
                    tvTarget.setText(j2.optString("target"));
                }
                //适合人群
                if(TextUtils.isEmpty(j2.optString("crowd"))){
                    llCrowd.setVisibility(View.GONE);
                }else{
                    llCrowd.setVisibility(View.VISIBLE);
                    tvCrowd.setText(j2.optString("crowd"));
                }
                //正文
                JSONArray j3 = j2.getJSONArray("content");
                for(int i=0;i<j3.length();i++){
                    JSONObject j4 = j3.getJSONObject(i);
                    if("1".equals(j4.getString("type"))){//内容为图片
                        ImageView iv = new ImageView(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        iv.setLayoutParams(params);
                        iv.setScaleType(ImageView.ScaleType.CENTER);
                        llContent.addView(iv);
                        MyApplication.imageLoader.displayImage(j4.getString("desc"),iv,MyApplication.options);
                    }else{//内容为文字
                        TextView tv = new TextView(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        tv.setLayoutParams(params);
                        llContent.addView(tv);
                        tv.setText(j4.getString("desc"));
                    }
                }
            }else{
                CommonMethod.toast(getContext(),"没有了");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

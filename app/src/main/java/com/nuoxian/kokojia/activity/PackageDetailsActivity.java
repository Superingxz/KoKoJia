package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.view.NoScrollListview;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 套餐详情页面
 */
public class PackageDetailsActivity extends BaseActivity {

    private NoScrollListview mListView;
    private TextView type1,type2,title,price,discount,originalPrice,courseCount,classNum,introduce;
    private AutoLinearLayout school;
    private ImageView imageView;
    private PackageDetailsAdapter adapter;
    private String id,uid;
    private List<Course> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_package_details);

        CommonMethod.showLoadingDialog("正在加载", this);
        uid = CommonMethod.getUid(this);
        id = getIntent().getStringExtra("id");

        initView();
        //加载适配器
        adapter = new PackageDetailsAdapter(this,list);
        mListView.setAdapter(adapter);
        //获取数据
        getData(Urls.packageDetailsUrl(id, uid));
        //listView的监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到课程详情页面
                Intent intent = new Intent(PackageDetailsActivity.this, CourseDetailsActivity.class);
                intent.putExtra("id", list.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void initView(){
        Typeface t = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.layout_package_details), t);
        //设置标题
        setTitle("套餐详情");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageDetailsActivity.this.finish();
            }
        });
        mListView = (NoScrollListview) findViewById(R.id.lv_package_details);
        mListView.setFocusable(false);
        type1 = (TextView) findViewById(R.id.tv_package_details_type1);
        type2 = (TextView) findViewById(R.id.tv_package_details_type2);
        title = (TextView) findViewById(R.id.tv_package_details_title);
        price = (TextView) findViewById(R.id.tv_package_details_price);
        discount = (TextView) findViewById(R.id.tv_package_details_discount);
        originalPrice = (TextView) findViewById(R.id.tv_package_details_original_price);
        courseCount = (TextView) findViewById(R.id.tv_package_details_course_count);
        classNum = (TextView) findViewById(R.id.tv_package_details_class_num);
        introduce = (TextView) findViewById(R.id.tv_package_details_introduce);
        school = (AutoLinearLayout) findViewById(R.id.ll_package_details_school);
        imageView = (ImageView) findViewById(R.id.iv_details_title);
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
                                JSONObject j2 = j1.getJSONObject("course");
                                title.setText(j2.getString("title"));//标题
                                introduce.setText(j2.getString("content"));//套餐介绍
                                courseCount.setText(j2.getString("course_count")+"套课程");//课程数
                                price.setText(j2.getString("price"));//套餐价
                                originalPrice.setText(j2.getString("original_price"));//原价
                                originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                classNum.setText(j2.getString("package_lesson_total")+"节课时");//课时
                                discount.setText("优惠"+j2.getString("youhui_price"));//优惠
                                JSONObject j3 = j2.getJSONObject("type_one");
                                type1.setText(j3.getString("name")+">");
                                JSONObject j4 = j2.getJSONObject("type_two");
                                type2.setText(j4.getString("name"));

                                JSONArray j7 = j2.getJSONArray("package_school");
                                for (int i=0;i<j7.length();i++){//学院有可能有多个，用for循环动态添加
                                    JSONObject j8 = j7.getJSONObject(i);
                                    TextView tv = new TextView(PackageDetailsActivity.this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(3, 0, 0, 0);
                                    tv.setLayoutParams(params);
                                    tv.setText(j8.getString("school_name"));
                                    tv.setTextColor(0xff00aeff);
                                    final String schoolId = j8.getString("school_id");
                                    //设置点击时间
                                    tv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //跳转到学院首页页面
                                            Intent intent = new Intent(PackageDetailsActivity.this,SchoolIndexActivity.class);
                                            intent.putExtra("id",schoolId);
                                            startActivity(intent);
                                        }
                                    });
                                    school.addView(tv);
                                }

                                JSONArray j5 = j2.getJSONArray("courselist");
                                for (int i=0;i<j5.length();i++){
                                    Course course = new Course();
                                    JSONObject j6 = j5.getJSONObject(i);
                                    course.setId(j6.getString("id"));
                                    course.setTrial_num(j6.getString("trial_num"));
                                    course.setCountdown(j6.optString("discount_end_date"));
                                    course.setPrice(j6.getString("price"));
                                    course.setDiscount_price(j6.optString("discount_price"));
                                    course.setClass_num(j6.getString("class_num"));
                                    course.setImage_url(j6.getString("image_url"));
                                    course.setTitle(j6.getString("title"));
                                    course.setIs_paid(j6.optString("is_paid"));
                                    list.add(course);
                                }
                                adapter.notifyDataSetChanged();
                                //collect_package 0未收藏1已收藏
                                //buy_status 1已购买2未购买3已下架
                                MyApplication.imageLoader.displayImage(j2.getString("image_url"), imageView, MyApplication.options);
                                CommonMethod.dismissLoadingDialog();
                            }else{
                                CommonMethod.toast(PackageDetailsActivity.this,"没有数据");
                                CommonMethod.dismissLoadingDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(PackageDetailsActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        });
        mQueue.add(request);
    }
}

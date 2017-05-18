package com.nuoxian.kokojia.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.CourseListActivity;
import com.nuoxian.kokojia.activity.SearchActivity;
import com.nuoxian.kokojia.adapter.ClassificationFragmentAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseCatalog;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.view.expandgridview.CustomGroup;
import com.nuoxian.kokojia.view.expandgridview.model.DargChildInfo;
import com.nuoxian.kokojia.view.expandgridview.model.DragIconInfo;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 */
public class ClassificationFragment extends Fragment {

    private TextView tvSearch, tvAll;
    private AutoLinearLayout reload;
    private CustomGroup mCustomGroup;
    private ArrayList<DragIconInfo> iconInfoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classification, container, false);
        initView(view);
        //加载适配器
        //获取数据
        getData(Urls.CLASSIFICATION_URL);
        //设置监听
        setListener();
        return view;
    }

    private void initView(View view) {
        mCustomGroup = (CustomGroup) view.findViewById(R.id.tv_classify_custom);
        tvSearch = (TextView) view.findViewById(R.id.tv_classify_search);
        Typeface typeface = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(tvSearch, typeface);
        reload = (AutoLinearLayout) view.findViewById(R.id.ll_reload);
        tvAll = (TextView) view.findViewById(R.id.tv_classify_all);
    }

    /**
     * 设置监听
     */
    private void setListener() {
        //搜索按钮监听
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMethod.startActivity(getContext(), SearchActivity.class);
            }
        });
        //全部课程
        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到课程列表界面，显示全部课程
                Intent intent = new Intent(getContext(), CourseListActivity.class);
                intent.putExtra("urlId", 2);
                intent.putExtra("urlPage", 1);
                intent.putExtra("title", "IT培训");
                getContext().startActivity(intent);
            }
        });
        //重新加载
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload.setVisibility(View.GONE);
                getData(Urls.CLASSIFICATION_URL);
            }
        });
    }

    /**
     * 获取数据
     *
     * @param url
     */
    private void getData(String url) {
        iconInfoList.clear();
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j1 = new JSONObject(response);
                    if ("1".equals(j1.getString("status"))) {
                        JSONArray j2 = j1.getJSONArray("course_type");
                        for (int i = 1; i < j2.length(); i++) {//因为第一个是全部课程所以不添加进来，从1开始
                            DragIconInfo info = new DragIconInfo();
                            JSONObject j3 = j2.getJSONObject(i);
                            info.setId(i);
                            info.setClassId(j3.getString("course_type_id"));//大分类id
                            info.setName(j3.getString("course_type_name"));//分类名称
                            info.setResIconId("http://www.kokojia.com/Public/course_type_icon/"+j3.getString("course_type_id")+".png");//分类图片
                            JSONArray j4 = j3.optJSONArray("type_two");
                            ArrayList<DargChildInfo> childList = new ArrayList<>();
                            if(j4!=null&&j4.length()>0){
                                DargChildInfo child = new DargChildInfo();
                                child.setId(0);
                                child.setClassId(j3.getString("course_type_id"));//大分类id
                                child.setName("全部");
                                child.setCourseName(j3.getString("course_type_name"));
                                childList.add(child);
                                for (int j = 0; j < j4.length(); j++) {
                                    JSONObject j5 = j4.getJSONObject(j);
                                    DargChildInfo childInfo = new DargChildInfo();
                                    childInfo.setId(j+1);
                                    childInfo.setClassId(j5.getString("id"));//小分类id
                                    childInfo.setName(j5.getString("name"));
                                    childInfo.setClassName(j3.getString("course_type_name"));
                                    childList.add(childInfo);
                                }
                                info.setCategory(DragIconInfo.CATEGORY_EXPAND);//设置可拓展
                            }else{
                                info.setCategory(DragIconInfo.CATEGORY_ONLY);//设置不可拓展
                            }
                            info.setChildList(childList);
                            iconInfoList.add(info);
                            //加载分类
                            mCustomGroup.initData(iconInfoList);
                        }
                    } else {
                        CommonMethod.toast(getContext(), "找不到数据");
                        //显示重新加载
                        reload.setVisibility(View.VISIBLE);
                    }
                    CommonMethod.dismissLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(getContext());
                //显示重新加载
                reload.setVisibility(View.VISIBLE);
                CommonMethod.dismissLoadingDialog();
            }
        });
        mQueue.add(request);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}

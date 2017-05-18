package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.zhy.autolayout.AutoLayoutActivity;

public class BaseActivity extends AutoLayoutActivity {

    private TextView tvTitle,tvBack,tvSearch;
    private LinearLayout llContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //设置标题栏的颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        //展示fontawesome
        Typeface typeface = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.ll_base),typeface);

        initView();
    }

    //初始化视图
    private void initView(){
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        //搜索按钮监听
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到搜索页面
                CommonMethod.startActivity(BaseActivity.this, SearchActivity.class);
            }
        });
    }

    /**
     * 显示搜索按钮
     */
    public void showSearchButton(){
        tvSearch.setVisibility(View.VISIBLE);
    }

    /**
     * 返回点击时间
     * @param listener
     */
    public void setReturn(View.OnClickListener listener){
        tvBack.setOnClickListener(listener);
    }

    /**
     * 添加正文布局
     */
    public void addContentView(int contentViewId){
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        llContent.addView(getLayoutInflater().inflate(contentViewId,null));
    }

    /**
     * 设置标题内容
     * @param title
     */
    public void setTitle(String title){
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
    }

    /**
     * 设置标题不可见的
     */
    public void setTitleInvisible(){
        tvTitle.setVisibility(View.INVISIBLE);
    }
}

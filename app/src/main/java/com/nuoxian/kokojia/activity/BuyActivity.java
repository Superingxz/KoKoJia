package com.nuoxian.kokojia.activity;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * 购买页面
 */
public class BuyActivity extends AutoLayoutActivity implements View.OnClickListener {

    private TextView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        //设置标题栏颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        initView();
    }

    private void initView(){
        Typeface tf = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        ivBack = (TextView) findViewById(R.id.iv_buy_back);
        ivBack.setTypeface(tf);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_buy_back:
                finish();
                break;
        }
    }
}

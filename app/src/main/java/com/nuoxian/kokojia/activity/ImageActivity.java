package com.nuoxian.kokojia.activity;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.utils.FontManager;
import com.zhy.autolayout.AutoLayoutActivity;

public class ImageActivity extends AutoLayoutActivity {

    private PhotoView imageView;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        tvBack = (TextView) findViewById(R.id.tv_back);
        Typeface type = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(tvBack, type);
        //返回监听
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回
                ImageActivity.this.finish();
            }
        });
        imageView = (PhotoView) findViewById(R.id.show);
        //加载图片
        MyApplication.imageLoader.displayImage(getIntent().getStringExtra("url"), imageView, MyApplication.options);
        // 启用图片缩放功能
        imageView.enable();
        float maxscal = imageView.getMaxScale();
        //设置 最大缩放倍数
        imageView.setMaxScale(maxscal);
    }

}

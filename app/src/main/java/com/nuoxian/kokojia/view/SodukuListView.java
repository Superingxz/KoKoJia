package com.nuoxian.kokojia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by 陈思龙 on 2016/7/1.
 */
public class SodukuListView extends GridView {

    public SodukuListView(Context context) {
        super(context);
    }

    public SodukuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SodukuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
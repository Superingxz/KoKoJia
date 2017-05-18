package com.nuoxian.kokojia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by 陈思龙 on 2016/7/1.
 * 自定义的gridview可以在listview中嵌套
 */
public class SodukuGridView extends GridView {

    public SodukuGridView(Context context) {
        super(context);
    }

    public SodukuGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SodukuGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
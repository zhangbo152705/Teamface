package com.hjhq.teamface.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2017/4/5.
 */

public class MineGridView extends GridView {
    public MineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MineGridView(Context context) {
        super(context);
    }

    public MineGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
package com.hjhq.teamface.custom.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * @Created SiberiaDante
 * @Describe：
 * @CreateTime: 2017/12/17
 * @UpDateTime:
 * @Email: 2654828081@qq.com
 * @GitHub: https://github.com/SiberiaDante
 */

public class JudgeNestedScrollView extends NestedScrollView {
    private boolean isNeedScroll = true;
    private float xDistance, yDistance, xLast, yLast;
    private int scaledTouchSlop;

    public JudgeNestedScrollView(Context context) {
        super(context, null);
    }

    public JudgeNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public JudgeNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                if (xDistance > 0.5 && yDistance > 0.5 && yDistance > xDistance) {
                    return true;
                }
                if (xDistance < 0.5 && yDistance < 0.5) {
                    return false;
                }
                if (yDistance > 5 && yDistance > xDistance) {
                    return true;
                }
                Log.e("SiberiaDante", isNeedScroll + "---xDistance ：" + xDistance + "---yDistance:" + yDistance + "-----scaledTouchSlop:" + scaledTouchSlop);
                return !(xDistance > yDistance || yDistance < scaledTouchSlop) && isNeedScroll;

        }
        return super.onInterceptTouchEvent(ev);
    }

    /*
        改方法用来处理NestedScrollView是否拦截滑动事件
         */
    public void setNeedScroll(boolean isNeedScroll) {
        this.isNeedScroll = isNeedScroll;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        //计算子类滚动距离
        return 0;
//        return super.computeScrollDeltaToGetChildRectOnScreen(rect);
    }
}

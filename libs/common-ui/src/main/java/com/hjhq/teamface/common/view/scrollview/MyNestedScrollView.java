package com.hjhq.teamface.common.view.scrollview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;


/**
 * @author Zhenhua on 2017/6/2 09:46.
 * @email zhshan@ctrip.com
 */

public class MyNestedScrollView extends NestedScrollView {

    private int scaledTouchSlop;
    private float xDistance, yDistance, xLast, yLast, xStart, yStart;
    private View fixView;
    private OnFixListener listener;
    private boolean fixed;
    private boolean isNeedScroll = true;

    private int mScrollY = 0;
    private int mBannerHeight = 0;

    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }

    public MyNestedScrollView(Context context) {
        super(context);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (getScrollY() >= fixView.getTop()) {
            fix();
        } else {
            dismiss();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            LinearLayout linearLayout = (LinearLayout) getChildAt(0);
            if (linearLayout != null) {
                for (int i = 0; i < linearLayout.getChildCount(); i++) {
                    if (linearLayout.getChildAt(i).getTag() != null && linearLayout.getChildAt(i).getTag().equals("fix")) {
                        fixView = linearLayout.getChildAt(i);
                    }
                }
            }
        }
    }


    public void setFixListener(OnFixListener listener) {
        this.listener = listener;
    }

    private void fix() {
        if (listener != null && !fixed) {
            listener.onFix();
            fixed = true;
        }
    }

    private void dismiss() {
        if (listener != null && fixed) {
            listener.onDismiss();
            fixed = false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    boolean flag = true;
    boolean isVertical = true;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xStart = ev.getX();
                yStart = ev.getY();
                // Log.v("起始位置", xStart + "#######" + yStart);
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - xStart);
                yDistance += Math.abs(curY - yStart);
                // Log.v("移动位置", curX + "#######" + curY);
                return  super.onInterceptTouchEvent(ev);
                /*if (7 > 0) {
                    return false;
                }

                if (xDistance > 0.5 && yDistance > 0.5 && yDistance > xDistance) {
                    return true;
                }
                if (xDistance < 0.5 && yDistance < 0.5) {
                    return false;
                }
                if (yDistance > 5 && yDistance > xDistance) {
                    return true;
                }*/
                //return super.onInterceptTouchEvent(ev);
                //return !(xDistance > yDistance || yDistance < scaledTouchSlop) && isNeedScroll;
            case MotionEvent.ACTION_UP:
                float x = ev.getX();
                float y = ev.getY();
                flag = false;
                isVertical = false;
               /* postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mScrollY <= mBannerHeight / 2) {
                            scrollTo(0, 0);
                        } else {
                            scrollTo(0, (int) DeviceUtils.dpToPixel(getContext(), 181));
                        }
                        requestLayout();
                    }
                }, 100);*/

                break;

           /* case MotionEvent.ACTION_UP:
                float upX = ev.getX();
                float upY = ev.getY();
                if (Math.abs(upX - xStart) < 10 || Math.abs(upY - yStart) < 10) {
                    return false;
                }
                break;*/

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setScrollY(int scrollY) {
        mScrollY = scrollY;
    }

    public void setBannerHeight(int bannerHeight) {
        mBannerHeight = bannerHeight;
    }

    public interface OnFixListener {
        void onFix();

        void onDismiss();
    }

    public void setNeedScroll(boolean isNeedScroll) {
        this.isNeedScroll = isNeedScroll;

    }
}

package com.hjhq.teamface.common.view;

import android.content.Context;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

/**
 * 带颜色渐变和缩放的指示器标题
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public class ScaleTransitionPagerTitleView extends ColorTransitionPagerTitleView {
    private float mMinScale = 0.9f;
    private OnSelectChangeListener mOnSelectChangeListener;

    public ScaleTransitionPagerTitleView(Context context) {
        super(context);
        this.setPadding(0, 0, 0, 0);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);
        // 实现颜色渐变
        setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
        setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);
        if (mOnSelectChangeListener != null) {
            mOnSelectChangeListener.onEnter(index);
        }
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);
        // 实现颜色渐变
        setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
        setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);
        if (mOnSelectChangeListener != null) {
            mOnSelectChangeListener.onLeave(index);
        }
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }


    public interface OnSelectChangeListener {
        void onEnter(int index);

        void onLeave(int index);
    }

    public void setOnSelectChangeListener(OnSelectChangeListener listener) {
        mOnSelectChangeListener = listener;
    }
}

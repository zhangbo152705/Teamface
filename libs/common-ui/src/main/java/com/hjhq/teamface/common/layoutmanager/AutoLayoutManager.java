package com.hjhq.teamface.common.layoutmanager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.log.LogUtil;

public class AutoLayoutManager extends RecyclerView.LayoutManager {
    private int lineNum = 0;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        LogUtil.e("EditText == MyLayoutManager");
        int sumWidth = getWidth();
        LogUtil.e("当前宽度==" + sumWidth);
        int curLineWidth = 0, curLineTop = 0, firstLineTop = 0;
        int lastLineMaxHeight = 0;
        int childCount = getChildCount();

        for (int i = 0; i < getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);
            ViewGroup.LayoutParams layoutParams1 = view.getLayoutParams();
            int width = 0;
            int height = 0;
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            view.setLayoutParams(layoutParams);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            width = getDecoratedMeasuredWidth(view);
            height = getDecoratedMeasuredHeight(view);
            curLineWidth += width;
            if (i == 0) {
                firstLineTop = getDecoratedTop(view);
            }
            if (curLineWidth <= sumWidth) {//不需要换行  
                layoutDecorated(view, curLineWidth - width, curLineTop, curLineWidth, curLineTop + height);
                //比较当前行多有item的最大高度  
                lastLineMaxHeight = Math.max(lastLineMaxHeight, height);
            } else {//换行  
                curLineWidth = width;
                if (lastLineMaxHeight == 0) {
                    lastLineMaxHeight = height;
                }
                //记录当前行top  
                curLineTop += lastLineMaxHeight;

                layoutDecorated(view, 0, curLineTop, width, curLineTop + height);
                lastLineMaxHeight = height;
            }
            lineNum = (curLineTop - firstLineTop) / height + 1;
            if (lineNum == 6){//zzh->ad: 当绘制到第六行时重新刷新数据
                RxManager.$(hashCode()).postDelayed(CustomConstants.MESSAGE_SEARCHDELEGATA_REFRRESH_CODE,i,500);
                Log.e("onLayoutChildren:","position:"+i);
            }
        }


    }

    public int getLineNum() {
        return lineNum;
    }
}
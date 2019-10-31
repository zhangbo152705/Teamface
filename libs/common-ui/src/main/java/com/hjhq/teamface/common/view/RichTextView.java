package com.hjhq.teamface.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/10/16.
 * Describe：监听内容变化
 */

public class RichTextView extends com.tencent.smtt.sdk.WebView {

    private int lastContentHeight = 0;

    public RichTextView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getContentHeight() != lastContentHeight) {
            lastContentHeight = getContentHeight();
            Log.e("RichTextView", lastContentHeight + "");
            final View parent = (View) getParent().getParent();
            final ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
            layoutParams.height = lastContentHeight;
            parent.setLayoutParams(layoutParams);
        }
    }

}

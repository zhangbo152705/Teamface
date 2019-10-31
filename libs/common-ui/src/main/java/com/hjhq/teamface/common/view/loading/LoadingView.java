package com.hjhq.teamface.common.view.loading;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.common.R;

/**
 * Created by Administrator on 2018/11/19.
 * Describe：加载动画
 */

public class LoadingView extends FrameLayout {
    private Context mContext;
    private ImageView imageView;
    private TextView textView;
    private Animation circleAnim;

    public LoadingView(@NonNull Context context) {
        super(context, null);
        mContext = context;
        initView();
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        initView();
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        View v = inflate(mContext, R.layout.loading_view, null);
        imageView = v.findViewById(R.id.iv);
        textView = v.findViewById(R.id.tv);
        addView(v);
        circleAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotation_anim);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        circleAnim.setInterpolator(interpolator);
        if (circleAnim != null) {
            imageView.startAnimation(circleAnim);  //开始动画
        }
    }

    public void setText(String text) {
        TextUtil.setText(textView, text);
    }

    public void stopAnim() {
        /*imageView.post(new Runnable() {
            @Override
            public void run() {
                if (circleAnim != null) {
                    try {
                        circleAnim.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
        if (circleAnim != null) {
            circleAnim.cancel();

        }
    }

    public void startAnim() {
        if (circleAnim != null) {
            imageView.startAnimation(circleAnim);
        }
    }
}

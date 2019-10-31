package com.hjhq.teamface.basis.util.device;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 屏幕工具类
 * Created by Lin on 2016/7/18.
 */
public class ScreenUtils {
    private ScreenUtils() {
    }


    /**
     * 屏幕高度
     *
     * @param context 任意上下文
     */
    public static float getScreenHeight(Context context) {
        return DeviceUtils.getDisplayMetrics(context).heightPixels;
    }

    /**
     * 屏幕宽度
     *
     * @param context 任意上下文
     */
    public static float getScreenWidth(Context context) {
        return DeviceUtils.getDisplayMetrics(context).widthPixels;
    }

    /**
     * @ 获取当前手机屏幕尺寸
     */
    public static float getScreenSize(Context mContext) {


        int densityDpi = mContext.getResources().getDisplayMetrics().densityDpi;
        float scaledDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        float density = mContext.getResources().getDisplayMetrics().density;
        float xdpi = mContext.getResources().getDisplayMetrics().xdpi;
        float ydpi = mContext.getResources().getDisplayMetrics().ydpi;
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        // 这样可以计算屏幕的物理尺寸
        float width2 = (width / xdpi) * (width / xdpi);
        float height2 = (height / ydpi) * (width / xdpi);


        return (float) Math.sqrt(width2 + height2);
    }

    /**
     * 是否是横屏
     *
     * @param context 任意上下文
     */
    public static boolean isLandscape(Context context) {
        boolean flag;
        if (context.getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        return flag;
    }

    /**
     * 是否是竖屏
     *
     * @param context 任意上下文
     */
    public static boolean isPortrait(Context context) {
        boolean flag = true;
        if (context.getResources().getConfiguration().orientation != 1)
            flag = false;
        return flag;
    }

    /**
     * 屏幕变暗
     *
     * @param activity 活动界面
     */
    public static void letScreenGray(Activity activity) {
        if (activity instanceof RxAppCompatActivity) {
            Observable.just(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(((RxAppCompatActivity) activity)
                            .bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(i -> updateGray(activity, .5f));
        }
    }

    /**
     * 屏幕变亮
     *
     * @param activity 活动界面
     */
    public static void letScreenLight(Activity activity) {
        if (activity instanceof RxAppCompatActivity)
            Observable.just(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(((RxAppCompatActivity) activity)
                            .bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(i -> updateGray(activity, 1f));
    }

    /**
     * 更改评论亮度
     *
     * @param activity 活动界面
     * @param alpha    透明度
     */
    private static void updateGray(Activity activity, float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha;        // 变亮只需将.3f改成1即可
        activity.getWindow().setAttributes(lp);
    }

    public static void setWindowStatusBarColor(Dialog dialog, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = dialog.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(dialog.getContext().getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

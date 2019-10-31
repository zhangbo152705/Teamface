
package com.hjhq.teamface.basis.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjhq.teamface.basis.R;

/**
 * Toast工具类，不与业务关联
 *
 * @author mou
 */
public class ToastUtils {
    //执行成功
    public static final String SUCCESS = "success";
    //执行失败
    public static final String FAILED = "failed";
    //警告
    public static final String WARNING = "warning";
    //拒绝
    public static final String REFUSED = "refused";
    private static Toast toast;
    private static Toast imageToast;
    private static ImageView toastImage;
    private static TextView toastText;

    /**
     * 显示成功
     *
     * @param res 文本资源
     */
    public static void showSuccess(Context mContext, int res) {
        final boolean b = NotificationManagerCompat.from(mContext).areNotificationsEnabled();
        if (b) {
            showSuccess(mContext, mContext.getString(res));
        } else {
            showSnackBar(mContext, mContext.getString(res));
        }
    }

    /**
     * 显示成功
     *
     * @param text 显示内容
     */
    public static void showSuccess(Context mContext, String text) {
        final boolean b = NotificationManagerCompat.from(mContext).areNotificationsEnabled();
        if (b) {
            showToast(mContext, text, SUCCESS);
        } else {
            showSnackBar(mContext, text);
        }
    }

    /**
     * 无通知权限时使用SnackBar提示
     *
     * @param mContext
     * @param message
     */
    public static void showSnackBar(Context mContext, String message) {
        if (mContext instanceof Activity) {
            View view = ((Activity) mContext).getWindow().getDecorView().getRootView();
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
            snackbar.setAction("确定", v -> {
                snackbar.dismiss();
            });
            snackbar.show();
        }
    }

    /**
     * 显示失败
     *
     * @param res 文本资源
     */
    public static void showError(Context mContext, int res) {
        final boolean b = NotificationManagerCompat.from(mContext).areNotificationsEnabled();
        if (b) {
            showError(mContext, mContext.getString(res));
        } else {
            showSnackBar(mContext, mContext.getString(res));
        }

    }

    /**
     * 显示失败
     *
     * @param text 显示内容
     */
    public static void showError(Context mContext, String text) {
        final boolean b = NotificationManagerCompat.from(mContext).areNotificationsEnabled();
        if (b) {
            showToast(mContext, text, FAILED);
        } else {
            showSnackBar(mContext, text);
        }
    }

    /**
     * 显示成功或失败或警告等
     *
     * @param text
     * @param type
     */
    public static void showToast(Context mContext, @NonNull String text, @NonNull String type) {
        if (mContext == null || TextUtils.isEmpty(text)) {
            return;
        }
        if (imageToast == null) {
            View view = View.inflate(mContext, R.layout.center_toast_layout, null);
            toastImage = view.findViewById(R.id.iv_icon);
            toastText = view.findViewById(R.id.tv_name);
            imageToast = new Toast(mContext);
            imageToast.setGravity(Gravity.CENTER, 0, 0);
            imageToast.setView(view);
            imageToast.setDuration(Toast.LENGTH_SHORT);
        }
        toastText.setText(text);
        switch (type) {
            //成功
            case SUCCESS:
                toastImage.setImageResource(R.drawable.action_ok);
                break;
            //失败
            case FAILED:
                toastImage.setImageResource(R.drawable.action_failed);
                break;
            //拒绝
            case REFUSED:
                toastImage.setImageResource(R.drawable.action_failed);
                break;
            //警告
            case WARNING:
                toastImage.setImageResource(R.drawable.action_failed);
                break;
            default:
                break;
        }
        imageToast.show();
    }

    /**
     * toast提示
     *
     * @param mContext 上下文
     * @param res      资源id
     */
    public static void showToast(Context mContext, int res) {

        showToast(mContext, mContext.getString(res));
    }

    /**
     * toast提示
     *
     * @param mContext 上下文
     * @param text     提示内容
     */
    public static void showToast(Context mContext, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        final boolean b = NotificationManagerCompat.from(mContext).areNotificationsEnabled();
        if (b) {
            if (toast == null) {
                toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(text);
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
            toast.show();
        } else {
            showSnackBar(mContext, text);
        }
    }
}

package com.hjhq.teamface.util;

import android.view.View;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.BaseActivity;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.common.CommonModel;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Subscriber;

/**
 * Created by Administrator on 2018/11/7.
 * Describe：
 */

public class TaskStatusHelper {
    /**
     * 更新个人任务状态
     *
     * @param mActivity
     * @param view
     * @param taskId
     * @param completeStatus
     * @param s
     */
    public static PopupWindow updatePersonalTaskStatus(boolean isSubTask, BaseActivity mActivity, View view, long taskId, boolean completeStatus, Subscriber<BaseBean> s) {
        if (completeStatus) {
            //激活
            DialogUtils.getInstance().sureOrCancel(mActivity, mActivity.getString(R.string.hint), "确定要激活该任务？", view
                    , () -> updatePersonalStatus(isSubTask, mActivity, taskId, s));
        } else {
            //完成
            DialogUtils.getInstance().sureOrCancel(mActivity, mActivity.getString(R.string.hint), "确定要完成该任务？", view
                    , () -> updatePersonalStatus(isSubTask, mActivity, taskId, s));
        }
        return null;
    }

    /**
     * 更新任务状态
     *
     * @param mActivity
     * @param view
     * @param jsonObject
     * @param completeStatus
     * @param projectCompleteStatus
     * @param s
     */
    public static PopupWindow updateTaskStatus(RxAppCompatActivity mActivity, View view, JSONObject jsonObject, boolean completeStatus, String projectCompleteStatus, Subscriber<BaseBean> s) {
        return updateTaskStatus(false, mActivity, view, jsonObject, completeStatus, projectCompleteStatus, s);
    }

    public static PopupWindow updateTaskStatus(boolean isSubTask, RxAppCompatActivity mActivity,
                                               View view, JSONObject jsonObject, boolean completeStatus,
                                               String projectCompleteStatus, Subscriber<BaseBean> s) {
        if (completeStatus) {
            //激活
            if ("1".equals(projectCompleteStatus)) {
                //需要激活原因
                PopupWindow popupWindow = DialogUtils.getInstance().inputDialog(mActivity, "激活原因", null, "必填", view, content -> {
                    if (TextUtil.isEmpty(content)) {
                        ToastUtils.showError(mActivity, "请输入激活原因");
                        return false;
                    }
                    jsonObject.put("remark", content);
                    updateStatus(isSubTask, mActivity, jsonObject, s);
                    return true;
                });
                return popupWindow;
            } else {
                /*DialogUtils.getInstance().sureOrCancel(mActivity, mActivity.getString(R.string.hint), "项目当前状态不支持该操作!", view
                        , () -> {
                        });*/
                updateStatus(isSubTask, mActivity, jsonObject, s);
            }
        } else {
            //完成
            DialogUtils.getInstance().sureOrCancel(mActivity, mActivity.getString(R.string.hint), "确定要完成该任务？", view
                    , () -> updateStatus(isSubTask, mActivity, jsonObject, s));
        }
        return null;
    }

    private static void updatePersonalStatus(boolean isSubTask, RxAppCompatActivity mActivity, long taskId, Subscriber<BaseBean> s) {
        if (isSubTask) {
            new CommonModel().updatePersonelSubTaskStatus(mActivity, taskId, s);
        } else {
            new CommonModel().updatePersonelTaskStatus(mActivity, taskId, s);
        }
    }

    private static void updateStatus(boolean isSubTask, RxAppCompatActivity mActivity, JSONObject jsonObject, Subscriber<BaseBean> s) {
        if (isSubTask) {
            new CommonModel().updateSubStatus(mActivity, jsonObject, s);
        } else {
            new CommonModel().updateStatus(mActivity, jsonObject, s);
        }
    }
}

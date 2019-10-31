package com.hjhq.teamface.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.hjhq.teamface.basis.bean.TaskInfoBean
import com.hjhq.teamface.basis.constants.ApproveConstants
import com.hjhq.teamface.basis.constants.Constants
import com.hjhq.teamface.basis.constants.ProjectConstants
import com.hjhq.teamface.basis.util.TextUtil
import com.hjhq.teamface.basis.util.file.SPHelper
import com.luojilab.component.componentlib.router.ui.UIRouter

/**
 * 任务帮助类
 * Created by Administrator on 2018/7/16.
 */

object TaskHelper {

    /**
     * 点击任务列表后处理
     *
     * @param mActivity
     * @param item
     * @param projectId
     */
    fun clickTaskItem(mActivity: Activity, item: TaskInfoBean) {
        val dataType = item.dataType
        val bundle = Bundle()
        when (dataType) {
            ProjectConstants.DATA_MEMO_TYPE -> {
                //备忘录
                bundle.putString(Constants.DATA_TAG1, item.bean_id.toString() + "")
                UIRouter.getInstance().openUri(mActivity, "DDComp://memo/detail", bundle)
            }
            ProjectConstants.DATA_TASK_TYPE -> {
                //任务
                if (item.project_id == 0L) {
                    bundle.putLong(ProjectConstants.TASK_ID, item.bean_id)
                   /* if ("1".equals(item.quote_status)) {
                        //引用任务
                        bundle.putLong(ProjectConstants.TASK_ID, item.quoteTaskId)
                    } else {
                        //非引用任务
                        bundle.putLong(ProjectConstants.TASK_ID, item.id)
                    }*/
                    bundle.putString(ProjectConstants.TASK_NAME, item.text_name)
                    bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PERSONAL_TASK_BEAN)
                    //CommonUtil.startActivtiy(mActivity, PersonalTaskDetailActivity::class.java, bundle)
                    UIRouter.getInstance().openUri(mActivity, "DDComp://project/personal_task_detail", bundle)

                } else {
                    bundle.putLong(ProjectConstants.PROJECT_ID, item.project_id)
                    bundle.putLong(ProjectConstants.TASK_ID, item.bean_id)//if (item.quoteTaskId <= 0L) item.taskInfoId else item.quoteTaskId
                    bundle.putString(ProjectConstants.TASK_NAME, item.text_name)
                    bundle.putString(Constants.MODULE_BEAN, item.bean_name)
                    bundle.putLong(ProjectConstants.TASK_FROM_TYPE, if (item.task_id == null) 1 else 2)
                    bundle.putLong(ProjectConstants.MAIN_TASK_ID, TextUtil.parseLong(item.task_id))
                    bundle.putString(ProjectConstants.MAIN_TASK_NODECODE, item.node_code)
                    bundle.putString(ProjectConstants.MAIN_TASK_NODE_ID, item.sub_id)
                    UIRouter.getInstance().openUri(mActivity, "DDComp://project/project_task_detail", bundle)
                }
            }
            ProjectConstants.DATA_CUSTOM_TYPE -> {
                //自定义
                bundle.putString(Constants.MODULE_BEAN, item.bean_name)
                bundle.putString(Constants.DATA_ID, item.bean_id.toString() + "")
                UIRouter.getInstance().openUri(mActivity, "DDComp://custom/detail", bundle)
            }
            ProjectConstants.DATA_APPROVE_TYPE -> {
                //审批
                bundle.putString(ApproveConstants.PROCESS_INSTANCE_ID, item.process_definition_id)
                bundle.putString(ApproveConstants.PROCESS_FIELD_V, item.process_field_v)
                bundle.putString(ApproveConstants.MODULE_BEAN, item.bean_name)
                bundle.putString(ApproveConstants.APPROVAL_DATA_ID, item.approval_data_id)
                bundle.putString(ApproveConstants.TASK_KEY, item.task_key)
                bundle.putString(ApproveConstants.TASK_NAME, item.task_name)
                bundle.putString(ApproveConstants.TASK_ID, item.task_id)
                bundle.putString(Constants.DATA_ID, item.id.toString() + "")
                // bundle.putString(ApproveConstants.APPROVAL_READ, item.complete_status)
                //TODO 需要type入口
                bundle.putInt(ApproveConstants.APPROVE_TYPE, if (SPHelper.getEmployeeId() == item.create_by.toString() + "") 0 else 1)
                UIRouter.getInstance().openUri(mActivity, "DDComp://app//approve/detail", bundle, Constants.REQUEST_CODE10)
            }
            else -> {
            }
        }
    }


    /**
     * 旋转控件
     */
    fun rotateView(context: Context, v: View, start: Float,
                   end: Float, duration: Int, endDrawable: Int) {
        val `object` = v.tag
        val icon: Drawable
        val animation: RotateAnimation
        if (null == `object`) {
            icon = context.resources.getDrawable(endDrawable)
            v.tag = 0
            animation = RotateAnimation(start, end,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)
        } else {
            icon = context.resources.getDrawable(endDrawable)
            v.tag = null
            animation = RotateAnimation(end, start,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)
        }
        animation.duration = duration.toLong()
        animation.fillAfter = true
        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) = Unit

            override fun onAnimationRepeat(animation: Animation) = Unit

            override fun onAnimationEnd(animation: Animation) {
                v.background = icon
            }
        })
        v.startAnimation(animation)
    }
}

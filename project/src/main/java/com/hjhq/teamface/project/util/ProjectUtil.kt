package com.hjhq.teamface.project.util

import android.content.Context
import com.hjhq.teamface.basis.util.TextUtil
import com.hjhq.teamface.basis.util.ToastUtils

/**
 * Created by Administrator on 2018/6/20.
 */

object ProjectUtil {
    /**
     * 项目        7修改信息8设置提醒9状态设置10导出任务11邀请成员12移除成员13项目进度14任务权限15任务排序
     * 任务分组     16添加17编辑18移动19删除
     * 列表        20新增21编辑名称22移动23删除24批量操作
     * 任务        25新增26设置提醒27点赞28评论
     * 文件(夹)    29编辑30删除31添加
     * 标签        32添加33移除
     */
    /**
     * 检测项目权限
     */
    fun checkProjectPermission(context: Context, priviledgeIds: String?, permission: Int): Boolean {
        if (priviledgeIds == null) {
            // ToastUtils.showError(context, "未获取到权限")
            return false
        }
        if (TextUtil.isEmpty(priviledgeIds)) {
            // ToastUtils.showError(context, "权限不足")
            return false
        }
        val split = priviledgeIds.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        return split.indices.any { split[it] == permission.toString() }
    }

    fun checkPermission(priviledgeIds: String?, permission: Int): Boolean {
        if (TextUtil.isEmpty(priviledgeIds)) {
            return false
        }
        val split = priviledgeIds!!.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        return split.indices.any { split[it] == permission.toString() }
    }

    /**
     *根据项目状态判断是否能继续进行
     */
    fun checkProjectStatus(context: Context, projectStatus: String, runnable: Runnable): Boolean {
        when (projectStatus) {
            "0" -> {
                runnable.run()
            }
            "1" -> {
                ToastUtils.showError(context, "项目已归档")
            }
            "2" -> {
                ToastUtils.showError(context, "项目已暂停")
            }
            "3" -> {
                ToastUtils.showError(context, "项目已删除")
            }
            else -> {
            }
        }
        return false;
    }

}

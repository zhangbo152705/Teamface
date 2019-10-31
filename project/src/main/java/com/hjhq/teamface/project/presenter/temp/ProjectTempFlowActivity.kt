package com.hjhq.teamface.project.presenter.temp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.alibaba.fastjson.JSON
import com.hjhq.teamface.basis.constants.Constants
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber
import com.hjhq.teamface.basis.zygote.ActivityPresenter
import com.hjhq.teamface.project.bean.*
import com.hjhq.teamface.project.model.ProjectModel
import com.hjhq.teamface.project.ui.temp.ProjectTempFlowDelegate

/**
 * 项目模板节点流程
 */
class ProjectTempFlowActivity : ActivityPresenter<ProjectTempFlowDelegate, ProjectModel>() {

    //private lateinit var dataList: List<TempNoteDataList>
    private lateinit var itemBean: TempDataList
    private lateinit var nodeList: NodeBean

    override fun create(savedInstanceState: Bundle?) {
        super.create(savedInstanceState)
        if (savedInstanceState == null) {
            itemBean = intent.getSerializableExtra(Constants.DATA_TAG1) as TempDataList
        }
    }


    override fun init() =
            model.queryTaskNoteViewByTempId(mContext, itemBean.id, object : ProgressSubscriber<AllNodeResultBean>(mContext) {
                override fun onNext(t: AllNodeResultBean) {
                    super.onNext(t)

                   /* dataList = t.data.dataList
                    viewDelegate.initNavigator(supportFragmentManager, dataList)*/

                    nodeList = t.getData().getRootNode()
                    viewDelegate.refreshNode(nodeList)
                }
            })

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var intent = Intent()
        intent.putExtra(Constants.DATA_TAG1, itemBean)
        setResult(Activity.RESULT_OK, intent)
        finish()
        return super.onOptionsItemSelected(item)
    }
}

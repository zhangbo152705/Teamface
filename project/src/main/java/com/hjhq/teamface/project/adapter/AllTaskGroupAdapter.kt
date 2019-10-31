package com.hjhq.teamface.project.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.alibaba.fastjson.JSONObject
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjhq.teamface.basis.database.CacheDataHelper
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber
import com.hjhq.teamface.basis.util.CollectionUtils
import com.hjhq.teamface.basis.util.TextUtil
import com.hjhq.teamface.project.R
import com.hjhq.teamface.project.bean.PersonalTaskBean
import com.hjhq.teamface.project.bean.PersonalTaskResultBean
import com.hjhq.teamface.project.bean.ProjectTaskListResultBean
import com.hjhq.teamface.project.model.ProjectModel
import com.trello.rxlifecycle.components.support.RxAppCompatActivity

/**
 * 个人任务适配器
 * Created by Administrator on 2018/4/10.
 */

class AllTaskGroupAdapter(data: List<PersonalTaskResultBean.DataBean>?) :
        BaseQuickAdapter<PersonalTaskResultBean.DataBean, BaseViewHolder>(R.layout.project_all_task_item, data) {
    private var queryType = 0
    var isSelect = false
        set(value) {
            field = value
        }

    override fun convert(helper: BaseViewHolder, item: PersonalTaskResultBean.DataBean) {
        helper.setText(R.id.tv_name, item.title)
        val ivSpread = helper.getView<ImageView>(R.id.iv_spread)
        var isLoad = false

        val mRecyclerView = helper.getView<RecyclerView>(R.id.recycler_view)
        helper.setVisible(R.id.recycler_view, item.isVisibility)
        ivSpread.isSelected = item.isVisibility
        mRecyclerView.layoutManager = object : LinearLayoutManager(mContext) {
            override fun canScrollVertically(): Boolean = false
        }
        var personalTaskAdapter = PersonalTaskAdapter(null)
        personalTaskAdapter.isSelect = isSelect
        mRecyclerView.adapter = personalTaskAdapter

        helper.getView<View>(R.id.ll_item).setOnClickListener {
            item.isVisibility = !item.isVisibility
            ivSpread.isSelected = item.isVisibility
            helper.setVisible(R.id.recycler_view, item.isVisibility)
            //加载缓存
            val projectId: String
            if(item.projectId.isNullOrBlank()) {
                //个人任务
                projectId = "personal";
                if(personalTaskAdapter.data == null)
                    personalTaskAdapter.setNewData(item.tasks)
                CacheDataHelper.saveCacheData(
                        CacheDataHelper.PROJECT_TASK_LIST_CACHE_DATA,
                        "all_task_list_type" + "_" + projectId,
                        JSONObject.toJSONString(item.tasks))
            } else {
                //项目任务
                projectId = item.projectId
            }

            if(personalTaskAdapter.data.size > 0) {
                item.tasks = personalTaskAdapter.data
            }
            if(item.isVisibility && !item.isQuest) {
                isLoad = true

                if(TextUtil.isEmpty(item.projectId) || item.isQuest) {
                    //个人任务 已请求的项目下的任务
                    personalTaskAdapter.setNewData(item.tasks)
                } else {
                    //获取项目下的任务
                    if(personalTaskAdapter.data == null) {
                        loadCacheData(mRecyclerView.adapter as PersonalTaskAdapter, projectId)
                        item.tasks = personalTaskAdapter.data
                    }
                    ProjectModel().queryTaskListByProjectId(mContext as RxAppCompatActivity, item.projectId,
                            queryType, object : ProgressSubscriber<ProjectTaskListResultBean>(mContext) {
                        override fun onNext(t: ProjectTaskListResultBean) {
                            super.onNext(t)
                            item.tasks = personalTaskAdapter.data
                            item.isQuest = true
                            CollectionUtils.notifyDataSetChanged(mRecyclerView.adapter, personalTaskAdapter.data, t.data)
                            CacheDataHelper.saveCacheData(
                                    CacheDataHelper.PROJECT_TASK_LIST_CACHE_DATA,
                                    "all_task_list_type" + "_" + item.projectId,
                                    JSONObject.toJSONString((mRecyclerView.adapter as PersonalTaskAdapter).data))
                        }
                    })
                }
            }
        }
    }

    fun setQueryType(queryType: Int) {
        this.queryType = queryType
    }

    /**
     * 加载缓存
     */
    private fun loadCacheData(adapter: PersonalTaskAdapter, projectId: String?) {
        val cacheData = CacheDataHelper.getCacheData(CacheDataHelper.PROJECT_LIST_CACHE_DATA, "all_task_list_type" + "_" + projectId)
        if(!TextUtils.isEmpty(cacheData)) {
            val cacheDataList = Gson().fromJson<List<PersonalTaskBean>>(cacheData, object : TypeToken<List<PersonalTaskBean>>() {
            }.type)
            if(cacheDataList != null && cacheDataList.size > 0) {
                // CollectionUtils.notifyDataSetChanged(adapter, adapter.data, cacheDataList)
                adapter.setNewData(cacheDataList)
                adapter.notifyDataSetChanged()
            }
        }
    }
}

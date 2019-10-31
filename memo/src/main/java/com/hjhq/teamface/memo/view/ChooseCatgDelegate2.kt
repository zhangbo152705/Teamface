package com.hjhq.teamface.memo.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener
import com.hjhq.teamface.basis.zygote.AppDelegate
import com.hjhq.teamface.memo.R
import com.hjhq.teamface.memo.adapter.KnowledgeClassAdapter
import com.hjhq.teamface.memo.adapter.KnowledgeTagAdapter

/**
 * Created by Administrator on 2018-12-10.
 * Describe： 选择知识分类视图
 */
class ChooseCatgDelegate2 : AppDelegate() {
    lateinit var recyclerView: RecyclerView

    override fun getRootLayoutId(): Int {
        return R.layout.memo_knowledge_choose_catg_activity
    }

    override fun isToolBar(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        recyclerView = get<RecyclerView>(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(getActivity())


    }

    fun setAdapter(adapter: KnowledgeClassAdapter) {
        recyclerView.adapter = adapter
    }

    fun setAdapter(adapter: KnowledgeTagAdapter) {
        recyclerView.adapter = adapter
    }

    fun setClickListener(listener: SimpleItemClickListener) {
        recyclerView.addOnItemTouchListener(listener)
    }
}
package com.hjhq.teamface.memo.ui

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hjhq.teamface.basis.bean.KnowledgeClassBean
import com.hjhq.teamface.basis.bean.KnowledgeTagBean
import com.hjhq.teamface.basis.constants.Constants
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener
import com.hjhq.teamface.basis.zygote.ActivityPresenter
import com.hjhq.teamface.memo.MemoModel
import com.hjhq.teamface.memo.adapter.KnowledgeClassAdapter
import com.hjhq.teamface.memo.adapter.KnowledgeTagAdapter
import com.hjhq.teamface.memo.view.ChooseCatgDelegate2

class ChooseCatgActivity : ActivityPresenter<ChooseCatgDelegate2, MemoModel>() {
    var type: Int = 0
    lateinit var classAdapter: KnowledgeClassAdapter
    lateinit var tagAdapter: KnowledgeTagAdapter
    var classList: ArrayList<KnowledgeClassBean> = ArrayList()
    var tagList: ArrayList<KnowledgeTagBean> = ArrayList()
    override fun init() {
        type = intent.getIntExtra(Constants.DATA_TAG1, -1);

        initAdapter()

    }

    @Suppress("UNCHECKED_CAST")
    private fun initAdapter() {
        if (type == 1) {
            viewDelegate.setTitle("选择分类")
            classList = intent.getSerializableExtra(Constants.DATA_TAG2) as ArrayList<KnowledgeClassBean>
            classAdapter = KnowledgeClassAdapter(classList)
            viewDelegate.setAdapter(classAdapter)
        } else if (type == 2) {
            viewDelegate.setTitle("选择标签")
            classList = intent.getSerializableExtra(Constants.DATA_TAG2) as ArrayList<KnowledgeClassBean>
            tagAdapter = KnowledgeTagAdapter(tagList)
            viewDelegate.setAdapter(tagAdapter)
        }


    }

    override fun bindEvenListener() {
        super.bindEvenListener()
        viewDelegate.setClickListener(object : SimpleItemClickListener() {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                super.onItemClick(adapter, view, position)
                when (type) {
                    1 -> processClassData(position)
                    2 -> processTagData(position)
                    else -> {

                    }
                }

            }

            override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                super.onItemChildClick(adapter, view, position)
            }
        })
    }

    private fun processClassData(position: Int) {
        for (item in classList) {
            item.isCheck = false
        }
        classList.get(position).isCheck = true
        classAdapter.notifyDataSetChanged()
    }

    private fun processTagData(position: Int) {
        tagList.get(position).isCheck = !tagList.get(position).isCheck
        tagAdapter.notifyDataSetChanged()
    }


}

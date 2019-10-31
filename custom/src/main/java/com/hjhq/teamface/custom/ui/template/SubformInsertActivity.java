package com.hjhq.teamface.custom.ui.template;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.InsertSubformBean;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.adapter.InsertSubformAdapter;
import com.luojilab.router.facade.annotation.RouteNode;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 引用组件 列表
 *
 * @author xj
 * @date 2017/8/31
 */

@RouteNode(path = "/subform_insert", desc = "列表")
public class SubformInsertActivity extends ActivityPresenter<ReferenceTempDelegate, DataTempModel> {

    protected InsertSubformAdapter dataTempAdapter;
    protected String keyValue;
    protected HashMap<String, Serializable> paramsMap;
    protected PageInfo mPageInfo;
    private int currentPageNum = 1;
    private int pageSize = Constants.PAGESIZE;
    private boolean isMulti = true;
    private String value = "";

    /**
     * 所在模块的bean
     */
    protected String moduleBean;
    /**
     * 关联查询字段 name
     */
    protected String referenceField;
    /**
     * 搜索框提示文字
     */
    protected String searchHint;
    //子表单的keyName
    private String subFormName;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            paramsMap = (HashMap<String, Serializable>) intent.getSerializableExtra(Constants.DATA_TAG1);
        }
        SoftKeyboardUtils.hide(this);
    }

    @Override
    public void init() {
        initAdapter();
        loadData(1);
        viewDelegate.mRefreshLayout.setEnableLoadMore(false);
        viewDelegate.get(R.id.tv_cancel).setVisibility(View.VISIBLE);
        viewDelegate.mSearchBar.setCancelText("确定");
    }

    /**
     * 初始化适配器
     */
    protected void initAdapter() {
        if (dataTempAdapter == null) {
            dataTempAdapter = new InsertSubformAdapter(null);
            dataTempAdapter.setType(isMulti);
            viewDelegate.setAdapter(dataTempAdapter);
        }
    }

    /**
     * 加载数据
     */
    public void loadData(int pageNum) {
        PageInfo info = new PageInfo();
        info.setPageNum(pageNum);
        info.setPageSize(pageSize);
        paramsMap.put("pageInfo", info);

        model.findSubRelationDataList(this, paramsMap, new ProgressSubscriber<InsertSubformBean>(this) {
            @Override
            public void onNext(InsertSubformBean dataTempResultBean) {
                super.onNext(dataTempResultBean);
                mPageInfo = dataTempResultBean.getData().getPageInfo();
                currentPageNum = mPageInfo.getPageNum();
                CollectionUtils.notifyDataSetChanged(dataTempAdapter, dataTempAdapter.getData(), dataTempResultBean.getData().getDataList());
                if (mPageInfo.getTotalPages() > mPageInfo.getPageNum()) {
                    viewDelegate.mRefreshLayout.setEnableLoadMore(true);
                } else {
                    viewDelegate.mRefreshLayout.setEnableLoadMore(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (isMulti) {
                    dataTempAdapter.getItem(position).setCheck(!dataTempAdapter.getItem(position).isCheck());
                    dataTempAdapter.notifyDataSetChanged();
                } else {
                    InsertSubformBean.DataBean.DataListBean item = dataTempAdapter.getItem(position);
                    if (item == null || item.getId() == null || item.getRow() == null) {
                        ToastUtils.showError(mContext, "数据错误！");
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DATA_TAG3, item);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        viewDelegate.mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                viewDelegate.mRefreshLayout.finishLoadMore(3000);
                if (mPageInfo != null) {
                    int totalPages = mPageInfo.getTotalPages();
                    int pageNum = mPageInfo.getPageNum();
                    if (pageNum < totalPages) {
                        loadData(pageNum + 1);
                    } else {
                        ToastUtils.showToast(mContext, "无更多数据");
                    }
                }
            }
        });

        viewDelegate.setSearchBarHint(searchHint);
        viewDelegate.setSearchListener(new SearchBar.SearchListener() {
            @Override
            public void clear() {
            }

            @Override
            public void cancel() {
                if (!isMulti) {
                    finish();
                    return;
                }
                List<InsertSubformBean.DataBean.DataListBean> data = dataTempAdapter.getData();
                ArrayList<InsertSubformBean.DataBean.DataListBean> list = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).isCheck()) {
                        list.add(data.get(i));
                    }
                }
                if (list.size() == 0) {
                    ToastUtils.showError(mContext, "未选择数据");
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_TAG3, list);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void search() {
                if (TextUtils.isEmpty(keyValue)) {
                    ToastUtils.showError(mContext, "请输入搜索内容！");
                    return;
                }
                paramsMap.put("fuzzySearch", keyValue);
                loadData(1);
            }

            @Override
            public void getText(String text) {
                keyValue = text;
                if (TextUtils.isEmpty(text)) {
                    List<InsertSubformBean.DataBean.DataListBean> data = dataTempAdapter.getData();
                    data.clear();
                    dataTempAdapter.notifyDataSetChanged();
                }
            }
        });
        viewDelegate.get(R.id.tv_cancel).setOnClickListener(v -> finish());
    }

}

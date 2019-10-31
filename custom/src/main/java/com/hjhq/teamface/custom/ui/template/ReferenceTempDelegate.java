package com.hjhq.teamface.custom.ui.template;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.custom.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;


/**
 * 引用组件列表 视图代理类
 *
 * @author xj
 * @date 2017/8/31
 */

public class ReferenceTempDelegate extends AppDelegate {
    public RecyclerView mRecyclerView;
    public SearchBar mSearchBar;
    public SmartRefreshLayout mRefreshLayout;

    @Override
    public int getRootLayoutId() {
        return R.layout.custom_activity_reference_temp;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }


    @Override
    public void initWidget() {
        super.initWidget();
        getToolbar().setVisibility(View.GONE);
        mSearchBar = get(R.id.search_bar);
        setLeftIcon(R.drawable.icon_blue_back);
        setLeftText(R.color.app_blue, "返回");
        mRecyclerView = get(R.id.recycler_view);
        mRefreshLayout = get(R.id.refresh_layout);
        mRefreshLayout.setEnableRefresh(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        View emptyView = mContext.getLayoutInflater().inflate(R.layout.custom_view_reference_temp_empty, null);
        adapter.setEmptyView(emptyView);
        mRecyclerView.setAdapter(adapter);

    }

    public void setSearchListener(SearchBar.SearchListener listener) {
        mSearchBar.setSearchListener(listener);
    }

    public void setSearchBarHint(String hint) {
        if (hint == null) {
            return;
        }
        mSearchBar.setHintText(hint);
    }

    /**
     * 设置toolsBar
     *
     * @param title
     */
    public void setToolsBar(String title) {
        getToolbar().setVisibility(View.VISIBLE);
        TextUtil.setText((TextView) get(R.id.title_tv), title);
        mSearchBar.setVisibility(View.GONE);
    }
}

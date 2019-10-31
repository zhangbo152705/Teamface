package com.hjhq.teamface.custom.ui.select;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.view.MylayoutManager;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.MyLinearDeviderDecoration;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.custom.R;

/**
 * @author Administrator
 * @date 2018/6/20
 */

public class SearchDataTempDelegate extends AppDelegate {
    public RecyclerView mRvContacts;
    public SearchBar mSearchBar;

    @Override
    public int getRootLayoutId() {
        return R.layout.custom_activity_task_quote_search;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle(R.string.approval);
        setRightMenuTexts(mContext.getString(R.string.confirm));


        mRvContacts = get(R.id.search_result_recycler_view);
        mRvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvContacts.addItemDecoration(new MyLinearDeviderDecoration(mContext));
        mSearchBar = get(R.id.search_bar);
        mSearchBar.setCancelVisibility(false);

        mRvContacts.setLayoutManager(new MylayoutManager(mContext));
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        mRvContacts.setAdapter(adapter);
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.view_workbench_empty, null);
        TextView tvtitle = emptyView.findViewById(R.id.title_tv);
        TextUtil.setText(tvtitle, "输入关键字进行搜索~");
        adapter.setEmptyView(emptyView);
    }
}

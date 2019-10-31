package com.hjhq.teamface.custom.ui.template;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.layoutmanager.AutoLayoutManager;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.adapter.SearchHistoryAdapter;
import com.hjhq.teamface.custom.ui.file.FileDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索页面视图代理类
 */

public class SearchDelegate extends AppDelegate {
    private RecyclerView historyRv;
    private RecyclerView resultRv;
    public SearchBar mSearchBar;
    private List<String> historyItemList = new ArrayList<>();
    private TextView clearAll;
    private View footerView;
    private View headerView;
    private AutoLayoutManager manager;
    private SearchHistoryAdapter historyListAdapter;


    @Override
    public int getRootLayoutId() {
        return R.layout.custom_activity_search_goods;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }


    @Override
    public void initWidget() {
        super.initWidget();
        mSearchBar = get(R.id.search_bar);
        resultRv = get(R.id.search_result_recycler_view);
        resultRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyRv = get(R.id.search_history_recycler_view);
        footerView = mContext.getLayoutInflater().inflate(R.layout.item_history_footer, null);
        headerView = mContext.getLayoutInflater().inflate(R.layout.item_history_header, null);
        clearAll = footerView.findViewById(R.id.tv_clean_history_search_log);
        manager = new AutoLayoutManager();
        historyRv.setLayoutManager(manager);
        historyListAdapter = new SearchHistoryAdapter(historyItemList);
        historyListAdapter.addFooterView(footerView);
        historyListAdapter.addHeaderView(headerView);
        View emptyView = mContext.getLayoutInflater().inflate(R.layout.view_workbench_empty, null);
        TextView emptyTitle = emptyView.findViewById(R.id.title_tv);
        TextUtil.setText(emptyTitle, "没有搜索记录~");
        historyListAdapter.setEmptyView(emptyView);
        historyRv.setAdapter(historyListAdapter);

    }


    public void setHintText(String hint) {
        mSearchBar.setHintText(hint);
    }

    //历史记录条目点击事件
    public void setRecyclerListener(SimpleItemClickListener listener) {
        historyRv.addOnItemTouchListener(listener);
    }

    //搜索结果条目点击事件
    public void setItemClickListener(SimpleItemClickListener listener) {
        resultRv.addOnItemTouchListener(listener);
    }

    //清空搜索记录
    public void setCleanButtonClickListener(View.OnClickListener listener) {
        clearAll.setOnClickListener(listener);
    }

    /**
     * 初始化搜索记录
     *
     * @param historyItem
     */
    public void getHistoryData(List<String> historyItem) {
        historyRv.setVisibility(View.VISIBLE);
        resultRv.setVisibility(View.GONE);
        historyItemList.clear();
        historyItemList.addAll(historyItem);
        historyListAdapter.notifyDataSetChanged();
        int line = manager.getLineNum();

        Log.e("getHistoryData", ">>>" + line + "");
        /*while (line > 6) {
            historyItemList.remove(historyItemList.size() - 1);
            historyListAdapter.notifyDataSetChanged();
            manager.requestLayout();
            line = manager.getLineNum();
        }*/

        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_SEARCHDELEGATA_REFRRESH_CODE, tag -> {//zzh->ad: 删除六行以后的数据
            int position = (int) tag;
            Log.e("onLayoutChildren:","RxManagerposition:"+position);
            if (historyItemList.size()>position && position>0){
                while (historyItemList.size()-1>position) {
                    historyItemList.remove(historyItemList.size() - 1);
                }
                historyListAdapter.notifyDataSetChanged();
                manager.requestLayout();
            }
        });
    }

    public void changeView(boolean flag) {
        if (flag) {
            historyRv.setVisibility(View.GONE);
            resultRv.setVisibility(View.VISIBLE);
        } else {
            historyRv.setVisibility(View.VISIBLE);
            resultRv.setVisibility(View.GONE);
        }
    }




    /**
     * 保存搜索记录
     *
     * @param keyword
     * @return
     */
    public List<String> saveSearchHistory(String keyword) {
        if (!historyItemList.contains(keyword) && !TextUtils.isEmpty(keyword)) {
            historyItemList.add(0, keyword);
        }
        return historyItemList;
    }

    /**
     * 移除一条搜索记录
     *
     * @param position
     */
    public void removeHistoryItem(int position) {
        historyItemList.remove(position);
        historyListAdapter.notifyDataSetChanged();
    }

    public void removeAllHistoryItem() {
        historyItemList.clear();
        historyListAdapter.notifyDataSetChanged();
    }

    /**
     * 点击历史条目后,将条目内容设置为当前搜索关键字
     *
     * @param position
     */
    public void setText(int position) {
        mSearchBar.setText(historyItemList.get(position));
    }

    public void showHistoryItem() {
        historyRv.setVisibility(View.VISIBLE);
        resultRv.setVisibility(View.GONE);
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        View emptyView = mContext.getLayoutInflater().inflate(R.layout.view_workbench_empty, null);
        adapter.setEmptyView(emptyView);
        resultRv.setAdapter(adapter);
    }

}

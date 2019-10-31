package com.hjhq.teamface.memo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.device.DeviceUtils;
import com.hjhq.teamface.basis.view.recycler.MyGridDividerDecoration;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.view.recycler.SpaceItemDecoration;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.view.EmptyView;
import com.hjhq.teamface.memo.MemoModel;
import com.hjhq.teamface.memo.R;
import com.hjhq.teamface.memo.adapter.KnowledgeGridAdapter;
import com.hjhq.teamface.memo.adapter.KnowledgeListAdapter;
import com.hjhq.teamface.memo.bean.KnowledgeBean;
import com.hjhq.teamface.memo.bean.KnowledgeListData;
import com.hjhq.teamface.memo.bean.MemoListBean;
import com.hjhq.teamface.memo.view.KnowledgeListFragmentDelegateV2;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnowledgeListFragmentV2 extends FragmentPresenter<KnowledgeListFragmentDelegateV2, MemoModel> {
    private int type;
    private RecyclerView mRecyclerView;
    private KnowledgeListAdapter mListAdapter;
    private KnowledgeGridAdapter mGridAdapter;
    private EmptyView mEmptyView1;
    private EmptyView mEmptyView2;
    private View mBlankView;
    private SmartRefreshLayout srlLayout;
    private KnowledgeListActivityV2 mActivity;
    private ClassicsFooter mClassicsFooter;
    //分页
    private int currentPageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    //总数
    private int totalNum = 0;
    private int state = Constants.NORMAL_STATE;
    private int pageSize = 20;
    private PageInfo pageInfo;
    //编辑状态
    private boolean isEditState = false;
    private boolean isFirstLoad = true;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private MyGridDividerDecoration mDecoration1;
    private SpaceItemDecoration mDecoration2;
    private List<KnowledgeBean> dataList = new ArrayList<>();
    private Map<String, Object> paramMap = new HashMap<>();


    static KnowledgeListFragmentV2 newInstance(int index) {
        KnowledgeListFragmentV2 myFragment = new KnowledgeListFragmentV2();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, index);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (KnowledgeListActivityV2) getActivity();
        mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        Bundle arguments = getArguments();
        if (arguments != null) {
            type = arguments.getInt(Constants.DATA_TAG1, 0);
            mListAdapter = new KnowledgeListAdapter(dataList);
            mGridAdapter = new KnowledgeGridAdapter(dataList);
            //mDecoration1 = new MyGridDividerDecoration(getActivity(), R.color.red, 30);
            mDecoration2 = new SpaceItemDecoration((int) DeviceUtils.dpToPixel(getActivity(), 10), 2);
        }
    }

    @Override
    protected void init() {
        mRecyclerView = viewDelegate.get(R.id.rv);
        mBlankView = viewDelegate.get(R.id.blank);
        srlLayout = viewDelegate.get(R.id.refresh_layout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mListAdapter);
        mClassicsFooter = new ClassicsFooter(mActivity);
        srlLayout.setRefreshFooter(mClassicsFooter);
        mEmptyView1 = new EmptyView(getActivity());
        mEmptyView1.setEmptyTitle("暂无内容");
        mEmptyView1.setEmptyImage(R.drawable.memo_empty_view_img);

        mEmptyView2 = new EmptyView(getActivity());
        mEmptyView2.setEmptyImage(R.drawable.memo_empty_view_img);
        mEmptyView2.setEmptyTitle("暂无内容");
        mListAdapter.setEmptyView(mEmptyView1);
        mGridAdapter.setEmptyView(mEmptyView2);

        //监听下拉刷新
        srlLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlLayout.finishRefresh();
                    }
                }, 500);
            }
        });
        srlLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mListAdapter.getData().size() >= totalNum) {
                    srlLayout.finishLoadMore();
                    return;
                }
                state = Constants.LOAD_STATE;
                getNetData();
            }
        });
        refreshData();

    }

    public void switchLayoutmanager() {
        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
            mRecyclerView.setAdapter(mGridAdapter);
            mBlankView.setVisibility(View.VISIBLE);

        } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setAdapter(mListAdapter);
            mBlankView.setVisibility(View.GONE);
        }
    }

    public void setEditState(boolean editState) {
        srlLayout.setEnabled(!editState);
        isEditState = editState;
    }

    public void refreshData() {
        currentPageNo = 1;
        state = Constants.REFRESH_STATE;
        getNetData();
        srlLayout.setEnableLoadMore(true);
    }

    public void refreshIfEmpty() {
        if (mListAdapter.getData().size() <= 0) {
            state = Constants.REFRESH_STATE;
            currentPageNo = 1;
            getNetData();
        }
    }

    public KnowledgeListAdapter getListAdapter() {
        return mListAdapter;
    }

    public KnowledgeGridAdapter getGridAdapter() {
        return mGridAdapter;
    }

    public void getNetData() {
        currentPageNo = state == Constants.LOAD_STATE ? currentPageNo + 1 : 1;
        pageSize = Constants.PAGESIZE;
        paramMap.put("range", type);
        paramMap.put("pageSize", pageSize);
        paramMap.put("pageNum", currentPageNo);
        paramMap.put("keyWord", "");
        model.getKnowledgeLsit(mActivity, paramMap, new ProgressSubscriber<KnowledgeListData>(mActivity, isFirstLoad) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                srlLayout.finishLoadMore();//zzh->ad:向上拉加载更多数据错误时停止加载
            }

            @Override
            public void onNext(KnowledgeListData knowledgeListData) {
                super.onNext(knowledgeListData);
                isFirstLoad = false;
                switch (state) {
                    case Constants.LOAD_STATE:
                        //dataList.clear();//zzh->ad:向上拉加载更多数据时原数据不清空
                        srlLayout.finishLoadMore();//zzh->ad:向上拉加载更多数据完成时停止加载
                        break;
                    case Constants.REFRESH_STATE:
                        dataList.clear();
                        break;
                    case Constants.NORMAL_STATE:

                        break;
                }

                dataList.addAll(knowledgeListData.getData().getDataList());
                mGridAdapter.notifyDataSetChanged();
                mListAdapter.notifyDataSetChanged();
                pageInfo = knowledgeListData.getData().getPageInfo();
                totalNum = pageInfo.getTotalRows();
                totalPages = pageInfo.getTotalPages();
                currentPageNo = pageInfo.getPageNum();
                if (currentPageNo >= pageInfo.getTotalPages()) {
                    srlLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });
    }

    private void showData(MemoListBean fileListResBean) {

        /*List<MemoListItemBean> newDataList = new ArrayList<>();
        if (fileListResBean.getData() != null && fileListResBean.getData().getDataList() != null) {
            newDataList.addAll(fileListResBean.getData().getDataList());
        }
        switch (state) {
            case Constants.REFRESH_STATE:
            case Constants.NORMAL_STATE:
                dataList.clear();
                srlLayout.finishRefresh();
                break;
            case Constants.LOAD_STATE:
                srlLayout.finishLoadMore();
                break;
            default:
                break;
        }
        if (newDataList.size() > 0) {
            dataList.addAll(newDataList);
        }

        mGridAdapter.notifyDataSetChanged();
        mListAdapter.notifyDataSetChanged();
        PageInfo pageInfo = fileListResBean.getData().getPageInfo();
        totalPages = pageInfo.getTotalPages();
        currentPageNo = pageInfo.getPageNum();
        totalNum = pageInfo.getTotalRows();
        if (totalNum == mListAdapter.getData().size()) {
            srlLayout.finishLoadMoreWithNoMoreData();
        }
        srlLayout.finishLoadMore();*/

    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageBean bean) {
        Log.e("onEvent","KnowledgeListFragmentV2:"+bean.getTag());
        if (bean != null && MemoConstant.FILTER_DATA.equals(bean.getTag())) {

            paramMap = (Map<String, Object>) bean.getObject();
            state = Constants.REFRESH_STATE;
            currentPageNo =1;
            paramMap.put("pageSize", pageSize);
            paramMap.put("pageNum", currentPageNo);
            paramMap.put("keyWord", "");
            getFilterData(paramMap);
        }
    }

    private void getFilterData(Map<String, Object> map) {
        model.getKnowledgeLsit(mActivity, map, new ProgressSubscriber<KnowledgeListData>(mActivity, true) {//筛选 显示进度条
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(KnowledgeListData knowledgeListData) {
                super.onNext(knowledgeListData);
                isFirstLoad = false;
                dataList.clear();
                dataList.addAll(knowledgeListData.getData().getDataList());
                mGridAdapter.notifyDataSetChanged();
                mListAdapter.notifyDataSetChanged();
                pageInfo = knowledgeListData.getData().getPageInfo();
                totalNum = pageInfo.getTotalRows();
                totalPages = pageInfo.getTotalPages();
                currentPageNo = pageInfo.getPageNum();
                if (currentPageNo >= pageInfo.getTotalPages()) {
                    srlLayout.finishLoadMoreWithNoMoreData();
                }
            }
        });
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mActivity, KnowledgeDetailActivity.class);
                intent.putExtra(Constants.DATA_TAG1, mListAdapter.getData().get(position).getId());
                intent.putExtra(Constants.DATA_TAG2, false);
                intent.putExtra(Constants.DATA_TAG5, TextUtil.parseInt(mListAdapter.getData().get(position).getType_status(),0));
                startActivityForResult(intent, Constants.REQUEST_CODE2);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            refreshData();
            switch (requestCode) {
                case Constants.REQUEST_CODE1:
                    refreshData();
                    break;
                case Constants.REQUEST_CODE2:
                    refreshData();
                    break;
                default:

                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

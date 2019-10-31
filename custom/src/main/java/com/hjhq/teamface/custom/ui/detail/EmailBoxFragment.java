package com.hjhq.teamface.custom.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.EmailBean;
import com.hjhq.teamface.basis.bean.EmailListBean;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.adapter.ChooseEmailListAdapter;
import com.hjhq.teamface.common.view.EmptyView;
import com.hjhq.teamface.custom.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件列表
 *
 * @author Administrator
 */

public class EmailBoxFragment extends FragmentPresenter<EmailBoxDelegate, DataDetailModel> {
    private String boxTag;
    private RecyclerView mRecyclerView;
    private ChooseEmailListAdapter mAdapter;
    private EmptyView mEmptyView;
    private SwipeRefreshLayout srlLayout;
    //分页
    private int currentPageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    //总数
    private int totalNum = 0;
    private int state = Constants.NORMAL_STATE;
    private int pageSize = 20;

    //编辑状态
    private boolean isEditState = false;
    private boolean isFirstLoad = true;


    static EmailBoxFragment newInstance(String tag) {
        EmailBoxFragment myFragment = new EmailBoxFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, tag);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            boxTag = arguments.getString(Constants.DATA_TAG1);
        }
        mAdapter = new ChooseEmailListAdapter(Integer.parseInt(boxTag), new ArrayList<>());

    }

    @Override
    protected void init() {
        mRecyclerView = viewDelegate.get(R.id.recycler_view);
        srlLayout = viewDelegate.get(R.id.swipe_refresh_layout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mEmptyView = new EmptyView(getActivity());
        mEmptyView.setEmptyImage(R.drawable.empty_view_img);
        mAdapter.setEmptyView(mEmptyView);
        srlLayout.setOnRefreshListener(() -> {
            if (isEditState) {
                srlLayout.setRefreshing(false);
                return;
            }
            if (boxTag.equals(EmailConstant.INBOX) || boxTag.equals(EmailConstant.TRASH)) {
                receiveEmail();
            } else {
                refreshData();
            }
            srlLayout.postDelayed(() -> srlLayout.setRefreshing(false), 300);
        });
        mAdapter.setOnLoadMoreListener(() -> {
            if (currentPageNo >= totalPages) {
                mAdapter.loadMoreComplete();
                mAdapter.loadMoreEnd(false);
                return;
            }
            state = Constants.LOAD_STATE;
            getNetData();
        }, mRecyclerView);
        loadCacheData();
        refreshData();
        closeDefaultAnimator();


    }

    /**
     * 加载缓存的数据
     */
    private void loadCacheData() {
        final List<EmailBean> cacheData = SPHelper.getEmailCacheData(boxTag);
        if (cacheData != null && cacheData.size() > 0) {
            mAdapter.setNewData(cacheData);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 关闭默认局部刷新动画
     */
    public void closeDefaultAnimator() {
        mRecyclerView.getItemAnimator().setAddDuration(0);
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        mRecyclerView.getItemAnimator().setMoveDuration(0);
        mRecyclerView.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        state = Constants.REFRESH_STATE;
        getNetData();
    }

    public ChooseEmailListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 获取数据
     */
    public void getNetData() {
        currentPageNo = state == Constants.LOAD_STATE ? currentPageNo + 1 : 1;
        pageSize = Constants.PAGESIZE;
        model.getEmailList(((ActivityPresenter) getActivity()), currentPageNo, pageSize, "", boxTag,
                new ProgressSubscriber<EmailListBean>(getActivity(), isFirstLoad) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        isFirstLoad = false;
                    }

                    @Override
                    public void onNext(EmailListBean emailListBean) {
                        super.onNext(emailListBean);
                        // mEmptyView.setEmptyTitle("无数据");
                        showData(emailListBean);
                        if (currentPageNo == 1) {
                            try {
                                //缓存第一页数据
                                SPHelper.setEmailCacheData(boxTag, JSONObject.toJSONString(emailListBean.getData().getDataList()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        isFirstLoad = false;
                    }
                });
    }

    /**
     * 收信
     */
    private void receiveEmail() {
        model.receive(((ActivityPresenter) getActivity()), new ProgressSubscriber<EmailListBean>(getContext(), isFirstLoad) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(EmailListBean emailListBean) {
                super.onNext(emailListBean);
                refreshData();
            }
        });
    }

    private void showData(EmailListBean bean) {

        List<EmailBean> newDataList = bean.getData().getDataList();
        List<EmailBean> oldDataList = mAdapter.getData();


        switch (state) {
            case Constants.REFRESH_STATE:
                oldDataList.clear();
                mAdapter.setEnableLoadMore(true);
                break;
            case Constants.LOAD_STATE:
                mAdapter.loadMoreEnd();
                break;
            default:
                break;
        }
        if (newDataList != null && newDataList.size() > 0) {
            oldDataList.addAll(newDataList);
            mAdapter.setNewData(oldDataList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } else {
            mAdapter.setNewData(oldDataList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        PageInfo pageInfo = bean.getData().getPageInfo();
        totalPages = pageInfo.getTotalPages();
        currentPageNo = pageInfo.getPageNum();
        totalNum = pageInfo.getTotalRows();
        if (mAdapter.getData().size() == totalNum) {
            mAdapter.loadMoreComplete();
        }
        if (getActivity() instanceof ChooseEmailActivity) {
            ((ChooseEmailActivity) getActivity()).setNum(boxTag, pageInfo.getTotalRows());
        }

    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.getData().get(position).setCheck(!mAdapter.getData().get(position).isCheck());
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            refreshData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取选中的邮件
     *
     * @return
     */
    public ArrayList<EmailBean> getCheckedData() {
        ArrayList<EmailBean> list = new ArrayList<>();
        final List<EmailBean> data = mAdapter.getData();
        for (EmailBean bean : data) {
            if (bean.isCheck()) {
                list.add(bean);
            }
        }
        return list;
    }
}

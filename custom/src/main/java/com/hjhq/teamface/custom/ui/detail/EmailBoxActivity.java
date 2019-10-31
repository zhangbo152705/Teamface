package com.hjhq.teamface.custom.ui.detail;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.EmailBean;
import com.hjhq.teamface.basis.bean.EmailListBean;
import com.hjhq.teamface.basis.bean.ModuleFunctionBean;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.adapter.EmailListAdapter;
import com.hjhq.teamface.common.utils.AuthHelper;
import com.hjhq.teamface.common.view.EmptyView;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.bean.TabListBean;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import java.util.List;

/**
 * Created by Administrator on 2018/9/29.
 * Describe：
 */

public class EmailBoxActivity extends ActivityPresenter<EmailBoxDelegate2, DataDetailModel> {
    private int currentPageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    private int state = Constants.NORMAL_STATE;
    private EmptyView mEmptyView;
    private EmailListAdapter emailAdapter;
    private String emailAccount;
    private String sorceBean;
    private int dataAuth;
    private TabListBean.DataBean.DataListBean mData;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState != null) {
            emailAccount = savedInstanceState.getString(Constants.DATA_TAG1);
            sorceBean = savedInstanceState.getString(Constants.DATA_TAG2);
            mData = (TabListBean.DataBean.DataListBean) savedInstanceState.getSerializable(Constants.DATA_TAG3);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                emailAccount = bundle.getString(Constants.DATA_TAG1);
                sorceBean = bundle.getString(Constants.DATA_TAG2);
                mData = (TabListBean.DataBean.DataListBean) bundle.getSerializable(Constants.DATA_TAG3);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.DATA_TAG1, emailAccount);
        outState.putString(Constants.DATA_TAG2, sorceBean);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void init() {
        mEmptyView = new EmptyView(this);
        mEmptyView.setEmptyImage(R.drawable.empty_view_img);
        emailAdapter = new EmailListAdapter(EmailConstant.RECEVER_BOX, null);
        emailAdapter.setEmptyView(mEmptyView);
        viewDelegate.setAdapter(emailAdapter);
        getModuleFunctionAuth();

    }

    /**
     * 得到功能权限
     */
    public void getModuleFunctionAuth() {
        AuthHelper.getInstance().getModuleFunctionAuth(this, sorceBean, new AuthHelper.InitialDataCompleteListener() {
            @Override
            public void complete(ModuleFunctionBean moduleFunctionBean) {
                dataAuth = TextUtil.parseInt(moduleFunctionBean.getData().get(0).getData_auth(), 0);
                loadTempData();
            }

            @Override
            public void error() {
                ToastUtils.showError(mContext, "获取权限失败，请退出重试");
                finish();
            }
        });
    }

    /**
     * 加载列表数据
     */
    private void loadTempData() {
        int pageNo = state == Constants.LOAD_STATE ? currentPageNo + 1 : 1;
        model.getEmailListyAccount(this, dataAuth, pageNo, Constants.PAGESIZE, emailAccount, new ProgressSubscriber<EmailListBean>(this) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(EmailListBean emailListBean) {
                super.onNext(emailListBean);
                List<EmailBean> newDataList = emailListBean.getData().getDataList();
                List<EmailBean> oldDataList = emailAdapter.getData();


                switch (state) {
                    case Constants.NORMAL_STATE:
                    case Constants.REFRESH_STATE:
                        oldDataList.clear();
                        emailAdapter.setEnableLoadMore(true);
                        break;
                    case Constants.LOAD_STATE:
                        emailAdapter.loadMoreEnd();
                        break;
                    default:
                        break;
                }
                if (newDataList != null && newDataList.size() > 0) {
                    oldDataList.addAll(newDataList);
                    emailAdapter.notifyDataSetChanged();
                }
                PageInfo pageInfo = emailListBean.getData().getPageInfo();
                totalPages = pageInfo.getTotalPages();
                currentPageNo = pageInfo.getPageNum();
            }
        });

        /*model.getTabListData(this, dataAuth, mTabListDataReqBean, rulesId, moduleId, tabId, 20, 1, new ProgressSubscriber<DataTempResultBean>(this) {
            @Override
            public void onNext(DataTempResultBean baseBean) {
                super.onNext(baseBean);
                showDataResult(baseBean);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });*/
    }

    @Override
    protected void bindEvenListener() {
        viewDelegate.mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                model.getAuth(mContext, emailAdapter.getData().get(position).getId(), sorceBean, new ProgressSubscriber<ViewDataAuthResBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                        super.onNext(viewDataAuthResBean);
                        if (viewDataAuthResBean == null || viewDataAuthResBean.getData() == null || !(("1".equals(viewDataAuthResBean.getData().getReadAuth())) || "3".equals(viewDataAuthResBean.getData().getReadAuth()) || "4".equals(viewDataAuthResBean.getData().getReadAuth()) || "5".equals(viewDataAuthResBean.getData().getReadAuth()))) {
                            ToastUtils.showToast(mContext, "无权限");
                            return;
                        }
                        Bundle bundle = new Bundle();
                        final EmailBean data = emailAdapter.getData().get(position);
                        int boxType;
                        if (EmailConstant.INBOX.equals(data.getMail_box_id())) {
                            boxType = EmailConstant.RECEVER_BOX;
                            bundle.putInt(Constants.DATA_TAG2, boxType);
                        } else if (EmailConstant.SENDED.equals(data.getMail_box_id())) {
                            boxType = EmailConstant.SENDED_BOX;
                            bundle.putInt(Constants.DATA_TAG2, boxType);
                        }
                        bundle.putString(Constants.DATA_TAG1, data.getId());
                        bundle.putSerializable(Constants.DATA_TAG3, data);
                        UIRouter.getInstance().openUri(mContext, "DDComp://email/detail", bundle);
                    }
                });


            }
        });

        //刷新
        viewDelegate.mSwipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
            viewDelegate.mSwipeRefreshLayout.setRefreshing(false);
        });

        //加载更更多
        emailAdapter.setOnLoadMoreListener(() -> {
            if (currentPageNo >= totalPages) {
                emailAdapter.loadMoreComplete();
                emailAdapter.setEnableLoadMore(false);
                return;
            }
            state = Constants.LOAD_STATE;
            loadTempData();
        }, viewDelegate.mRecyclerView);
        super.bindEvenListener();

    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        state = Constants.REFRESH_STATE;
        loadTempData();
    }
}

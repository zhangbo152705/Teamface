package com.hjhq.teamface.project.presenter.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.SelectTaskAdapter;
import com.hjhq.teamface.basis.bean.QuoteTaskResultBean;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.ui.task.SelectTaskDelegate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * 选择任务
 *
 * @author Administrator
 * @date 2018/6/21
 */

public class SelectTaskActivity extends ActivityPresenter<SelectTaskDelegate, TaskModel> {
    private SelectTaskAdapter adapter;

    private int currentPageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    private int state = Constants.NORMAL_STATE;

    /**
     * 项目ID
     */
    private String projectId;
    private String title;
    /**
     * 0 项目任务 1个人任务
     */
    private int fromType;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            projectId = getIntent().getStringExtra(ProjectConstants.PROJECT_ID);
            fromType = projectId == null ? 1 : 0;
            title = getIntent().getStringExtra(Constants.DATA_TAG1);
        }
    }

    @Override
    public void init() {
        viewDelegate.setTitle(title);
        adapter = new SelectTaskAdapter(null);
        viewDelegate.setAdapter(adapter);
        getTaskList();
    }


    private void getTaskList() {
        int pageNum = state == Constants.LOAD_STATE ? currentPageNo + 1 : 1;
        model.queryQuoteTask(mContext, pageNum, Constants.PAGESIZE, null, fromType, projectId, new ProgressSubscriber<QuoteTaskResultBean>(mContext) {
            @Override
            public void onNext(QuoteTaskResultBean bean) {
                super.onNext(bean);
                QuoteTaskResultBean.DataBean dataBean = bean.getData();
                List<QuoteTaskResultBean.DataBean.DataListBean> dataList = dataBean.getDataList();

                switch (state) {
                    case Constants.NORMAL_STATE:
                    case Constants.REFRESH_STATE:
                        CollectionUtils.notifyDataSetChanged(adapter, adapter.getData(), dataList);
                        break;
                    case Constants.LOAD_STATE:
                        adapter.addData(dataList);
                        adapter.loadMoreComplete();
                        break;
                    default:
                        break;
                }

                //分页信息
                PageInfo pageInfo = dataBean.getPageInfo();
                totalPages = pageInfo.getTotalPages();
                currentPageNo = pageInfo.getPageNum();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (state == Constants.LOAD_STATE) {
                    adapter.loadMoreFail();
                }
            }
        });
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(ProjectConstants.PROJECT_ID, projectId);
            CommonUtil.startActivtiyForResult(mContext, SearchTaskActivity.class, Constants.REQUEST_CODE1, bundle);
        }, R.id.search_rl);

        viewDelegate.mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapte, View view, int position) {
                QuoteTaskResultBean.DataBean.DataListBean data = (QuoteTaskResultBean.DataBean.DataListBean) adapte.getItem(position);
                data.setCheck(!data.isCheck());
                adapte.notifyItemChanged(position);
            }
        });
        viewDelegate.swipeRefreshWidget.setOnRefreshListener(() -> {
            state = Constants.REFRESH_STATE;
            getTaskList();
            viewDelegate.swipeRefreshWidget.setRefreshing(false);
        });

        //加载更更多
        adapter.setOnLoadMoreListener(() -> {
            if (currentPageNo >= totalPages) {
                adapter.loadMoreEnd();
                return;
            }
            state = Constants.LOAD_STATE;
            getTaskList();
        }, viewDelegate.mRecyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<QuoteTaskResultBean.DataBean.DataListBean> list = adapter.getData();
        StringBuilder sb = new StringBuilder();
        final String[] beanName = {ProjectConstants.PERSONAL_TASK_BEAN};
        List<QuoteTaskResultBean.DataBean.DataListBean> selectedList = new ArrayList<>();
        Observable.from(list).filter(QuoteTaskResultBean.DataBean.DataListBean::isCheck).subscribe(dataBean -> {
            if (fromType == 0) {
                beanName[0] = dataBean.getBean_name();
            }
            selectedList.add(dataBean);
            sb.append(",").append(dataBean.getId());
        });
        if (TextUtil.isEmpty(sb)) {
            ToastUtils.showToast(mContext, "请选择任务");
            return super.onOptionsItemSelected(item);
        }
        sb.deleteCharAt(0);
        Intent intent = new Intent();
        intent.putExtra(Constants.DATA_TAG1, sb.toString());
        intent.putExtra(Constants.DATA_TAG2, beanName[0]);
        intent.putExtra(Constants.DATA_TAG3, fromType);
        intent.putExtra(Constants.DATA_TAG4, (Serializable) selectedList);
        intent.putExtra(Constants.DATA_TAG5, projectId);
        setResult(RESULT_OK, intent);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && Constants.REQUEST_CODE1 == requestCode) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}

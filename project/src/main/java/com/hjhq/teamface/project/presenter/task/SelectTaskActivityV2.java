package com.hjhq.teamface.project.presenter.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.bean.QuoteTaskResultBean;
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
import com.hjhq.teamface.project.adapter.ProjectFilterAdapter;
import com.hjhq.teamface.project.adapter.SelectTaskAdapter;
import com.hjhq.teamface.project.bean.QuoteTaskListResultBean;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.ui.task.SelectTaskDelegateV2;
import com.luojilab.router.facade.annotation.RouteNode;

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

@RouteNode(path = "/choose_task", desc = "关联/引用任务")
public class SelectTaskActivityV2 extends ActivityPresenter<SelectTaskDelegateV2, TaskModel> {
    private SelectTaskAdapter adapter;
    private ProjectFilterAdapter filterAdapter;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mRlMenu;
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
    public void init() {
        mRlMenu = viewDelegate.get(R.id.rl_menu);
        mDrawerLayout = viewDelegate.get(R.id.drawer_layout);
        adapter = new SelectTaskAdapter(null);
        filterAdapter = new ProjectFilterAdapter(null);
        viewDelegate.setAdapter(adapter);
        viewDelegate.setProjectAdapter(filterAdapter);
        getProjectList();
    }

    /**
     * 获取任务列表
     */
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

    /**
     * 获取项目列表
     */
    private void getProjectList() {
        model.queryTaskList(mContext, new ProgressSubscriber<QuoteTaskListResultBean>(mContext) {
            @Override
            public void onNext(QuoteTaskListResultBean bean) {
                super.onNext(bean);
                CollectionUtils.notifyDataSetChanged(filterAdapter, filterAdapter.getData(), bean.getData());
                if (filterAdapter.getData().size() > 0) {
                    filterAdapter.getData().get(0).setCheck(true);
                    final QuoteTaskListResultBean.DataBean dataBean = filterAdapter.getData().get(0);
                    final String id = dataBean.getProjectId();
                    title = dataBean.getTitle();
                    viewDelegate.setCurrentProjectName(title);
                    if (TextUtils.isEmpty(id)) {
                        projectId = "0";
                        fromType = 1;
                    } else {
                        projectId = id;
                        fromType = 0;
                    }
                    getTaskList();
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
        viewDelegate.mRvProject.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapte, View view, int position) {
                final List<QuoteTaskListResultBean.DataBean> list = filterAdapter.getData();
                for (QuoteTaskListResultBean.DataBean data : list) {
                    data.setCheck(false);
                }
                list.get(position).setCheck(true);
                adapte.notifyDataSetChanged();
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
        viewDelegate.get(R.id.iv_filter).setOnClickListener(v -> {
            mDrawerLayout.openDrawer(mRlMenu);
        });
        viewDelegate.get(R.id.rl_toolbar_back).setOnClickListener(v -> {
            onBackPressed();
        });
        viewDelegate.get(R.id.tv_menu).setOnClickListener(v -> {
            onOptionsItemSelected(null);
        });
        viewDelegate.get(R.id.tv_confirm).setOnClickListener(v -> {
            //选定项目
            mDrawerLayout.closeDrawer(mRlMenu);
            mRlMenu.postDelayed(() -> {
                String id = getCheckedProjectId();
                if (!id.equals(projectId)) {
                    projectId = id;
                    state = Constants.REFRESH_STATE;
                    getTaskList();
                }
            }, 200);

        });

    }

    private String getCheckedProjectId() {
        String id = "0";
        final List<QuoteTaskListResultBean.DataBean> data = filterAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isCheck()) {
                title = data.get(i).getTitle();
                viewDelegate.setCurrentProjectName(title);
                String check = data.get(i).getProjectId();
                if (!TextUtils.isEmpty(check)) {
                    id = check;
                    fromType = 0;
                } else {
                    fromType = 1;
                }
            }

        }
        return id;
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
        return true;
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

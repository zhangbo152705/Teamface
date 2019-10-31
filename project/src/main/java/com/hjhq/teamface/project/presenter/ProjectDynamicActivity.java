package com.hjhq.teamface.project.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.adapter.DynamicAdapter;
import com.hjhq.teamface.common.bean.DynamicListResultBean;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.ProjectLabelsAdapter;
import com.hjhq.teamface.project.adapter.TaskAllDynamicAdapter;
import com.hjhq.teamface.project.bean.ProjectLabelsListBean;
import com.hjhq.teamface.project.bean.QueryManagerRoleResultBean;
import com.hjhq.teamface.project.bean.TaskAllDynamicBean;
import com.hjhq.teamface.project.bean.TaskAllDynamicDetailBean;
import com.hjhq.teamface.project.model.ProjectModel2;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.ui.ProjectDynamicDelegate;
import com.hjhq.teamface.project.ui.ProjectLabelsDelegate;
import com.hjhq.teamface.project.util.ProjectUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * 项目标签
 * Created by Administrator on 2018/4/10.
 */
public class ProjectDynamicActivity extends ActivityPresenter<ProjectDynamicDelegate, TaskModel> {
    private DynamicAdapter mDynamicAdapter;
    private List<DynamicListResultBean.DataBean.TimeListBean> dynamicDataList = new ArrayList<>();
    private long projectId;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            projectId = intent.getLongExtra(ProjectConstants.PROJECT_ID, 0);
        }
    }

    @Override
    public void init() {
        mDynamicAdapter = new DynamicAdapter(dynamicDataList);
        viewDelegate.mRecyclerView.setAdapter(mDynamicAdapter);

        getDynamicList();
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();

    }

    /**
     * 动态列表
     */
    private void getDynamicList() {
        model.getDynamicList(this, projectId + "", ProjectConstants.PROJECT_DYNAMIC_BEAN_NAME,
                new ProgressSubscriber<DynamicListResultBean>(this) {
                    @Override
                    public void onNext(DynamicListResultBean dynamicListResultBean) {
                        super.onNext(dynamicListResultBean);
                        List<DynamicListResultBean.DataBean> data = dynamicListResultBean.getData();
                        List<DynamicListResultBean.DataBean.TimeListBean> list = new ArrayList<>();
                        Observable.from(data)
                                .filter(bean -> bean.getTimeList() != null && bean.getTimeList().size() > 0)
                                .subscribe(bean -> {
                                    List<DynamicListResultBean.DataBean.TimeListBean> timeList = bean.getTimeList();
                                    timeList.get(0).setTimeDate(bean.getTimeDate());
                                    list.addAll(bean.getTimeList());
                                });
                        dynamicDataList.addAll(list);
                        viewDelegate.setNum(dynamicDataList.size());
                        mDynamicAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



}

package com.hjhq.teamface.project.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.AllTaskGroupAdapter;
import com.hjhq.teamface.project.adapter.TaskGroupAapter;
import com.hjhq.teamface.project.bean.PersonalTaskBean;
import com.hjhq.teamface.project.bean.PersonalTaskDeatilListBean;
import com.hjhq.teamface.project.bean.PersonalTaskResultBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.presenter.task.TaskRemindActivity;
import com.hjhq.teamface.project.ui.ListDelegate;
import com.hjhq.teamface.project.util.ProjectUtil;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.Observable;


/**
 * 所有任务列表
 *
 * @author Administrator
 * @date 2018/4/10
 */
public class AllTaskListFragmentNew extends FragmentPresenter<ListDelegate, ProjectModel> {
    TaskGroupAapter allTaskAdapter;
    private RxAppCompatActivity mActivity;
    private int mType;
    JSONObject jsonObject = new JSONObject();


    public static AllTaskListFragmentNew newInstance(int index) {
        AllTaskListFragmentNew myFragment = new AllTaskListFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, index);
        myFragment.setArguments(bundle);
        return myFragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getInt(Constants.DATA_TAG1, 0);
        }
    }

    @Override
    protected void init() {
        mActivity = (RxAppCompatActivity) getActivity();
        allTaskAdapter = new TaskGroupAapter(null);
        viewDelegate.setAdapter(allTaskAdapter);
        loadCacheData();
        requestNetData(false);
    }

    /**
     * 加载缓存
     */
    private void loadCacheData() {
        final String cacheData = CacheDataHelper.getCacheData(CacheDataHelper.PROJECT_TASK_CACHE_DATA, "task_list_type" + "_" + mType);
        if (!TextUtils.isEmpty(cacheData)) {
            List<PersonalTaskResultBean.DataBean> cacheDataList = new Gson().fromJson(cacheData, new TypeToken<List<PersonalTaskResultBean.DataBean>>() {
            }.getType());
            if (cacheDataList != null && cacheDataList.size() > 0) {
                allTaskAdapter.setNewData(cacheDataList);
            }
        }
    }

    /**
     * 请求网络数据
     */
    private void requestNetData(boolean showLoading) {
        jsonObject.clear();
        jsonObject.put("queryType", mType);
        requestNetData(jsonObject, showLoading);
    }

    private void requestNetData(JSONObject jsonObject, boolean showLoading) {

        model.queryPersonalTask(mActivity, jsonObject, new ProgressSubscriber<PersonalTaskResultBean>(mActivity, showLoading) {
            @Override
            public void onNext(PersonalTaskResultBean bean) {
                super.onNext(bean);
                CollectionUtils.notifyDataSetChanged(allTaskAdapter, allTaskAdapter.getData(), bean.getData());
                CacheDataHelper.saveCacheData(CacheDataHelper.PROJECT_TASK_CACHE_DATA,
                        "task_list_type" + "_" + mType, JSONObject.toJSONString(allTaskAdapter.getData()));
            }
        });
    }




    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        //刷新
        viewDelegate.mSwipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
            viewDelegate.mSwipeRefreshLayout.setRefreshing(false);
        });

        viewDelegate.mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                PersonalTaskResultBean.DataBean data = (PersonalTaskResultBean.DataBean) adapter.getData().get(position);
                String projectId = data.getProjectId();
                Bundle bundle = new Bundle();
                bundle.putString(ProjectConstants.PROJECT_ID,projectId );
                bundle.putString(ProjectConstants.PROJECT_PERSION_TASK_CARD_TITLE,data.getTitle() );
                bundle.putInt(ProjectConstants.PROJECT_PERSION_TASK_CARD_INDEXT_TITLE,mType);
                if (TextUtils.isEmpty(projectId)){
                    bundle.putLong(ProjectConstants.PROJECT_PERSION_TASK_CARD_TYPE, 1);//个人任务
                }else {
                    bundle.putLong(ProjectConstants.PROJECT_PERSION_TASK_CARD_TYPE, 2);//项目任务
                }
                CommonUtil.startActivtiy(mActivity, TaskCardListActivity.class, bundle);
            }


        });

    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        requestNetData(true);
    }


}

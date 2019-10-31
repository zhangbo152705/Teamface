package com.hjhq.teamface.project.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.project.adapter.AllTaskGroupAdapter;
import com.hjhq.teamface.project.bean.PersonalTaskBean;
import com.hjhq.teamface.project.bean.PersonalTaskDeatilListBean;
import com.hjhq.teamface.project.bean.PersonalTaskResultBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.ui.ListDelegate;
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
public class AllTaskListFragment extends FragmentPresenter<ListDelegate, ProjectModel> {
    AllTaskGroupAdapter allTaskAdapter;
    private RxAppCompatActivity mActivity;
    private int mType;
    JSONObject jsonObject = new JSONObject();

    private int pageNum=0;//当前页码
    private int pageSize = 1000;

    public static AllTaskListFragment newInstance(int index) {
        AllTaskListFragment myFragment = new AllTaskListFragment();
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
        allTaskAdapter = new AllTaskGroupAdapter(null);
        allTaskAdapter.setQueryType(mType);
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
                //关闭筛选
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

        RxManager.$(mActivity.hashCode()).onSticky(ProjectConstants.PERSONAL_TASK_DETAIL_LIST_QUERY, indext -> {
            if ((int)indext == mType){

            }
        });
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        requestNetData(true);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageBean event) {
        if (ProjectConstants.PERSONAL_TASK_FILTER_CODE == event.getCode()) {
            allTaskAdapter.getData().clear();
            allTaskAdapter.notifyDataSetChanged();
            Fragment parentFragment = getParentFragment();
            if (parentFragment == null || !(parentFragment instanceof ListTempFragment)) {
                return;
            }
            if (((ListTempFragment) getParentFragment()).getCurrentItem() != mType) {
                return;
            }
            String sortField = event.getTag();
            jsonObject.clear();
            jsonObject.put("queryType", mType);
            jsonObject.put("sortField", sortField);
            jsonObject.put("queryWhere", event.getObject());
            requestNetData(jsonObject, false);
        } else if (event.getCode() == ProjectConstants.PROJECT_TASK_REFRESH_CODE) {
            if (TextUtils.isEmpty(event.getTag())) {
                return;
            }
            List<PersonalTaskResultBean.DataBean> data = allTaskAdapter.getData();
            if (!CollectionUtils.isEmpty(data)) {
                PersonalTaskResultBean.DataBean dataBean = data.get(0);

                Observable.from(dataBean.getTasks()).filter(item -> event.getTag().equals(item.getId() + "")).subscribe(item -> {
                    requestNetData(false);
                });
            }
        }
    }
}

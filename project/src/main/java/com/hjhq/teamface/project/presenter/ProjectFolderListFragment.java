package com.hjhq.teamface.project.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.bean.FolderNaviData;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.ProjectFolderListAdapter;
import com.hjhq.teamface.project.bean.ProjectFolderListBean;
import com.hjhq.teamface.project.model.ProjectModel2;
import com.hjhq.teamface.project.ui.ProjectFolderListFragmentDelegate;

import java.util.ArrayList;
import java.util.List;


/**
 * 任务列表
 * Created by Administrator on 2018/4/10.
 */
public class ProjectFolderListFragment extends FragmentPresenter<ProjectFolderListFragmentDelegate, ProjectModel2> {
    private int index;
    private String projectName;
    BaseQuickAdapter adapter;
    private long projectId;
    /**
     * 总页数
     */
    private int totalPages = 1;
    private int currentPageNo = 1;
    private int state = Constants.NORMAL_STATE;

    List<ProjectFolderListBean.DataBean.DataListBean> mList = new ArrayList<>();

    static ProjectFolderListFragment newInstance(int index, String projectName) {
        ProjectFolderListFragment myFragment = new ProjectFolderListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, index);
        bundle.putString(Constants.DATA_TAG2, projectName);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    public ProjectFolderListFragment() {
    }

    @SuppressLint("ValidFragment")
    public ProjectFolderListFragment(long projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            index = arguments.getInt(Constants.DATA_TAG1, 0);
            projectName = arguments.getString(Constants.DATA_TAG2);
        }
    }

    @Override
    protected void init() {

        adapter = new ProjectFolderListAdapter(mList);
        viewDelegate.setAdapter(adapter);
        loadCacheData();
        getNetData();
    }

    /**
     * 加载缓存
     */
    private void loadCacheData() {
        final String cacheData = CacheDataHelper.getCacheData(CacheDataHelper.PROJECT_FOLDER_LIST_CACHE_DATA, "project_folder_list" + "_" + projectId);
        if (!TextUtils.isEmpty(cacheData)) {
            List<ProjectFolderListBean.DataBean.DataListBean> cacheDataList =
                    new Gson().fromJson(cacheData, new TypeToken<List<ProjectFolderListBean.DataBean.DataListBean>>() {
                    }.getType());
            if (cacheDataList != null && cacheDataList.size() > 0) {
                mList.clear();
                mList.addAll(cacheDataList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 获取文件夹列表
     */
    public void getNetData() {
        int pageNo = state == Constants.LOAD_STATE ? currentPageNo + 1 : 1;
        model.getProjectFolderList(((ActivityPresenter) getActivity()), projectId, Constants.PAGESIZE, pageNo,
                new ProgressSubscriber<ProjectFolderListBean>(getActivity(), false) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (state == Constants.LOAD_STATE) {
                            adapter.loadMoreFail();
                        }
                    }

                    @Override
                    public void onNext(ProjectFolderListBean baseBean) {
                        super.onNext(baseBean);
                        showDataResult(baseBean);
                    }
                });
    }

    private void showDataResult(ProjectFolderListBean projectListBean) {
        List<ProjectFolderListBean.DataBean.DataListBean> dataList = projectListBean.getData().getDataList();

        switch (state) {
            case Constants.NORMAL_STATE:
            case Constants.REFRESH_STATE:
                CollectionUtils.notifyDataSetChanged(adapter, adapter.getData(), dataList);
                break;
            case Constants.LOAD_STATE:
                mList.addAll(dataList);
                adapter.notifyDataSetChanged();
                adapter.loadMoreComplete();
                break;
            default:
                break;
        }
        if (currentPageNo == 1) {
            CacheDataHelper.saveCacheData(CacheDataHelper.PROJECT_TASK_LIST_CACHE_DATA,
                    "project_folder_list" + "_" + projectId, JSONObject.toJSONString(dataList));
        }
        PageInfo pageInfo = projectListBean.getData().getPageInfo();
        totalPages = pageInfo.getTotalPages();
        currentPageNo = pageInfo.getPageNum();
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setItemClickListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
               /* Bundle bundle = new Bundle();
                bundle.putString(Constants.DATA_TAG1, mList.get(position).getData_id());
                bundle.putBoolean(Constants.DATA_TAG2, true);
                bundle.putString(Constants.DATA_TAG3, mList.get(position).getLibrary_type());
                bundle.putString(Constants.DATA_TAG4, mList.get(position).getFile_name());
                bundle.putString(Constants.DATA_TAG5, projectId + "");
                bundle.putString(Constants.DATA_TAG6, mList.get(position).getId());
                bundle.putSerializable(Constants.DATA_TAG7, new ArrayList<>());
                bundle.putString(Constants.DATA_TAG8, ((ProjectDetailActivity) getActivity()).priviledgeIds);
                CommonUtil.startActivtiyForResult(getActivity(), ProjectFolderListActivity.class, Constants.REQUEST_CODE2, bundle);
               */
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DATA_TAG1, mList.get(position).getData_id());
                bundle.putBoolean(Constants.DATA_TAG2, SPHelper.getEmployeeId().equals(mList.get(position).getId()));
                bundle.putString(Constants.DATA_TAG3, mList.get(position).getLibrary_type());
                bundle.putString(Constants.DATA_TAG4, mList.get(position).getFile_name());
                bundle.putString(Constants.DATA_TAG5, projectId + "");
                bundle.putString(Constants.DATA_TAG6, mList.get(position).getId());
                ArrayList<FolderNaviData> list = new ArrayList<FolderNaviData>();
                List<FolderNaviData> data = new ArrayList<>();
                if (data.size() > 0) {
                    list.addAll(data);
                    FolderNaviData data1 = new FolderNaviData();
                    data1.setFloderType(TextUtil.parseInt(mList.get(position).getLibrary_type()));
                    data1.setFolderId(projectId + "");
                    data1.setFolderName(projectName);
                    data1.setFolderLevel(0);
                    list.add(data1);
                    FolderNaviData data2 = new FolderNaviData();
                    data2.setFloderType(TextUtil.parseInt(mList.get(position).getLibrary_type()));
                    data2.setFolderId(projectId + "");
                    data2.setFolderName(mList.get(position).getFile_name());
                    data2.setFolderLevel(1);
                    list.add(data2);
                }

                bundle.putSerializable(Constants.DATA_TAG7, list);
                bundle.putBoolean(Constants.DATA_TAG8, true);
                CommonUtil.startActivtiy(getActivity(), ProjectFileActivity.class, bundle);
            }
        });
        //刷新
        viewDelegate.mRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
            viewDelegate.mRefreshLayout.setRefreshing(false);
        });

        //加载更更多
        adapter.setOnLoadMoreListener(() -> {
            if (currentPageNo >= totalPages) {
                adapter.loadMoreEnd();
                return;
            }
            state = Constants.LOAD_STATE;
            getNetData();
        }, viewDelegate.mRecyclerView);
        viewDelegate.get(R.id.search_layout).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, ProjectConstants.SEARCH_ROOT_PROJECT_FILE);
            bundle.putString(Constants.DATA_TAG2, projectId + "");
            CommonUtil.startActivtiyForResult(getActivity(), ProjectSearchActivity.class, Constants.REQUEST_CODE1, bundle);

        });
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        state = Constants.REFRESH_STATE;
        getNetData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE1:

                    break;
                case Constants.REQUEST_CODE2:
                    getNetData();
                    break;
                case Constants.REQUEST_CODE3:

                    break;
            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}

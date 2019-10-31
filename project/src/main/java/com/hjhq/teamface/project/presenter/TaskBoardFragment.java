package com.hjhq.teamface.project.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EventConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.popupwindow.OnMenuSelectedListener;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.AllNodeResultBean;
import com.hjhq.teamface.project.bean.NodeBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.presenter.navigation.AddGroupActivity;
import com.hjhq.teamface.project.presenter.navigation.TaskGroupActivity;
import com.hjhq.teamface.project.ui.TaskBoardDelegate;
import com.hjhq.teamface.project.util.ProjectUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务看板
 *
 * @author Administrator
 * @date 2018/4/10
 */
public class TaskBoardFragment extends FragmentPresenter<TaskBoardDelegate, ProjectModel> {
    private ProjectDetailActivity mActivity;
    private ArrayList<NodeBean> dataList;
    private String[] actionMenu;

    @Override
    public void init() {
        mActivity = (ProjectDetailActivity) getActivity();
        loadCacheData();
        getAllNode();
    }

    /**
     * 分组操作菜单
     */
    private void initMenu() {
        //16添加,17编辑,18移动,19删除
        if (ProjectUtil.INSTANCE.checkPermission(mActivity.priviledgeIds, 16) && ProjectUtil.INSTANCE.checkPermission(mActivity.priviledgeIds, 17)) {
            actionMenu = getResources().getStringArray(R.array.project_task_group_menu1);
        } else if (ProjectUtil.INSTANCE.checkPermission(mActivity.priviledgeIds, 16)) {
            actionMenu = getResources().getStringArray(R.array.project_task_group_menu2);
        } else if (ProjectUtil.INSTANCE.checkPermission(mActivity.priviledgeIds, 17)) {
            actionMenu = getResources().getStringArray(R.array.project_task_group_menu3);
        }
        if (actionMenu != null && actionMenu.length > 0) {
            viewDelegate.mMenu.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 加载缓存
     */
    private void loadCacheData() {
        final String cacheData = CacheDataHelper.getCacheData(CacheDataHelper.PROJECT_TASK_LIST_CACHE_DATA, "task_node_list" + "_" + mActivity.projectId);
        if (!TextUtils.isEmpty(cacheData)) {
            List<NodeBean> cacheDataList = new Gson().fromJson(cacheData, new TypeToken<List<NodeBean>>() {
            }.getType());
            if (cacheDataList != null && cacheDataList.size() > 0) {
                viewDelegate.initNavigator(getChildFragmentManager(), cacheDataList);
            }
        }
    }

    /**
     * 获取所有节点
     */
    private void getAllNode() {
        model.getAllNode(mActivity, mActivity.projectId, new ProgressSubscriber<AllNodeResultBean>(mActivity, false) {
            @Override
            public void onNext(AllNodeResultBean baseBean) {
                super.onNext(baseBean);
                dataList = baseBean.getData().getDataList();
                viewDelegate.initNavigator(getChildFragmentManager(), dataList);
            }
        });
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        RxManager.$(mActivity.hashCode()).onSticky(ProjectConstants.PROJECT_ROLE_TAG, s -> {
            initMenu();
        });
        viewDelegate.setOnClickListener(v -> {
            if (ProjectDetailActivity.IS_TEMPLETE_PROJECT) {
                ToastUtils.showToast(getActivity(), "模板项目不能操作");
                return;
            }
            ProjectUtil.INSTANCE.checkProjectStatus(getActivity(), ProjectDetailActivity.projectStatus, () -> {
                PopUtils.showBottomMenu(getActivity(), viewDelegate.getRootView(), null, actionMenu, new OnMenuSelectedListener() {
                    @Override
                    public boolean onMenuSelected(int p) {
                        Bundle bundle = new Bundle();
                        switch (actionMenu[p]) {
                            case "新增分组":
                                bundle.putLong(ProjectConstants.PROJECT_ID, mActivity.projectId);
                                CommonUtil.startActivtiy(getContext(), AddGroupActivity.class, bundle);
                                break;
                            case "管理分组":
                                bundle.putSerializable(Constants.DATA_TAG1, dataList);
                                bundle.putLong(ProjectConstants.PROJECT_ID, mActivity.projectId);
                                CommonUtil.startActivtiyForResult(getContext(), TaskGroupActivity.class, ProjectConstants.MAIN_NODE_SORT_REQUEST_CODE, bundle);
                                break;
                        }
                        return false;
                    }
                });
            });
        }, R.id.iv_navigation);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ProjectConstants.MAIN_NODE_SORT_REQUEST_CODE) {
            List<NodeBean> nodeList = (List<NodeBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            ArrayList<NodeBean> clone = (ArrayList<NodeBean>) dataList.clone();
            //循环判断位置改变
            for (int i = 0; i < clone.size(); i++) {
                NodeBean oldNode = clone.get(i);
                for (int j = 0; j < nodeList.size(); j++) {
                    NodeBean newNode = nodeList.get(j);
                    if (oldNode.getId() == newNode.getId()) {
                        dataList.set(j, clone.get(i));
                        break;
                    }
                }
            }
            refreshNode();
        }
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageBean event) {
        /*//筛选
        if (ProjectConstants.PROJECT_TASK_FILTER_CODE == event.getCode()) {
            String sortField = event.getTag();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("queryType", type);
            jsonObject.put("sortField", sortField);
            jsonObject.put("queryWhere", event.getObject());
            model.queryProjectTask(mActivity, jsonObject, new ProgressSubscriber<BaseBean>(mActivity) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });
            return;
        }*/
        Object object = event.getObject();
        if (!(object instanceof NodeBean)) {
            return;
        }
        NodeBean nodeBean = (NodeBean) object;
        switch (event.getCode()) {
            case EventConstant.PROJECT_MAIN_NODE_ADD_TAG:
                //主节点新增
                model.getAllNode(mActivity, mActivity.projectId, new ProgressSubscriber<AllNodeResultBean>(mActivity, false) {
                    @Override
                    public void onNext(AllNodeResultBean baseBean) {
                        super.onNext(baseBean);
                        ArrayList<NodeBean> nodeList = baseBean.getData().getDataList();
                        dataList.clear();
                        dataList.addAll(nodeList);
                        refreshNode();
                    }
                });
                break;
            case EventConstant.PROJECT_MAIN_NODE_EDIT_TAG:
                //主节点编辑
                int editIndex = indexOf(nodeBean.getMain_id());
                if (editIndex < 0) {
                    return;
                }
                dataList.get(editIndex).setName(nodeBean.getName());
                viewDelegate.refreshNode(dataList);
                break;
            case EventConstant.PROJECT_MAIN_NODE_DEL_TAG:
                //主节点删除
                int delIndex = indexOf(nodeBean.getMain_id());
                if (delIndex < 0) {
                    return;
                }
                dataList.remove(delIndex);
                refreshNode();
                break;
            default:
                break;
        }
    }

    /**
     * 刷新节点
     */
    private void refreshNode() {
        viewDelegate.refreshNode(dataList);
        viewDelegate.mAdapter.notifyDataSetChanged();
        viewDelegate.mViewPager.setCurrentItem(0);
    }

    /**
     * 判断位置
     */
    private int indexOf(long id) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

}

package com.hjhq.teamface.project.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CommonNewResultBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.SaveTaskLayoutRequestBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.presenter.add.AddProjectActivity;
import com.hjhq.teamface.project.presenter.task.AddTaskActivity;
import com.hjhq.teamface.project.ui.ProjectDelegate;
import com.hjhq.teamface.project.view.CommomDialog;
import com.hjhq.teamface.project.widget.utils.ProjectCustomUtil;
import com.luojilab.router.facade.annotation.RouteNode;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目
 *
 * @author Administrator
 * @date 2018/4/10
 */
@RouteNode(path = "/main", desc = "项目首页")
public class ProjectActivity extends ActivityPresenter<ProjectDelegate, ProjectModel> implements View.OnClickListener {
    List<Fragment> fragments = new ArrayList<>(2);
    private String keyword;
    private CommomDialog editDialog;
    @Override
    public void init() {
        fragments.add(ListTempFragment.newInstance(0));
        fragments.add(ListTempFragment.newInstance(1));
        viewDelegate.initFilter();
        viewDelegate.setAdapter(fragments);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.rl_back, R.id.tv_project, R.id.tv_task, R.id.iv_add_bar, R.id.iv_search_bar);
        viewDelegate.mSearchBar.setSearchListener(new SearchBar.SearchListener() {
            @Override
            public void clear() {
                keyword = "";
            }

            @Override
            public void cancel() {
                viewDelegate.hideSearch();
            }

            @Override
            public void search() {
                ((ListTempFragment) fragments.get(0)).search(keyword);
                SoftKeyboardUtils.hide(viewDelegate.get(R.id.et_search_in_searchbar));
            }

            @Override
            public void getText(String text) {
                keyword = text;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_back) {
            finish();
        } else if (id == R.id.tv_project) {
            viewDelegate.setCurrentItem(0);
        } else if (id == R.id.tv_task) {
            viewDelegate.setCurrentItem(1);
        } else if (id == R.id.iv_add_bar) {
            if (viewDelegate.getCurrentItem() == 0) {
                CommonUtil.startActivtiyForResult(mContext, AddProjectActivity.class, Constants.REQUEST_CODE1, new Bundle());
            } else {
                /*Bundle bundle = new Bundle();
                bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PERSONAL_TASK_BEAN);
                CommonUtil.startActivtiyForResult(mContext, AddTaskActivity.class, ProjectConstants.ADD_TASK_TASK_REQUEST_CODE, bundle);*/
                showEditDialog();
            }
        } else if (id == R.id.iv_search_bar) {
            if (viewDelegate.getCurrentItem() == 0) {
                viewDelegate.showSearch();
            } else {
                viewDelegate.openDrawer();
            }
        }
    }

    /**
     * 编辑弹窗
     */
    public void showEditDialog(){
        editDialog = new CommomDialog(mContext,0,"",0,new CommomDialog.OnCloseListener(){

            @Override
            public void onClick(CommomDialog dialog, boolean confirm,int type) {
                if (confirm){
                    String editName = dialog.contentTxt.getText().toString();
                    if (TextUtil.isEmpty(editName)){
                        ToastUtils.showToast(mContext,getResources().getString(R.string.project_selete_edit_node));
                        return;
                    }else {
                        addPersonTask(editName);
                    }
                    dialog.dismiss();
                }
            }
        });
        if (editDialog != null && !editDialog.isShowing()){
            editDialog.show();
        }
    }

    public void addPersonTask(String taskName){
        SaveTaskLayoutRequestBean bean = new SaveTaskLayoutRequestBean();
        bean.setBean(ProjectConstants.PERSONAL_TASK_BEAN);
        JSONObject json = new JSONObject();
        json.put("text_name",taskName);
        bean.setData(json);
        model.addPersonalTask(mContext, bean, new ProgressSubscriber<CommonNewResultBean>(mContext) {
                @Override
                public void onNext(CommonNewResultBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "新增成功");
                    /*Intent intent = new Intent();
                    intent.putExtra(Constants.DATA_TAG1, baseBean.getData().getId());
                    setResult(RESULT_OK, intent);
                    finish();
                    EventBusUtils.sendEvent(new MessageBean(ProjectConstants.PERSONAL_TASK_REFRESH_CODE, null, null));*/
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });
    }

    @Override
    public void onBackPressed() {
        if (!viewDelegate.closeDrawer()) {
            super.onBackPressed();
        }
    }

    /**
     * 筛选
     */
    public void filter() {
        viewDelegate.closeDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE1 && resultCode == RESULT_OK) {
            //新增项目，刷新列表
            ((ListTempFragment) fragments.get(0)).refreshData();
        } else if (requestCode == ProjectConstants.ADD_TASK_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            //新增个人任务,刷新列表
            ((ListTempFragment) fragments.get(1)).refreshData();
        }
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageBean event) {
        if (ProjectConstants.PERSONAL_TASK_FILTER_CODE == event.getCode()) {
            viewDelegate.closeDrawer();
        }
    }
}

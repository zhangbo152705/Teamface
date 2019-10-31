package com.hjhq.teamface.project.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.FragmentAdapter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.ProjectMemberResultBean;
import com.hjhq.teamface.project.bean.QueryManagerRoleResultBean;
import com.hjhq.teamface.project.bean.QuerySettingResultBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.presenter.add.ProjectAddFolderActivity;
import com.hjhq.teamface.project.presenter.add.ProjectAddShareActivity;
import com.hjhq.teamface.project.presenter.filter.FilterFragment;
import com.hjhq.teamface.project.presenter.filter.TaskSearchActivity;
import com.hjhq.teamface.project.ui.ProjectDetailDelegate;
import com.hjhq.teamface.project.util.ProjectUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.hjhq.teamface.common.utils.CommonUtil.startActivtiy;
import static com.hjhq.teamface.common.utils.CommonUtil.startActivtiyForResult;

/**
 * 项目详情
 *
 * @author Administrator
 * @date 2018/4/12
 */

public class ProjectDetailActivity extends ActivityPresenter<ProjectDetailDelegate, ProjectModel> implements View.OnClickListener {
    static final String[] MORE_FUNCTION = {"项目设置", "成员管理", "项目标签","项目动态"};
    private FragmentAdapter mAdapter;
    private List<Fragment> fragments;
    public long projectId;
    public String projectName;
    public String priviledgeIds;
    public String projectTimeStatus;
    public String projectCompleteStatus;
    private TaskBoardFragmentNew taskFragment;
    private boolean isCreator = false;
    private boolean isMember = false;

    public static String projectStatus;
    private FilterFragment mFilterFragment;
    private boolean initFragmentFlag = false;
    /**
     * 模板项目不准编辑列表和分组
     */
    public static boolean IS_TEMPLETE_PROJECT = true;


    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            projectId = intent.getLongExtra(ProjectConstants.PROJECT_ID, 0);
            projectName = intent.getStringExtra(ProjectConstants.PROJECT_NAME);
            projectStatus = intent.getStringExtra(ProjectConstants.PROJECT_STATUS);
            IS_TEMPLETE_PROJECT = intent.getBooleanExtra(ProjectConstants.PROJECT_TEMPLETE, true);
        }
        if (projectId == 0) {
            ToastUtils.showError(this, "项目ID为空");
            finish();
        }
    }

    @Override
    public void init() {
        getProjectInfo();
        queryProjectMember();
        initFilter();


        getProjectRoleInfo();
    }

    /**
     * 获取项目信息
     */
    private void getProjectInfo() {
        model.querySettingById(mContext, projectId, new ProgressSubscriber<QuerySettingResultBean>(mContext, false) {
            @Override
            public void onNext(QuerySettingResultBean querySettingResultBean) {
                super.onNext(querySettingResultBean);
                QuerySettingResultBean.DataBean data = querySettingResultBean.getData();
                if (data.getStar_level() == 1) {
                    viewDelegate.showMenu(0, 2, 3);
                }
                isCreator = SPHelper.getEmployeeId().equals(data.getCreate_by() + "");
                projectName = data.getName();
                viewDelegate.setToolTitle(projectName);
                projectTimeStatus = data.getProject_time_status();
                projectCompleteStatus = data.getProject_complete_status();
                projectStatus = data.getProject_status();
                viewDelegate.getRootView().postDelayed(() -> {
                    EventBusUtils.sendStickyEvent(new MessageBean(0, ProjectConstants.PROJECT_STATUS_CHANGE, "0".equals(projectStatus)));
                    EventBusUtils.sendEvent(new MessageBean(0, ProjectConstants.PROJECT_STATUS_CHANGE, "0".equals(projectStatus)));
                }, 2000);
            }

            @Override
            public void onError(Throwable e) {
                dismissWindowView();
                e.printStackTrace();
                ToastUtils.showError(mContext, "获取项目信息失败");
            }
        });
    }

    /**
     * 查询项目成员
     */
    private void queryProjectMember() {
        model.queryProjectMember(mContext, projectId, new ProgressSubscriber<ProjectMemberResultBean>(mContext) {
            @Override
            public void onNext(ProjectMemberResultBean projectMemberResultBean) {
                super.onNext(projectMemberResultBean);
                List<ProjectMemberResultBean.DataBean.DataListBean> data = projectMemberResultBean.getData().getDataList();
                if (data != null && data.size() > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        isMember = SPHelper.getEmployeeId().equals(data.get(i).getEmployee_id() + "");
                        if (isCreator) {
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化筛选控件
     */
    public void initFilter() {
        mFilterFragment = FilterFragment.newInstance(1, projectId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer_content, mFilterFragment).commit();
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.rl_task, R.id.rl_share, R.id.rl_library);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rl_task) {
            //任务
            viewDelegate.showMenu(1, 2, 3);
            viewDelegate.setCurrentItem(0);
            changeIconState(1);
            viewDelegate.setToolTitle(projectName);
        } else if (viewId == R.id.rl_share) {
            //分享
            if (isCreator || isMember) {
                viewDelegate.showMenu(5);
            } else {
                viewDelegate.showMenu();
            }
            viewDelegate.setCurrentItem(1);
            changeIconState(2);
            viewDelegate.setToolTitle("分享");
        } else if (viewId == R.id.rl_library) {
            //文库
            viewDelegate.setToolTitle("文库");
            changeIconState(3);
            if (TextUtils.isEmpty(priviledgeIds)) {
                viewDelegate.showMenu();
            } else {
                if (ProjectUtil.INSTANCE.checkPermission(priviledgeIds, 31)) {
                    viewDelegate.showMenu(4);
                } else {
                    viewDelegate.showMenu();
                }
            }
            viewDelegate.setCurrentItem(2);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                updateStar(0);
                break;
            case 1:
                updateStar(1);
                break;
            case 2:
                viewDelegate.openDrawer();
                //openSearh();
                break;
            case 3:
                moreFunction();
                break;
            case 4:
                //添加文件夹
                addFolder();
                break;
            case 5:
                //添加分享
                addShare();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSearh() {
        Bundle bundle = new Bundle();
        bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
        bundle.putString(ProjectConstants.PROJECT_NAME, projectName);
        CommonUtil.startActivtiyForResult(mContext, TaskSearchActivity.class, Constants.REQUEST_CODE7, bundle);

    }

    @Override
    public void onBackPressed() {
        if (!viewDelegate.closeDrawer()) {
            super.onBackPressed();
        }
    }

    /**
     * 新增分享
     */
    private void addShare() {
        ProjectUtil.INSTANCE.checkProjectStatus(mContext, projectStatus, () -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG2, ProjectConstants.TYPE_ADD_SHARE);
            bundle.putString(Constants.DATA_TAG3, projectId + "");
            CommonUtil.startActivtiyForResult(mContext, ProjectAddShareActivity.class, Constants.REQUEST_CODE9, bundle);
        });
    }

    private void changeIconState(int index) {
        switch (index) {
            case 1:


                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
        }
    }

    /**
     * 新建文件夹
     */
    private void addFolder() {
        ProjectUtil.INSTANCE.checkProjectStatus(mContext, projectStatus, () -> {
            if (ProjectUtil.INSTANCE.checkPermission(priviledgeIds, 31)) {
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.DATA_TAG1, projectId);
                bundle.putInt(ProjectConstants.EDIT_FOLDER_TYPE, ProjectConstants.ADD_FOLDER);
                CommonUtil.startActivtiyForResult(mContext, ProjectAddFolderActivity.class, Constants.REQUEST_CODE2, bundle);
            } else {
                ToastUtils.showError(mContext, "无权限");
            }
        });


    }

    /**
     * 更改星标
     *
     * @param starLevel 星标 0为否，1为是
     */
    private void updateStar(int starLevel) {
        ProjectUtil.INSTANCE.checkProjectStatus(mContext, projectStatus, () -> {
            model.updateStar(mContext, projectId, starLevel, new ProgressSubscriber<BaseBean>(mContext) {
                @Override
                public void onNext(BaseBean bean) {
                    super.onNext(bean);
                    if (starLevel == 0) {
                        viewDelegate.showMenu(1, 2, 3);
                    } else {
                        viewDelegate.showMenu(0, 2, 3);
                    }
                    EventBusUtils.sendEvent(new MessageBean(ProjectConstants.PROJECT_INFO_EDIT_EVENT_CODE, null, null));
                }
            });
        });

    }

    /**
     * 更多功能
     */
    private void moreFunction() {
        PopUtils.showBottomMenu(this, viewDelegate.getRootView(), null, MORE_FUNCTION, p -> {
            Bundle bundle = new Bundle();
            bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
            switch (p) {
                case 0:
                    startActivtiyForResult(mContext, EditProjectActivity.class, Constants.REQUEST_CODE8, bundle);
                    break;
                case 1:
                    bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                    bundle.putString(ProjectConstants.PROJECT_STATUS, projectStatus);
                    startActivtiy(mContext, ProjectMemberActivity.class, bundle);
                    break;
                case 2:
                    bundle.putInt(Constants.DATA_TAG1, ProjectConstants.EDIT_MODE);
                    bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                    bundle.putString(ProjectConstants.PROJECT_STATUS, projectStatus);
                    startActivtiy(mContext, ProjectLabelsActivity.class, bundle);
                    break;
                case 3:
                    bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                    startActivtiy(mContext, ProjectDynamicActivity.class, bundle);
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    /**
     * 获取项目角色信息
     */
    public void getProjectRoleInfo() {
        //查询管理员权限
        new TaskModel().queryManagementRoleInfo(mContext,
                TextUtil.parseLong(SPHelper.getEmployeeId()), projectId,
                new ProgressSubscriber<QueryManagerRoleResultBean>(mContext, false) {
                    @Override
                    public void onNext(QueryManagerRoleResultBean queryManagerRoleResultBean) {
                        super.onNext(queryManagerRoleResultBean);
                        QueryManagerRoleResultBean.DataBean.PriviledgeBean priviledge = queryManagerRoleResultBean.getData().getPriviledge();
                        priviledgeIds = priviledge.getPriviledge_ids();
                        RxManager.$(ProjectDetailActivity.this.hashCode()).post(ProjectConstants.PROJECT_ROLE_TAG, priviledgeIds);
                        if (!initFragmentFlag) {
                            initFragment();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissWindowView();
                        e.printStackTrace();
                        ToastUtils.showError(mContext, "获取项目角色权限失败");
                    }
                });
    }

    private void initFragment() {
        initFragmentFlag = true;
        fragments = new ArrayList<>(4);
        fragments.add(taskFragment = new TaskBoardFragmentNew(projectStatus));
        fragments.add(ProjectShareListFragment.newInstance(projectId));
        fragments.add(new ProjectFolderListFragment(projectId, projectName));
        mAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        viewDelegate.setAdapter(mAdapter);
    }


    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageBean event) {
        if (ProjectConstants.PROJECT_TASK_FILTER_CODE == event.getCode()) {
            viewDelegate.closeDrawer();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            if (Constants.REQUEST_CODE8 == requestCode) {
                String state = data.getStringExtra(Constants.DATA_TAG1);
                if (TextUtils.isEmpty(state)) {
                    setResult(RESULT_OK);
                    refashDetailData();
                    return;
                }
                EventBusUtils.sendEvent(new MessageBean(0, ProjectConstants.PROJECT_STATUS_CHANGE, "0".equals(state)));
                switch (state) {
                    case ProjectConstants.PROJECT_STATUS_DELETED:
                        //项目已删除
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case ProjectConstants.PROJECT_STATUS_PAUSE:
                        //项目已暂停
                        setResult(RESULT_OK);
                        refashDetailData();
                        break;
                    case ProjectConstants.PROJECT_STATUS_FILED:
                        //项目已归档
                        setResult(RESULT_OK);
                        refashDetailData();
                        break;
                    case ProjectConstants.PROJECT_STATUS_RUNNING:
                        refashDetailData();
                        setResult(RESULT_OK);
                        break;
                    default:
                        break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 刷新项目数据
     */
    private void refashDetailData() {
        getProjectInfo();
        //initFilter();
        queryProjectMember();
        mFilterFragment.getNewData();
        getProjectRoleInfo();
    }
}

package com.hjhq.teamface.project.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.popupwindow.OnMenuSelectedListener;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.ui.time.DateTimeSelectPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.EditProjectBean;
import com.hjhq.teamface.basis.bean.ProjectInfoBean;
import com.hjhq.teamface.project.bean.QueryManagerRoleResultBean;
import com.hjhq.teamface.project.model.ProjectModel2;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.ui.add.NewOrEditProjectDelegate;
import com.hjhq.teamface.project.util.ProjectUtil;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目设置
 * Created by Administrator on 2018/4/10.
 */
@RouteNode(path = "/setting", desc = "项目设置")
public class EditProjectActivity extends ActivityPresenter<NewOrEditProjectDelegate, ProjectModel2> {
    private String[] projectOpenStatusMenu;
    private String[] progressCalculateMenu;
    private String[] projectStatusMenu;
    private String[] projectStateChangeNotifyMenu;
    private int currentScopeIndex = 0;
    private int currentProgressIndex = 0;
    private Member admin;
    private Calendar mStartCalendar;
    private Calendar mStopCalendar;
    private long mProjectId;
    private String datetimeType;
    private long mSatrtTime;
    private long mEndTime;
    private ProjectInfoBean.DataBean mProjectInfo;
    private boolean canChangeInfo = false;
    private boolean canChangeState = false;
    private String priviledgeIds;
    private String mCurrentState;
    /**
     * 更改截止时间时是否弹窗
     */
    private String projectTimeStatus;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            mProjectId = getIntent().getLongExtra(ProjectConstants.PROJECT_ID, 0L);
            if (mProjectId == 0) {
                ToastUtils.showError(mContext, "数据错误");
                finish();
            }
        }
    }

    @Override
    public void init() {
        //软键盘弹出时,底部操作按钮不被顶起
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewDelegate.get(R.id.next0).setVisibility(View.INVISIBLE);
        viewDelegate.get(R.id.tv_name).setVisibility(View.INVISIBLE);
        viewDelegate.get(R.id.et_name).setVisibility(View.INVISIBLE);
        viewDelegate.get(R.id.rl_root).setVisibility(View.INVISIBLE);

        datetimeType = TextUtil.getNonNull("yyyy-MM-dd", null);
        mStartCalendar = Calendar.getInstance();
        mStopCalendar = Calendar.getInstance();
        projectStatusMenu = getResources().getStringArray(R.array.project_status_array);
        projectOpenStatusMenu = getResources().getStringArray(R.array.project_open_array);
        progressCalculateMenu = getResources().getStringArray(R.array.project_progress_type_array);
        projectStateChangeNotifyMenu = getResources().getStringArray(R.array.project_state_change_notify_array);
        mStartCalendar = Calendar.getInstance();
        mStopCalendar = Calendar.getInstance();
        viewDelegate.setTitle("项目设置");
        viewDelegate.setRightMenuTexts("完成");
        viewDelegate.showMenu();
        viewDelegate.get(R.id.rl_template).setVisibility(View.GONE);
        viewDelegate.get(R.id.rl_auth).setVisibility(View.GONE);
        viewDelegate.get(R.id.rl_admin).setClickable(false);
        viewDelegate.get(R.id.rl_start_time).setClickable(false);
        viewDelegate.get(R.id.rl_end_time).setClickable(false);
        viewDelegate.get(R.id.rl_scope).setClickable(false);
        queryProjectRoleInfo();

    }


    /**
     * 获取项目角色信息
     */
    public void queryProjectRoleInfo() {
        new TaskModel().queryManagementRoleInfo(mContext, TextUtil.parseLong(SPHelper.getEmployeeId()), mProjectId, new ProgressSubscriber<QueryManagerRoleResultBean>(mContext) {
            @Override
            public void onNext(QueryManagerRoleResultBean queryManagerRoleResultBean) {
                super.onNext(queryManagerRoleResultBean);
                priviledgeIds = queryManagerRoleResultBean.getData().getPriviledge().getPriviledge_ids();
                canChangeInfo = ProjectUtil.INSTANCE.checkPermission(priviledgeIds, 7);
                canChangeState = ProjectUtil.INSTANCE.checkPermission(priviledgeIds, 9);
                getNetData();
            }

            @Override
            public void onError(Throwable e) {
                dismissWindowView();
                e.printStackTrace();
                ToastUtils.showError(mContext, "获取项目角色信息失败");
            }
        });
    }


    /**
     * 获取网络数据
     */
    private void getNetData() {
        model.getProjectSettingDetail(mContext, mProjectId, new ProgressSubscriber<ProjectInfoBean>(mContext) {
            @Override
            public void onNext(ProjectInfoBean bean) {
                super.onNext(bean);
                mProjectInfo = bean.getData();
                showData();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void showData() {
        if (mProjectInfo == null) {
            ToastUtils.showError(mContext, "数据错误");
            return;
        }
        //负责人
        String name = mProjectInfo.getLeader_name();
        String avatar = mProjectInfo.getPic_url();
        String leaderId = mProjectInfo.getLeader();
        if (!TextUtils.isEmpty(SPHelper.getEmployeeId()) && SPHelper.getEmployeeId().equals(leaderId)) {
            canChangeInfo = true;
            canChangeState = true;
        }
        if (!TextUtil.isEmpty(leaderId) && !"0".equals(leaderId)) {
            viewDelegate.setProjectPrinciple(avatar, name);
            admin = new Member();
            admin.setEmployee_name(name);
            admin.setPicture(avatar);
            admin.setId(TextUtil.parseLong(leaderId));
        }

        //项目状态
        String projectStatus = mProjectInfo.getProject_status();
        mCurrentState = projectStatus;
        switch (projectStatus) {
            case ProjectConstants.PROJECT_STATUS_RUNNING:
                viewDelegate.setProjectStatusText(projectStatusMenu[0]);
                break;
            case ProjectConstants.PROJECT_STATUS_FILED:
                viewDelegate.setProjectStatusText(projectStatusMenu[1]);
                break;
            case ProjectConstants.PROJECT_STATUS_PAUSE:
                viewDelegate.setProjectStatusText(projectStatusMenu[2]);
                break;
            case ProjectConstants.PROJECT_STATUS_DELETED:
                viewDelegate.setProjectStatusText(projectStatusMenu[3]);
                break;
        }
        //项目可见范围（0不公开 1公开）
        String rangeStatus = mProjectInfo.getVisual_range_status();
        switch (rangeStatus) {
            case ProjectConstants.PROJECT_OPEN_STATE_NO:
                currentScopeIndex = 1;
                viewDelegate.setScopeText(projectOpenStatusMenu[currentScopeIndex]);
                break;
            case ProjectConstants.PROJECT_OPEN_STATE_YES:
                currentScopeIndex = 0;
                viewDelegate.setScopeText(projectOpenStatusMenu[currentScopeIndex]);
                break;
            default:
                currentScopeIndex = 1;
                viewDelegate.setScopeText(projectOpenStatusMenu[currentScopeIndex]);
                break;
        }
        mSatrtTime = TextUtil.parseLong(mProjectInfo.getStart_time());
        mEndTime = TextUtil.parseLong(mProjectInfo.getEnd_time());
        //开始时间
        if (mSatrtTime > 0) {
            mStartCalendar.setTimeInMillis(mSatrtTime);
            viewDelegate.setStartTime(mStartCalendar);
        }
        //截止时间
        if (mEndTime > 0) {
            mStopCalendar.setTimeInMillis(mEndTime);
            viewDelegate.setEndTime(mStopCalendar);
        }

        //进度 0自动计算 1手动填写
        switch (mProjectInfo.getProject_progress_status()) {
            case ProjectConstants.PROJECT_PROGRESS_STATUS_AUTO:
                currentProgressIndex = 0;
                viewDelegate.setProgressText(0, progressCalculateMenu[currentProgressIndex]);
                viewDelegate.setProjectAutoProgress(mProjectInfo.getProject_progress_number());
                break;
            case ProjectConstants.PROJECT_PROGRESS_STATUS_INPUT:
                currentProgressIndex = 1;
                viewDelegate.setProgressText(1, progressCalculateMenu[currentProgressIndex]);
                viewDelegate.setProjectInputProgress(mProjectInfo.getProject_progress_content());
                break;
            default:
                viewDelegate.get(R.id.rl_progress).setVisibility(View.GONE);
                break;
        }
        viewDelegate.setProjectInputProgress(mProjectInfo.getProject_progress_content());
        viewDelegate.setProjectAutoProgress(mProjectInfo.getProject_progress_number());


        //描述
        viewDelegate.setProjectDes(mProjectInfo.getNote());
        //项目名字
        viewDelegate.setProjectName(mProjectInfo.getName());
        if (canChangeInfo) {
            viewDelegate.get(R.id.et_name).setVisibility(View.VISIBLE);
        } else {
            viewDelegate.get(R.id.tv_name).setVisibility(View.VISIBLE);
        }
        //设置底部按钮
        if (canChangeState) {
            viewDelegate.setButtomOptions(mProjectInfo.getProject_status());
        } else {
            viewDelegate.get(R.id.fl_can).setVisibility(View.GONE);
        }

        if (canChangeInfo) {
            //创建者/进行中
            viewDelegate.projectEditable(ProjectConstants.PROJECT_STATUS_RUNNING.equals(mProjectInfo.getProject_status()));

        } else {
            viewDelegate.projectEditable(false);
            switch (mProjectInfo.getProject_progress_status()) {
                case ProjectConstants.PROJECT_PROGRESS_STATUS_AUTO:
                    viewDelegate.setProjectAutoProgress(mProjectInfo.getProject_progress_number());
                    break;
                case ProjectConstants.PROJECT_PROGRESS_STATUS_INPUT:
                    viewDelegate.setProjectAutoProgress(mProjectInfo.getProject_progress_content());
                    break;
                default:
                    viewDelegate.get(R.id.rl_progress).setVisibility(View.GONE);
                    break;
            }
            viewDelegate.get(R.id.et_progress).setVisibility(View.GONE);
            viewDelegate.get(R.id.tv_progress).setVisibility(View.VISIBLE);
            viewDelegate.get(R.id.rl_progress).setVisibility(View.VISIBLE);
        }

        switch (mProjectInfo.getProject_status()) {
            case ProjectConstants.PROJECT_STATUS_RUNNING:
                //0进行中
                viewDelegate.setProjectStatusText(projectStatusMenu[0]);
                break;
            case ProjectConstants.PROJECT_STATUS_FILED:
                //1已归档
                viewDelegate.setProjectStatusText(projectStatusMenu[1]);
                break;
            case ProjectConstants.PROJECT_STATUS_PAUSE:
                //2已暂停
                viewDelegate.setProjectStatusText(projectStatusMenu[2]);
                break;
            case ProjectConstants.PROJECT_STATUS_DELETED:
                //3已删除
                viewDelegate.setProjectStatusText(projectStatusMenu[3]);
                break;
            default:
                break;
        }
        //初始化完成,显示数据
        viewDelegate.get(R.id.rl_root).setVisibility(View.VISIBLE);

    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        //负责人
        viewDelegate.get(R.id.rl_admin).setOnClickListener(v -> {
            ArrayList<Member> list = new ArrayList<Member>();
            if (admin != null) {
                admin.setCheck(true);
                list.add(admin);
            }
            Bundle bundle = new Bundle();
            bundle.putInt(C.CHECK_TYPE_TAG, C.RADIO_SELECT);
            bundle.putSerializable(C.SPECIAL_MEMBERS_TAG, list);
            CommonUtil.startActivtiyForResult(mContext,
                    SelectMemberActivity.class, Constants.REQUEST_CODE4, bundle);
        });
        //开始时间
        viewDelegate.get(R.id.rl_start_time).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, mStartCalendar);
            bundle.putString(DateTimeSelectPresenter.FORMAT, datetimeType);
            CommonUtil.startActivtiyForResult(mContext, DateTimeSelectPresenter.class, Constants.REQUEST_CODE1, bundle);
        });
        //结束时间
        viewDelegate.get(R.id.rl_end_time).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, mStopCalendar);
            bundle.putString(DateTimeSelectPresenter.FORMAT, datetimeType);
            CommonUtil.startActivtiyForResult(mContext, DateTimeSelectPresenter.class, Constants.REQUEST_CODE2, bundle);
        });
        //项目进度
        viewDelegate.get(R.id.rl_progress_type).setOnClickListener(v -> {
            PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "", progressCalculateMenu, currentProgressIndex, 0, new OnMenuSelectedListener() {
                @Override
                public boolean onMenuSelected(int p) {
                    currentProgressIndex = p;
                    viewDelegate.setProgressText(p, progressCalculateMenu[currentProgressIndex]);
                    return false;
                }
            });
        });
        //可见范围
        viewDelegate.get(R.id.rl_scope).setOnClickListener(v -> {
            PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "", projectOpenStatusMenu, currentScopeIndex, 0, p -> {
                currentScopeIndex = p;
                viewDelegate.setScopeText(projectOpenStatusMenu[currentScopeIndex]);
                return false;
            });
        });
        //开始时间
        viewDelegate.get(R.id.rl_start_time).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, mStartCalendar);
            bundle.putString(DateTimeSelectPresenter.FORMAT, datetimeType);
            //bundle.putLong(Constants.DATA_TAG1, System.currentTimeMillis());
            bundle.putLong(Constants.DATA_TAG1, 0L);
            bundle.putString(Constants.DATA_TAG2, "开始时间提示语");
            CommonUtil.startActivtiyForResult(mContext, DateTimeSelectPresenter.class, Constants.REQUEST_CODE1, bundle);
        });
        //结束时间
        viewDelegate.get(R.id.rl_end_time).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, mStartCalendar);
            bundle.putString(DateTimeSelectPresenter.FORMAT, datetimeType);
            //bundle.putLong(Constants.DATA_TAG1, System.currentTimeMillis());
            bundle.putLong(Constants.DATA_TAG1, 0L);
            bundle.putString(Constants.DATA_TAG2, "截止时间提示语");
            CommonUtil.startActivtiyForResult(mContext, DateTimeSelectPresenter.class, Constants.REQUEST_CODE2, bundle);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        editProject();
        return super.onOptionsItemSelected(item);
    }

    /**
     * 编辑项目
     *
     * @return
     */
    private void editProject() {
        if (!ProjectUtil.INSTANCE.checkProjectPermission(mContext, priviledgeIds, 7)) {
            return;
        }
        EditProjectBean bean = new EditProjectBean();
        bean.setId(mProjectId);
        String name = viewDelegate.getProjectName();
        String des = viewDelegate.getProjectDes();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast(mContext, "项目名字不能为空");
            return;
        }

        bean.setName(name);
        bean.setDescribe(des);
        bean.setStartTime(mSatrtTime);
        bean.setEndTime(mEndTime);
        if (admin == null || admin.getId() <= 0) {
            ToastUtils.showToast(mContext, "负责人不能为空");
            return;
        }
        bean.setLeader(admin.getId());
        int progressValue = viewDelegate.getProgressValue();

        if (currentProgressIndex == 0) {
            bean.setProgressContent(0);
        } else {
            if (progressValue < 0 || viewDelegate.getProgressValue() > 100) {
                ToastUtils.showToast(mContext, "进度为0-100之间的整数");
                return;
            }
            bean.setProgressContent(viewDelegate.getProgressValue());
        }
        bean.setProgressStatus(currentProgressIndex);
        bean.setVisualRange(currentScopeIndex == 0 ? 1 : 0);


        model.saveProjectInfo(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onCompleted() {
                super.onCompleted();
                //getNetData();
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_TAG1, mCurrentState);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showError(mContext, "操作失败");
            }
        });
        return;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE1:
                if (resultCode == RESULT_OK) {
                    //开始时间
                    if (data != null) {
                        Calendar c = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
                        if (c != null) {
                            if (mEndTime > 0 && c.getTimeInMillis() > mEndTime) {
                                ToastUtils.showToast(mContext, "开始时间不能晚于截止时间");
                                return;
                            }
                            mStartCalendar = c;
                            mSatrtTime = c.getTimeInMillis();
                            viewDelegate.setStartTime(mStartCalendar);
                        }
                    }
                } else if (resultCode == Constants.CLEAR_RESULT_CODE) {
                    viewDelegate.setStartTime(null);
                    mSatrtTime = 0L;
                }

                break;
            case Constants.REQUEST_CODE2:
                if (resultCode == RESULT_OK) {
                    //截止时间
                    if (data != null) {
                        Calendar c = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);

                        if (c != null) {
                            if (mSatrtTime > 0 && mSatrtTime > c.getTimeInMillis()) {
                                ToastUtils.showToast(mContext, "截止时间不能早于开始时间");
                                return;
                            }
                            mStopCalendar = c;
                            mEndTime = c.getTimeInMillis();
                            viewDelegate.setEndTime(mStopCalendar);
                        }
                    }
                } else if (resultCode == Constants.CLEAR_RESULT_CODE) {
                    viewDelegate.setEndTime(null);
                    mEndTime = 0L;
                }
                break;
            case Constants.REQUEST_CODE3:
                if (resultCode == RESULT_OK) {

                }
                break;
            case Constants.REQUEST_CODE4:
                if (resultCode == RESULT_OK) {
                    //负责人
                    if (data != null) {
                        ArrayList<Member> list = (ArrayList<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
                        if (list != null & list.size() > 0) {
                            admin = list.get(0);
                            viewDelegate.setAdmin(admin);
                        } else {
                            admin = null;
                            viewDelegate.setAdmin(null);
                        }
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 修改项目状态
     *
     * @param status
     */
    public void switchProjectStatus(String status) {
        if (!ProjectUtil.INSTANCE.checkProjectPermission(mContext, priviledgeIds, 9)) {
            return;
        }
        String subTitle = "";
        switch (status) {
            case ProjectConstants.PROJECT_STATUS_DELETED:
                subTitle = projectStateChangeNotifyMenu[3];
                break;
            case ProjectConstants.PROJECT_STATUS_FILED:
                subTitle = projectStateChangeNotifyMenu[1];
                break;
            case ProjectConstants.PROJECT_STATUS_PAUSE:
                subTitle = projectStateChangeNotifyMenu[2];
                break;
            case ProjectConstants.PROJECT_STATUS_RUNNING:
                subTitle = projectStateChangeNotifyMenu[0];
                break;
            default:
                break;
        }

        projectOption(status, subTitle);
    }

    private void projectOption(final String status, String subTitle) {
        DialogUtils.getInstance().sureOrCancel(mContext, "", subTitle, viewDelegate.getRootView(),
                new DialogUtils.OnClickSureListener() {
                    @Override
                    public void clickSure() {
                        Map<String, String> map = new HashMap<>();
                        map.put("id", mProjectId + "");
                        map.put("status", status);
                        model.editProjectStatus(mContext, map, new ProgressSubscriber<BaseBean>(mContext) {
                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                ToastUtils.showError(mContext, "操作失败");
                            }

                            @Override
                            public void onNext(BaseBean baseBean) {
                                super.onNext(baseBean);
                                Intent intent = new Intent();
                                intent.putExtra(Constants.DATA_TAG1, status);
                                mCurrentState = status;
                                if (ProjectConstants.PROJECT_STATUS_DELETED.equals(status)) {
                                    // TODO: 2018/7/11 项目被删除通知

                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    // TODO: 2018/7/11 状态变更通知
                                    queryProjectRoleInfo();
                                    setResult(RESULT_OK, intent);
                                }
                            }
                        });
                    }
                });
    }

}

package com.hjhq.teamface.project.presenter.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CommonNewResultBean;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.ui.member.SelectRangeActivity;
import com.hjhq.teamface.common.ui.time.DateTimeSelectPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.select.PickListView;
import com.hjhq.teamface.customcomponent.widget2.select.TimeSelectView;
import com.hjhq.teamface.project.bean.ProjectLabelsListBean;
import com.hjhq.teamface.project.bean.ProjectMemberResultBean;
import com.hjhq.teamface.project.bean.SaveTaskLayoutRequestBean;
import com.hjhq.teamface.project.bean.SaveTaskResultBean;
import com.hjhq.teamface.project.bean.TaskLayoutResultBean;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.ui.task.AddTaskDelegate;
import com.hjhq.teamface.project.widget.utils.ProjectCustomUtil;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * 新建任务
 *
 * @author Administrator
 * @date 2018/4/10
 */
public class AddTaskActivity extends ActivityPresenter<AddTaskDelegate, TaskModel> {

    private String moduleBean;
    public long projectId;
    private ArrayList<ProjectLabelBean> projectLables;
    private List<EntryBean> entrys = new ArrayList<>();
    private List<Member> memberList = new ArrayList<Member>();
    private List<CustomBean> rows;
    private TaskLayoutResultBean.DataBean.EnableLayoutBean enableLayout;
    private TimeSelectView startTime;
    private TimeSelectView endTime;
    private Calendar startTimeCalendar = Calendar.getInstance();
    private Calendar endTimeCalendar = Calendar.getInstance();
    private int startTimeCode = -1;
    private int endTimeCode = -2;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            moduleBean = getIntent().getStringExtra(Constants.MODULE_BEAN);
            projectId = getIntent().getLongExtra(ProjectConstants.PROJECT_ID, 0);
        } else {
            moduleBean = savedInstanceState.getString(Constants.MODULE_BEAN);
            projectId = savedInstanceState.getLong(ProjectConstants.PROJECT_ID, 0);
        }
    }

    @Override
    public void init() {
        //个人任务
        if (ProjectConstants.PERSONAL_TASK_BEAN.equals(moduleBean)) {
            viewDelegate.hideCheckView();
        }
        viewDelegate.setDefaultCheckOne(new Member(TextUtil.parseLong(SPHelper.getEmployeeId()), SPHelper.getUserName(), C.EMPLOYEE));
        getTaskLayout();
        getProjectMember();
        startTimeCalendar.setTimeInMillis(System.currentTimeMillis());
        endTimeCalendar.setTimeInMillis(System.currentTimeMillis());
    }

    private void getProjectMember() {
        model.queryProjectMember(mContext, projectId, new ProgressSubscriber<ProjectMemberResultBean>(mContext) {
            @Override
            public void onNext(ProjectMemberResultBean projectMemberResultBean) {
                super.onNext(projectMemberResultBean);
                List<ProjectMemberResultBean.DataBean.DataListBean> data = projectMemberResultBean.getData().getDataList();
                memberList.clear();
                for (int i = 0; i < data.size(); i++) {
                    Member m = new Member();
                    m.setName(data.get(i).getEmployee_name());
                    m.setEmployee_name(data.get(i).getEmployee_name());
                    m.setId(data.get(i).getEmployee_id());
                    m.setPicture(data.get(i).getEmployee_pic());
                    m.setPost_name(data.get(i).getProject_role());
                    memberList.add(m);
                }
                setProjectMember();
            }
        });

    }

    /**
     * 设置项目成员
     */
    private void setProjectMember() {
        if (memberList.size() > 0 && rows != null && rows.size() > 0 && enableLayout != null) {
            for (CustomBean layoutBean : rows) {
                if ("personnel_principal".equals(layoutBean.getName()) && memberList.size() > 0) {
                    layoutBean.getField().setChooseRange(memberList);
                }
                layoutBean.setModuleBean(moduleBean);
            }
            viewDelegate.drawLayout(enableLayout, null,
                    CustomConstants.ADD_STATE, ProjectConstants.PERSONAL_TASK_BEAN.equals(moduleBean), moduleBean);
            fillLabels();
        }
    }

    /**
     * 获取任务布局与标签
     */
    private void getTaskLayout() {
        if (ProjectConstants.PERSONAL_TASK_BEAN.equals(moduleBean)) {
            //获取个人任务标签
            model.getAllLabel(this, null, 2, new ProgressSubscriber<ProjectLabelsListBean>(this) {
                @Override
                public void onNext(ProjectLabelsListBean projectLabelsListBean) {
                    super.onNext(projectLabelsListBean);
                    projectLables = projectLabelsListBean.getData();
                    fillLabels();
                }
            });
        } else {
            //获取项目任务标签
            model.getProjectLabel(this, projectId, 0, new ProgressSubscriber<ProjectLabelsListBean>(this) {
                @Override
                public void onNext(ProjectLabelsListBean projectLabelsListBean) {
                    super.onNext(projectLabelsListBean);
                    projectLables = projectLabelsListBean.getData();
                    fillLabels();
                }
            });
        }
        model.getTaskLayout(this, moduleBean, new ProgressSubscriber<TaskLayoutResultBean>(this) {
            @Override
            public void onNext(TaskLayoutResultBean taskLayoutResultBean) {
                super.onNext(taskLayoutResultBean);

                enableLayout = taskLayoutResultBean.getData().getEnableLayout();
                rows = enableLayout.getRows();
                if (CollectionUtils.isEmpty(rows)) {
                    return;
                } else {
                    viewDelegate.drawLayout(enableLayout, null, CustomConstants.ADD_STATE, true, moduleBean);
                    if (entrys != null && entrys.size() > 0) {
                        if (viewDelegate.listView != null && viewDelegate.listView.size() > 0) {
                            for (BaseView view : viewDelegate.listView) {
                                if (ProjectConstants.PROJECT_TASK_LABEL.equals(view.getKeyName())) {
                                    ((PickListView) view).setEntrys(entrys);
                                }
                            }
                        }
                    }
                    stupidTimeThings();
                }
                setProjectMember();
            }
        });
    }

    private void stupidTimeThings() {
        for (BaseView view : viewDelegate.listView) {
            if (view instanceof TimeSelectView) {
                int code = view.getCode();
                if ("datetime_starttime".equals(((TimeSelectView) view).getKeyName())) {
                    startTimeCode = code;
                    startTime = ((TimeSelectView) view);
                } else if ("datetime_deadline".equals(((TimeSelectView) view).getKeyName())) {
                    endTimeCode = code;
                    endTime = ((TimeSelectView) view);
                }
            }
        }
    }

    /**
     * 填充标签
     */
    private void fillLabels() {
        entrys.clear();
        if (projectLables != null && projectLables.size() > 0) {
            if (ProjectConstants.PERSONAL_TASK_BEAN.equals(moduleBean)) {
                Observable.from(projectLables)
                        .subscribe(lable -> entrys.add(new EntryBean(lable.getId(), lable.getName(), lable.getColour())));
            } else {
                Observable.from(projectLables)
                        .filter(lable -> !CollectionUtils.isEmpty(lable.getChildList()))
                        .flatMap(new Func1<ProjectLabelBean, Observable<ProjectLabelBean>>() {
                            @Override
                            public Observable<ProjectLabelBean> call(ProjectLabelBean projectLabelBean) {
                                return Observable.from(projectLabelBean.getChildList());
                            }
                        })
                        .subscribe(lable -> entrys.add(new EntryBean(lable.getId(), lable.getName(), lable.getColour())));
            }
        }
        if (viewDelegate.listView != null && viewDelegate.listView.size() > 0) {
            for (BaseView view : viewDelegate.listView) {
                if (ProjectConstants.PROJECT_TASK_LABEL.equals(view.getKeyName())) {
                    ((PickListView) view).setEntrys(entrys);
                }
            }
        }
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.sBtnTaskCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewDelegate.switchTaskCheck(isChecked);
        });
        //查重
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_REPEAT_CHECK_CODE, tag -> UIRouter.getInstance().openUri(mContext, "DDComp://custom/repeatCheck", (Bundle) tag));
        //关联列表
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_REFERENCE_TEMP_CODE, tag -> {
            SoftKeyboardUtils.hide(AddTaskActivity.this);
            MessageBean messageBean = (MessageBean) tag;
            UIRouter.getInstance().openUri(mContext, "DDComp://custom/referenceTemp", (Bundle) messageBean.getObject(), messageBean.getCode());
        });


        viewDelegate.memberView.setOnAddMemberClickedListener(() ->
                model.queryProjectMember(mContext, projectId, new ProgressSubscriber<ProjectMemberResultBean>(mContext) {
                    @Override
                    public void onNext(ProjectMemberResultBean baseBean) {
                        super.onNext(baseBean);
                        List<ProjectMemberResultBean.DataBean.DataListBean> data = baseBean.getData().getDataList();
                        ArrayList<Member> chooseRanger = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(data)) {
                            for (ProjectMemberResultBean.DataBean.DataListBean projectMember : data) {
                                chooseRanger.add(new Member(projectMember.getEmployee_id(), projectMember.getEmployee_name(), C.EMPLOYEE));
                            }
                        }

                        List<Member> members = viewDelegate.memberView.getMembers();
                        Bundle bundle = new Bundle();
                        bundle.putInt(C.CHECK_TYPE_TAG, C.RADIO_SELECT);
                        bundle.putSerializable(C.CHOOSE_RANGE_TAG, chooseRanger);
                        bundle.putSerializable(C.SELECTED_MEMBER_TAG, (Serializable) members);
                        //TODO 需要使用项目成员列表界面
                        CommonUtil.startActivtiyForResult(mContext, SelectRangeActivity.class, Constants.REQUEST_CODE1, bundle);
                    }
                }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SoftKeyboardUtils.hide(this);
        saveTaskLayoutData();
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存任务布局信息
     */
    private void saveTaskLayoutData() {
        SaveTaskLayoutRequestBean bean = new SaveTaskLayoutRequestBean();

        if (ProjectConstants.PERSONAL_TASK_BEAN.equals(moduleBean)) {
            bean.setBean(ProjectConstants.PERSONAL_TASK_BEAN);
        } else {
            if (viewDelegate.sBtnTaskCheck.isChecked() && CollectionUtils.isEmpty(viewDelegate.memberView.getMembers())) {
                ToastUtils.showToast(mContext, "校验人不能为空");
                return;
            }
            bean.setBean(ProjectConstants.PROJECT_TASK_MOBULE_BEAN + projectId);
        }

        JSONObject json = new JSONObject();
        boolean put = ProjectCustomUtil.put(mContext, viewDelegate.listView, json);
        if (!put) {
            return;
        }
        bean.setData(json);

        if (ProjectConstants.PERSONAL_TASK_BEAN.equals(moduleBean)) {
            json.put("participants_only", viewDelegate.getOnlyParticipantStatus());
            model.addPersonalTask(mContext, bean, new ProgressSubscriber<CommonNewResultBean>(mContext) {
                @Override
                public void onNext(CommonNewResultBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "新增成功");
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DATA_TAG1, baseBean.getData().getId());
                    setResult(RESULT_OK, intent);
                    finish();
                    EventBusUtils.sendEvent(new MessageBean(ProjectConstants.PERSONAL_TASK_REFRESH_CODE, null, null));
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });
        } else {
            model.saveTaskLayout(mContext, bean, new ProgressSubscriber<SaveTaskResultBean>(mContext) {
                @Override
                public void onNext(SaveTaskResultBean saveTaskResultBean) {
                    super.onNext(saveTaskResultBean);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DATA_TAG1, saveTaskResultBean.getData().getId());
                    intent.putExtra(Constants.DATA_TAG2, viewDelegate.getTaskCheckStatus());
                    intent.putExtra(Constants.DATA_TAG3, viewDelegate.getCheckOneId());
                    intent.putExtra(Constants.DATA_TAG4, viewDelegate.getOnlyParticipantStatus());
                    for (String key : json.keySet()) {
                        switch (key) {
                            case ProjectConstants.PROJECT_TASK_DEADLINE:
                                intent.putExtra(Constants.DATA_TAG5, json.getString(key));
                                break;
                            case ProjectConstants.PROJECT_TASK_EXECUTOR:
                                intent.putExtra(Constants.DATA_TAG6, json.getString(key));
                                break;
                            case ProjectConstants.PROJECT_TASK_STARTTIME:
                                intent.putExtra(Constants.DATA_TAG7, json.getString(key));
                                break;
                            case ProjectConstants.PROJECT_TASK_NAME:
                                intent.putExtra(Constants.DATA_TAG8, json.getString(key));
                            case ProjectConstants.PROJECT_TAG:
                                intent.putExtra(Constants.DATA_TAG9, json.getString(key));
                                break;
                        }
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                    EventBusUtils.sendEvent(new MessageBean(ProjectConstants.PROJECT_TASK_REFRESH_CODE, null, null));
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE1) {
            //检验人员
            List<Member> members = (List<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            viewDelegate.memberView.setMembers(members);
        }
        if (requestCode == startTimeCode) {
            //开始时间
            startTimeCalendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
            if (startTimeCalendar.getTimeInMillis() <= System.currentTimeMillis()) {
                ToastUtils.showToast(mContext, "开始时间不能早于当前时间");
                if (startTime != null) {
                    startTime.onActivityResult(startTimeCode, Constants.CLEAR_RESULT_CODE, null);
                }
            }
        }
        if (requestCode == endTimeCode) {
            //结束时间
            endTimeCalendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
            if (endTimeCalendar.getTimeInMillis() <= System.currentTimeMillis() || endTimeCalendar.getTimeInMillis() <= startTimeCalendar.getTimeInMillis()) {
                ToastUtils.showToast(mContext, "结束时间不能早于开始或当前时间");
                if (endTime != null) {
                    endTime.onActivityResult(endTimeCode, Constants.CLEAR_RESULT_CODE, null);
                }
            }
        }
    }
}

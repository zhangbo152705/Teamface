package com.hjhq.teamface.project.presenter.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.Photo;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.QueryTaskCompleteAuthResultBean;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.database.SocketMessage;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.network.utils.TransformerHelper;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CloneUtils;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.JsonParser;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.ScreenUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.adapter.CommentAdapter;
import com.hjhq.teamface.common.adapter.DynamicAdapter;
import com.hjhq.teamface.common.bean.CommentDetailResultBean;
import com.hjhq.teamface.common.bean.DynamicListResultBean;
import com.hjhq.teamface.common.ui.ImagePagerActivity;
import com.hjhq.teamface.common.ui.member.SelectRangeActivity;
import com.hjhq.teamface.common.ui.voice.VoicePlayActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.CommentInputView;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.select.PickListView;
import com.hjhq.teamface.customcomponent.widget2.select.PickListViewSelectActivity;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.TaskAllDynamicAdapter;
import com.hjhq.teamface.project.adapter.TaskStatusAdapter;
import com.hjhq.teamface.project.bean.ProjectLabelsListBean;
import com.hjhq.teamface.project.bean.ProjectMemberResultBean;
import com.hjhq.teamface.project.bean.QueryHierarchyResultBean;
import com.hjhq.teamface.project.bean.QueryManagerRoleResultBean;
import com.hjhq.teamface.project.bean.QueryTaskAuthResultBean;
import com.hjhq.teamface.project.bean.QueryTaskDetailResultBean;
import com.hjhq.teamface.project.bean.RelationListResultBean;
import com.hjhq.teamface.project.bean.SubListResultBean;
import com.hjhq.teamface.project.bean.SubTaskBean;
import com.hjhq.teamface.project.bean.TaskAllDynamicBean;
import com.hjhq.teamface.project.bean.TaskAllDynamicDetailBean;
import com.hjhq.teamface.project.bean.TaskLayoutResultBean;
import com.hjhq.teamface.project.bean.TaskLikeBean;
import com.hjhq.teamface.project.bean.TaskListResultBean;
import com.hjhq.teamface.project.bean.TaskMemberListResultBean;
import com.hjhq.teamface.project.bean.TaskStatusBean;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.presenter.ProjectDetailActivity;
import com.hjhq.teamface.project.presenter.RelationTaskCardListActivity;
import com.hjhq.teamface.project.presenter.SubTaskCardListActivity;
import com.hjhq.teamface.project.ui.task.TaskDetailDelegate;
import com.hjhq.teamface.project.util.ProjectUtil;
import com.hjhq.teamface.project.util.TaskUtil;
import com.hjhq.teamface.project.view.CommomDialog;
import com.hjhq.teamface.project.widget.utils.ProjectCustomUtil;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.iwf.photopicker.PhotoPicker;
import rx.Observable;
import rx.functions.Func1;

/**
 * 任务详情
 * @author Administrator
 * @date 2018/6/25
 */
@RouteNode(path = "/project_task_detail", desc = "项目任务详情")
public class TaskDetailActivity extends ActivityPresenter<TaskDetailDelegate, TaskModel> implements View.OnClickListener {
    public long projectId;
    private long taskId;
    private long mainTaskId;
    private String nodeId;
    private String dataId = "";
    /**
     * 任务自定义布局 的bean
     */
    private String moduleBean;
    /**
     * 任务类型 1 任务 2子任务
     */
    long taskType = 1;
    private String nodeCode;
    private CommentAdapter mCommentAdapter;
    private DynamicAdapter mDynamicAdapter;
    private TaskStatusAdapter mTaskStatusAdapter;
    private TaskAllDynamicAdapter mTaskAllDynamicAdapter;

    /**
     * 0：未完成 1：完成
     */
    private boolean completeStatus;
    private boolean projectIsRunning = false;
    private String projectStatus;
    /**
     * 检验状态  0 未检验 1检验通过  2检验驳回
     */
    private String passedStatus;
    /**
     * 点赞人
     */
    private List<TaskLikeBean> shareArr;
    /**
     * 节点id
     */
    private long subNodeId;

    private String taskName;
    private Map<String, Object> detailMap = new HashMap<>();
    private String associatesStatus;
    /**
     * 0：无需校验 1：需校验
     */
    private String checkStatus;
    private String checkMember;
    private Member executeMember;

    private String oldDeadline;

    List<SubTaskBean> subTaskArr;//子任务列表

    /**
     * 任务权限
     */
    private List<QueryTaskAuthResultBean.DataBean> taskAuthList;
    /**
     * 项目角色权限
     */
    private String priviledgeIds;
    /**
     * 任务权限角色
     */
    private List<String> taskRoles = new ArrayList<>();
    private boolean[] taskAuths;
    private String projectCompleteStatus;
    /**
     * 协作人是否可以修改
     */
    private boolean editAssociates;

    public boolean editTaskPesssiom = false;
    private List<DynamicListResultBean.DataBean.TimeListBean> dynamicDataList = new ArrayList<>();
    private List<CommentDetailResultBean.DataBean> commentDataList = new ArrayList<>();
    private List<TaskStatusBean> taskStatusList = new ArrayList<>();
    private List<TaskAllDynamicDetailBean> taskDynamicList = new ArrayList<>();
    private int allDynamicPageSize = 8;
    private int maxAllDynamicPageSize = 50;
    private int mdynamicType = 0;

    //校验人
    private String employee_name;
    private String employee_pic;
    private String employee_id;
    private Member checkMemberData;

    private QueryTaskDetailResultBean.DataBean taskData;
    private String updateDeadlineAuth;

    private boolean isAttachmentEdit = false;
    private CommomDialog editDialog;

    private int dynamicType;

    private ArrayList<ProjectLabelBean> projectLables;
    private List<EntryBean> lableEntrys = new ArrayList<>();

    private String statudState = "";//当前状态
    private List<EntryBean> statudStateList;
    private List<EntryBean> statusEntrys = new ArrayList<>();
    private boolean isSuspend = false;//是否暂停
    private String remark;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            projectId = intent.getLongExtra(ProjectConstants.PROJECT_ID, 0);
            taskId = intent.getLongExtra(ProjectConstants.TASK_ID, 0);
            nodeId = intent.getStringExtra(ProjectConstants.MAIN_TASK_NODE_ID);

            mainTaskId = intent.getLongExtra(ProjectConstants.MAIN_TASK_ID, 0);
            taskName = intent.getStringExtra(ProjectConstants.TASK_NAME);
            moduleBean = intent.getStringExtra(Constants.MODULE_BEAN);
            taskType = intent.getLongExtra(ProjectConstants.TASK_FROM_TYPE, 1);
            nodeCode = intent.getStringExtra(ProjectConstants.MAIN_TASK_NODECODE);
            if (taskId == 0) {
                dataId = mainTaskId + "";
            } else {
                dataId = taskId + "";
            }
        }
    }

    @Override
    public void init() {
        viewDelegate.setTaskTitle(taskName);
        viewDelegate.showMenu(0);
        mCommentAdapter = new CommentAdapter(commentDataList);
        viewDelegate.mRvComment.setAdapter(mCommentAdapter);
        mDynamicAdapter = new DynamicAdapter(dynamicDataList);
        viewDelegate.mRvDynamic.setAdapter(mDynamicAdapter);
        mTaskStatusAdapter = new TaskStatusAdapter(taskStatusList);
        viewDelegate.mRvState.setAdapter(mTaskStatusAdapter);
        if (taskType == 1){
            viewDelegate.mCommentInputView.setData(ProjectConstants.PROJECT_TASK_DYNAMIC_BEAN_NAME, dataId);
        }else if(taskType==2) {
            viewDelegate.mCommentInputView.setData(ProjectConstants.PROJECT_SUB_TASK_DYNAMIC_BEAN_NAME, dataId);
        }
        viewDelegate.get(R.id.tv_comment).setSelected(true);

        mTaskAllDynamicAdapter = new TaskAllDynamicAdapter(taskDynamicList);
        viewDelegate.trendRecyclerView.setAdapter(mTaskAllDynamicAdapter);
        if (taskType == 2) {
            viewDelegate.hideCheckView();
        }
        viewDelegate.hidePersionRelevance();
        initData();
    }

    private void initData() {
        //获取项目角色信息
        queryProjectRoleInfo();
        //根据项目ID查询权限
        queryTaskAuth();
        //获取任务所有角色的信息
        queryTaskRoles();
        //任务层级关系接口 只有主任务需要获取
        if (taskType == 1){
            queryByHierarchy();
        }
        //详情
        queryTaskDetail();
        //获取任务 协助人角色 的信息（子任务或者任务）
        queryTaskMemberList();
        //被关联任务
        if (taskType == 1) {
            queryByRelationList();
        } else {
            viewDelegate.hideBeRelevance();
        }
        //关联任务
        queryRelationList();
        //点赞列表
        queryShareList();
        //所有动态
        getAllDynamic((int) taskType, 0, allDynamicPageSize);
        //截止时间权限
        queryEditDeadlineAuth();
        //标签列表
        getTaskLable();

    }

    /**
     * 获取任务权限
     */
    private void queryTaskAuth() {
        model.queryTaskAuthList(this, projectId, new ProgressSubscriber<QueryTaskAuthResultBean>(this) {
            @Override
            public void onNext(QueryTaskAuthResultBean taskAuthResultBean) {
                super.onNext(taskAuthResultBean);
                taskAuthList = taskAuthResultBean.getData();
                permissionHandle();
            }

            @Override
            public void onError(Throwable e) {
                dismissWindowView();
                e.printStackTrace();
                ToastUtils.showError(mContext, "获取任务权限失败");
            }
        });
    }

    /**
     * 获取全部角色
     */
    private void queryTaskRoles() {
        model.queryTaskMemberList(this, projectId, taskId, taskType, 0, new ProgressSubscriber<TaskMemberListResultBean>(this) {
            @Override
            public void onNext(TaskMemberListResultBean taskMemberListResultBean) {
                super.onNext(taskMemberListResultBean);
                TaskMemberListResultBean.DataBean data = taskMemberListResultBean.getData();
                taskRoles.clear();
                if (!CollectionUtils.isEmpty(data.getDataList())) {
                    Observable.from(data.getDataList()).subscribe(dataBean -> taskRoles.add(dataBean.getProject_task_role()));
                }
                permissionHandle();
            }
        });
    }

    /**
     * 获取项目角色信息
     */
    public void queryProjectRoleInfo() {
        //查询管理员权限
        new TaskModel().queryManagementRoleInfo(this, TextUtil.parseLong(SPHelper.getEmployeeId()), projectId, new ProgressSubscriber<QueryManagerRoleResultBean>(this) {
            @Override
            public void onNext(QueryManagerRoleResultBean queryManagerRoleResultBean) {
                super.onNext(queryManagerRoleResultBean);
                QueryManagerRoleResultBean.DataBean.PriviledgeBean priviledge = queryManagerRoleResultBean.getData().getPriviledge();
                priviledgeIds = priviledge.getPriviledge_ids();
            }

            @Override
            public void onError(Throwable e) {
                dismissWindowView();
                e.printStackTrace();
                ToastUtils.showError(mContext, "获取项目角色权限失败");
            }
        });
    }

    /**
     * 获取任务布局与标签
     */
    private void getTaskLable() {
        model.getProjectLabel(this, projectId, 0, new ProgressSubscriber<ProjectLabelsListBean>(this) {
            @Override
            public void onNext(ProjectLabelsListBean projectLabelsListBean) {
                super.onNext(projectLabelsListBean);
                projectLables = projectLabelsListBean.getData();
                fillLabels();
            }
        });
    }


    /**
     * 填充标签
     */
    private void fillLabels() {
        lableEntrys.clear();
        if (projectLables != null && projectLables.size() > 0) {
            Observable.from(projectLables)
                    .filter(lable -> !CollectionUtils.isEmpty(lable.getChildList()))
                    .flatMap(new Func1<ProjectLabelBean, Observable<ProjectLabelBean>>() {
                        @Override
                        public Observable<ProjectLabelBean> call(ProjectLabelBean projectLabelBean) {
                            return Observable.from(projectLabelBean.getChildList());
                        }
                    })
                    .subscribe(lable -> lableEntrys.add(new EntryBean(lable.getId(), lable.getName(), lable.getColour())));

        }
        if (viewDelegate.listView != null && viewDelegate.listView.size() > 0) {
            for (BaseView view : viewDelegate.listView) {
                if (ProjectConstants.PROJECT_TASK_LABEL.equals(view.getKeyName())) {
                    ((PickListView) view).setEntrys(lableEntrys);
                }
            }
        }
    }

    /**
     * 任务层级关系接口
     */
    private void queryByHierarchy() {
        model.queryByHierarchy(this, taskId, new ProgressSubscriber<QueryHierarchyResultBean>(this) {
            @Override
            public void onNext(QueryHierarchyResultBean taskLayoutResultBean) {
                super.onNext(taskLayoutResultBean);
                QueryHierarchyResultBean.DataBean hierarchyData = taskLayoutResultBean.getData();
                subNodeId = hierarchyData.getSubnodeid2() == 0 ? hierarchyData.getSubnodeid() : hierarchyData.getSubnodeid2();
                viewDelegate.setNavigatorLayout(hierarchyData);
            }
        });
    }

    /**
     * 获取协作人列表信息
     */
    private void queryTaskMemberList() {
        model.queryTaskMemberList(this, projectId, taskId, taskType, 1, new ProgressSubscriber<TaskMemberListResultBean>(this) {
            @Override
            public void onNext(TaskMemberListResultBean taskMemberListResultBean) {
                super.onNext(taskMemberListResultBean);
                TaskMemberListResultBean.DataBean data = taskMemberListResultBean.getData();
                List<Member> members = new ArrayList<>();
                if (!CollectionUtils.isEmpty(data.getDataList())) {
                    for (TaskMemberListResultBean.DataBean.DataListBean dataBean : data.getDataList()) {
                        Member member = new Member(dataBean.getEmployee_id(), dataBean.getEmployee_name(), C.EMPLOYEE);
                        member.setSign_id(dataBean.getId());
                        member.setPicture(dataBean.getEmployee_pic());
                        members.add(member);
                    }
                }
                handleAssociates(members);

            }
        });
    }

    /**
     * 处理协作人数据
     *
     * @param members
     */
    public void handleAssociates(List<Member> members) {
        Observable.just(1)
                .filter(o -> !CollectionUtils.isEmpty(members))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(i -> {
                    viewDelegate.setAssociates(members);
                });
    }


    /**
     * 获取子任务
     */
    private void querySubList() {
        if (TextUtil.isEmpty(nodeCode)) {
            nodeCode = "";
        }
        model.querySubList(this, taskId, nodeCode, new ProgressSubscriber<SubListResultBean>(this, false) {
            @Override
            public void onNext(SubListResultBean baseBean) {
                super.onNext(baseBean);
                //子任务
                subTaskArr = baseBean.getData().getDataList();
                setSubTaskHead();
            }
        });
    }

    /**
     * 获取被关联列表
     */
    private void queryByRelationList() {
        model.queryByRelationList(this, taskId, new ProgressSubscriber<RelationListResultBean>(mContext) {
            @Override
            public void onNext(RelationListResultBean baseBean) {
                super.onNext(baseBean);
                //被关联
                List<RelationListResultBean.DataBean.ModuleDataBean> relationArr2 = baseBean.getData().getDataList();
                viewDelegate.setBeRelationHead(relationArr2.size());
            }
        });
    }

    /**
     * 获取关联任务
     */
    private void queryRelationList() {
        model.queryRelationList(this, taskId, taskType, new ProgressSubscriber<RelationListResultBean>(mContext) {
            @Override
            public void onNext(RelationListResultBean baseBean) {
                super.onNext(baseBean);
                //关联任务
                List<RelationListResultBean.DataBean.ModuleDataBean> relationArr = baseBean.getData().getDataList();
                viewDelegate.setRelationHead(relationArr.size());
            }
        });
    }

    /**
     * 点赞列表
     */
    private void queryShareList() {
        model.praiseQueryList(this, taskId, taskType, new ProgressSubscriber<TaskListResultBean>(mContext, false) {
            @Override
            public void onNext(TaskListResultBean baseBean) {
                super.onNext(baseBean);
                shareArr = baseBean.getData().getDataList();
                viewDelegate.setLikeNum(CollectionUtils.size(shareArr));

                if (!CollectionUtils.isEmpty(shareArr)) {
                    String employeeId = SPHelper.getEmployeeId();
                    Observable.from(shareArr)
                            .filter(share -> employeeId.equals(share.getId()))
                            .subscribe(share -> viewDelegate.setLikeStatus(true));
                }
            }
        });
    }

    /**
     * 查询任务详情
     */
    private void queryTaskDetail() {
        if (taskType == 1) {
            model.queryTaskDetail(this, taskId, moduleBean, (queryTaskDetailResultBean, taskLayoutResultBean) -> {
                handleTaskDetail(queryTaskDetailResultBean, taskLayoutResultBean);
                return taskLayoutResultBean;
            }, new ProgressSubscriber<TaskLayoutResultBean>(this) {
                @Override
                public void onNext(TaskLayoutResultBean taskLayoutResultBean) {
                    super.onNext(taskLayoutResultBean);
                    viewDelegate.drawLayout(taskLayoutResultBean.getData().getEnableLayout(), moduleBean, false, completeStatus, projectStatus);
                }
            });
        } else if (taskType == 2) {
            model.querySubTaskDetail(this, taskId, moduleBean, (queryTaskDetailResultBean, taskLayoutResultBean) -> {
                handleTaskDetail(queryTaskDetailResultBean, taskLayoutResultBean);
                return taskLayoutResultBean;
            }, new ProgressSubscriber<TaskLayoutResultBean>(this) {
                @Override
                public void onNext(TaskLayoutResultBean taskLayoutResultBean) {
                    super.onNext(taskLayoutResultBean);
                    viewDelegate.drawLayout(taskLayoutResultBean.getData().getEnableLayout(), moduleBean, false, completeStatus, projectStatus);
                }
            });
        }
    }

    /**
     * 获取所有状态
     */

    public void getAllDynamic(int taskType, int dynamicType, int pageSize) {
        model.queryTaskDynamicList(mContext, taskId, taskType, dynamicType, pageSize, new ProgressSubscriber<TaskAllDynamicBean>(mContext, true) {
            @Override
            public void onNext(TaskAllDynamicBean detailResultBean) {
                super.onNext(detailResultBean);
                List<TaskAllDynamicDetailBean> data = detailResultBean.getData().getDataList();
                CollectionUtils.notifyDataSetChanged(mTaskAllDynamicAdapter, mTaskAllDynamicAdapter.getData(), data);
                if (data.size() < 5) {
                    viewDelegate.hideMoreDynamicData();
                } else {
                    viewDelegate.showMoreDynamicData();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 评论时滚动到底部
     */
    private void scrollToBottom() {
        viewDelegate.getRootView().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*final int h1 = viewDelegate.get(R.id.sv_root).getBottom();
                viewDelegate.get(R.id.sv_root).scrollTo(0, h1);
                ScrollView s = new ScrollView(mContext);*/
                viewDelegate.mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                viewDelegate.mCommentInputView.getInputView().requestFocus();
            }
        }, 100);
    }

    /**
     * 处理任务详情
     *
     * @param queryTaskDetailResultBean
     * @param taskLayoutResultBean
     */
    private void handleTaskDetail(QueryTaskDetailResultBean queryTaskDetailResultBean, TaskLayoutResultBean taskLayoutResultBean) {
        if (TextUtil.isEmpty(nodeCode)) {
            if (!TextUtil.isEmpty(queryTaskDetailResultBean.getData().getNode_code())) {
                nodeCode = queryTaskDetailResultBean.getData().getNode_code();
            }
        }
        nodeId = queryTaskDetailResultBean.getData().getNode_id();
        //子任务
        querySubList();
        projectCompleteStatus = queryTaskDetailResultBean.getData().getComplete_status();
        projectIsRunning = "0".equals(queryTaskDetailResultBean.getData().getProject_status());
        projectStatus = queryTaskDetailResultBean.getData().getProject_status();
        ProjectUtil.INSTANCE.checkProjectStatus(mContext, projectStatus, () -> {
            viewDelegate.mFlComment.post(() -> {
                viewDelegate.get(R.id.fl_comment).setVisibility(View.VISIBLE);
            });
        });
        TaskLayoutResultBean.DataBean.EnableLayoutBean enableLayout = taskLayoutResultBean.getData().getEnableLayout();
        List<CustomBean> rows = enableLayout.getRows();
        taskData = queryTaskDetailResultBean.getData();//zzh->ad taskData改为全局变量
        try {
            final Object execution = taskData.getCustomArr().get("personnel_principal");
            String jsonArr = JSONObject.toJSONString(execution);
            JSONArray ja = JSONObject.parseArray(jsonArr);
            if (ja != null && ja.size() > 0) {
                JSONObject object = ja.getJSONObject(0);
                executeMember = new Member();
                executeMember.setName(object.getString("name"));
                executeMember.setId(object.getLong("id"));
                executeMember.setPicture(object.getString("picture"));
                executeMember.setEmployee_name(object.getString("name"));
                //zzh->ad:原taskData.getCustomArr().get("datetime_deadline")直接强转long 报错 改为Long.parseLong
                if (taskData != null && taskData.getCustomArr() != null && taskData.getCustomArr().get("datetime_deadline") != null) {
                    String dateTime = taskData.getCustomArr().get("datetime_deadline").toString();
                    if (!TextUtil.isEmpty(dateTime) && TextUtil.isInteger(dateTime)) {
                        executeMember.setUpdateTime(Long.parseLong(dateTime));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Observable.just(1)
                .filter(o -> taskData != null)
                .compose(TransformerHelper.applySchedulers())
                .subscribe(i ->
                        setDetail(taskData));


        detailMap = taskData.getCustomArr();
        if (!CollectionUtils.isEmpty(rows)) {
            for (CustomBean customBean : rows) {
                Object value = detailMap.get(customBean.getName());
                if (ProjectConstants.PROJECT_TASK_LABEL.equals(customBean.getName())) {
                    List<Map> list = new ArrayList<>();
                    //List<EntryBean> entrys = new ArrayList<>();//zzh->ad:增加entrys赋值
                    List<ProjectLabelBean> projectLabelBeen = new JsonParser<ProjectLabelBean>().jsonFromList(value, ProjectLabelBean.class);
                    Observable.from(projectLabelBeen).subscribe(label -> {
                        Map<String, Object> map = new HashMap<>();
                        EntryBean entry = new EntryBean();
                        map.put("value", label.getId());
                        map.put("label", label.getName());
                        map.put("color", label.getColour());
                        entry.setValue(label.getId());
                        entry.setLabel(label.getName());
                        entry.setColor(label.getColour());
                        list.add(map);
                        //entrys.add(lableEntrys);
                    });
                    detailMap.put(ProjectConstants.PROJECT_TASK_LABEL, list);
                    customBean.setValue(list);
                    customBean.setEntrys(lableEntrys);
                } else {
                    customBean.setValue(value);
                }

                //zzh:判断 标签 是否隐藏
                if (ProjectConstants.PROJECT_TASK_LABEL.equals(customBean.getName())) {
                    if (customBean.getField().getDetailView() != null){
                        boolean falg;
                        if (customBean.getField().getDetailView().equals("0")){
                            falg = true;
                        }else {
                            falg = false;
                        }
                        Observable.just(1)
                                .filter(o -> falg)
                                .compose(TransformerHelper.applySchedulers())
                                .subscribe(i -> {
                                    viewDelegate.hideLayout(viewDelegate.lable_rl);
                                });
                    }
                }
                //zzh:判断优先级 是否隐藏
                if (ProjectConstants.PROJECT_TASK_PRIORITY.equals(customBean.getName())) {
                    if (customBean.getField().getDetailView() != null){
                        boolean falg;
                        if (customBean.getField().getDetailView().equals("0")){
                            falg = true;
                        }else {
                            falg = false;
                        }
                        Observable.just(1)
                                .filter(o -> falg)
                                .compose(TransformerHelper.applySchedulers())
                                .subscribe(i -> {
                                    viewDelegate.hideLayout(viewDelegate.priority_rl);
                                });
                    }

                }
            }
        }

        //状态
        Object status = detailMap.get(ProjectConstants.PROJECT_TASK_STATUS);
        if (status != null) {
            List<EntryBean> statusBeen = new JsonParser<EntryBean>().jsonFromList(status, EntryBean.class);
            Observable.just(1)
                    .filter(o -> !CollectionUtils.isEmpty(statusBeen))
                    .compose(TransformerHelper.applySchedulers())
                    .subscribe(i -> {
                        statudState = statusBeen.get(0).getLabel();
                        if (!TextUtil.isEmpty(statudState) && mContext.getResources().getString(R.string.project_suspended).indexOf(statudState) != -1) {
                            isSuspend = true;
                            viewDelegate.setIsSuspend(isSuspend);
                        }
                        statudStateList = statusBeen;
                        viewDelegate.setPickStatus(statusBeen);
                    });
        }
    }

    /**
     * 设置子任务数量
     */
    public void setSubTaskHead() {
        final int[] completeSize = {0};
        int total = 0;
        if (!CollectionUtils.isEmpty(subTaskArr)) {
            total = subTaskArr.size();
            Observable.from(subTaskArr)
                    .filter(bean -> "1".equals(bean.getComplete_status()))
                    .subscribe(bean -> completeSize[0] += 1);
        }
        viewDelegate.setSubTaskHead(total, completeSize[0]);
    }


    /**
     * 设置任务详情数据
     *
     * @param taskData
     */
    private void setDetail(QueryTaskDetailResultBean.DataBean taskData) {

        //子任务设置导航栏
        if (taskType == 2){
            String parrentTaskName = taskData.getParent_task_name();
            viewDelegate.setSubNavigatorLayout(parrentTaskName);
        }

        List<Member> members = null;
        if (executeMember != null && !TextUtil.isEmpty(executeMember.getName())) {
            members = new ArrayList<>();
            members.add(executeMember);
        }
        viewDelegate.setExecutor(members);

        //zzh->ad: 校验人
        if (taskType != 2) {
            employee_name = taskData.getEmployee_name();
            employee_pic = taskData.getEmployee_pic();
            employee_id = taskData.getCheck_member();
            checkMemberData = new Member();
            if (!TextUtil.isEmpty(employee_name)) {
                checkMemberData.setPicture(employee_pic);
                checkMemberData.setName(employee_name);
                checkMemberData.setEmployee_name(employee_name);
                if (!TextUtil.isEmpty(employee_id)) {
                    checkMemberData.setId(Long.parseLong(employee_id));
                } else {
                    checkMemberData.setId(0);
                }
                viewDelegate.setDefaultCheckOne(checkMemberData);
            }

        }


        //协作人是否可见
        associatesStatus = taskData.getAssociates_status();
        viewDelegate.setCheckedImmediatelyNoEvent("1".equals(taskData.getAssociates_status()));
        completeStatus = "1".equals(taskData.getComplete_status());
        viewDelegate.setTaskStatus(completeStatus);
        checkStatus = taskData.getCheck_status();
        passedStatus = taskData.getPassed_status();
        checkMember = taskData.getCheck_member();
        viewDelegate.setCheckStatus(ProjectConstants.CHECK_STATUS_NULL);
        if ("1".equals(checkStatus)) {
            //需要检验
            if (completeStatus) {
                //已完成
                if (ProjectConstants.CHECK_STATUS_WAIT.equals(passedStatus) && completeStatus
                        && SPHelper.getEmployeeId().equals(taskData.getCheck_member())) {
                    viewDelegate.setCheckBtnVis(View.VISIBLE);
                }
                viewDelegate.setCheckStatus(passedStatus);
            } else {
                if (ProjectConstants.CHECK_STATUS_REJECT.equals(passedStatus)) {
                    //已驳回
                    viewDelegate.setCheckStatus(ProjectConstants.CHECK_STATUS_REJECT);
                }

            }
            if (taskType != 2) {
                viewDelegate.openTaskStatus();//zzh->ad 新增 不需要校验隐藏校验控件
            }
        } else {
            //不需要检验
            viewDelegate.setCheckStatus(ProjectConstants.CHECK_STATUS_NULL);
            viewDelegate.closeTaskStatus();//zzh->ad 新增 不需要校验隐藏校验控件
        }

        //检验按钮编辑权限控制
        if (!getEditAuth()) {
            viewDelegate.hideCheckBtnView();
        }

    }

    /**
     * 是否可以编辑
     *
     * @return
     */
    public boolean getEditAuth() {
        boolean flag = false;
        if (editTaskPesssiom && !completeStatus && !isSuspend && !projectStatus.equals("1") && !projectStatus.equals("2")) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 选择检验人
     */
    public void selectStateMenberView() {
        if (getEditAuth()) {
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

                    List<Member> members = viewDelegate.state_member_view.getMembers();
                    Bundle bundle = new Bundle();
                    bundle.putInt(C.CHECK_TYPE_TAG, C.RADIO_SELECT);
                    bundle.putSerializable(C.CHOOSE_RANGE_TAG, chooseRanger);
                    bundle.putSerializable(C.SELECTED_MEMBER_TAG, (Serializable) members);
                    //TODO 需要使用项目成员列表界面
                    CommonUtil.startActivtiyForResult(mContext, SelectRangeActivity.class, Constants.REQUEST_CODE16, bundle);
                }
            });
        }
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.ll_associates_control, R.id.ll_sub_task_control, R.id.ll_relevance_control
                , R.id.ll_be_relevance_control, R.id.tv_comment, R.id.tv_dynamic, R.id.tv_look_status, R.id.iv_like
                , R.id.tv_add_relevance, R.id.iv_task_status, R.id.ll_check, R.id.ll_add_sub_task, R.id.nomals_edit, R.id.picklist_status_li);

        //zzh->ad:检验人
        viewDelegate.sBtnTaskCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (taskType != 2) {
                viewDelegate.switchTaskCheck(isChecked);
            }
        });
        viewDelegate.sBtnTaskCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewDelegate.getTaskCheckStatus().equals("1") && TextUtil.isEmpty(viewDelegate.getCheckOneId())) {//判断是否已有检验人
                    selectStateMenberView();
                } else {
                    saveTaskLayoutData(null);
                }
            }
        });
        //zzh->ad:检验人
        viewDelegate.state_member_view.setOnAddMemberClickedListener(() -> {
            selectStateMenberView();
        });

        //zzh->ad:执行人
        viewDelegate.executor_member_view.setOnAddMemberClickedListener(() -> {
                    if (getEditAuth()) {
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

                                List<Member> members = viewDelegate.executor_member_view.getMembers();
                                Bundle bundle = new Bundle();
                                bundle.putInt(C.CHECK_TYPE_TAG, C.RADIO_SELECT);
                                bundle.putSerializable(C.CHOOSE_RANGE_TAG, chooseRanger);
                                bundle.putSerializable(C.SELECTED_MEMBER_TAG, (Serializable) members);
                                //TODO 需要使用项目成员列表界面
                                CommonUtil.startActivtiyForResult(mContext, SelectRangeActivity.class, Constants.REQUEST_CODE18, bundle);
                            }
                        });
                    }
                }
        );

        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_DATA_DETAIL_CODE, tag -> UIRouter.getInstance().openUri(mContext, "DDComp://custom/detail", (Bundle) tag));
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_FILE_DETAIL_CODE, tag -> UIRouter.getInstance().openUri(mContext, "DDComp://custom/file", (Bundle) tag));
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_FILE_DETAIL_DYNAMIC_CODE, tag -> {
                    mdynamicType = (int) tag;
                    getAllDynamic((int) taskType, mdynamicType, allDynamicPageSize);
                }

        );


        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_FILE_DETAIL_ATTACH_CODE, tag -> {//zzh->ad:附件编辑
            if (isAttachmentEdit) {
                saveTaskLayoutData(null);
            }

        });
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_FILE_DETAIL_DELETE_ATTACH_CODE, tag -> {//zzh->ad:删除附件
            if (getEditAuth()) {
                saveTaskLayoutData(null);
            }
        });

        /**
         * 清空标签操作
         */
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_TASK_DETAIL_CLEAR_LABLE_CODE, o -> {
            saveTaskLayoutData(null);
        });

        viewDelegate.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.DATA_TAG1, (Serializable) shareArr);
            CommonUtil.startActivtiy(mContext, TaskThumbUpActivity.class, bundle);
        }, R.id.tv_like_num);
        viewDelegate.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
            CommonUtil.startActivtiy(mContext, ProjectDetailActivity.class, bundle);
        }, R.id.tv_sub_node_name, R.id.tv_node_name, R.id.tv_project_name);

        /**
         * 协作人
         */
        viewDelegate.membersView.setOnAddMemberClickedListener(() -> {
            if (!editAssociates) {
                ToastUtils.showError(mContext, "没有权限！");
                return;
            }
            List<Member> members = viewDelegate.membersView.getMembers();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.DATA_TAG1, (Serializable) members);
            bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
            bundle.putLong(ProjectConstants.TASK_ID, taskId);
            bundle.putLong(ProjectConstants.TASK_FROM_TYPE, taskType);
            if (taskType == 2) {
                bundle.putLong(ProjectConstants.MAIN_TASK_ID, mainTaskId);
            }

            CommonUtil.startActivtiyForResult(mContext, TaskMemberActivity.class, Constants.REQUEST_CODE1, bundle);

        });

        //协作人可见
        viewDelegate.sbtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            model.updateTaskAssociatesState(mContext, taskId, isChecked ? 1 : 0, taskType, new ProgressSubscriber<BaseBean>(mContext) {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    viewDelegate.setCheckedImmediatelyNoEvent(!isChecked);
                }
            });
        });

        viewDelegate.mRvComment.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                CommentDetailResultBean.DataBean bean = (CommentDetailResultBean.DataBean) adapter.getItem(position);
                if (CollectionUtils.isEmpty(bean.getInformation())) {
                    return;
                }
                UploadFileBean uploadFileBean = bean.getInformation().get(0);
                if (bean.getItemType() == 2) {
                    //语音
                    bundle.putString(Constants.DATA_TAG1, uploadFileBean.getFile_url());
                    bundle.putInt(Constants.DATA_TAG2, uploadFileBean.getVoiceTime());
                    CommonUtil.startActivtiy(mContext, VoicePlayActivity.class, bundle);
                } else if (bean.getItemType() == 3) {
                    //文件
                    SocketMessage socketMessage = new SocketMessage();
                    socketMessage.setMsgID(bean.getId());
                    socketMessage.setFileName(uploadFileBean.getFile_name());
                    socketMessage.setFileUrl(uploadFileBean.getFile_url());
                    socketMessage.setFileSize(TextUtil.parseInt(uploadFileBean.getFile_size()));
                    socketMessage.setFileType(uploadFileBean.getFile_type());
                    socketMessage.setSenderName(bean.getEmployee_name());
                    socketMessage.setServerTimes(TextUtil.parseLong(bean.getDatetime_time()));
                    bundle.putSerializable(MsgConstant.MSG_DATA, socketMessage);
                    UIRouter.getInstance().openUri(mContext, "DDComp://filelib/file_detail", bundle);
                }
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                CommentDetailResultBean.DataBean bean = (CommentDetailResultBean.DataBean) adapter.getItem(position);
                UploadFileBean uploadFileBean = bean.getInformation().get(0);
                ArrayList<Photo> list = Photo.toPhotoList(uploadFileBean);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImagePagerActivity.PICTURE_LIST, list);
                bundle.putInt(ImagePagerActivity.SELECT_INDEX, 0);
                CommonUtil.startActivtiy(mContext, ImagePagerActivity.class, bundle);
            }
        });

        /***
         * 评论成功回调
         */
        viewDelegate.mCommentInputView.setOnChangeListener(new CommentInputView.OnChangeListener() {
            @Override
            public void onSend(int state) {
                scrollToBottom();
            }

            @Override
            public void onLoad(int state) {

            }

            @Override
            public void onSuccess(CommentDetailResultBean.DataBean bean) {
                getAllDynamic((int) taskType, mdynamicType, allDynamicPageSize);
                scrollToBottom();
            }
        });


        viewDelegate.tvTaskName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getEditAuth()) {
                    showEditDialog();
                }
            }
        });

        viewDelegate.dynamic_more_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDelegate.dynamic_more_data.setVisibility(View.GONE);
                allDynamicPageSize = maxAllDynamicPageSize;
                getAllDynamic((int) taskType, dynamicType, allDynamicPageSize);
            }
        });

    }

    /**
     * 编辑弹窗
     *
     * @param
     */
    public void showEditDialog() {
        editDialog = new CommomDialog(mContext, 0, taskName, 0, new CommomDialog.OnCloseListener() {

            @Override
            public void onClick(CommomDialog dialog, boolean confirm, int type) {
                if (confirm) {
                    String editName = dialog.contentTxt.getText().toString();
                    if (TextUtil.isEmpty(editName)) {

                        ToastUtils.showToast(mContext, getResources().getString(R.string.project_selete_edit_node));
                        return;
                    } else {
                        viewDelegate.tvTaskName.setText(editName);
                        saveTaskLayoutData(null);
                    }
                    dialog.dismiss();
                }

            }
        });
        if (editDialog != null && !editDialog.isShowing()) {
            editDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Bundle bundle = new Bundle();
        if (id == R.id.ll_associates_control) {
            //协作人显示切换
            viewDelegate.switchAssociates();
        } else if (id == R.id.ll_sub_task_control) {
            //显示切换子任务
            //subTaskClick();
            if (getEditAuth()) {
                Bundle subBundle = new Bundle();
                subBundle.putLong(ProjectConstants.TASK_FROM_TYPE, 1);
                subBundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                subBundle.putLong(ProjectConstants.TASK_ID, taskId);
                subBundle.putString(ProjectConstants.MAIN_TASK_NODECODE, nodeCode);
                subBundle.putString(ProjectConstants.PROJECT_STATUS, projectStatus);
                subBundle.putLong(ProjectConstants.PARRENT_TASK_FROM_TYPE, taskType);

                CommonUtil.startActivtiy(mContext, SubTaskCardListActivity.class, subBundle);
            }
        } else if (id == R.id.ll_relevance_control) {
            //显示切换关联
            //relevanceClick();
            Bundle relationBundle = new Bundle();
            relationBundle.putLong(ProjectConstants.PROJECT_RLATION_TYPE, 1);
            relationBundle.putLong(ProjectConstants.TASK_ID, taskId);
            relationBundle.putLong(ProjectConstants.TASK_FROM_TYPE, taskType);
            relationBundle.putLong(ProjectConstants.PROJECT_ID, projectId);
            relationBundle.putString(ProjectConstants.TASK_NAME, taskName);
            relationBundle.putLong(ProjectConstants.SUBNODE_ID, subNodeId);
            CommonUtil.startActivtiy(mContext, RelationTaskCardListActivity.class, relationBundle);
        } else if (id == R.id.ll_be_relevance_control) {
            //显示切换被关联
            // beRelevanceClick();
            Bundle beRelationBundle = new Bundle();
            beRelationBundle.putLong(ProjectConstants.PROJECT_RLATION_TYPE, 2);
            beRelationBundle.putLong(ProjectConstants.TASK_ID, taskId);
            beRelationBundle.putLong(ProjectConstants.TASK_FROM_TYPE, taskType);
            beRelationBundle.putLong(ProjectConstants.PROJECT_ID, projectId);
            beRelationBundle.putString(ProjectConstants.TASK_NAME, taskName);
            beRelationBundle.putLong(ProjectConstants.SUBNODE_ID, subNodeId);
            beRelationBundle.putBoolean(ProjectConstants.PROJECT_IS_BE_RLATION_TYPE, true);
            CommonUtil.startActivtiy(mContext, RelationTaskCardListActivity.class, beRelationBundle);
        } else if (id == R.id.tv_comment) {
            //评论
            /*bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PROJECT_TASK_DYNAMIC_BEAN_NAME);
            bundle.putString(Constants.DATA_ID, taskId + "");
            CommonUtil.startActivtiy(this, CommentActivity.class, bundle);*/
            viewDelegate.get(R.id.tv_comment).setSelected(true);
            viewDelegate.get(R.id.tv_dynamic).setSelected(false);
            viewDelegate.get(R.id.tv_look_status).setSelected(false);
            viewDelegate.mRvComment.setVisibility(View.VISIBLE);
            viewDelegate.mRvState.setVisibility(View.GONE);
            viewDelegate.mRvDynamic.setVisibility(View.GONE);
            ProjectUtil.INSTANCE.checkProjectStatus(mContext, projectStatus, () -> {
                viewDelegate.mFlComment.post(() -> {
                    viewDelegate.get(R.id.fl_comment).setVisibility(View.VISIBLE);
                });
            });

        } else if (id == R.id.tv_dynamic) {
            //动态
            SoftKeyboardUtils.hide(viewDelegate.mCommentInputView.getInputView());
            viewDelegate.get(R.id.tv_comment).setSelected(false);
            viewDelegate.get(R.id.tv_dynamic).setSelected(true);
            viewDelegate.get(R.id.tv_look_status).setSelected(false);
            viewDelegate.mRvComment.setVisibility(View.GONE);
            viewDelegate.mRvState.setVisibility(View.GONE);
            viewDelegate.mRvDynamic.setVisibility(View.VISIBLE);
            viewDelegate.get(R.id.fl_comment).setVisibility(View.GONE);
           /* bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PROJECT_TASK_DYNAMIC_BEAN_NAME);
            bundle.putString(Constants.DATA_ID, taskId + "");
            CommonUtil.startActivtiy(this, DynamicActivity.class, bundle);*/
        } else if (id == R.id.tv_look_status) {
            //查看状态
            SoftKeyboardUtils.hide(viewDelegate.mCommentInputView.getInputView());
            viewDelegate.get(R.id.fl_comment).setVisibility(View.GONE);
            viewDelegate.get(R.id.tv_comment).setSelected(false);
            viewDelegate.get(R.id.tv_dynamic).setSelected(false);
            viewDelegate.get(R.id.tv_look_status).setSelected(true);
            viewDelegate.mRvComment.setVisibility(View.GONE);
            viewDelegate.mRvState.setVisibility(View.VISIBLE);
            viewDelegate.mRvDynamic.setVisibility(View.GONE);
            /*bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
            bundle.putLong(ProjectConstants.TASK_ID, taskId);
            bundle.putLong(ProjectConstants.TASK_FROM_TYPE, taskType);
            CommonUtil.startActivtiy(mContext, TaskStatusActivity.class, bundle);*/
        } else if (id == R.id.iv_like) {
            //点赞
            ProjectUtil.INSTANCE.checkProjectStatus(mContext, projectStatus, () -> {
                likeOrUnLike();
            });
        } else if (id == R.id.ll_check) {
            //校验
            ProjectUtil.INSTANCE.checkProjectStatus(mContext, projectStatus, () -> {
                TaskUtil.inputDialog(mContext, viewDelegate.getRootView(), false, new TaskUtil.OnInputClickListner() {
                    @Override
                    public boolean inputClickSure(PopupWindow popup, String content) {
                        updatePassedStatus(popup, 1, content);
                        return true;
                    }

                    @Override
                    public boolean inputClickCancel(PopupWindow popup, String content) {
                        updatePassedStatus(popup, 2, content);
                        return true;
                    }
                });
            });

        } else if (id == R.id.nomals_edit) {
            if (viewDelegate.listView.size() != 0 && !completeStatus && getEditAuth()) {
                bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PROJECT_TASK_MOBULE_BEAN + projectId);
                bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                bundle.putLong(ProjectConstants.TASK_ID, taskId);
                bundle.putSerializable(Constants.DATA_TAG1, (Serializable) detailMap);
                bundle.putString(ProjectConstants.CHECK_MEMBER, checkMember);
                bundle.putString(ProjectConstants.CHECK_STATUS, checkStatus);
                bundle.putString(ProjectConstants.ASSOCIATE_STATUS, associatesStatus);
                bundle.putLong(ProjectConstants.LAYOUT_ID, TextUtil.parseLong(detailMap.get("id") + ""));
                bundle.putLong(ProjectConstants.TASK_FROM_TYPE, taskType);
                CommonUtil.startActivtiyForResult(mContext, EditTaskActivity.class, ProjectConstants.EDIT_TASK_REQUEST_CODE, bundle);
            } else {
                ToastUtils.showError(mContext, "没有权限");
            }
        } else if (id == R.id.picklist_status_li) {
            if (editTaskPesssiom && !projectStatus.equals("1") && !projectStatus.equals("2")) {
                fillStatus();
            } else {
                ToastUtils.showError(mContext, "没有权限");
            }

        }
    }

    /**
     * 处理数据
     */
    private void fillStatus() {
        String[] stateArr = new String[]{mContext.getResources().getString(R.string.project_no_start), mContext.getResources().getString(R.string.project_ongoing)
                , mContext.getResources().getString(R.string.project_suspended), mContext.getResources().getString(R.string.project_complete)};
        statusEntrys.clear();
        for (int i = 0; i < stateArr.length; i++) {
            EntryBean enter = new EntryBean();
            enter.setLabel(stateArr[i]);
            enter.setValue(i + "");
            enter.setFromType(Constants.STATE_FROM_PROJECR);
            statusEntrys.add(enter);
        }
        Bundle bundle = new Bundle();
        ArrayList<EntryBean> clone = CloneUtils.clone(((ArrayList<EntryBean>) statusEntrys));
        if (!TextUtil.isEmpty(statudState)) {
            for (EntryBean enter : clone) {
                if (enter.getLabel().equals(statudState)) {
                    enter.setCheck(true);
                }
            }
        }
        bundle.putSerializable(Constants.DATA_TAG1, clone);
        bundle.putBoolean(Constants.DATA_TAG2, true);
        bundle.putInt(Constants.DATA_TAG3, 1);
        CommonUtil.startActivtiyForResult(mContext, PickListViewSelectActivity.class, Constants.REQUEST_CODE19, bundle);
    }

    /**
     * 校验
     *
     * @param popup
     * @param status
     * @param content
     */
    private void updatePassedStatus(PopupWindow popup, int status, String content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", taskId);
        jsonObject.put("status", status);
        jsonObject.put("content", content);
        model.updatePassedStatus(mContext, jsonObject, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                popup.dismiss();
                ScreenUtils.letScreenLight(mContext);
                queryTaskDetail();
                viewDelegate.get(R.id.ll_check).setVisibility(View.GONE);
                if (status == 1) {
                    //通过校验
                    viewDelegate.ivCheckStatus.setImageResource(R.drawable.project_icon_check_pass);
                } else if (status == 2) {
                    //校验驳回
                    viewDelegate.ivCheckStatus.setImageResource(R.drawable.project_icon_check_reject);
                }
                viewDelegate.get(R.id.sv_root).scrollTo(0, 0);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }



    /**
     * 点赞
     */
    private void likeOrUnLike() {
        if (!ProjectUtil.INSTANCE.checkProjectPermission(mContext, priviledgeIds, 27)) {
            return;
        }
        viewDelegate.setLikeStatus();
        viewDelegate.setLikeNum(CollectionUtils.size(shareArr) + (viewDelegate.isLike() ? 1 : -1));
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", taskId);
        jsonObject.put("status", viewDelegate.isLike() ? 1 : 0);
        jsonObject.put("typeStatus", taskType);
        model.sharePraise(this, jsonObject, new ProgressSubscriber<BaseBean>(mContext, false) {
            @Override
            public void onNext(BaseBean o) {
                super.onNext(o);
                queryShareList();
            }

            @Override
            public void onError(Throwable e) {
                dismissWindowView();
                e.printStackTrace();
                if (viewDelegate.isLike()) {
                    ToastUtils.showError(mContext, "点赞失败");
                } else {
                    ToastUtils.showError(mContext, "取消点赞失败");
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == Constants.REQUEST_CODE1) {
            //更改协作人
            queryTaskMemberList();
        } else if (requestCode == ProjectConstants.ADD_TASK_REQUEST_CODE) {
            //新增
            TaskNew(data);
        }else if (requestCode == ProjectConstants.EDIT_TASK_REQUEST_CODE) {
            //编辑任务
            //editTask(data);
            String newData = data.getStringExtra(Constants.DATA_TAG9);
            saveTaskLayoutData(newData);
        } else if (requestCode == ProjectConstants.MOVE_TASK_REQUEST_CODE) {
            //移动任务
            queryByHierarchy();
        } else if (requestCode == ProjectConstants.ADD_SUB_TASK_REQUEST_CODE) {
            //新增子任务
            querySubList();
            queryTaskMemberList();
        } else if (requestCode == Constants.REQUEST_CODE16) {
            //检验人员
            List<Member> members = (List<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            viewDelegate.state_member_view.setMembers(members);
            saveTaskLayoutData(null);
        } else if (requestCode == Constants.REQUEST_CODE18) {
            //执行人员
            List<Member> members = (List<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            viewDelegate.executor_member_view.setMembers(members);
            saveTaskLayoutData(null);
        } else if (requestCode == Constants.REQUEST_CODE19) {
            List<EntryBean> resultArr = (ArrayList<EntryBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            statudStateList = getStateValue(resultArr);
            remark = data.getStringExtra(Constants.DATA_TAG3);
            saveTaskLayoutData(null);
        } else {
            if (viewDelegate.attachmenView == null || requestCode == viewDelegate.attachmenView.FILE_SELECT
                    || requestCode == viewDelegate.attachmenView.REQUEST_CODE_AT_ATTACHMENT || requestCode == PhotoPicker.REQUEST_CODE) {
                isAttachmentEdit = true;
            } else {
                saveTaskLayoutData(null);
            }
        }
        Log.e("onActivityResult", "requestCode:" + requestCode);
        Log.e("onActivityResult", "resultCode:" + resultCode);
        viewDelegate.mCommentInputView.onActivityResult(requestCode, resultCode, data);
    }

    public ArrayList<EntryBean> getStateValue(List<EntryBean> entrys) {
        ArrayList<EntryBean> entryList = new ArrayList();
        for (int i = 0; i < entrys.size(); i++) {
            if (entrys.get(i).isCheck()) {
                EntryBean map = new EntryBean();
                map.setValue(entrys.get(i).getValue());
                map.setLabel(entrys.get(i).getLabel());
                map.setColor(entrys.get(i).getColor());
                entryList.add(map);
            }
        }
        return entryList;
    }

    /**
     * 任务新增
     *
     * @param data
     */
    private void TaskNew(Intent data) {
        AppModuleBean appModeluBean = (AppModuleBean) data.getSerializableExtra(Constants.DATA_TAG1);

        Bundle bundle = new Bundle();
        String moduleBean = appModeluBean.getEnglish_name();
        switch (moduleBean) {
            case ProjectConstants.TASK_MODULE_BEAN:
                bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PROJECT_TASK_MOBULE_BEAN + projectId);
                bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                CommonUtil.startActivtiyForResult(mContext, AddTaskActivity.class, ProjectConstants.ADD_TASK_TASK_REQUEST_CODE, bundle);
                break;
            case MemoConstant.BEAN_NAME:
                UIRouter.getInstance().openUri(mContext, "DDComp://memo/add", bundle, ProjectConstants.ADD_TASK_MEMO_REQUEST_CODE);
                break;
            default:
                if (ApproveConstants.APPROVAL_MODULE_BEAN.equals(appModeluBean.getApplication_id())) {
                    bundle.putString(Constants.MODULE_BEAN, moduleBean);
                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/add", bundle, ProjectConstants.ADD_TASK_APPROVE_REQUEST_CODE);
                } else {
                    bundle.putString(Constants.MODULE_BEAN, moduleBean);
                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/add", bundle, CustomConstants.REQUEST_ADDCUSTOM_CODE);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (TextUtils.isEmpty(projectStatus)) {
            ToastUtils.showError(mContext, "权限错误");
            return true;
        }
        ProjectUtil.INSTANCE.checkProjectStatus(mContext, projectStatus, this::showOptionMenu);

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    private Boolean showOptionMenu() {
        List<String> menuList = new ArrayList<>();
        if (taskType == 1) {
            if (!completeStatus && checkTaskPermission(1)) {
                // menuList.add("编辑任务");
            }
            if (ProjectUtil.INSTANCE.checkPermission(priviledgeIds, 26)) {
                menuList.add("设置任务提醒");
            }
            if (checkTaskPermission(5)) {
                menuList.add("设置重复任务");
            }
            if (checkTaskPermission(4)) {
                menuList.add("移动任务");
            }
            if (checkTaskPermission(14)) {
                menuList.add("复制任务");
            }
            if (!completeStatus && checkTaskPermission(3)) {
                menuList.add("删除任务");
            }
            if (CollectionUtils.isEmpty(menuList)) {
                ToastUtils.showError(mContext, "没有权限");
                return true;
            }
        } else if (taskType == 2) {
            //1编辑2完成3删除4移动5设置重复6添加协作人7移除协作人
            //8新增子任务9编辑子任务10删除子任务11仅协作人可见12添加关联13取消关联14复制任务
            if (!completeStatus && checkTaskPermission(9)) {
                //menuList.add("编辑任务");
            }
            if (ProjectUtil.INSTANCE.checkPermission(priviledgeIds, 26)) {
                menuList.add("设置任务提醒");
            }
            if (!completeStatus && checkTaskPermission(10)) {
                menuList.add("删除任务");
            }
            if (CollectionUtils.isEmpty(menuList)) {
                ToastUtils.showError(mContext, "没有权限");
                return true;
            }
        }


        if (CollectionUtils.isEmpty(menuList)) {
            return false;
        }
        String[] menus = menuList.toArray(new String[menuList.size()]);
        PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), null, menus, p -> {
            Bundle bundle = new Bundle();
            switch (menus[p]) {
                case "编辑任务":
                    bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PROJECT_TASK_MOBULE_BEAN + projectId);
                    bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                    bundle.putLong(ProjectConstants.TASK_ID, taskId);
                    bundle.putSerializable(Constants.DATA_TAG1, (Serializable) detailMap);
                    bundle.putString(ProjectConstants.CHECK_MEMBER, checkMember);
                    bundle.putString(ProjectConstants.CHECK_STATUS, checkStatus);
                    bundle.putString(ProjectConstants.ASSOCIATE_STATUS, associatesStatus);
                    bundle.putLong(ProjectConstants.LAYOUT_ID, TextUtil.parseLong(detailMap.get("id") + ""));
                    bundle.putLong(ProjectConstants.TASK_FROM_TYPE, taskType);
                    CommonUtil.startActivtiyForResult(mContext, EditTaskActivity.class, ProjectConstants.EDIT_TASK_REQUEST_CODE, bundle);
                    break;
                case "设置任务提醒":
                    bundle.putLong(ProjectConstants.TASK_ID, taskId);
                    bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                    bundle.putLong(ProjectConstants.TASK_FROM_TYPE, taskType);
                    CommonUtil.startActivtiy(mContext, TaskRemindActivity.class, bundle);
                    break;
                case "设置重复任务":
                    bundle.putLong(ProjectConstants.TASK_ID, taskId);
                    CommonUtil.startActivtiy(mContext, RepeatTaskActivity.class, bundle);
                    break;
                case "移动任务":
                    bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                    bundle.putLong(ProjectConstants.TASK_ID, taskId);
                    bundle.putString(ProjectConstants.MAIN_TASK_NODECODE, nodeCode);
                    CommonUtil.startActivtiyForResult(mContext, MoveTaskActivity.class, ProjectConstants.MOVE_TASK_REQUEST_CODE, bundle);
                    break;
                case "复制任务":
                    bundle.putLong(ProjectConstants.TASK_ID, taskId);
                    bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                    bundle.putString(ProjectConstants.MAIN_TASK_NODECODE, nodeCode);
                    CommonUtil.startActivtiy(mContext, CopyTaskActivity.class, bundle);
                    break;
                case "删除任务":
                    DialogUtils.getInstance().sureOrCancel(mContext, "删除任务后，所有子任务也将同时被删除。", null, viewDelegate.getRootView(), () -> {
                        model.delTask(mContext, taskId, new ProgressSubscriber<BaseBean>(mContext) {
                            @Override
                            public void onNext(BaseBean baseBean) {
                                super.onNext(baseBean);
                                ToastUtils.showSuccess(mContext, "删除成功");
                                finish();
                                EventBusUtils.sendEvent(new MessageBean(ProjectConstants.PROJECT_TASK_REFRESH_CODE, null, null));
                            }
                        });
                    });
                    break;
                default:
                    break;
            }
            return false;
        });
        return null;
    }


    /**
     * 检测任务权限
     *
     * @param permission
     */
    private boolean checkTaskPermission(int permission) {
        if (taskAuths != null) {
            return taskAuths[permission - 1];
        }
        return false;
    }

    /**
     * 权限处理
     */
    private synchronized void permissionHandle() {
        if (CollectionUtils.isEmpty(taskRoles) || CollectionUtils.isEmpty(taskAuthList)) {
            return;
        }
        taskAuths = new boolean[14];
        for (QueryTaskAuthResultBean.DataBean auth : taskAuthList) {
            for (String taskRole : taskRoles) {
                if (taskRole.equals(auth.getRole_type())) {
                    Class<QueryTaskAuthResultBean.DataBean> clz = QueryTaskAuthResultBean.DataBean.class;
                    try {
                        for (int i = 0; i < 14; i++) {
                            Method mt = clz.getMethod("getAuth_" + (i + 1));
                            if ("1".equals(mt.invoke(auth))) {
                                taskAuths[i] = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        if (checkTaskPermission(11)) {//
            viewDelegate.sbtn.setEnabled(true);
        }
        if (checkTaskPermission(6) && checkTaskPermission(7)) {
            editAssociates = true;
        }

        editTaskPesssiom = checkTaskPermission(1);
        viewDelegate.seteditTaskPesssiom(editTaskPesssiom);
    }


    /**
     * 保存任务布局信息
     */
    private void saveTaskLayoutData(String jsonStr) {
        if (!ProjectConstants.PERSONAL_TASK_BEAN.equals(moduleBean)) {
            if (viewDelegate.sBtnTaskCheck.isChecked() && CollectionUtils.isEmpty(viewDelegate.state_member_view.getMembers())) {
                ToastUtils.showError(mContext, "校验人不能为空");
                return;
            }
        }

        JSONObject json = new JSONObject();
        boolean put = ProjectCustomUtil.put(mContext, viewDelegate.listView, json);
        if (!put) {
            return;
        }
        Object object = taskData.getCustomArr();
        String jsonArr = JSONObject.toJSONString(object);
        JSONObject oldData = (JSONObject) JSONObject.toJSON(object);
        //项目任务
        oldDeadline = oldData.get(ProjectConstants.PROJECT_TASK_DEADLINE) + "";
        String newDeadline = json.get(ProjectConstants.PROJECT_TASK_DEADLINE) + "";
        if (oldDeadline != null && newDeadline != null && !oldDeadline.equals(newDeadline)) {
            if (TextUtil.isEmpty(updateDeadlineAuth)) {
                ToastUtils.showError(mContext, "未获取到截止时间相关权限");
                queryEditDeadlineAuth();
            } else if ("1".equals(updateDeadlineAuth)) {
                DialogUtils.getInstance().inputDialog(mContext, "修改原因", null, "必填", viewDelegate.getRootView(), content -> {
                    if (TextUtil.isEmpty(content)) {
                        ToastUtils.showError(mContext, "请输入修改原因");
                        return false;
                    } else {
                        updateTask(json, oldData, content, jsonStr);
                    }
                    return true;
                });
            } else {
                updateTask(json, oldData, remark, jsonStr);
            }
        } else {
            updateTask(json, oldData, remark, jsonStr);
        }
    }

    /**
     * 编辑任务
     *
     * @param newData
     * @param remark
     */
    public void updateTask(JSONObject newData, JSONObject oldDta, String remark, String jsonStr) {
        JSONObject bean = getEditTaskData(newData, oldDta, remark, jsonStr);
        if (taskType == 2) {
            model.editMainTaskSub(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "编辑成功");
                    queryTaskDetail();
                }
            });
        } else {
            model.editMainTask(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "编辑成功");
                    queryTaskDetail();
                }
            });
        }

    }

    /**
     * 获取编辑任务json对象
     */
    public JSONObject getEditTaskData(JSONObject newData, JSONObject oldDta, String remark, String jsonStr) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", projectId);
        jsonObject.put("taskId", taskId);
        jsonObject.put("bean", ProjectConstants.PROJECT_TASK_MOBULE_BEAN + projectId);
        jsonObject.put("checkStatus", viewDelegate.getTaskCheckStatus());
        if (!TextUtil.isEmpty(viewDelegate.getCheckOneId())) {
            jsonObject.put("checkMember", Long.parseLong(viewDelegate.getCheckOneId()));
        } else {
            jsonObject.put("checkMembers", 0);
        }
        jsonObject.put("associatesStatus", viewDelegate.getOnlyParticipantStatus());
        jsonObject.put("taskName", viewDelegate.tvTaskName.getText().toString());
        if (!TextUtil.isEmpty(newData.getString(ProjectConstants.PROJECT_TASK_DEADLINE))) {
            jsonObject.put("endTime", Long.parseLong(newData.getString(ProjectConstants.PROJECT_TASK_DEADLINE)));
        } else {
            jsonObject.put("endTime", 0);
        }
        if (!TextUtil.isEmpty(newData.getString(ProjectConstants.PROJECT_TASK_STARTTIME))) {
            jsonObject.put("startTime", Long.parseLong(newData.getString(ProjectConstants.PROJECT_TASK_STARTTIME)));
        } else {
            jsonObject.put("startTime", 0);
        }
        if (!TextUtil.isEmpty(viewDelegate.getExecutorOneId())) {
            jsonObject.put("executorId", Long.parseLong(viewDelegate.getExecutorOneId()));
        } else {
            jsonObject.put("executorId", 0);
        }
        if (newData.get("attachment_customnumber") == null || TextUtil.isEmpty(newData.get("attachment_customnumber").toString())) {
            newData.put("attachment_customnumber", new ArrayList<Object>());
        }
        if (CollectionUtils.isEmpty(statudStateList)) {
            statudStateList = new ArrayList<EntryBean>();
            EntryBean bean = new EntryBean();
            bean.setLabel("未开始");
            bean.setColor("");
            bean.setValue("0");
            statudStateList.add(bean);
        }

        newData.put("picklist_status", statudStateList);

        newData.put("text_name", viewDelegate.tvTaskName.getText().toString());
        newData.put("personnel_principal", viewDelegate.getExecutorOneId());
        JSONObject data = handleData(newData, oldDta);
        JSONObject data2 = null;
        if (!TextUtil.isEmpty(jsonStr)) {
            JSONObject coustomData = JSONObject.parseObject(jsonStr);
            if (coustomData != null && data != null) {
                data2 = handleData(coustomData, data);
            }
        }


        jsonObject.put("oldData", oldDta);
        if (data2 != null) {
            jsonObject.put("data", data2);
        } else {
            jsonObject.put("data", data);
        }
        if (remark != null) {
            jsonObject.put("remark", remark);
        }
        if (taskType == 2) {
            jsonObject.put("taskId", mainTaskId);
            jsonObject.put("id", taskId);

        }
        jsonObject.put("nodeId", nodeId);
        return jsonObject;
    }

    public JSONObject handleData(JSONObject newData, JSONObject oldDta) {
        JSONObject data = (JSONObject) oldDta.clone();
        Set<String> iterator = oldDta.keySet();
        Set<String> newIterator = newData.keySet();
        for (String key : iterator) {
            for (String newKey : newIterator) {
                if (key.equals(newKey)) {
                    data.put(newKey, newData.get(newKey));
                }
            }
        }
        return data;
    }

    /**
     * 修改截止时间权限
     */
    private void queryEditDeadlineAuth() {
        model.queryTaskCompleteAuth(mContext, projectId, taskType, taskId, new ProgressSubscriber<QueryTaskCompleteAuthResultBean>(mContext) {
            @Override
            public void onNext(QueryTaskCompleteAuthResultBean queryTaskAuthResultBean) {
                super.onNext(queryTaskAuthResultBean);
                updateDeadlineAuth = queryTaskAuthResultBean.getData().getProject_time_status();
            }
        });
    }

}

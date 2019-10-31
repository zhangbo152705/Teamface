package com.hjhq.teamface.project.presenter.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.ApproveListBean;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.MemoListItemBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ModuleBean;
import com.hjhq.teamface.basis.bean.Photo;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
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
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.adapter.CommentAdapter;
import com.hjhq.teamface.common.adapter.DynamicAdapter;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.bean.CommentDetailResultBean;
import com.hjhq.teamface.common.bean.DynamicListResultBean;
import com.hjhq.teamface.common.ui.ImagePagerActivity;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.ui.member.SelectRangeActivity;
import com.hjhq.teamface.common.ui.voice.VoicePlayActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.utils.ProjectDialog;
import com.hjhq.teamface.common.utils.TaskHelper;
import com.hjhq.teamface.common.view.CommentInputView;
import com.hjhq.teamface.common.view.boardview.DragItemAdapter;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.select.PickListView;
import com.hjhq.teamface.customcomponent.widget2.select.PickListViewSelectActivity;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.PersonalSubTaskAdapter;
import com.hjhq.teamface.project.adapter.RelationTaskAdapter;
import com.hjhq.teamface.project.adapter.TaskAllDynamicAdapter;
import com.hjhq.teamface.project.adapter.TaskStatusAdapter;
import com.hjhq.teamface.project.bean.PersonalSubListResultBean;
import com.hjhq.teamface.project.bean.PersonalSubTaskBean;
import com.hjhq.teamface.project.bean.PersonalTaskDetailResultBean;
import com.hjhq.teamface.project.bean.PersonalTaskLikeBean;
import com.hjhq.teamface.project.bean.PersonalTaskListResultBean;
import com.hjhq.teamface.project.bean.PersonalTaskMembersResultBean;
import com.hjhq.teamface.basis.bean.PersonalTaskRoleResultBan;
import com.hjhq.teamface.project.bean.PersonalTaskStatusResultBean;
import com.hjhq.teamface.project.bean.ProjectLabelsListBean;
import com.hjhq.teamface.project.bean.ProjectMemberResultBean;
import com.hjhq.teamface.project.bean.RelationListResultBean;
import com.hjhq.teamface.project.bean.SavePersonalRelationRequestBean;
import com.hjhq.teamface.project.bean.SavePersonalTaskLayoutRequestBean;
import com.hjhq.teamface.project.bean.TaskAllDynamicBean;
import com.hjhq.teamface.project.bean.TaskAllDynamicDetailBean;
import com.hjhq.teamface.project.bean.TaskLayoutResultBean;
import com.hjhq.teamface.project.bean.TaskLikeBean;
import com.hjhq.teamface.project.bean.TaskStatusBean;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.presenter.RelationTaskCardListActivity;
import com.hjhq.teamface.project.presenter.SelectModuleActivity;
import com.hjhq.teamface.project.presenter.SubTaskCardListActivity;
import com.hjhq.teamface.project.ui.task.TaskDetailDelegate;
import com.hjhq.teamface.project.view.CommomDialog;
import com.hjhq.teamface.project.widget.utils.ProjectCustomUtil;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.iwf.photopicker.PhotoPicker;
import rx.Observable;
import rx.functions.Func1;


/**
 * 个人任务详情
 *
 * @author Administrator
 * @date 2018/6/25
 */
@RouteNode(path = "/personal_task_detail", desc = "个人任务详情")
public class PersonalTaskDetailActivity extends ActivityPresenter<TaskDetailDelegate, TaskModel> implements View.OnClickListener {
    private long taskId;
    private String dataId = "";
    /**
     * 任务自定义布局 的bean
     */
    private String moduleBean;

    /**
     * 任务类型 0 任务 1子任务
     */
    int fromType = 0;
    private PersonalSubTaskAdapter subTaskAdapter;
    private RelationTaskAdapter taskItemAdapter;
    private RelationTaskAdapter beTaskItemAdapter;
    private CommentAdapter mCommentAdapter;
    private DynamicAdapter mDynamicAdapter;
    private TaskStatusAdapter mTaskStatusAdapter;
    /**
     * 已完成状态
     */
    private boolean completeStatus;
    /**
     * 点赞人
     */
    private List<PersonalTaskLikeBean> shareArr;
    private String taskName;
    private HashMap<String, Object> detailMap = new HashMap<>();
    private String associatesStatus;
    /**
     * 执行人
     */
    private Member executeMember;
    /**
     * 自定义任务id
     */
    private String projectCustomId;
    /**
     * "项目" 关联的模块数据名称
     */
    private String relationData;
    /**
     * 模块数据id
     */
    private String relationId;
    /**
     * 0创建人 1执行人 2协作人
     */
    private String taskRole;
    /**
     * 编辑协作人
     */
    private boolean editAssociates;
    //动态分页
    private boolean isFirstLoad = true;
    private int currentPageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    private int state = Constants.NORMAL_STATE;
    private List<DynamicListResultBean.DataBean.TimeListBean> dynamicDataList = new ArrayList<>();
    private List<CommentDetailResultBean.DataBean> commentDataList = new ArrayList<>();
    private List<TaskStatusBean> taskStatusList = new ArrayList<>();

    private TaskAllDynamicAdapter mTaskAllDynamicAdapter;//zzh->ad:新增
    private List<TaskAllDynamicDetailBean> taskDynamicList = new ArrayList<>();//zzh->ad:新增
    private int allDynamicPageSize = 8;
    private int maxAllDynamicPageSize = 50;
    private int mdynamicType = 0;
    private boolean isAttachmentEdit = false;

    private PersonalTaskDetailResultBean.DataBean taskData;
    private CommomDialog editDialog;

    private ArrayList<ProjectLabelBean> projectLables;
    private List<EntryBean> lableEntrys = new ArrayList<>();

    List<PersonalSubTaskBean> subTaskArr;//子任务列表

    private String  statudState = "";//当前状态
    private List<EntryBean> statudStateList;
    private List<EntryBean> statusEntrys = new ArrayList<>();
    private boolean isSuspend=false;//是否已暂停
    private String remark;
    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            taskId = intent.getLongExtra(ProjectConstants.TASK_ID, 0);
            taskName = intent.getStringExtra(ProjectConstants.TASK_NAME);
            moduleBean = intent.getStringExtra(Constants.MODULE_BEAN);
            fromType = intent.getIntExtra(ProjectConstants.TASK_FROM_TYPE, 0);
            dataId = taskId + "";
        }
    }

    @Override
    public void init() {
        viewDelegate.setTaskTitle(taskName);
        viewDelegate.hideNavigator();
        subTaskAdapter = new PersonalSubTaskAdapter(null);
        setSubTaskHead();
        viewDelegate.setSubTaskAdapter(subTaskAdapter);
        taskItemAdapter = new RelationTaskAdapter();
        viewDelegate.setRelevanceAdapter(taskItemAdapter);
        beTaskItemAdapter = new RelationTaskAdapter();
        viewDelegate.setBeRelevanceAdapter(beTaskItemAdapter);
        mCommentAdapter = new CommentAdapter(commentDataList);
        viewDelegate.mRvComment.setAdapter(mCommentAdapter);
        mDynamicAdapter = new DynamicAdapter(dynamicDataList);
        viewDelegate.mRvDynamic.setAdapter(mDynamicAdapter);
        mTaskStatusAdapter = new TaskStatusAdapter(taskStatusList);
        viewDelegate.mRvState.setAdapter(mTaskStatusAdapter);
        viewDelegate.mCommentInputView.setData(ProjectConstants.PROJECT_DYNAMIC_BEAN_NAME, dataId);
        viewDelegate.get(R.id.tv_comment).setSelected(true);

        mTaskAllDynamicAdapter = new TaskAllDynamicAdapter(taskDynamicList);
        viewDelegate.trendRecyclerView.setAdapter(mTaskAllDynamicAdapter);
        viewDelegate.seteditTaskPesssiom(true);

        initData();
        viewDelegate.hideCheckView();//zzh->ad:个人任务隐藏检验
    }

    private void initData() {
        //获取详情
        queryTaskDetail();

        //获取协助人列表（子任务或者任务）
        queryPersonalTaskMembers();

        if (fromType == 0) {
            //子任务
            querySubList();
        } else {
            viewDelegate.hideSubTask();
        }
        //关联任务
        queryRelationList();

        if (fromType == 0) {
            //被关联任务
            queryPersonalByRelations();
        } else {
            viewDelegate.hideBeRelevance();
        }
        //点赞列表
        queryShareList();
        //权限
        queryPersonalTaskRole();
        //评论列表
        //getCommentList();
        //状态列表
        //getTaskStatus();
        //动态列表
        //getDynamicList();

        getAllDynamic(fromType,0,allDynamicPageSize);//zzh->ad:新增
        getTaskLable();
    }

    /**
     * 获取权限
     */
    private void queryPersonalTaskRole() {
        model.queryPersonalTaskRole(mContext, taskId, fromType, new ProgressSubscriber<PersonalTaskRoleResultBan>(mContext) {
            @Override
            public void onNext(PersonalTaskRoleResultBan baseBean) {
                super.onNext(baseBean);
                taskRole = baseBean.getData().getRole();
                if ("2".equals(taskRole)) {
                    viewDelegate.showMenu();
                } else {
                    viewDelegate.showMenu(0);
                    editAssociates = true;
                    viewDelegate.sbtn.setEnabled(true);
                }
            }
        });
    }

    /**
     * 获取任务布局与标签
     */
    private void getTaskLable() {
        model.getAllLabel(this, null, 2, new ProgressSubscriber<ProjectLabelsListBean>(this) {
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
        Observable.from(projectLables)
                .subscribe(lable -> lableEntrys.add(new EntryBean(lable.getId(), lable.getName(), lable.getColour())));

        if (viewDelegate.listView != null && viewDelegate.listView.size() > 0) {
            for (BaseView view : viewDelegate.listView) {
                if (ProjectConstants.PROJECT_TASK_LABEL.equals(view.getKeyName())) {
                    ((PickListView) view).setEntrys(lableEntrys);
                }
            }
        }
    }

    /**
     * 查询协作人列表
     */
    private void queryPersonalTaskMembers() {
        model.queryPersonalTaskMembers(this, taskId, fromType, new ProgressSubscriber<PersonalTaskMembersResultBean>(this) {
            @Override
            public void onNext(PersonalTaskMembersResultBean taskMemberListResultBean) {
                super.onNext(taskMemberListResultBean);

                List<Member> members = new ArrayList<>();
                if (!CollectionUtils.isEmpty(taskMemberListResultBean.getData())) {
                    for (PersonalTaskMembersResultBean.DataListBean dataBean : taskMemberListResultBean.getData()) {
                        Member member = new Member(dataBean.getEmployee_id(), dataBean.getEmployee_name(), C.EMPLOYEE,dataBean.getPicture());
                        members.add(member);
                    }
                }
                viewDelegate.setAssociates(members);
            }
        });
    }

    /**
     * 获取子任务
     */
    private void querySubList() {
        model.queryPersonalSubList(this, taskId, new ProgressSubscriber<PersonalSubListResultBean>(this) {
            @Override
            public void onNext(PersonalSubListResultBean baseBean) {
                super.onNext(baseBean);
                //子任务
                 subTaskArr = baseBean.getData();
                //CollectionUtils.notifyDataSetChanged(subTaskAdapter, subTaskAdapter.getData(), subTaskArr);
                setSubTaskHead();
            }
        });
    }

    /**
     * 获取关联任务
     */
    private void queryRelationList() {
        model.queryPersonalRelations(this, taskId, fromType, new ProgressSubscriber<RelationListResultBean>(mContext) {
            @Override
            public void onNext(RelationListResultBean baseBean) {
                super.onNext(baseBean);
                //关联任务
                List<RelationListResultBean.DataBean.ModuleDataBean> relationArr = baseBean.getData().getDataList();
                CollectionUtils.notifyDataSetChanged(taskItemAdapter, taskItemAdapter.getData(), relationArr);
                viewDelegate.setRelationHead(relationArr.size());
            }
        });
    }

    /**
     * 获取被关联列表
     */
    private void queryPersonalByRelations() {
        model.queryPersonalByRelations(this, taskId, new ProgressSubscriber<RelationListResultBean>(mContext) {
            @Override
            public void onNext(RelationListResultBean baseBean) {
                super.onNext(baseBean);
                //被关联
                List<RelationListResultBean.DataBean.ModuleDataBean> relationArr2 = baseBean.getData().getDataList();
                CollectionUtils.notifyDataSetChanged(beTaskItemAdapter, beTaskItemAdapter.getData(), relationArr2);
                viewDelegate.setBeRelationHead(relationArr2.size());
            }
        });
    }

    /**
     * 点赞列表
     */
    private void queryShareList() {
        model.praisePersonQueryList(this, taskId, fromType, new ProgressSubscriber<PersonalTaskListResultBean>(mContext, false) {
            @Override
            public void onNext(PersonalTaskListResultBean baseBean) {
                super.onNext(baseBean);
                shareArr = baseBean.getData();
                if (shareArr == null) {
                    shareArr = new ArrayList<>();
                }
                viewDelegate.setLikeNum(shareArr.size());

                String employeeId = SPHelper.getEmployeeId();
                Observable.from(shareArr)
                        .filter(share -> employeeId.equals(share.getEmployee_id()))
                        .subscribe(share -> viewDelegate.setLikeStatus(true));
            }
        });
    }

    /**
     * 查询任务详情
     */
    private void queryTaskDetail() {
        if (fromType == 0) {
            model.queryPersonalTaskDetail(this, taskId, moduleBean, (queryTaskDetailResultBean, taskLayoutResultBean) -> {
                //将详情设置到布局中
                handleTaskDetail(queryTaskDetailResultBean, taskLayoutResultBean);
                return taskLayoutResultBean;
            }, new ProgressSubscriber<TaskLayoutResultBean>(this) {
                @Override
                public void onNext(TaskLayoutResultBean taskLayoutResultBean) {
                    super.onNext(taskLayoutResultBean);
                    viewDelegate.drawLayout(taskLayoutResultBean.getData().getEnableLayout(), moduleBean, true,completeStatus,"");
                }
            });
        } else if (fromType == 1) {
            model.queryPersonalSubTaskDetail(this, taskId, moduleBean, (queryTaskDetailResultBean, taskLayoutResultBean) -> {
                //将详情设置到布局中
                handleTaskDetail(queryTaskDetailResultBean, taskLayoutResultBean);
                return taskLayoutResultBean;
            }, new ProgressSubscriber<TaskLayoutResultBean>(this) {
                @Override
                public void onNext(TaskLayoutResultBean taskLayoutResultBean) {
                    super.onNext(taskLayoutResultBean);
                    viewDelegate.drawLayout(taskLayoutResultBean.getData().getEnableLayout(), moduleBean, true,completeStatus,"");
                }
            });
        }
    }

    /**
     * 得到评论列表
     */
    public void getCommentList() {
        model.getCommentDetail(mContext, taskId + "", ProjectConstants.PROJECT_TASK_DYNAMIC_BEAN_NAME,
                new ProgressSubscriber<CommentDetailResultBean>(mContext, false) {
                    @Override
                    public void onNext(CommentDetailResultBean commentDetailResultBean) {
                        super.onNext(commentDetailResultBean);
                        List<CommentDetailResultBean.DataBean> data = commentDetailResultBean.getData();
                        CollectionUtils.notifyDataSetChanged(mCommentAdapter, mCommentAdapter.getData(), data);
                /*if (data.size() > 0) {
                    viewDelegate.showMore(false);
                    viewDelegate.getmRecyclerView().scrollToPosition(data.size() - 1);
                }*/
                        if (!isFirstLoad) {
                            scrollToBottom();
                        }
                        isFirstLoad = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    /**
     * 任务状态列表
     */
    public void getTaskStatus() {
        model.queryPersonalTaskStatus(mContext, taskId, fromType, new ProgressSubscriber<PersonalTaskStatusResultBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(PersonalTaskStatusResultBean dataBean) {
                super.onNext(dataBean);
                taskStatusList.clear();
                taskStatusList.addAll(dataBean.getData());
                mTaskStatusAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 动态列表
     */
    private void getDynamicList() {
        int pageNum = state == Constants.LOAD_STATE ? currentPageNo : 1;

        model.getDynamicList(this, taskId + "", ProjectConstants.PROJECT_TASK_DYNAMIC_BEAN_NAME,
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

                        switch (state) {
                            case Constants.NORMAL_STATE:
                            case Constants.REFRESH_STATE:
                                dynamicDataList.clear();
                                mDynamicAdapter.setEnableLoadMore(true);
                                break;
                            case Constants.LOAD_STATE:
                                mDynamicAdapter.loadMoreEnd();
                                break;
                            default:
                                break;
                        }
                        dynamicDataList.addAll(list);
                        mDynamicAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (state == Constants.LOAD_STATE) {
                            mDynamicAdapter.loadMoreFail();
                        }
                    }
                });
    }

    /**
     * 获取所有状态
     */
    public void getAllDynamic(int taskType, int dynamicType,int pageSize) {
        int type = 0;
        if (taskType == 0){
            type =3;
        }else{
            type = 4;
        }
        model.queryTaskDynamicList(mContext, taskId ,type,dynamicType,pageSize,  new ProgressSubscriber<TaskAllDynamicBean>(mContext, true) {
            @Override
            public void onNext(TaskAllDynamicBean detailResultBean) {
                super.onNext(detailResultBean);
                List<TaskAllDynamicDetailBean> data = detailResultBean.getData().getDataList();
                CollectionUtils.notifyDataSetChanged(mTaskAllDynamicAdapter, mTaskAllDynamicAdapter.getData(), data);
                    if (data.size()<5){
                    viewDelegate.hideMoreDynamicData();
                }else {
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
     * 将详情设置到布局中
     *
     * @param queryTaskDetailResultBean
     * @param taskLayoutResultBean
     */
    private void handleTaskDetail(PersonalTaskDetailResultBean queryTaskDetailResultBean, TaskLayoutResultBean taskLayoutResultBean) {

        //将详情设置到布局中
        TaskLayoutResultBean.DataBean.EnableLayoutBean enableLayout = taskLayoutResultBean.getData().getEnableLayout();
        List<CustomBean> rows = enableLayout.getRows();
       taskData = queryTaskDetailResultBean.getData();
        try {
            final Object execution = taskData.getCustomLayout().get("personnel_principal");
            String jsonArr = JSONObject.toJSONString(execution);
            JSONArray ja = new JSONArray(jsonArr);
            if (ja.length() > 0) {
                org.json.JSONObject object = ja.getJSONObject(0);
                executeMember = new Member();
                executeMember.setName(object.optString("name"));
                executeMember.setId(object.optLong("id"));
                executeMember.setPicture(object.optString("picture"));
                executeMember.setEmployee_name(object.optString("name"));
                executeMember.setUpdateTime(((long) taskData.getCustomLayout().get("datetime_deadline")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TaskDetailActivity", "Parse member error!");
        }

        Observable.just(1)
                .compose(TransformerHelper.applySchedulers())
                .subscribe(i ->
                        setDetail(taskData));
        detailMap = (HashMap<String, Object>) taskData.getCustomLayout();
        if (!CollectionUtils.isEmpty(rows)) {
            for (CustomBean customBean : rows) {
                Object value = detailMap.get(customBean.getName());
                if (ProjectConstants.PROJECT_TASK_RELATION.equals(customBean.getName())) {
                    //关联关系
                    String relationId = taskData.getRelation_id();
                    String relationData = taskData.getRelation_data();
                    String from_status = taskData.getFrom_status();
                    String bean_name = taskData.getBean_name();
                    if (TextUtil.isEmpty(relationId) && TextUtil.isEmpty(relationData)) {
                        detailMap.put(ProjectConstants.PROJECT_TASK_RELATION, "");
                        customBean.setValue("");
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", relationId);
                        map.put("name", relationData);
                        map.put("from_status", from_status);
                        map.put("bean_name", bean_name);
                        List<Map> list = new ArrayList<>();
                        list.add(map);
                        detailMap.put(ProjectConstants.PROJECT_TASK_RELATION, list);
                        customBean.setValue(list);
                    }
                } else if (ProjectConstants.PROJECT_TASK_LABEL.equals(customBean.getName())) {
                    List<Map> list = new ArrayList<>();
                    List<ProjectLabelBean> projectLabelBeen = new JsonParser<ProjectLabelBean>().jsonFromList(value, ProjectLabelBean.class);
                    Observable.from(projectLabelBeen).subscribe(label -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("value", label.getId());
                        map.put("label", label.getName());
                        map.put("color", label.getColour());
                        list.add(map);
                    });
                    detailMap.put(ProjectConstants.PROJECT_TASK_LABEL, list);
                    customBean.setValue(list);
                    customBean.setEntrys(lableEntrys);
                } else {
                    customBean.setValue(value);
                }
            }
        }

        //状态
        Object status = detailMap.get(ProjectConstants.PROJECT_TASK_STATUS);
        if (status != null){
            List<EntryBean> statusBeen = new JsonParser<EntryBean>().jsonFromList(status, EntryBean.class);
            Observable.just(1)
                    .filter(o -> !CollectionUtils.isEmpty(statusBeen))
                    .compose(TransformerHelper.applySchedulers())
                    .subscribe(i ->{
                        statudState = statusBeen.get(0).getLabel();
                        if (!TextUtil.isEmpty(statudState) && mContext.getResources().getString(R.string.project_suspended).indexOf(statudState) != -1){
                            isSuspend =true;
                            viewDelegate.setIsSuspend(isSuspend);
                        }
                        statudStateList = statusBeen;
                        viewDelegate.setPickStatus(statusBeen);
                    });
        }


    }




    public void setSubTaskHead() {
        //List<PersonalSubTaskBean> data = subTaskAdapter.getData();
        final int[] completeSize = {0};
        int total = 0;
        if (!CollectionUtils.isEmpty(subTaskArr)){
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
    private void setDetail(PersonalTaskDetailResultBean.DataBean taskData) {
        if (taskData == null) {
            return;
        }
        List<Member> members = null;
        if (executeMember != null && !TextUtil.isEmpty(executeMember.getName())) {
            members = new ArrayList<>();
            members.add(executeMember);
        }
        viewDelegate.setExecutor(members);
        //协作人是否可见
        associatesStatus = taskData.getParticipants_only();
        viewDelegate.setCheckedImmediatelyNoEvent("1".equals(associatesStatus));
        completeStatus = "1".equals(taskData.getComplete_status());
        viewDelegate.setTaskStatus(completeStatus);

        projectCustomId = taskData.getProject_custom_id();
        relationData = taskData.getRelation_data();
        relationId = taskData.getRelation_id();

    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.ll_associates_control, R.id.ll_sub_task_control, R.id.ll_relevance_control
                , R.id.ll_be_relevance_control, R.id.tv_comment, R.id.tv_dynamic, R.id.tv_look_status, R.id.iv_like
                , R.id.tv_add_relevance, R.id.iv_task_status, R.id.ll_check, R.id.ll_add_sub_task,R.id.nomals_edit,R.id.picklist_status_li);
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_DATA_DETAIL_CODE, tag -> UIRouter.getInstance().openUri(mContext, "DDComp://custom/detail", (Bundle) tag));
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_FILE_DETAIL_CODE, tag -> UIRouter.getInstance().openUri(mContext, "DDComp://custom/file", (Bundle) tag));

        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_FILE_DETAIL_DYNAMIC_CODE, tag -> {
            mdynamicType = (int) tag;
            getAllDynamic(fromType, mdynamicType, allDynamicPageSize);
        });


        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_FILE_DETAIL_ATTACH_CODE, tag -> {//zzh->ad:附件编辑
            if (isAttachmentEdit) {
                saveTaskLayoutData(null);
            }

        });
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_FILE_DETAIL_DELETE_ATTACH_CODE, tag -> {//zzh->ad:删除附件
            if(getEditAuth()){
                saveTaskLayoutData(null);
            }
        });
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_TASK_DETAIL_CLEAR_RELEVANCE_CODE, tag -> {//zzh->ad:附件编辑
                saveTaskLayoutData(null);
        });

        /**
         * 清空标签操作
         */
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_TASK_DETAIL_CLEAR_LABLE_CODE, o -> {
            saveTaskLayoutData(null);

        });

        viewDelegate.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ArrayList<TaskLikeBean> list = new ArrayList<>();
            for (PersonalTaskLikeBean bean : shareArr) {
                TaskLikeBean like = new TaskLikeBean();
                like.setName(bean.getEmployee_name());
                like.setId(bean.getEmployee_id());
                list.add(like);
            }
            bundle.putSerializable(Constants.DATA_TAG1, list);
            CommonUtil.startActivtiy(mContext, TaskThumbUpActivity.class, bundle);
        }, R.id.tv_like_num);
        //子任务
        viewDelegate.subTaskRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                PersonalSubTaskBean item = (PersonalSubTaskBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putLong(ProjectConstants.TASK_ID, item.getId());
                bundle.putInt(ProjectConstants.TASK_FROM_TYPE, 1);
                bundle.putString(ProjectConstants.TASK_NAME, item.getName());
                bundle.putString(Constants.MODULE_BEAN,ProjectConstants.PERSONAL_TASK_BEAN);//zzh->ad:item.getbeanName 改为ProjectConstants.PERSONAL_TASK_BEAN
                CommonUtil.startActivtiy(PersonalTaskDetailActivity.this, PersonalTaskDetailActivity.class, bundle);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                PersonalSubTaskBean item = subTaskAdapter.getItem(position);
                boolean complete = "1".equals(item.getComplete_status());
                updateSubTaskStatus(item.getId(), complete);
            }
        });

        //zzh->ad:执行人
        viewDelegate.executor_member_view.setOnAddMemberClickedListener(() -> {
                    if (!editAssociates || !getEditAuth()) {
                        ToastUtils.showError(mContext, "没有权限！");
                        return;
                    }
                    List<Member> members = viewDelegate.executor_member_view.getMembers();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(C.SPECIAL_MEMBERS_TAG, (Serializable) members);
                    bundle.putInt(C.CHECK_TYPE_TAG, C.RADIO_SELECT);
                    CommonUtil.startActivtiyForResult(mContext, SelectMemberActivity.class, Constants.REQUEST_CODE18, bundle);
                }

        );

        //协作人
        viewDelegate.membersView.setOnAddMemberClickedListener( () ->{
            if (!editAssociates) {
                ToastUtils.showError(mContext, "没有权限！");
                return;
            }
            List<Member> members = viewDelegate.membersView.getMembers();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.DATA_TAG1, (Serializable) members);
            bundle.putLong(ProjectConstants.TASK_ID, taskId);
            bundle.putLong(ProjectConstants.TASK_FROM_TYPE, fromType);
            CommonUtil.startActivtiyForResult(mContext, TaskMemberActivity.class, Constants.REQUEST_CODE1, bundle);

        });


       /* //协作人
        viewDelegate.setOnClickListener(v -> {
            if (!editAssociates) {
                ToastUtils.showError(mContext, "没有权限！");
                return;
            }
            List<Member> members = viewDelegate.membersView.getMembers();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.DATA_TAG1, (Serializable) members);
            bundle.putLong(ProjectConstants.TASK_ID, taskId);
            bundle.putLong(ProjectConstants.TASK_FROM_TYPE, fromType);
            CommonUtil.startActivtiyForResult(mContext, TaskMemberActivity.class, Constants.REQUEST_CODE1, bundle);
        }, R.id.add_member_ll);*/

        //协作人可见
        viewDelegate.sbtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            model.updatePersonalTaskAssociatesState(mContext, taskId, isChecked ? 1 : 0, fromType, new ProgressSubscriber<BaseBean>(mContext) {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    viewDelegate.setCheckedImmediatelyNoEvent(!isChecked);
                }
            });
        });

        //关联任务
        taskItemAdapter.setOnItemClickListener(new TaskItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DragItemAdapter adapter, View view, int position) {
                TaskInfoBean item = (TaskInfoBean) adapter.getItemList().get(position);
                TaskHelper.INSTANCE.clickTaskItem(mContext, item);
            }

            @Override
            public void onItemChildClick(DragItemAdapter adapter, View view, int position) {
            }

            @Override
            public void onItemLongClick(DragItemAdapter adapter, View view, int position) {
                if (!checkRole()) return;
                //取消关联
                DialogUtils.getInstance().sureOrCancel(mContext, "确定取消该关联任务吗？", null, view.getRootView(), () -> {
                    TaskInfoBean item = (TaskInfoBean) adapter.getItemList().get(position);
                    //个人任务
                    new TaskModel().canclePersonalRelation(mContext, item.getId(), taskId, fromType, new ProgressSubscriber<BaseBean>(mContext) {
                        @Override
                        public void onNext(BaseBean baseBean) {
                            super.onNext(baseBean);
                            queryRelationList();
                            ToastUtils.showSuccess(mContext, "取消成功");
                        }
                    });
                });
            }
        });
        //被关联任务
        beTaskItemAdapter.setOnItemClickListener(new TaskItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DragItemAdapter adapter, View view, int position) {
                TaskInfoBean item = (TaskInfoBean) adapter.getItemList();
                TaskHelper.INSTANCE.clickTaskItem(mContext, item);
            }

            @Override
            public void onItemChildClick(DragItemAdapter adapter, View view, int position) {
            }

            @Override
            public void onItemLongClick(DragItemAdapter adapter, View view, int position) {
            }
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
                commentDataList.add(bean);
                mCommentAdapter.notifyDataSetChanged();
                scrollToBottom();

            }
        });

        viewDelegate.tvTaskName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getEditAuth()){
                    showEditDialog();
                }
            }
        });

        viewDelegate.dynamic_more_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDelegate.dynamic_more_data.setVisibility(View.GONE);
                allDynamicPageSize = maxAllDynamicPageSize;
                getAllDynamic(fromType,mdynamicType, allDynamicPageSize);
            }
        });
    }

    /**
     * 编辑弹窗
     * @param
     */
    public void showEditDialog(){
        editDialog = new CommomDialog(mContext,0,taskName,0,new CommomDialog.OnCloseListener(){

            @Override
            public void onClick(CommomDialog dialog, boolean confirm,int type) {
                if (confirm){
                    String editName = dialog.contentTxt.getText().toString();
                    if (TextUtil.isEmpty(editName)){

                        ToastUtils.showToast(mContext,getResources().getString(R.string.project_selete_edit_node));
                        return;
                    }else{
                        viewDelegate.tvTaskName.setText(editName);
                        saveTaskLayoutData(null);
                    }
                    dialog.dismiss();
                }

            }
        });
        if (editDialog != null && !editDialog.isShowing()){
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
        } else if (id == R.id.ll_add_sub_task) {
            //添加子任务
           addSubTaskClick();

        } else if (id == R.id.ll_sub_task_control) {
            //显示切换子任务
            //subTaskClick();
            if (getEditAuth()){
                Bundle subBundle = new Bundle();
                subBundle.putLong(ProjectConstants.TASK_FROM_TYPE,2);
                subBundle.putLong(ProjectConstants.TASK_ID,taskId);
                subBundle.putString(ProjectConstants.PROJECT_CUSTOM_ID, projectCustomId);
                long taskType;
                if (fromType == 0){
                    taskType =1;
                }else {
                    taskType =2;
                }
                subBundle.putLong(ProjectConstants.PARRENT_TASK_FROM_TYPE, taskType);
                CommonUtil.startActivtiy(mContext, SubTaskCardListActivity.class, subBundle);
            }
        } else if (id == R.id.ll_relevance_control) {
            //显示切换关联
           // relevanceClick();
            Bundle relationBundle = new Bundle();
            relationBundle.putLong(ProjectConstants.PROJECT_RLATION_TYPE,3);
            relationBundle.putLong(ProjectConstants.TASK_ID,taskId);
            relationBundle.putLong(ProjectConstants.TASK_FROM_TYPE,fromType);
            CommonUtil.startActivtiy(mContext, RelationTaskCardListActivity.class, relationBundle);
        } else if (id == R.id.ll_be_relevance_control) {
            //显示切换被关联
            //beRelevanceClick();
            Bundle beRelationBundle = new Bundle();
            beRelationBundle.putLong(ProjectConstants.PROJECT_RLATION_TYPE,4);
            beRelationBundle.putLong(ProjectConstants.TASK_ID,taskId);
            beRelationBundle.putLong(ProjectConstants.TASK_FROM_TYPE,fromType);
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
            viewDelegate.get(R.id.fl_comment).setVisibility(View.VISIBLE);
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
            likeOrUnLike();
        } else if (id == R.id.tv_add_relevance) {
            //添加关联
            addRelevanceClick();
        } else if (id == R.id.iv_task_status) {
            //完成、激活任务
            updateStatusClick();
        }else if(id ==R.id.nomals_edit){
            if (getEditAuth()){
                bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PERSONAL_TASK_BEAN);
                bundle.putLong(ProjectConstants.TASK_ID, taskId);
                bundle.putSerializable(Constants.DATA_TAG1, (Serializable) detailMap);
                bundle.putString(ProjectConstants.ASSOCIATE_STATUS, associatesStatus);
                bundle.putString(ProjectConstants.PROJECT_CUSTOM_ID, projectCustomId);
                bundle.putString(ProjectConstants.RELATION_DATA, relationData);
                bundle.putString(ProjectConstants.RELATION_ID, relationId);
                CommonUtil.startActivtiyForResult(mContext, EditTaskActivity.class, ProjectConstants.EDIT_TASK_REQUEST_CODE, bundle);
            }
        }else if(id == R.id.picklist_status_li){
            fillStatus();
        }
    }

    /**
     * 处理数据
     */
    private void fillStatus() {
        String[] stateArr =new String[]{mContext.getResources().getString(R.string.project_no_start),mContext.getResources().getString(R.string.project_ongoing)
                ,mContext.getResources().getString(R.string.project_suspended),mContext.getResources().getString(R.string.project_complete)};
        statusEntrys.clear();
        for(int i=0;i<stateArr.length;i++){
            EntryBean enter = new EntryBean();
            enter.setLabel(stateArr[i]);
            enter.setValue(i+"");
            enter.setFromType(Constants.STATE_FROM_PROJECR);
            statusEntrys.add(enter);
        }
        Bundle bundle = new Bundle();
        ArrayList<EntryBean> clone = CloneUtils.clone(((ArrayList<EntryBean>) statusEntrys));
        if(!TextUtil.isEmpty(statudState)){
            for(EntryBean enter: clone){
                if (enter.getLabel().equals(statudState)){
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
     * 添加子任务点击
     */
    private void addSubTaskClick() {
        if (!checkRole()) return;
        Bundle bundle = new Bundle();
        bundle.putLong(ProjectConstants.TASK_ID, taskId);
        bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PERSONAL_TASK_BEAN);
        if (executeMember != null && !TextUtils.isEmpty(executeMember.getId() + "")) {
            bundle.putSerializable(ProjectConstants.EXECUTE_MEMBER, executeMember);
        }
        bundle.putString(ProjectConstants.PROJECT_CUSTOM_ID, projectCustomId);
        bundle.putString(ProjectConstants.RELATION_DATA, relationData);
        bundle.putString(ProjectConstants.RELATION_ID, relationId);
        CommonUtil.startActivtiyForResult(mContext, AddSubTaskActivity.class, ProjectConstants.ADD_SUB_TASK_REQUEST_CODE, bundle);
    }

    /**
     * 编辑权限
     * @return
     */
    public boolean getEditAuth(){
        boolean flag = false;
        if (!completeStatus && !isSuspend){//暂停和已完成不可编辑
            flag =true;
        }else{
            flag =false;
        }
        return  flag;
    }

    /**
     * 判断权限
     *
     * @return
     */
    private boolean checkRole() {
        if (taskRole == null) {
            ToastUtils.showError(mContext, "未获取到权限");
            return false;
        }
        if ("0".equals(taskRole) || "1".equals(taskRole)) {
            return true;
        } else {
            ToastUtils.showError(mContext, "权限不足");
            return false;
        }
    }

    /**
     * 添加关联点击
     */
    private void addRelevanceClick() {
        if (!checkRole()) return;
        PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), null, new String[]{"新建", "引用"}, p -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, Constants.SELECT_FOR_TASK);
            if (p == 0) {
                CommonUtil.startActivtiyForResult(mContext, SelectModuleActivity.class, ProjectConstants.ADD_TASK_REQUEST_CODE, bundle);
            } else {
                CommonUtil.startActivtiyForResult(mContext, SelectModuleActivity.class, ProjectConstants.QUOTE_TASK_REQUEST_CODE, bundle);
            }
            return false;
        });
    }

    /**
     * 完成、激活任务或子任务 点击
     */
    private void updateStatusClick() {
        if (!checkRole()) return;

        ProjectDialog.updatePersonalTaskStatus(fromType == 1, mContext, viewDelegate.getRootView(), taskId, completeStatus, new ProgressSubscriber<BaseBean>(mContext, 1) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                queryTaskDetail();
                getDynamicList();
                EventBusUtils.sendEvent(new MessageBean(ProjectConstants.PERSONAL_TASK_REFRESH_CODE, taskId + "", null));
            }
        });
    }

    /**
     * 完成、激活子任务
     */
    private void updateSubTaskStatus(long taskId, boolean completeStatus) {
        if (!checkRole()) return;
        ProjectDialog.updatePersonalTaskStatus(true, mContext, viewDelegate.getRootView(), taskId, completeStatus, new ProgressSubscriber<BaseBean>(mContext, 1) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                querySubList();
            }
        });
    }


    /**
     * 点赞
     */
    private void likeOrUnLike() {
        viewDelegate.setLikeStatus();
        viewDelegate.setLikeNum(shareArr.size() + (viewDelegate.isLike() ? 1 : -1));
        JSONObject jsonObject = new JSONObject();

        //（分享，任务，子任务）记录id
        jsonObject.put("task_id", taskId);
        // 0不点赞  1点赞
        jsonObject.put("status", viewDelegate.isLike() ? 1 : 0);
        //点赞类型，0 任务，1子任务
        jsonObject.put("from_type", fromType);
        model.sharePersonalPraise(this, jsonObject, new ProgressSubscriber<BaseBean>(mContext, false) {
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

    /**
     * 被关联任务
     */
    private void beRelevanceClick() {
        viewDelegate.switchBeRelevance();
    }

    /**
     * 关联任务
     */
    private void relevanceClick() {
        viewDelegate.switchRelevance();
    }

    /**
     * 子任务点击
     */
    private void subTaskClick() {
        viewDelegate.switchSubTask();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == Constants.REQUEST_CODE1) {//协作人
            queryPersonalTaskMembers();
        }else if (requestCode == Constants.REQUEST_CODE18) {//执行人
            //执行人员
            List<Member> members = (List<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            if (!CollectionUtils.isEmpty(members)){
                viewDelegate.executor_member_view.setMembers(members);
                saveTaskLayoutData(null);
            }
        } else if (requestCode == ProjectConstants.ADD_TASK_REQUEST_CODE) {
            //新增
            TaskNew(data);
        } else if (requestCode == ProjectConstants.ADD_TASK_TASK_REQUEST_CODE) {
            //新增任务
            AddRelevaceByNewTask(data);
        } else if (requestCode == CustomConstants.REQUEST_ADDCUSTOM_CODE) {
            //新增自定义模块
            AddRelevaceByNewCustom(data);
        } else if (requestCode == ProjectConstants.ADD_TASK_MEMO_REQUEST_CODE) {
            //新增备忘录
            AddRelevaceByNewMemo(data);
        } else if (requestCode == ProjectConstants.ADD_TASK_APPROVE_REQUEST_CODE) {
            //新增审批
            AddRelevaceByNewApprove(data);
        } else if (requestCode == ProjectConstants.QUOTE_TASK_REQUEST_CODE) {
            //引用
            taskQuote(data);
        } else if (requestCode == ProjectConstants.QUOTE_TASK_MEMO_REQUEST_CODE) {
            //引用备忘录
            quoteMemo(data);
        } else if (requestCode == ProjectConstants.QUOTE_TASK_APPROVE_REQUEST_CODE) {
            //引用审批
            quoteApprove(data);
        } else if (requestCode == ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE) {
            //引用任务
            quoteTask(data);
        } else if (requestCode == ProjectConstants.QUOTE_TASK_CUSTOM_REQUEST_CODE) {
            //引用自定义
            quoteCustom(data);
        } else if (requestCode == ProjectConstants.EDIT_TASK_REQUEST_CODE) {
            //编辑任务
            //queryTaskDetail();
            String newData = data.getStringExtra(Constants.DATA_TAG9);
            saveTaskLayoutData(newData);
        } else if (requestCode == ProjectConstants.ADD_SUB_TASK_REQUEST_CODE) {
            //新增子任务
            querySubList();
            queryPersonalTaskMembers();
        }else if(requestCode == Constants.REQUEST_CODE19){//状态编辑
            List<EntryBean> resultArr= (ArrayList<EntryBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            statudStateList = getStateValue(resultArr);
            remark = data.getStringExtra(Constants.DATA_TAG3);
            saveTaskLayoutData(null);
        }else{
            if (viewDelegate.attachmenView ==null || requestCode == viewDelegate.attachmenView.FILE_SELECT
                    || requestCode == viewDelegate.attachmenView.REQUEST_CODE_AT_ATTACHMENT || requestCode == PhotoPicker.REQUEST_CODE ){
                isAttachmentEdit =true;
            }else {
                saveTaskLayoutData(null);
            }
        }
        viewDelegate.mCommentInputView.onActivityResult(requestCode, resultCode, data);
    }

    public ArrayList<EntryBean> getStateValue(List<EntryBean> entrys) {
        ArrayList<EntryBean> entryList = new ArrayList();
        for (int i = 0; i < entrys.size(); i++) {
            if (entrys.get(i).isCheck()) {
                EntryBean map = new EntryBean();
                map.setValue( entrys.get(i).getValue());
                map.setLabel(entrys.get(i).getLabel());
                map.setColor(entrys.get(i).getColor());
                entryList.add(map);
            }
        }
        return entryList;
    }

    public void saveTaskLayoutData(String jsonStr){
        SavePersonalTaskLayoutRequestBean personalBean = new SavePersonalTaskLayoutRequestBean();
        JSONObject json = new JSONObject();
        boolean put = ProjectCustomUtil.put(mContext, viewDelegate.listView, json);
        if (!put) {
            return;
        }

        json.put("text_name",viewDelegate.tvTaskName.getText().toString());
        json.put("id", projectCustomId);
        //个人任务关联
        String relationData = "";
        String relationId = "";
        String fromStatus = "";
        if (!TextUtil.isEmpty(json.get("relation_id") + "") && !TextUtil.isEmpty(json.get("relation_data") + "")) {
            relationData = json.get("relation_data") + "";
            relationId = json.get("relation_id") + "";
            fromStatus = json.get("from_status")+"";
        }
        personalBean.setRelation_data(relationData);
        personalBean.setRelation_id(relationId);
        personalBean.setFrom_status(fromStatus);
        json.put("reference_relation", "");

        HashMap<String, Object> data = null;
        data = handleData(json,detailMap);

        if(!TextUtil.isEmpty(jsonStr)){
            JSONObject coustomData = JSONObject.parseObject(jsonStr);
            data = handleData(coustomData,data);
        }
        personalBean.setOldData(detailMap);
        if (data.get("attachment_customnumber") == null || TextUtil.isEmpty(data.get("attachment_customnumber").toString())){
                data.put("attachment_customnumber",new ArrayList<Object>());
        }
        data.put("personnel_principal",viewDelegate.getExecutorOneId());
        if (CollectionUtils.isEmpty(statudStateList)){
            statudStateList = new  ArrayList<EntryBean>();
            EntryBean bean = new EntryBean();
            bean.setLabel("未开始");
            bean.setColor("");
            bean.setValue("0");
            statudStateList.add(bean);
        }
        data.put("picklist_status",statudStateList);

        personalBean.setCustomLayout(data);
        personalBean.setBean_name(ProjectConstants.PERSONAL_TASK_BEAN);
        personalBean.setId(taskId);
        personalBean.setParticipants_only(viewDelegate.getOnlyParticipantStatus());
        personalBean.setName(json.getString(ProjectConstants.PROJECT_TASK_NAME));
        if (!TextUtil.isEmpty(remark)){
            personalBean.setRemark(remark);
        }
        if (fromType == 0){
            model.editPersonalTask(mContext, personalBean, new ProgressSubscriber<BaseBean>(mContext,false) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "编辑成功");
                    queryTaskDetail();
                }
            });
        }else {
            model.editPersonalTaskSub(mContext, personalBean, new ProgressSubscriber<BaseBean>(mContext,false) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    ToastUtils.showSuccess(mContext, "编辑成功");
                    queryTaskDetail();
                /*setResult(RESULT_OK);
                finish();*/
                }
            });
        }
    }

    public HashMap<String, Object> handleData(JSONObject newData,HashMap<String, Object> oldDta){

        HashMap<String, Object> data = (HashMap<String, Object>) oldDta.clone();
        Set<String> iterator = oldDta.keySet();
        Set<String> newIterator = newData.keySet();
        for (String key :iterator){
            for (String newKey :newIterator){
                if (key.equals(newKey)){
                    data.put(newKey,newData.get(newKey));
                }
            }
        }
        return data;
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
                //任务
                bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PERSONAL_TASK_BEAN);
                CommonUtil.startActivtiyForResult(mContext, AddTaskActivity.class, ProjectConstants.ADD_TASK_TASK_REQUEST_CODE, bundle);
                break;
            case MemoConstant.BEAN_NAME:
                //备忘录
                UIRouter.getInstance().openUri(mContext, "DDComp://memo/add", bundle, ProjectConstants.ADD_TASK_MEMO_REQUEST_CODE);
                break;
            default:
                if (ApproveConstants.APPROVAL_MODULE_BEAN.equals(appModeluBean.getApplication_id())) {
                    //审批下面的模块
                    bundle.putString(Constants.MODULE_BEAN, moduleBean);
                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/add", bundle, ProjectConstants.ADD_TASK_APPROVE_REQUEST_CODE);
                } else {
                    //自定义
                    bundle.putString(Constants.MODULE_BEAN, moduleBean);
                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/add", bundle, CustomConstants.REQUEST_ADDCUSTOM_CODE);
                }
                break;
        }
    }

    /**
     * 通过新增任务添加关联
     *
     * @param data
     */
    private void AddRelevaceByNewTask(Intent data) {
        long id = data.getLongExtra(Constants.DATA_TAG1, 0);
        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();

        bean.setBean_name(ProjectConstants.PERSONAL_TASK_BEAN);
        bean.setRelation_id(id + "");
        bean.setTask_id(taskId);
        bean.setFrom_type(fromType);
        addRelevance(bean);
    }

    /**
     * 通过新增自定义添加关联
     *
     * @param data
     */
    private void AddRelevaceByNewCustom(Intent data) {
        ModuleBean.DataBean moduleBean = (ModuleBean.DataBean) data.getSerializableExtra(Constants.DATA_TAG1);
        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();

        bean.setBean_name(moduleBean.getBean());
        bean.setRelation_id(moduleBean.getDataId() + "");
        bean.setModule_name(moduleBean.getModuleName());
        bean.setModule_id(moduleBean.getModuleId());
        bean.setTask_id(taskId);
        bean.setFrom_type(fromType);
        addRelevance(bean);
    }

    /**
     * 通过新增备忘录添加关联
     *
     * @param data
     */
    private void AddRelevaceByNewMemo(Intent data) {
        long dataId = data.getLongExtra(Constants.DATA_TAG1, 0);
        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();
        bean.setBean_name(MemoConstant.BEAN_NAME);
        bean.setRelation_id(dataId + "");
        bean.setTask_id(taskId);
        bean.setFrom_type(fromType);

        addRelevance(bean);
    }

    /**
     * 通过新增审批添加关联
     *
     * @param data
     */
    private void AddRelevaceByNewApprove(Intent data) {
        ModuleBean.DataBean moduleBean = (ModuleBean.DataBean) data.getSerializableExtra(Constants.DATA_TAG1);
        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();

        bean.setBean_name(moduleBean.getBean());
        bean.setRelation_id(moduleBean.getDataId() + "");
        bean.setModule_name(moduleBean.getModuleName());
        bean.setModule_id(moduleBean.getModuleId());
        bean.setTask_id(taskId);
        bean.setFrom_type(fromType);
        addRelevance(bean);
    }

    /**
     * 任务引用
     *
     * @param data
     */
    private void taskQuote(Intent data) {
        AppModuleBean appModeluBean = (AppModuleBean) data.getSerializableExtra(Constants.DATA_TAG1);

        String moduleBean = appModeluBean.getEnglish_name();

        Bundle bundle = new Bundle();
        switch (moduleBean) {
            case ProjectConstants.TASK_MODULE_BEAN:
                //任务
                CommonUtil.startActivtiyForResult(mContext, QuoteTaskActivity.class, ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE, bundle);
                break;
            case MemoConstant.BEAN_NAME:
                UIRouter.getInstance().openUri(mContext, "DDComp://memo/choose_memo", bundle, ProjectConstants.QUOTE_TASK_MEMO_REQUEST_CODE);
                break;
            default:
                if (ApproveConstants.APPROVAL_MODULE_BEAN.equals(appModeluBean.getApplication_id())) {
                    //审批下面的模块
                    bundle.putString(Constants.DATA_TAG1, moduleBean);
                    bundle.putString(Constants.DATA_TAG2, appModeluBean.getChinese_name());
                    bundle.putString(Constants.DATA_TAG3, appModeluBean.getId());
                    UIRouter.getInstance().openUri(mContext, "DDComp://app/approve/select", bundle, ProjectConstants.QUOTE_TASK_APPROVE_REQUEST_CODE);
                } else {
                    //自定义模块
                    bundle.putString(Constants.MODULE_BEAN, moduleBean);
                    bundle.putString(Constants.MODULE_ID, appModeluBean.getId());
                    bundle.putString(Constants.NAME, appModeluBean.getChinese_name());
                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/select", bundle, ProjectConstants.QUOTE_TASK_CUSTOM_REQUEST_CODE);
                }
                break;
        }
    }

    /**
     * 引用备忘录
     *
     * @param data
     */
    private void quoteMemo(Intent data) {
        ArrayList<MemoListItemBean> choosedItem = (ArrayList<MemoListItemBean>) data.getSerializableExtra(Constants.DATA_TAG1);
        if (CollectionUtils.isEmpty(choosedItem)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (MemoListItemBean memo : choosedItem) {
            sb.append("," + memo.getId());
        }
        sb.deleteCharAt(0);

        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();
        bean.setBean_name(MemoConstant.BEAN_NAME);
        bean.setRelation_id(sb.toString());
        bean.setTask_id(taskId);
        bean.setFrom_type(fromType);

        addRelevance(bean);
    }

    /**
     * 引用审批
     *
     * @param data
     */
    private void quoteApprove(Intent data) {
        ArrayList<ApproveListBean> datas = (ArrayList<ApproveListBean>) data.getSerializableExtra(Constants.DATA_TAG1);
        String moduleName = data.getStringExtra(Constants.DATA_TAG2);
        String moduleId = data.getStringExtra(Constants.DATA_TAG3);
        String moduleBean = data.getStringExtra(Constants.DATA_TAG4);
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (ApproveListBean approve : datas) {
            sb.append("," + approve.getApproval_data_id());
        }
        sb.deleteCharAt(0);

        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();
        bean.setBean_name(moduleBean);
        bean.setModule_id(moduleId);
        bean.setModule_name(moduleName);
        bean.setRelation_id(sb.toString());
        bean.setTask_id(taskId);
        bean.setFrom_type(fromType);

        addRelevance(bean);
    }

    /**
     * 引用任务
     *
     * @param data
     */
    private void quoteTask(Intent data) {
        String ids = data.getStringExtra(Constants.DATA_TAG1);
        String beanName = data.getStringExtra(Constants.DATA_TAG2);

        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();
        bean.setBean_name(beanName);
        bean.setRelation_id(ids);
        bean.setTask_id(taskId);
        bean.setFrom_type(fromType);

        addRelevance(bean);
    }

    /**
     * 引用自定义
     *
     * @param data
     */
    private void quoteCustom(Intent data) {
        String moduleBean = data.getStringExtra(Constants.MODULE_BEAN);
        ArrayList<DataTempResultBean.DataBean.DataListBean> datas = (ArrayList<DataTempResultBean.DataBean.DataListBean>) data.getSerializableExtra(Constants.DATA_TAG1);
        String moduleName = data.getStringExtra(Constants.NAME);
        String moduleId = data.getStringExtra(Constants.MODULE_ID);
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (DataTempResultBean.DataBean.DataListBean dataListBean : datas) {
            sb.append("," + dataListBean.getId().getValue());
        }
        sb.deleteCharAt(0);

        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();
        bean.setBean_name(moduleBean);
        bean.setModule_id(moduleId);
        bean.setModule_name(moduleName);
        bean.setRelation_id(sb.toString());
        bean.setTask_id(taskId);
        bean.setFrom_type(fromType);

        addRelevance(bean);
    }

    /**
     * 通过新增 添加关联
     *
     * @param bean
     */
    private void addRelevance(SavePersonalRelationRequestBean bean) {
        model.addRelevanceByPersonal(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mContext, "添加成功!");
                queryRelationList();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuClick();
        return super.onOptionsItemSelected(item);
    }

    private void menuClick() {
        List<String> menuList = new ArrayList<>();
        if (!completeStatus && ("0".equals(taskRole) || "1".equals(taskRole))) {
           // menuList.add("编辑任务");
        }
        if ("0".equals(taskRole) || "1".equals(taskRole)) {
            menuList.add("设置任务提醒");
        }
        if ("0".equals(taskRole) || "1".equals(taskRole)) {
            menuList.add("设置重复任务");
        }
        if (!completeStatus && ("0".equals(taskRole) || "1".equals(taskRole))) {
            menuList.add("删除任务");
        }
        if (CollectionUtils.isEmpty(menuList)) {
            return;
        }
        String[] menus = menuList.toArray(new String[menuList.size()]);
        PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), null, menus, p -> {
            Bundle bundle = new Bundle();
            switch (p) {
                /*case 0:
                    bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PERSONAL_TASK_BEAN);
                    bundle.putLong(ProjectConstants.TASK_ID, taskId);
                    bundle.putSerializable(Constants.DATA_TAG1, (Serializable) detailMap);
                    bundle.putString(ProjectConstants.ASSOCIATE_STATUS, associatesStatus);
                    bundle.putString(ProjectConstants.PROJECT_CUSTOM_ID, projectCustomId);
                    bundle.putString(ProjectConstants.RELATION_DATA, relationData);
                    bundle.putString(ProjectConstants.RELATION_ID, relationId);
                    CommonUtil.startActivtiyForResult(mContext, EditTaskActivity.class, ProjectConstants.EDIT_TASK_REQUEST_CODE, bundle);
                    break;*/
                case 0:
                    bundle.putLong(ProjectConstants.TASK_ID, taskId);
                    bundle.putLong(ProjectConstants.TASK_FROM_TYPE, fromType);
                    CommonUtil.startActivtiy(mContext, PersonalTaskRemindActivity.class, bundle);
                    break;
                case 1:
                    bundle.putLong(ProjectConstants.TASK_ID, taskId);
                    CommonUtil.startActivtiy(mContext, PersonalTaskRepeatActivity.class, bundle);
                    break;
                case 2:
                    DialogUtils.getInstance().sureOrCancel(mContext, "删除任务后，所有子任务也将同时被删除。", null, viewDelegate.getRootView(), () -> {
                        model.delPersonalTask(mContext, taskId + "", new ProgressSubscriber<BaseBean>(mContext) {
                            @Override
                            public void onNext(BaseBean baseBean) {
                                super.onNext(baseBean);
                                ToastUtils.showSuccess(mContext, "删除成功");
                                EventBusUtils.sendEvent(new MessageBean(ProjectConstants.PERSONAL_TASK_REFRESH_CODE, null, null));
                                finish();
                            }
                        });
                    });
                    break;
                default:
                    break;
            }
            return false;
        });
    }

}

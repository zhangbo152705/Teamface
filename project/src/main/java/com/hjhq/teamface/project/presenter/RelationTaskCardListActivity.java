package com.hjhq.teamface.project.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.ApproveListBean;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CommonNewResultBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.MemoListItemBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ModuleBean;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.ProjectPicklistStatusBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CloneUtils;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.utils.TaskHelper;
import com.hjhq.teamface.common.view.boardview.DragItemAdapter;
import com.hjhq.teamface.customcomponent.widget2.select.PickListViewSelectActivity;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.RelationTaskAdapter;
import com.hjhq.teamface.project.adapter.TaskCardApter;
import com.hjhq.teamface.project.bean.PersonalTaskDeatilListBean;
import com.hjhq.teamface.project.bean.PersonalTaskDetailResultBean;
import com.hjhq.teamface.project.bean.QueryTaskDetailResultBean;
import com.hjhq.teamface.project.bean.QuoteRelationRequestBean;
import com.hjhq.teamface.project.bean.RelationListResultBean;
import com.hjhq.teamface.project.bean.SavePersonalRelationRequestBean;
import com.hjhq.teamface.project.bean.SavePersonalTaskLayoutRequestBean;
import com.hjhq.teamface.project.bean.SaveRelationRequestBean;
import com.hjhq.teamface.project.bean.SaveTaskLayoutRequestBean;
import com.hjhq.teamface.project.bean.TaskCardBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.presenter.task.AddTaskActivity;
import com.hjhq.teamface.project.presenter.task.PersonalTaskDetailActivity;
import com.hjhq.teamface.project.presenter.task.QuoteTaskActivity;
import com.hjhq.teamface.project.presenter.task.SelectTaskActivity;
import com.hjhq.teamface.project.presenter.task.TaskDetailActivity;
import com.hjhq.teamface.project.ui.RelationCardDelegate;
import com.hjhq.teamface.project.ui.TaskCardDelegate;
import com.hjhq.teamface.project.view.CommomDialog;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.iwf.photopicker.PhotoPicker;

/**
 * 关联列表
 * @author ZZH
 * @date 2018/4/10
 */
public class RelationTaskCardListActivity extends ActivityPresenter<RelationCardDelegate, TaskModel> implements View.OnClickListener {

    private String title;
    private RelationTaskAdapter relationItemAdapter;
    private long taskId;
    private long taskType;//项目任务类型 1 任务 2子任务  //个人任务 0主任务 子任务
    private long fromType; //1项目任务关联任务 ,2项目任务被关联任务,3个人任务关联任务 4个人任务被关联任务
    private long projectId;
    private long subNodeId;
    private String taskName;
    private boolean isBeRelation =false;
    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);

        if(savedInstanceState == null){
            Intent intent = getIntent();
            if(intent != null){
                title = intent.getStringExtra(ProjectConstants.PROJECT_PERSION_TASK_CARD_TITLE);
                taskId = intent.getLongExtra(ProjectConstants.TASK_ID, 0);
                taskType = intent.getLongExtra(ProjectConstants.TASK_FROM_TYPE, 0);
                fromType = intent.getLongExtra(ProjectConstants.PROJECT_RLATION_TYPE, 1);
                projectId = intent.getLongExtra(ProjectConstants.PROJECT_ID, 0);
                subNodeId = intent.getLongExtra(ProjectConstants.SUBNODE_ID, 0);
                taskName = intent.getStringExtra(ProjectConstants.TASK_NAME);
                isBeRelation = intent.getBooleanExtra(ProjectConstants.PROJECT_IS_BE_RLATION_TYPE,false);
            }
        }
    }

    @Override
    public void init() {
        if (fromType ==1 || fromType==3){
            viewDelegate.setTitle(getResources().getString(R.string.project_relation));
        }else if (fromType ==2 || fromType==4){
            viewDelegate.setTitle(getResources().getString(R.string.project_berelation));
        }
        relationItemAdapter = new RelationTaskAdapter();
        viewDelegate.setAdapter(relationItemAdapter);
        relationItemAdapter.setBeRelation(isBeRelation);
        if (isBeRelation){
            viewDelegate.ivAdd.setVisibility(View.INVISIBLE);
        }

        initData();
    }

    /***
     * 加载网络数据
     */
    public void initData(){
        if (fromType == 1){
            queryRelationList();
        }else if (fromType ==2){
            queryByRelationList();
        }else if (fromType ==3){
            queryPerdionRelationList();
        }else if (fromType == 4){
            queryPersonalByRelations();
        }
    }

    /**
     * 获取项目任务被关联列表
     */
    private void queryByRelationList() {
        model.queryByRelationList(this, taskId, new ProgressSubscriber<RelationListResultBean>(mContext) {
            @Override
            public void onNext(RelationListResultBean baseBean) {
                super.onNext(baseBean);
                //被关联
                List<RelationListResultBean.DataBean.ModuleDataBean> relationArr2 = baseBean.getData().getDataList();
                CollectionUtils.notifyDataSetChanged(relationItemAdapter, relationItemAdapter.getData(), relationArr2);
            }
        });
    }

    /**
     * 获取项目关联任务
     */
    private void queryRelationList() {
        model.queryRelationList(this, taskId, taskType, new ProgressSubscriber<RelationListResultBean>(mContext) {
            @Override
            public void onNext(RelationListResultBean baseBean) {
                super.onNext(baseBean);
                //关联任务
                List<RelationListResultBean.DataBean.ModuleDataBean> relationArr = baseBean.getData().getDataList();
                CollectionUtils.notifyDataSetChanged(relationItemAdapter, relationItemAdapter.getData(), relationArr);
            }
        });
    }

    /**
     * 获取个人关联任务
     */
    private void queryPerdionRelationList() {
        model.queryPersonalRelations(this, taskId,(int)taskType, new ProgressSubscriber<RelationListResultBean>(mContext) {
            @Override
            public void onNext(RelationListResultBean baseBean) {
                super.onNext(baseBean);
                //关联任务
                List<RelationListResultBean.DataBean.ModuleDataBean> relationArr = baseBean.getData().getDataList();
                CollectionUtils.notifyDataSetChanged(relationItemAdapter, relationItemAdapter.getData(), relationArr);
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
                CollectionUtils.notifyDataSetChanged(relationItemAdapter, relationItemAdapter.getData(), relationArr2);
            }
        });
    }







    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.rl_back, R.id.iv_add_bar);
        viewDelegate.recycler_view.addOnItemTouchListener(new SimpleItemClickListener(){


            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                RelationListResultBean.DataBean.ModuleDataBean itemBeam = (RelationListResultBean.DataBean.ModuleDataBean) adapter.getData().get(position);
                TaskInfoBean item =  itemBeam.getModuleDataList().get(0);
                String beanName = item.getBean_name();
                int dataType = item.getDataType();
                Bundle bundle = new Bundle();

                if (view.getId() == R.id.ll_relation) {//引用
                    if (dataType == 1) {//备忘录
                        UIRouter.getInstance().openUri(mContext, "DDComp://memo/choose_memo", bundle, ProjectConstants.QUOTE_TASK_MEMO_REQUEST_CODE);
                    } else if (dataType == 2) {//任务
                        bundle.putString(ProjectConstants.PROJECT_ID, item.getProject_id() + "");
                        bundle.putString(Constants.DATA_TAG1, item.getTitle());
                        CommonUtil.startActivtiyForResult(mContext, SelectTaskActivity.class, ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE, bundle);
                    } else if (dataType == 4) {//审批
                        bundle.putString(Constants.DATA_TAG1, beanName);
                        bundle.putString(Constants.DATA_TAG2, item.getModule_name());
                        bundle.putString(Constants.DATA_TAG3, item.getModule_id()+"");
                        UIRouter.getInstance().openUri(mContext, "DDComp://app/approve/select", bundle, ProjectConstants.QUOTE_TASK_APPROVE_REQUEST_CODE);
                    } else if (dataType == 3) {//自定义
                        bundle.putString(Constants.MODULE_BEAN, beanName);
                        bundle.putString(Constants.MODULE_ID, item.getModule_id()+"");
                        bundle.putString(Constants.NAME, item.getModule_name());
                        UIRouter.getInstance().openUri(mContext, "DDComp://custom/select", bundle, ProjectConstants.QUOTE_TASK_CUSTOM_REQUEST_CODE);
                    }

                } else if (view.getId() == R.id.ll_add) {//新建
                    if (dataType == 1) {//备忘录
                        UIRouter.getInstance().openUri(mContext, "DDComp://memo/add", bundle, ProjectConstants.ADD_TASK_MEMO_REQUEST_CODE);
                    } else if (dataType == 2) {//任务

                    } else if (dataType == 4) {//审批
                        bundle.putString(Constants.MODULE_BEAN, beanName);
                        UIRouter.getInstance().openUri(mContext, "DDComp://custom/add", bundle, ProjectConstants.ADD_TASK_APPROVE_REQUEST_CODE);
                    } else if (dataType == 3) {//自定义
                        bundle.putString(Constants.MODULE_BEAN, beanName);
                        UIRouter.getInstance().openUri(mContext, "DDComp://custom/add", bundle, CustomConstants.REQUEST_ADDCUSTOM_CODE);
                    }
                }
            }
        });



        relationItemAdapter.setOnItemClickListener(new TaskItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DragItemAdapter adapter, View view, int position) {
                TaskInfoBean item = (TaskInfoBean) adapter.getItemList().get(position);
                queryAuth(item);
            }

            @Override
            public void onItemChildClick(DragItemAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemLongClick(DragItemAdapter adapter, View view, int position) {
                if (fromType == 1) {
                     /* if (!checkTaskPermission(13)) {
                    ToastUtils.showError(mContext, "没有权限");
                    return;
                     }*/
                    DialogUtils.getInstance().sureOrCancel(mContext, "确定取消该关联任务吗？", null, view.getRootView(), () -> {
                        TaskInfoBean item = (TaskInfoBean) adapter.getItemList().get(position);
                        new TaskModel().cancleRelation(mContext, item.getBean_id(), new ProgressSubscriber<BaseBean>(mContext) {
                            @Override
                            public void onNext(BaseBean baseBean) {
                                super.onNext(baseBean);
                                initData();
                                ToastUtils.showSuccess(mContext, "取消成功");
                            }
                        });
                    });
                }else if (fromType == 3){
                    //if (!checkRole()) return;
                    //取消关联
                    DialogUtils.getInstance().sureOrCancel(mContext, "确定取消该关联任务吗？", null, view.getRootView(), () -> {
                        TaskInfoBean item = (TaskInfoBean) adapter.getItemList().get(position);
                        //个人任务
                        new TaskModel().canclePersonalRelation(mContext, item.getId(), taskId, taskType, new ProgressSubscriber<BaseBean>(mContext) {
                            @Override
                            public void onNext(BaseBean baseBean) {
                                super.onNext(baseBean);
                                queryRelationList();
                                ToastUtils.showSuccess(mContext, "取消成功");
                            }
                        });
                    });
                }

            }
        });

        //刷新
        viewDelegate.swipe_refresh_layout.setOnRefreshListener(() -> {
            initData();
            viewDelegate.swipe_refresh_layout.setRefreshing(false);
        });

    }

    /**
     * 验证权限后查看关联

     */
    public void queryAuth(TaskInfoBean item) {
        Map<String,Object> remap =new HashMap<>();
        String remapStr = "";
        if(item.getDataType() == 2){
            remap.put("data_Type",item.getDataType());
            remap.put("taskInfoId",item.getBean_id());
            remap.put("beanName",item.getBean_name());
            remap.put("taskName",item.getTask_name());
            remap.put("id",item.getBean_id());
            remap.put("projectId",item.getProject_id());
            remapStr = JSON.toJSONString(remap);
        }
        model.queryAuth(mContext, item.getBean_name(), "", item.getBean_id()+"",remapStr,
                new ProgressSubscriber<ViewDataAuthResBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtils.showError(mContext, "查询权限错误");
                    }

                    @Override
                    public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                        super.onNext(viewDataAuthResBean);
                        if (viewDataAuthResBean.getData() != null && !TextUtils.isEmpty(viewDataAuthResBean.getData().getReadAuth())) {
                            switch (viewDataAuthResBean.getData().getReadAuth()) {
                                case "0":
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case "1":
                                    TaskHelper.INSTANCE.clickTaskItem(mContext, item);
                                    break;
                                case "2":
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case "3":
                                   // ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    TaskHelper.INSTANCE.clickTaskItem(mContext, item);
                                    break;
                                case "4":
                                    // ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    TaskHelper.INSTANCE.clickTaskItem(mContext, item);
                                    break;
                                case "5":
                                    // ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    TaskHelper.INSTANCE.clickTaskItem(mContext, item);
                                    break;
                            }
                        } else {
                            ToastUtils.showError(mContext, "查询权限错误");
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_back) {
            finish();
        }else if(id == R.id.iv_add_bar){
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, Constants.SELECT_FOR_TASK);
            CommonUtil.startActivtiyForResult(mContext, SelectModuleActivity.class, ProjectConstants.QUOTE_TASK_REQUEST_CODE, bundle);
        }
    }

    /**
     * 添加关联点击
     */
    private void addRelevanceClick() {
        /*if (!checkTaskPermission(12)) {
            ToastUtils.showError(mContext, "没有权限");
            return;
        }*/
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
     * 个人任务引用
     *
     * @param data
     */
    private void persionTaskQuote(Intent data) {
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
     * 项目任务引用
     * @param data
     */
    private void taskQuote(Intent data) {
        AppModuleBean appModeluBean = (AppModuleBean) data.getSerializableExtra(Constants.DATA_TAG1);

        String moduleBean = appModeluBean.getEnglish_name();

        Bundle bundle = new Bundle();
        switch (moduleBean) {
            case ProjectConstants.TASK_MODULE_BEAN:
                bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                CommonUtil.startActivtiyForResult(mContext, QuoteTaskActivity.class, ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE, bundle);
                break;
            case MemoConstant.BEAN_NAME:
                bundle.putInt(ProjectConstants.TASK_FROM_TYPE,1);
                UIRouter.getInstance().openUri(mContext, "DDComp://memo/choose_memo", bundle, ProjectConstants.QUOTE_TASK_MEMO_REQUEST_CODE);
                break;
            default:
                if (ApproveConstants.APPROVAL_MODULE_BEAN.equals(appModeluBean.getApplication_id())) {
                    bundle.putString(Constants.DATA_TAG1, moduleBean);
                    bundle.putString(Constants.DATA_TAG2, appModeluBean.getChinese_name());
                    bundle.putString(Constants.DATA_TAG3, appModeluBean.getId());
                    bundle.putInt(ProjectConstants.TASK_FROM_TYPE,1);
                    UIRouter.getInstance().openUri(mContext, "DDComp://app/approve/select", bundle, ProjectConstants.QUOTE_TASK_APPROVE_REQUEST_CODE);
                } else {
                    bundle.putString(Constants.MODULE_BEAN, moduleBean);
                    bundle.putString(Constants.MODULE_ID, appModeluBean.getId());
                    bundle.putString(Constants.NAME, appModeluBean.getChinese_name());
                    bundle.putInt(ProjectConstants.TASK_FROM_TYPE,1);
                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/select", bundle, ProjectConstants.QUOTE_TASK_CUSTOM_REQUEST_CODE);
                }
                break;
        }
    }

    /**
     * 个人任务新增
     *
     * @param data
     */
    private void persionTaskNew(Intent data) {
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
     * 项目任务新增
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

    /**
     * 个人任务通过新增任务添加关联
     * @param data
     */
    private void persionAddRelevaceByNewTask(Intent data) {
        long id = data.getLongExtra(Constants.DATA_TAG1, 0);
        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();

        bean.setBean_name(ProjectConstants.PERSONAL_TASK_BEAN);
        bean.setRelation_id(id + "");
        bean.setTask_id(taskId);
        bean.setFrom_type((int) taskType);
        bean.setBeanType("2");
        addRelevance(bean);
    }

    /**
     * 项目任务通过新增任务添加关联
     *
     * @param data
     */
    private void AddRelevaceByNewTask(Intent data) {
        long id = data.getLongExtra(Constants.DATA_TAG1, 0);
        SaveRelationRequestBean bean = new SaveRelationRequestBean();
        bean.setProjectId(projectId);
        bean.setBean(ProjectConstants.PROJECT_TASK_MOBULE_BEAN + projectId);
        bean.setDataId(id);
        bean.setSubnodeId(subNodeId);
        bean.setCheckMember(data.getStringExtra(Constants.DATA_TAG3));
        bean.setCheckStatus(data.getStringExtra(Constants.DATA_TAG2));
        bean.setAssociatesStatus(data.getStringExtra(Constants.DATA_TAG4));
        bean.setEndTime(data.getStringExtra(Constants.DATA_TAG5));
        bean.setExecutorId(data.getStringExtra(Constants.DATA_TAG6));
        bean.setStartTime(data.getStringExtra(Constants.DATA_TAG7));
        bean.setTaskName(data.getStringExtra(Constants.DATA_TAG8));
        bean.setTaskId(taskId);
        bean.setTaskType(taskType);
        bean.setBean_type("2");

        addRelevanceByNew(bean);
    }


    /**
     * 个人任务通过新增自定义添加关联
     *
     * @param data
     */
    private void persionAddRelevaceByNewCustom(Intent data) {
        ModuleBean.DataBean moduleBean = (ModuleBean.DataBean) data.getSerializableExtra(Constants.DATA_TAG1);
        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();

        bean.setBean_name(moduleBean.getBean());
        bean.setRelation_id(moduleBean.getDataId() + "");
        bean.setModule_name(moduleBean.getModuleName());
        bean.setModule_id(moduleBean.getModuleId());
        bean.setTask_id(taskId);
        bean.setFrom_type((int)taskType);
        bean.setBeanType("3");
        addRelevance(bean);
    }

    /**
     * 项目任务通过新增自定义添加关联
     *
     * @param data
     */
    private void AddRelevaceByNewCustom(Intent data) {
        ModuleBean.DataBean moduleBean = (ModuleBean.DataBean) data.getSerializableExtra(Constants.DATA_TAG1);
        SaveRelationRequestBean bean = new SaveRelationRequestBean();
        bean.setProjectId(projectId);
        bean.setBean(moduleBean.getBean());
        bean.setDataId(moduleBean.getDataId());
        bean.setSubnodeId(subNodeId);
        bean.setModuleId(moduleBean.getModuleId());
        bean.setModuleName(moduleBean.getModuleName());
        bean.setTaskId(taskId);
        bean.setTaskType(taskType);
        bean.setTaskName(taskName);
        bean.setBean_type("3");

        addRelevanceByNew(bean);
    }

    /**
     * 个人任务通过新增备忘录添加关联
     *
     * @param data
     */
    private void persionAddRelevaceByNewMemo(Intent data) {
        long dataId = data.getLongExtra(Constants.DATA_TAG1, 0);
        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();
        bean.setBean_name(MemoConstant.BEAN_NAME);
        bean.setRelation_id(dataId + "");
        bean.setTask_id(taskId);
        bean.setFrom_type((int)taskType);
        bean.setBeanType("1");
        addRelevance(bean);
    }
    /**
     * 项目任务通过新增备忘录添加关联
     * @param data
     */
    private void AddRelevaceByNewMemo(Intent data) {
        long dataId = data.getLongExtra(Constants.DATA_TAG1, 0);
        SaveRelationRequestBean bean = new SaveRelationRequestBean();
        bean.setProjectId(projectId);
        bean.setBean(MemoConstant.BEAN_NAME);
        bean.setDataId(dataId);
        bean.setSubnodeId(subNodeId);
        bean.setTaskId(taskId);
        bean.setTaskType(taskType);
        bean.setTaskName(taskName);
        bean.setBean_type("1");

        addRelevanceByNew(bean);
    }
    /**
     * 个人任务通过新增审批添加关联
     *
     * @param data
     */
    private void persionAddRelevaceByNewApprove(Intent data) {
        ModuleBean.DataBean moduleBean = (ModuleBean.DataBean) data.getSerializableExtra(Constants.DATA_TAG1);
        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();

        bean.setBean_name(moduleBean.getBean());
        bean.setRelation_id(moduleBean.getDataId() + "");
        bean.setModule_name(moduleBean.getModuleName());
        bean.setModule_id(moduleBean.getModuleId());
        bean.setTask_id(taskId);
        bean.setFrom_type((int)taskType);
        bean.setBeanType("4");
        addRelevance(bean);
    }


    /**
     * 项目任务 通过新增审批添加关联
     *
     * @param data
     */
    private void AddRelevaceByNewApprove(Intent data) {
        ModuleBean.DataBean moduleBean = (ModuleBean.DataBean) data.getSerializableExtra(Constants.DATA_TAG1);
        SaveRelationRequestBean bean = new SaveRelationRequestBean();
        bean.setProjectId(projectId);
        bean.setBean(moduleBean.getBean());
        bean.setDataId(moduleBean.getDataId());
        bean.setSubnodeId(subNodeId);
        bean.setModuleId(moduleBean.getModuleId());
        bean.setModuleName(moduleBean.getModuleName());
        bean.setTaskId(taskId);
        bean.setTaskType(taskType);
        bean.setTaskName(taskName);
        bean.setBean_type("4");

        addRelevanceByNew(bean);
    }


    /**
     * 个人任务引用备忘录
     *
     * @param data
     */
    private void persionquoteMemo(Intent data) {
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
        bean.setFrom_type((int)taskType);
        bean.setBeanType("1");

        addRelevance(bean);
    }

    /**
     * 项目任务 引用备忘录
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

        QuoteRelationRequestBean bean = new QuoteRelationRequestBean();
        bean.setBean(MemoConstant.BEAN_NAME);
        bean.setProjectId(projectId);
        bean.setBean_type(1);
        bean.setQuoteTaskId(sb.toString());
        bean.setTaskId(taskId);
        bean.setTaskType(taskType);

        addRelevanceByQuote(bean);
    }

    /**
     * 个人任务引用审批
     *
     * @param data
     */
    private void persionquoteApprove(Intent data) {
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
        bean.setFrom_type((int)taskType);
        bean.setBeanType("4");
        addRelevance(bean);
    }

    /**
     * 项目任务 引用审批
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

        QuoteRelationRequestBean bean = new QuoteRelationRequestBean();
        bean.setBean(moduleBean);
        bean.setModuleId(moduleId);
        bean.setModuleName(moduleName);
        bean.setProjectId(projectId);
        bean.setBean_type(4);
        bean.setQuoteTaskId(sb.toString());
        bean.setTaskId(taskId);
        bean.setTaskType(taskType);

        addRelevanceByQuote(bean);
    }

    /**
     * 个人任务引用项目任务
     *
     * @param data
     */
    private void persionquoteTask(Intent data) {
        String ids = data.getStringExtra(Constants.DATA_TAG1);
        String beanName = data.getStringExtra(Constants.DATA_TAG2);
        int seleteTaskType = data.getIntExtra(Constants.DATA_TAG3,0);//zzh:所选人物类型:1个人任务 0:项目任务
        String bean_id = "";
        if (!TextUtil.isEmpty(beanName) && beanName.length()>15){
            bean_id = beanName.substring(15,beanName.length());
        }
        SavePersonalRelationRequestBean bean = new SavePersonalRelationRequestBean();
        bean.setBean_name("project_custom");
        bean.setProjectId(bean_id);
        bean.setRelation_id(ids);
        bean.setTask_id(taskId);
        bean.setFrom_type((int)taskType);
        if (seleteTaskType ==1){
            bean.setBeanType("5");
        }else {
            bean.setBeanType("2");
        }

        addRelevance(bean);
    }
    /**
     * 项目任务引用项目任务
     *
     * @param data
     */
    private void quoteTask(Intent data) {
        String ids = data.getStringExtra(Constants.DATA_TAG1);
        String beanName = data.getStringExtra(Constants.DATA_TAG2);
        int seleteTaskType = data.getIntExtra(Constants.DATA_TAG3,0);//zzh:所选人物类型:1个人任务 0:项目任务
        QuoteRelationRequestBean bean = new QuoteRelationRequestBean();
        bean.setBean(beanName);
        bean.setProjectId(projectId);
        if (seleteTaskType == 1){//个人任务
            bean.setBean_type(5);
        }else {//项目任务
            bean.setBean_type(2);
        }
        bean.setQuoteTaskId(ids);
        bean.setTaskId(taskId);
        bean.setTaskType(taskType);

        addRelevanceByQuote(bean);
    }

    /**
     * 个人任务引用自定义
     *
     * @param data
     */
    private void persionquoteCustom(Intent data) {
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
        bean.setFrom_type((int)taskType);
        bean.setBeanType("3");

        addRelevance(bean);
    }

    /**
     * 项目引用自定义
     *
     * @param data
     */
    private void quoteCustom(Intent data) {
        String moduleBean = data.getStringExtra(Constants.MODULE_BEAN);
        ArrayList<DataTempResultBean.DataBean.DataListBean> datas = (ArrayList<DataTempResultBean.DataBean.DataListBean>) data.getSerializableExtra(Constants.DATA_TAG1);
        String moduleId = data.getStringExtra(Constants.MODULE_ID);
        String moduleName = data.getStringExtra(Constants.NAME);
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (DataTempResultBean.DataBean.DataListBean dataListBean : datas) {
            sb.append("," + dataListBean.getId().getValue());
        }
        sb.deleteCharAt(0);

        QuoteRelationRequestBean bean = new QuoteRelationRequestBean();
        bean.setBean(moduleBean);
        bean.setModuleId(moduleId);
        bean.setModuleName(moduleName);
        bean.setProjectId(projectId);
        bean.setBean_type(3);
        bean.setQuoteTaskId(sb.toString());
        bean.setTaskId(taskId);
        bean.setTaskType(taskType);

        addRelevanceByQuote(bean);
    }


    /**
     * 个人任务 通过新增 添加关联
     *
     * @param bean
     */
    private void addRelevance(SavePersonalRelationRequestBean bean) {

        model.addRelevanceByPersonal(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mContext, "添加成功!");
                initData();
            }
        });
    }

    /**
     * 项目任务通过新增 添加关联
     *
     * @param bean
     */
    private void addRelevanceByNew(SaveRelationRequestBean bean) {
        model.addRelevanceByNew(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mContext, "添加成功!");
                initData();
            }
        });
    }

    /**
     * 项目任务 通过引用 添加关联
     * @param bean
     */
    private void addRelevanceByQuote(QuoteRelationRequestBean bean) {
        model.addRelevanceByQuote(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mContext, "添加成功!");
                initData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
         if (requestCode == ProjectConstants.ADD_TASK_REQUEST_CODE) {
            //新增
             if(fromType ==1 || fromType ==2){
                 TaskNew(data);
             }else if(fromType ==3 || fromType ==4){
                 persionTaskNew(data);
             }

        } else if (requestCode == ProjectConstants.ADD_TASK_TASK_REQUEST_CODE) {
            //新增任务
             if(fromType ==1 || fromType ==2){
                 AddRelevaceByNewTask(data);
             }else if(fromType ==3 || fromType ==4){
                 persionAddRelevaceByNewTask(data);
             }

        } else if (requestCode == CustomConstants.REQUEST_ADDCUSTOM_CODE) {
            //新增自定义模块
             if(fromType ==1 || fromType ==2){
                 AddRelevaceByNewCustom(data);
             }else if(fromType ==3 || fromType ==4){
                 persionAddRelevaceByNewCustom(data);
             }
        } else if (requestCode == ProjectConstants.ADD_TASK_MEMO_REQUEST_CODE) {
            //新增备忘录
             if(fromType ==1 || fromType ==2){
                 AddRelevaceByNewMemo(data);
             }else if(fromType ==3 || fromType ==4){
                 persionAddRelevaceByNewMemo(data);
             }

        } else if (requestCode == ProjectConstants.ADD_TASK_APPROVE_REQUEST_CODE) {
            //新增审批
             if(fromType ==1 || fromType ==2){
                 AddRelevaceByNewApprove(data);
             }else if(fromType ==3 || fromType ==4){
                 persionAddRelevaceByNewApprove(data);
             }

        } else if (requestCode == ProjectConstants.QUOTE_TASK_REQUEST_CODE) {
            //引用
             if(fromType ==1 || fromType ==2){
                 taskQuote(data);
             }else if(fromType ==3 || fromType ==4){
                 persionTaskQuote(data);
             }

        } else if (requestCode == ProjectConstants.QUOTE_TASK_MEMO_REQUEST_CODE) {
            //引用备忘录
             int type = data.getIntExtra(Constants.DATA_TAG_TYPE,1);
             if (type == 1){
                 if(fromType ==1 || fromType ==2){
                     quoteMemo(data);
                 }else if(fromType ==3 || fromType ==4){
                     persionquoteMemo(data);
                 }
             }else if(type == 2){//新增备忘录
                 if(fromType ==1 || fromType ==2){
                     AddRelevaceByNewMemo(data);
                 }else if(fromType ==3 || fromType ==4){
                     persionAddRelevaceByNewMemo(data);
                 }
             }
        } else if (requestCode == ProjectConstants.QUOTE_TASK_APPROVE_REQUEST_CODE) {
            //引用审批
             int type = data.getIntExtra(Constants.DATA_TAG_TYPE,1);
             if (type == 1){
                 if(fromType ==1 || fromType ==2){
                     quoteApprove(data);
                 }else if(fromType ==3 || fromType ==4){
                     persionquoteApprove(data);
                 }
             }else if (type == 2){//新增审批
                 if(fromType ==1 || fromType ==2){
                     AddRelevaceByNewApprove(data);
                 }else if(fromType ==3 || fromType ==4){
                     persionAddRelevaceByNewApprove(data);
                 }
             }


        } else if (requestCode == ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE) {
            //引用任务
             if(fromType ==1 || fromType ==2){
                 quoteTask(data);
             }else if(fromType ==3 || fromType ==4){
                 persionquoteTask(data);
             }

        } else if (requestCode == ProjectConstants.QUOTE_TASK_CUSTOM_REQUEST_CODE) {
             int type = data.getIntExtra(Constants.DATA_TAG_TYPE,1);
             if(type == 1){
                 //引用自定义
                 if(fromType ==1 || fromType ==2){
                     quoteCustom(data);
                 }else if(fromType ==3 || fromType ==4){
                     persionquoteCustom(data);
                 }
             }else if (type == 2){
                 //新增自定义模块
                 if(fromType ==1 || fromType ==2){
                     AddRelevaceByNewCustom(data);
                 }else if(fromType ==3 || fromType ==4){
                     persionAddRelevaceByNewCustom(data);
                 }
             }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

}

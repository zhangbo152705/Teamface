package com.hjhq.teamface.project.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.ProjectPicklistStatusBean;
import com.hjhq.teamface.basis.bean.QueryTaskCompleteAuthResultBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CloneUtils;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.JsonParser;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.utils.ProjectDialog;
import com.hjhq.teamface.customcomponent.widget2.select.PickListViewSelectActivity;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.AllNodeResultBean;
import com.hjhq.teamface.project.bean.NodeBean;
import com.hjhq.teamface.project.bean.QueryManagerRoleResultBean;
import com.hjhq.teamface.project.bean.QueryTaskAuthResultBean;
import com.hjhq.teamface.project.bean.QueryTaskDetailResultBean;
import com.hjhq.teamface.project.bean.TaskMemberListResultBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.ui.TaskBoardDelegateNew;
import com.hjhq.teamface.project.util.ProjectUtil;
import com.hjhq.teamface.project.view.CommomDialog;
import com.hjhq.teamface.project.view.CommonPopupWindow;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;

/**
 * 任务看板
 *
 * @author Administrator
 * @date 2018/4/10
 */

public class TaskBoardFragmentNew extends FragmentPresenter<TaskBoardDelegateNew, ProjectModel>  implements View.OnClickListener{
    protected int code = this.hashCode() % 10000;
    private ProjectDetailActivity mActivity;
    private NodeBean dataList;
    private String[] actionMenu;
    private CommomDialog editDialog;
    private List<String> addItemClassList;
    private List<String> addItemTaskList;
    private CommonPopupWindow  popupWindow;

    private String priviledgeIds;
    private List<QueryTaskAuthResultBean.DataBean> taskAuthList;
    private List<String> taskRoles = new ArrayList<>();
    private boolean[] taskAuths;
    private int taskType = 0;
    NodeBean parentNode = null;
    long id = 0;
    private Map<String, Object> paramMap = new HashMap<>();
    private String projectStatus;


    private List<EntryBean> lableEntrys = new ArrayList<>();
    private String[] stateArr = null;
    private Object stateValue;
    private String remark;
    private boolean isRefreshData = true;


    @SuppressLint({"NewApi", "ValidFragment"})
    public TaskBoardFragmentNew(){
    }

    @SuppressLint({"NewApi", "ValidFragment"})
    public TaskBoardFragmentNew(String projectStatus){
      this.projectStatus = projectStatus;
    }

    @Override
    public void init() {
        mActivity = (ProjectDetailActivity) getActivity();
        loadCacheData();
        queryProjectRoleInfo();
        queryTaskAuth();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRefreshData){
            getAllNode();
        }
        isRefreshData = true;
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
               // viewDelegate.initNavigator(getChildFragmentManager(), cacheDataList);
            }
        }
        addItemClassList = new ArrayList<String>(){{add(getResources().getString(R.string.project_add_sameclass)); add(getResources().getString(R.string.project_add_nextclass));}};
        addItemTaskList = new ArrayList<String>(){{add(getResources().getString(R.string.project_add_sametask)); add(getResources().getString(R.string.project_add_nexttask));}};
    }

    /**
     *获取主任务布局信息
     */
    public void queryTaskDetail(long taskId,String taskName,NodeBean node){
        if (taskId == 0){
            if (viewDelegate.selectNode.getSelectNode().getNode_type() ==2) {
               taskId = Long.parseLong(node.getData_id());
            } else if (viewDelegate.selectNode.getSelectNode().getNode_type() ==3){
                taskId = node.getTask_info().getId();
            }
        }
        model.queryTaskDetail(mActivity,taskId, new ProgressSubscriber<QueryTaskDetailResultBean>(mActivity,true) {
                    @Override
                    public void onNext(QueryTaskDetailResultBean taskAuthResultBean) {
                        super.onNext(taskAuthResultBean);
                        if (taskAuthResultBean != null && taskAuthResultBean.getData().getCustomArr() != null){
                            QueryTaskDetailResultBean.DataBean taskData = taskAuthResultBean.getData();
                            Object object = taskData.getCustomArr();
                            JSONObject customArr = (JSONObject) JSONObject.toJSON(object);
                            JSONObject editjsonObject = getEditTaskData(node,taskName,customArr,0,taskData);
                            editTaskBoard(editjsonObject);
                        }
                     }
        });
    }

    /**
     *获取子主任务布局信息
     */
    public void querySubTaskDetail(long taskId,String taskName,NodeBean node){
        if (taskId == 0){
            if (viewDelegate.selectNode.getSelectNode().getNode_type() ==2) {
                taskId = Long.parseLong(node.getData_id());
            } else if (viewDelegate.selectNode.getSelectNode().getNode_type() ==3){
                taskId = node.getTask_info().getId();
            }
        }
        model.querySubTaskDetail(mActivity,taskId, new ProgressSubscriber<QueryTaskDetailResultBean>(mActivity,true) {
            @Override
            public void onNext(QueryTaskDetailResultBean taskAuthResultBean) {
                super.onNext(taskAuthResultBean);
                if (taskAuthResultBean != null && taskAuthResultBean.getData().getCustomArr() != null){
                    QueryTaskDetailResultBean.DataBean  taskData = taskAuthResultBean.getData();
                    Object object = taskData.getCustomArr();
                    JSONObject oldData = (JSONObject) JSONObject.toJSON(object);
                    JSONObject editjsonObject = getEditTaskData(node,taskName,oldData,1,taskData);
                    editTaskBoardSub(editjsonObject);
                    Log.e("queryTaskDetail","editjsonObject:"+editjsonObject.toJSONString());
                }

            }
        });
    }

    /**
     *编辑主任务名称
     */
    public void editTaskBoard(JSONObject obj){
        model.editTaskBoard(mActivity,obj, new ProgressSubscriber<BaseBean>(mActivity,true) {
            @Override
            public void onNext(BaseBean bean) {
                super.onNext(bean);
                getAllNode();
                stateValue = null;
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stateValue = null;
            }
        });
    }

    /**
     *编辑子任务名称
     */
    public void editTaskBoardSub(JSONObject obj){
        model.editTaskBoardSub(mActivity,obj, new ProgressSubscriber<BaseBean>(mActivity,true) {
            @Override
            public void onNext(BaseBean bean) {
                super.onNext(bean);
                getAllNode();
                stateValue = null;//把上次选择状态置null
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stateValue = null;
            }
        });
    }


    /**
     * 获取项目角色信息
     */
    public void queryProjectRoleInfo() {
        //查询管理员权限
        new TaskModel().queryManagementRoleInfo(mActivity, TextUtil.parseLong(SPHelper.getEmployeeId()), mActivity.projectId,
                new ProgressSubscriber<QueryManagerRoleResultBean>(mActivity) {
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
                ToastUtils.showError(mActivity, "获取项目角色权限失败");
            }
        });
    }

    /**
     * 获取任务权限
     */
    private void queryTaskAuth() {
        model.queryTaskAuthList(mActivity, mActivity.projectId, new ProgressSubscriber<QueryTaskAuthResultBean>(mActivity) {
            @Override
            public void onNext(QueryTaskAuthResultBean taskAuthResultBean) {
                super.onNext(taskAuthResultBean);
                taskAuthList = taskAuthResultBean.getData();
                //permissionHandle();
            }

            @Override
            public void onError(Throwable e) {
                dismissWindowView();
                e.printStackTrace();
                ToastUtils.showError(mActivity, "获取任务权限失败");
            }
        });
    }

    /**
     * 获取全部角色
     */
    private void queryTaskRoles(long taskId,int taskTyped,String taskName) {
        model.queryTaskMemberList(mActivity, mActivity.projectId, taskId, taskType, 0, new ProgressSubscriber<TaskMemberListResultBean>(mActivity) {
            @Override
            public void onNext(TaskMemberListResultBean taskMemberListResultBean) {
                super.onNext(taskMemberListResultBean);
                TaskMemberListResultBean.DataBean data = taskMemberListResultBean.getData();
                taskRoles.clear();
                taskAuths = null;
                if (!CollectionUtils.isEmpty(data.getDataList())) {
                    Observable.from(data.getDataList()).subscribe(dataBean -> taskRoles.add(dataBean.getProject_task_role()));
                    permissionHandle();

                    if (taskTyped == 1) {//删除主任务
                        if (taskAuths[2]) {
                            deleteNodeTask(taskId);
                        } else {
                            ToastUtils.showToast(mActivity, getResources().getString(R.string.project_no_auth));
                        }
                    } else if (taskTyped == 2) {//删除子任务
                        if (taskAuths[2]) {
                            deleteNodeTaskSub(taskId);
                        } else {
                            ToastUtils.showToast(mActivity, getResources().getString(R.string.project_no_auth));
                        }

                    } else if (taskTyped == 3) {//编辑主任务
                        if (taskAuths[0]) {
                            JSONObject obj = new JSONObject();
                            obj.put("taskId", taskId);
                            showEditDialog(obj, 4, taskName);
                        } else {
                            ToastUtils.showToast(mActivity, getResources().getString(R.string.project_no_auth));
                        }
                    }else if (taskTyped ==4){//编辑子任务
                        if (taskAuths[0]) {
                            JSONObject obj = new JSONObject();
                            obj.put("taskId", taskId);
                            showEditDialog(obj,6,taskName);
                        } else {
                            ToastUtils.showToast(mActivity, getResources().getString(R.string.project_no_auth));
                        }
                    }else if (taskTyped == 5){//编辑任务状态
                        if (taskAuths[0]) {
                            getStatuValues();
                        } else {
                            ToastUtils.showToast(mActivity, getResources().getString(R.string.project_no_auth));
                        }
                    }else if (taskTyped == 6){//新增子任务
                        if (taskAuths[7]){
                            JSONObject jsonObject = getAddNextSubTaskData(viewDelegate.selectNode.getSelectNode());
                            showEditDialog(jsonObject,5,"");
                        } else {
                            ToastUtils.showToast(mActivity, getResources().getString(R.string.project_no_auth));
                        }
                    }
                } else {
                    ToastUtils.showToast(mActivity, getResources().getString(R.string.project_no_auth));
                }
            }
        });
    }

    /**
     * 获取所有节点
     */
    private void getAllNode() {
        JSONObject obj = new JSONObject();
        obj.put("id",mActivity.projectId);
        model.getAllNode(mActivity,mActivity.projectId, new ProgressSubscriber<AllNodeResultBean>(mActivity, true) {
            @Override
            public void onNext(AllNodeResultBean baseBean) {
                super.onNext(baseBean);
                dataList = baseBean.getData().getRootNode();
                String objstr = JSON.toJSONString(dataList);
                Log.e("getAllNode","objstr:"+objstr);
               //viewDelegate.initNavigator(getChildFragmentManager(), dataList);
               viewDelegate.refreshNode(dataList);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dataList = new NodeBean();
                viewDelegate.refreshNode(dataList);
                ToastUtils.showToast(mActivity,mActivity.getResources().getString(R.string.project_load_node_error));
            }
        });



    }
    /**
     * 获取所有节点
     */
    private void getAllNode(String filter) {
        JSONObject obj = new JSONObject();
        obj.put("id",mActivity.projectId);
        obj.put("filterParam",paramMap);
        model.getAllNodeBytePost(mActivity,obj, new ProgressSubscriber<AllNodeResultBean>(mActivity, true) {
            @Override
            public void onNext(AllNodeResultBean baseBean) {
                super.onNext(baseBean);
                dataList = baseBean.getData().getRootNode();
                String objstr = JSON.toJSONString(dataList);
                Log.e("getAllNode","objstr:"+objstr);
                //viewDelegate.initNavigator(getChildFragmentManager(), dataList);
                viewDelegate.refreshNode(dataList);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dataList = new NodeBean();
                viewDelegate.refreshNode(dataList);
                ToastUtils.showToast(mActivity,mActivity.getResources().getString(R.string.project_load_node_error));
            }
        });

    }
    /**
     * 删除节点
     */
    private void deleteNode( String nodeName,long nodeId,String nodeCode) {

        DialogUtils.getInstance().inputDialog(mActivity, "确定删除列表:  '"+nodeName +" '", null, "请输入要删除列表名称", viewDelegate.getRootView(), content -> {
            if (TextUtil.isEmpty(content) || !content.equals(nodeName)) {
                ToastUtils.showError(mActivity, "请输入要删除列表名称");
                return false;
            }
            model.deleteNode(mActivity, nodeName,nodeId,nodeCode, new ProgressSubscriber<BaseBean>(mActivity, true) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    //viewDelegate.refreshNode(dataList);
                    getAllNode();
                }
            });
            return true;
        });

        /*DialogUtils.getInstance().sureOrCancel(mActivity, "确定删除节点:  "+nodeName +"?", null, viewDelegate.getRootView(), () -> {
            model.deleteNode(mActivity, nodeName,nodeId,nodeCode, new ProgressSubscriber<BaseBean>(mActivity, true) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    //viewDelegate.refreshNode(dataList);
                    getAllNode();
                }
            });
        });*/


    }

    /**
     * 删除任务
     */
    private void deleteNodeTask(long taskId) {

        DialogUtils.getInstance().sureOrCancel(mActivity, "删除任务后，所有子任务也将同时被删除。", null, viewDelegate.getRootView(), () -> {
            model.deleteNodeTask(mActivity,taskId, new ProgressSubscriber<BaseBean>(mActivity, true) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    getAllNode();
                }
            });
        });



    }

    /**
     * 删除子任务
     */
    private void deleteNodeTaskSub(long taskId) {

        DialogUtils.getInstance().sureOrCancel(mActivity, "删除任务后，所有子任务也将同时被删除。", null, viewDelegate.getRootView(), () -> {
            model.deleteNodeTaskSub(mActivity,taskId, new ProgressSubscriber<BaseBean>(mActivity, true) {
                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    getAllNode();
                }
            });
        });


    }

    /**
     * 编辑节点
     */
    private void editNode( String nodeName,long nodeId,String nodeCode) {
        model.updateNode(mActivity, nodeName,nodeId,nodeCode, new ProgressSubscriber<BaseBean>(mActivity, true) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                //viewDelegate.refreshNode(dataList);
                getAllNode();
            }
        });

    }

    /**
     * 增加节点
     */
    private void addNodeClass( JSONObject obj) {
        model.addNodeClass(mActivity,obj, new ProgressSubscriber<BaseBean>(mActivity, true) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                getAllNode();
            }
        });
    }

    /**
     * 增加任务
     */
    private void addNodeTask( JSONObject obj) {
        model.addNodeTask(mActivity,obj, new ProgressSubscriber<BaseBean>(mActivity, true) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                getAllNode();
            }
        });
    }

    /**
     * 增加子任务
     */
    private void addNodeSubTask( JSONObject obj) {
        model.addNodeSubTask(mActivity,obj, new ProgressSubscriber<BaseBean>(mActivity, true) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                getAllNode();
            }
        });
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        RxManager.$(mActivity.hashCode()).onSticky(ProjectConstants.PROJECT_ROLE_TAG, s -> {
           // initMenu();
        });
        RxManager.$(mActivity.hashCode()).onSticky(ProjectConstants.PROJECT_ADD_FIRST_CLASS_TAG, s -> {
            if (handAuther()){
                return;
            }
            int type = (Integer)s;
            if (type == 1){
                JSONObject obj = getAddNextClassData(dataList,1);
                showEditDialog(obj,2,"");
            }else if (type == 3){
               // updateStatusTask();
               if (viewDelegate.selectNode != null && viewDelegate.selectNode.getSelectNode() != null){
                   if (viewDelegate.selectNode.getSelectNode().getTask_info() != null){
                       long taskId = viewDelegate.selectNode.getSelectNode().getTask_info().getId();
                       queryTaskRoles(taskId,5,"");
                   }
               }
            }

        });

        RxManager.$(mActivity.hashCode()).onSticky(ProjectConstants.PROJECT_UPDATE_TITLE, s -> {
            if (viewDelegate.selectNode.getSelectNode().getNode_type() == 2 || viewDelegate.selectNode.getSelectNode().getNode_type() == 3){
                viewDelegate.task_class_name.setText(mActivity.getResources().getString(R.string.project_add_sametask));
                viewDelegate.task_name.setText(mActivity.getResources().getString(R.string.project_add_nexttask));

            }else{
                viewDelegate.task_class_name.setText(mActivity.getResources().getString(R.string.project_task_classify));
                viewDelegate.task_name.setText(mActivity.getResources().getString(R.string.project_task_sub));
            }
        });

        viewDelegate.project_task_delete_re.setOnClickListener(this);
        viewDelegate.project_task_edit_re.setOnClickListener(this);
        viewDelegate.project_task_classify_re.setOnClickListener(this);
        viewDelegate.project_task_sub_re.setOnClickListener(this);
        viewDelegate.tv_zoom.setOnClickListener(this);
    }

    /**
     * 获取状态
     */
    public void getStatuValues(){
        Bundle bundle = new Bundle();
        fillLabels();
        ArrayList<EntryBean> clone = CloneUtils.clone(((ArrayList<EntryBean>) lableEntrys));
        bundle.putSerializable(Constants.DATA_TAG1, clone);
        bundle.putBoolean(Constants.DATA_TAG2, true);
        bundle.putInt(Constants.DATA_TAG3, 1);
        CommonUtil.startActivtiyForResult(mActivity, PickListViewSelectActivity.class, code, bundle);
    }

    /**
     * 处理数据
     */
    private void fillLabels() {
        String State ="";
        if (viewDelegate.selectNode != null && viewDelegate.selectNode.getSelectNode() != null && viewDelegate.selectNode.getSelectNode().getTask_info() != null){
            ArrayList<ProjectPicklistStatusBean> stateArr = viewDelegate.selectNode.getSelectNode().getTask_info().getPicklist_status();
            if (!CollectionUtils.isEmpty(stateArr)){
                State =  stateArr.get(0).getLabel();
            }
        }
        stateArr =new String[]{mActivity.getResources().getString(R.string.project_no_start),mActivity.getResources().getString(R.string.project_ongoing)
                ,mActivity.getResources().getString(R.string.project_suspended),mActivity.getResources().getString(R.string.project_complete)};
        lableEntrys.clear();
        for(int i=0;i<stateArr.length;i++){
            EntryBean enter = new EntryBean();
            enter.setLabel(stateArr[i]);
            enter.setValue(i+"");
            enter.setFromType(Constants.STATE_FROM_PROJECR);
            if (!TextUtil.isEmpty(State) && stateArr[i].indexOf(State) != -1){
                enter.setCheck(true);
            }
            lableEntrys.add(enter);
        }
    }


    /**
     * 处理任务完成或激活状态
     */
    public void updateStatusTask(){
        if(viewDelegate.selectNode.getSelectNode() != null && viewDelegate.selectNode.getSelectNode().getTask_info() != null){
            NodeBean node = viewDelegate.selectNode.getSelectNode();

            boolean projectIsRunning = "0".equals(mActivity.projectStatus);
            boolean taskCompleteStatus = "1".equals(node.getTask_info().getComplete_status());

            long projectId = node.getTask_info().getProject_id();
            taskType = 0;
            long taskId =0;
            JSONObject jsonObject = new JSONObject();

            //项目任务
            if (viewDelegate.selectNode.getSelectNode().getNode_type() ==2) {
                taskType = 1;
                if(node.getData_id() != null && TextUtil.isInteger(node.getData_id())){
                    taskId = Long.parseLong(node.getData_id());
                    jsonObject.put("id", node.getData_id());//任务的Id
                }
            } else if (viewDelegate.selectNode.getSelectNode().getNode_type() ==3){
                 taskId = node.getTask_info().getId();
                 jsonObject.put("id", node.getTask_info().getId()+"");//任务的Id
                taskType = 2;
            }

            jsonObject.put("completeStatus", taskCompleteStatus ? 0 : 1);
            if (projectIsRunning) {
                new TaskModel().queryTaskCompleteAuth(mActivity, projectId, taskType, taskId,
                        new ProgressSubscriber<QueryTaskCompleteAuthResultBean>(mActivity) {
                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                            }

                            @Override
                            public void onNext(QueryTaskCompleteAuthResultBean dataBean) {
                                super.onNext(dataBean);
                                if ("1".equals(dataBean.getData().getFinish_task_role())) {
                                    //更新状态
                                    boolean isSubTask = false;
                                    if (taskType == 1){
                                        isSubTask = false;
                                    }else if (taskType == 2){
                                        isSubTask =true;
                                    }
                                    ProjectDialog.updateTaskStatus(isSubTask,((RxAppCompatActivity) getActivity()), viewDelegate.getRootView(),
                                            jsonObject, taskCompleteStatus, dataBean.getData().getProject_complete_status(), new ProgressSubscriber<BaseBean>(getActivity(), 1) {
                                                @Override
                                                public void onNext(BaseBean baseBean) {
                                                    super.onNext(baseBean);
                                                   // item.setComplete_status(taskCompleteStatus ? "0" : "1");
                                                  //  ((TaskItemAdapter) adapter).notifyItemChanged(position);
                                                  //  refreshColumn(listAdapter, subNodeId);
                                                    getAllNode();
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    super.onError(e);
                                                }
                                            });
                                } else {
                                    ToastUtils.showError(getActivity(), "没有权限");
                                }
                            }
                        });

            } else {
                ToastUtils.showToast(getActivity(), "项目当前状态不支持该操作!");
            }

        }

    }

    public boolean handAuther(){
        boolean flag = false;
        if (projectStatus != null ){
            if (projectStatus.equals("1")){
                ToastUtils.showError(mActivity, "项目已归档");
                flag= true;
            }else if(projectStatus.equals("2")){
                ToastUtils.showError(mActivity, "项目已暂停");
                flag= true;
            }
        }
        return  flag;
    }

    @Override
    public void onClick(View v) {

         if (handAuther()){
             return;
         }

        if (v.getId() == R.id.project_task_delete_re){//删除
            if (viewDelegate.selectNode != null && viewDelegate.selectNode.getSelectNode() != null&& viewDelegate.selectNode.getActionType() ==0){
                if (viewDelegate.selectNode.getSelectNode().getNode_type() ==1){//删除分类
                    if (!ProjectUtil.INSTANCE.checkProjectPermission(mActivity, priviledgeIds, 23)) {
                        ToastUtils.showToast(mActivity,getResources().getString(R.string.project_no_auth));
                        return;
                    }
                    String nodeName = viewDelegate.selectNode.getSelectNode().getNode_name();
                    long nodeId = viewDelegate.selectNode.getSelectNode().getId();
                    String nodeCode = viewDelegate.selectNode.getSelectNode().getNode_code();
                    deleteNode(nodeName,nodeId,nodeCode);
                }else if (viewDelegate.selectNode.getSelectNode().getNode_type() ==2 ){//删除主任务
                    if (!TextUtil.isEmpty(viewDelegate.selectNode.getSelectNode().getData_id())){
                        long id =  Long.parseLong(viewDelegate.selectNode.getSelectNode().getData_id());
                        //deleteNodeTask(id);
                        queryTaskRoles(id,1,"");
                    }
                }else if ( viewDelegate.selectNode.getSelectNode().getNode_type() ==3){//删除子任务
                    if (!TextUtil.isEmpty(viewDelegate.selectNode.getSelectNode().getData_id())){
                        long id =  Long.parseLong(viewDelegate.selectNode.getSelectNode().getData_id());
                        //deleteNodeTask(id);
                        queryTaskRoles(id,2,"");
                    }
                }
            }else{
                ToastUtils.showToast(mActivity,getResources().getString(R.string.project_selete_delete_node));
            }

        }else if(v.getId() == R.id.project_task_edit_re){//编辑
                if (viewDelegate.selectNode != null && viewDelegate.selectNode.getSelectNode() != null&& viewDelegate.selectNode.getActionType() ==0){
                    if (viewDelegate.selectNode.getSelectNode().getNode_type() ==1){//编辑分类
                        if (!ProjectUtil.INSTANCE.checkProjectPermission(mActivity, priviledgeIds, 21)) {
                            ToastUtils.showToast(mActivity,getResources().getString(R.string.project_no_auth));
                            return;
                        }
                        String nodeName = viewDelegate.selectNode.getSelectNode().getNode_name();
                        long nodeId = viewDelegate.selectNode.getSelectNode().getId();
                        String nodeCode = viewDelegate.selectNode.getSelectNode().getNode_code();
                        JSONObject obj = new JSONObject();
                        obj.put("nodeId",nodeId);
                        obj.put("nodeCode",nodeCode);
                        showEditDialog(obj,1,nodeName);
                    }else if (viewDelegate.selectNode.getSelectNode().getNode_type() ==2){//编辑主任务
                        String dataId = viewDelegate.selectNode.getSelectNode().getData_id();
                        String taskName = viewDelegate.selectNode.getSelectNode().getTask_name();
                        if (dataId != null &&  TextUtil.isInteger(dataId)){
                            long taskId = Long.parseLong(dataId);
                            queryTaskRoles(taskId,3,taskName);

                        }
                    }else if (viewDelegate.selectNode.getSelectNode().getNode_type() ==3){//编辑子任务
                        JSONObject obj = new JSONObject();
                        if (viewDelegate.selectNode.getSelectNode().getTask_info() != null){
                            String taskName = viewDelegate.selectNode.getSelectNode().getTask_name();
                            long taskId = viewDelegate.selectNode.getSelectNode().getTask_info().getId();
                            obj.put("taskId",taskId);
                            queryTaskRoles(taskId,4,taskName);

                        }
                    }

                }else{
                    ToastUtils.showToast(mActivity,getResources().getString(R.string.project_selete_edit_task));
                }
        }else if(v.getId() == R.id.project_task_classify_re){//新增分类
            if (viewDelegate.selectNode != null && viewDelegate.selectNode.getSelectNode() != null
                    && viewDelegate.selectNode.getActionType() ==0 && viewDelegate.selectNode.getSelectNode().getNode_type() ==1){
                if (!ProjectUtil.INSTANCE.checkProjectPermission(mActivity, priviledgeIds, 20)) {
                    ToastUtils.showToast(mActivity,getResources().getString(R.string.project_no_auth));
                    return;
                }
                showAddPopupWindow(addItemClassList,v,viewDelegate.selectNode.getSelectNode(),1);
            }else if (viewDelegate.selectNode != null && viewDelegate.selectNode.getSelectNode() != null
                    && viewDelegate.selectNode.getActionType() ==0 &&
                    (viewDelegate.selectNode.getSelectNode().getNode_type() ==2|| viewDelegate.selectNode.getSelectNode().getNode_type() ==3)){//新增同级任务

                JSONObject jsonObject =null;
                if (viewDelegate.selectNode.getSelectNode().getNode_type() == 2){//新增主任务
                    if (!ProjectUtil.INSTANCE.checkProjectPermission(mActivity, priviledgeIds, 25)) {
                        ToastUtils.showToast(mActivity,getResources().getString(R.string.project_no_auth));
                        return;
                    }
                    jsonObject = getAddSaveTaskData(viewDelegate.selectNode.getSelectNode());
                    showEditDialog(jsonObject,3,"");
                }else{//新增子任务
                    long taskId = viewDelegate.selectNode.getSelectNode().getTask_info().getId();
                    queryTaskRoles(taskId,6,"");
                }

            }else{
                ToastUtils.showToast(mActivity,getResources().getString(R.string.project_add_location));
            }

        }else if(v.getId() == R.id.project_task_sub_re){//新增下级任务
            if (viewDelegate.selectNode != null && viewDelegate.selectNode.getSelectNode() != null
                    && viewDelegate.selectNode.getActionType() ==0 && viewDelegate.selectNode.getSelectNode().getNode_type() ==1){
                if (!ProjectUtil.INSTANCE.checkProjectPermission(mActivity, priviledgeIds, 25)) {
                    ToastUtils.showToast(mActivity,getResources().getString(R.string.project_no_auth));
                    return;
                }
                showAddPopupWindow(addItemTaskList,v,viewDelegate.selectNode.getSelectNode(),2);
            }else if (viewDelegate.selectNode != null && viewDelegate.selectNode.getSelectNode() != null
                    && viewDelegate.selectNode.getActionType() ==0 &&
                    (viewDelegate.selectNode.getSelectNode().getNode_type() ==2 || viewDelegate.selectNode.getSelectNode().getNode_type() ==3)) {//新增下任务
                JSONObject jsonObject =null;
                if (viewDelegate.selectNode.getSelectNode().getNode_type() == 1) {//新增主任务
                    if (!ProjectUtil.INSTANCE.checkProjectPermission(mActivity, priviledgeIds, 25)) {
                        ToastUtils.showToast(mActivity,getResources().getString(R.string.project_no_auth));
                        return;
                    }
                    jsonObject = getAddNextTaskData(viewDelegate.selectNode.getSelectNode());
                    showEditDialog(jsonObject, 3,"");
                }else{//新增子任务

                    long taskId = viewDelegate.selectNode.getSelectNode().getTask_info().getId();
                    queryTaskRoles(taskId,6,"");

                }
            }else{
                ToastUtils.showToast(getActivity(),getActivity().getResources().getString(R.string.project_add_task_location));
            }
        }else if (v.getId() == R.id.tv_zoom){//缩放 /收起
            int tag = TextUtil.parseInt(viewDelegate.tv_zoom.getTag().toString(),0) ;
            Log.e("tag",":"+tag);
            if (tag ==0){
                viewDelegate.showZoom(1);
                viewDelegate.tv_zoom.setImageResource(R.drawable.project_task_close);
                viewDelegate.tv_zoom.setTag(1);

            }else {
                viewDelegate.showZoom(0);
                viewDelegate.tv_zoom.setImageResource(R.drawable.project_task_open);
                viewDelegate.tv_zoom.setTag(0);
            }
        }
    }

    /**
     * 获取编辑任务json对象
     */
   public JSONObject getEditTaskData(NodeBean node,String taskName,JSONObject data,int type,QueryTaskDetailResultBean.DataBean taskData){
       JSONObject jsonObject = new JSONObject();
       jsonObject.put("projectId", mActivity.projectId);
       long taskId = 0;
       String dataId = node.getData_id();
       if (dataId != null && TextUtil.isInteger(dataId)){
           taskId = Long.parseLong(dataId);
       }
       jsonObject.put("taskId",taskId);
       jsonObject.put("bean",ProjectConstants.PROJECT_TASK_MOBULE_BEAN + mActivity.projectId);
       jsonObject.put("checkStatus",taskData.getCheck_status());
       jsonObject.put("checkMembers",taskData.getCheck_member());

       try{
           Object execution = data.get("personnel_principal");
           String arr = JSONObject.toJSONString(execution);
           JSONArray jsonArr = JSONObject.parseArray(arr);
           if (jsonArr != null && jsonArr.size() > 0) {
               JSONObject object = jsonArr.getJSONObject(0);
               jsonObject.put("executorId",object.getLong("id"));

           }else{
               jsonObject.put("executorId",0);
           }
       }catch (Exception e){
           jsonObject.put("executorId",0);
       }
       String endTime = "";
       String startTime = "";
       if (!TextUtil.isEmpty(data.get("datetime_deadline")+"")){
           endTime= new BigDecimal(data.getLong("datetime_deadline")).toPlainString();
       }
       if (!TextUtil.isEmpty(data.get("datetime_starttime")+"")){
           startTime= new BigDecimal(data.getLong("datetime_starttime")).toPlainString();
       }
       Log.e("getEditTaskData","endTime:"+endTime);

       jsonObject.put("associatesStatus",taskData.getAssociates_status());
       if (TextUtil.isEmpty(taskName)){
           taskName= taskData.getTask_name();
       }
       jsonObject.put("taskName",taskName);
       jsonObject.put("endTime",endTime);
       jsonObject.put("startTime",startTime);



       data.put("datetime_deadline",endTime);
       data.put("datetime_starttime",startTime);
       jsonObject.put("oldData",data);

       JSONObject cldata = (JSONObject) data.clone();
       if (stateValue != null){
           cldata.put("picklist_status",stateValue);
       }
       cldata.put("text_name",taskName);

       String picklist_tag = "";
       try{

           if (data.get("picklist_tag") != null){
               String jsonArr = JSONObject.toJSONString(data.get("picklist_tag"));
               JSONArray ja = JSONObject.parseArray(jsonArr);
               for(int i =0;i<ja.size();i++){
                   JSONObject object = ja.getJSONObject(i);
                   if (object.getLong("id") != 0){
                       if (TextUtil.isEmpty(picklist_tag)){
                           picklist_tag = picklist_tag + object.getLong("id");
                       }else {
                           picklist_tag = picklist_tag +","+ object.getLong("id");
                       }
                   }
               }
           }

       }catch (Exception e){
           Log.e("getEditTaskData","e:"+e.toString());
       }

       cldata.put("picklist_tag",picklist_tag);

       //关联组件,人员组件,部门组件处理 reference
       Set<String> Iterator = cldata.keySet();
       for (String key : Iterator) {
           if (!TextUtil.isEmpty(key) && key.indexOf("reference") != -1 && cldata.get(key) != null) {
               final Object referenceExecution = cldata.get(key);
               String referenceTag = getIdTag(referenceExecution);
               cldata.put(key, referenceTag);
           }else if (!TextUtil.isEmpty(key) && key.indexOf("department") != -1 && cldata.get(key) != null){
               final Object departmentExecution = cldata.get(key);
               String departmentTag = getIdTag(departmentExecution);
               cldata.put(key, departmentTag);
           }else if (!TextUtil.isEmpty(key) && key.indexOf("personnel") != -1 && cldata.get(key) != null){
               final Object personnelExecution = cldata.get(key);
               String personnelTag = getIdTag(personnelExecution);
               cldata.put(key, personnelTag);
           }
       }

       jsonObject.put("data",cldata);
       if (TextUtil.isEmpty(remark)){
           jsonObject.put("remark","");
       }else {
           jsonObject.put("remark",remark);
       }

       jsonObject.put("nodeId",node.getId());

       if (type == 1){
           jsonObject.put("id",node.getTask_info().getId());
       }
       return jsonObject;
   }


    public String getIdTag(Object obj){
        String idTag = "";
        try {
            String jsonArr = JSONObject.toJSONString(obj);
            JSONArray ja = JSONObject.parseArray(jsonArr);
            for (int i = 0; i < ja.size(); i++) {
                JSONObject object = ja.getJSONObject(i);
                if (object.getLong("id") != 0) {
                    if (TextUtil.isEmpty(idTag)) {
                        idTag = idTag + object.getLong("id");
                    } else {
                        idTag = idTag + "," + object.getLong("id");
                    }
                }
            }
        }catch (Exception e){
            Log.e("getIdTag",e.toString());
        }

        return idTag;
    }

    /**
     * 获取新增同级分类Json对象
     * @param node
     * @return
     */
    public JSONObject getAddSaveClassData(NodeBean node){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("node_name", node.getNode_name());
        jsonObject.put("project_id", mActivity.projectId);
        jsonObject.put("parent_id", node.getParent_id());
        jsonObject.put("node_type",node.getNode_type());
        jsonObject.put("node_level",node.getNode_level());
        id = node.getParent_id();
        parentNode = null;
        getNodeParent(dataList);
        if (parentNode != null){
            String brotherNodeId = "";
            for (NodeBean broNode : parentNode.getChild()){
                  if (node.getId() == broNode.getId()){
                      if (TextUtil.isEmpty(brotherNodeId)){
                          brotherNodeId = brotherNodeId + String.valueOf(broNode.getId()) + ",-1";
                      }else {
                          brotherNodeId = brotherNodeId+"," + String.valueOf(broNode.getId()) + ",-1";
                      }
                  }else{
                      if(TextUtil.isEmpty(brotherNodeId)){
                          brotherNodeId =brotherNodeId+ broNode.getId();
                      }else {
                          brotherNodeId =brotherNodeId+","+ broNode.getId();
                      }
                  }
            }

            jsonObject.put("parent_node_code",parentNode.getNode_code());
            jsonObject.put("brother_node_id",brotherNodeId);


            Log.e("addNode","brother_node_id:"+brotherNodeId);
            Log.e("addNode","parent_node_code:"+parentNode.getNode_code());
        }


       // jsonObject.put("parent_node_code",parent_node_code);

        return jsonObject;
    }

    /**
     * 获取新增下一级分类Json对象
     * @param node
     * @return
     */
    public JSONObject getAddNextClassData(NodeBean node,int type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("node_name", node.getNode_name());
        jsonObject.put("project_id", mActivity.projectId);
        jsonObject.put("parent_id", node.getId());
        if (type != 0){
            jsonObject.put("node_type",type);
        }else {
            jsonObject.put("node_type", node.getNode_type());
        }
        jsonObject.put("node_level", node.getNode_level() + 1);
        String brotherNodeId = "";
        for (NodeBean broNode : node.getChild()) {
            if (node.getId() == broNode.getId()) {

                if (TextUtil.isEmpty(brotherNodeId)){
                    brotherNodeId = brotherNodeId + String.valueOf(broNode.getId()) + ",-1";
                }else {
                    brotherNodeId = brotherNodeId+"," + String.valueOf(broNode.getId()) + ",-1";
                }
            } else {
                if(TextUtil.isEmpty(brotherNodeId)){
                    brotherNodeId =brotherNodeId+ broNode.getId();
                }else {
                    brotherNodeId =brotherNodeId+","+ broNode.getId();
                }
            }
        }
        jsonObject.put("parent_node_code", node.getNode_code());
        jsonObject.put("brother_node_id", brotherNodeId);

        Log.e("addNode", "brother_node_id:" + brotherNodeId);
        Log.e("addNode", "parent_node_code:" + node.getNode_code());
        return jsonObject;
    }

    /**
     * 获取新增同级主任务Json对象
     * @param node
     * @return
     */
    public JSONObject getAddSaveTaskData(NodeBean node) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", mActivity.projectId);
        jsonObject.put("bean", ProjectConstants.PROJECT_TASK_MOBULE_BEAN + mActivity.projectId);
        jsonObject.put("checkStatus", 0);
        jsonObject.put("checkMember", 0);
        jsonObject.put("associatesStatus", 0);
        jsonObject.put("endTime", 0);
        jsonObject.put("startTime", 0);
        jsonObject.put("executorId", 0);
        //jsonObject.put("data", new JSONObject());
        jsonObject.put("parentNodeId", node.getParent_id());
        id = node.getParent_id();
        parentNode = null;
        getNodeParent(dataList);
        if (parentNode != null) {
            String brotherNodeId = "";
            for (NodeBean broNode : parentNode.getChild()) {
                if (node.getId() == broNode.getId()) {

                    if (TextUtil.isEmpty(brotherNodeId)){
                        brotherNodeId = brotherNodeId + String.valueOf(broNode.getId()) + ",-1";
                    }else {
                        brotherNodeId = brotherNodeId+"," + String.valueOf(broNode.getId()) + ",-1";
                    }
                } else {
                        if(TextUtil.isEmpty(brotherNodeId)){
                            brotherNodeId =brotherNodeId+ broNode.getId();
                        }else {
                            brotherNodeId =brotherNodeId+","+ broNode.getId();
                        }
                }
            }
            if (TextUtil.isEmpty(brotherNodeId)){
                brotherNodeId = "-1";
            }
            jsonObject.put("parentNodeCode", parentNode.getNode_code());
            jsonObject.put("brotherNodeId", brotherNodeId);


            Log.e("addNode", "brother_node_id:" + brotherNodeId);
            Log.e("addNode", "parent_node_code:" + parentNode.getNode_code());
        }
        return jsonObject;
    }

    /**
     * 获取新增同级子任务Json对象
     * @param node
     * @return
     */
    public JSONObject getAddSaveTasSubkData(NodeBean node) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", mActivity.projectId);
        jsonObject.put("bean", ProjectConstants.PROJECT_TASK_MOBULE_BEAN + mActivity.projectId);
        jsonObject.put("checkStatus", 0);
        jsonObject.put("checkMember", 0);
        jsonObject.put("associatesStatus", 0);
        jsonObject.put("endTime", 0);
        jsonObject.put("startTime", 0);
        jsonObject.put("executorId", 0);


        //jsonObject.put("data", new JSONObject());
       // jsonObject.put("parentNodeId", node.getParent_id());
        id = node.getParent_id();
        parentNode = null;
        getNodeParent(dataList);
        if (parentNode != null) {
            String brotherNodeId = "";
            for (NodeBean broNode : parentNode.getChild()) {
                if (node.getId() == broNode.getId()) {

                    if (TextUtil.isEmpty(brotherNodeId)){
                        brotherNodeId = brotherNodeId + String.valueOf(broNode.getId()) + ",-1";
                    }else {
                        brotherNodeId = brotherNodeId+"," + String.valueOf(broNode.getId()) + ",-1";
                    }
                } else {
                    if(TextUtil.isEmpty(brotherNodeId)){
                        brotherNodeId =brotherNodeId+ broNode.getId();
                    }else {
                        brotherNodeId =brotherNodeId+","+ broNode.getId();
                    }
                }
            }
            if (TextUtil.isEmpty(brotherNodeId)){
                brotherNodeId = "-1";
            }
            jsonObject.put("brotherNodeId", brotherNodeId);
            int parentTaskType = 0;
            if(parentNode.getNode_type() == 2){
                parentTaskType =1;
            }else  if(parentNode.getNode_type() == 3){
                parentTaskType =2;
            }
            jsonObject.put("parentTaskType", parentTaskType);
            jsonObject.put("taskId", parentNode.getData_id());


            Log.e("addNode", "brother_node_id:" + brotherNodeId);
            Log.e("addNode", "parent_node_code:" + parentNode.getNode_code());
        }
        return jsonObject;
    }

    /**
     * 获取新增下级主任务Json对象
     * @param node
     * @return
     */
    public JSONObject getAddNextTaskData(NodeBean node) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", mActivity.projectId);
        jsonObject.put("bean", ProjectConstants.PROJECT_TASK_MOBULE_BEAN + mActivity.projectId);
        jsonObject.put("checkStatus", 0);
        jsonObject.put("checkMember", 0);
        jsonObject.put("associatesStatus", 0);
        jsonObject.put("endTime", 0);
        jsonObject.put("startTime", 0);
        jsonObject.put("executorId", 0);
        //jsonObject.put("data", new JSONObject());
        jsonObject.put("parentNodeId", node.getId());

        String brotherNodeId = "";
        for (NodeBean broNode : node.getChild()) {
            if (node.getId() == broNode.getId()) {

                if (TextUtil.isEmpty(brotherNodeId)){
                    brotherNodeId = brotherNodeId + String.valueOf(broNode.getId()) + ",-1";
                }else {
                    brotherNodeId = brotherNodeId+"," + String.valueOf(broNode.getId()) + ",-1";
                }
            } else {
                if(TextUtil.isEmpty(brotherNodeId)){
                    brotherNodeId =brotherNodeId+ broNode.getId();
                }else {
                    brotherNodeId =brotherNodeId+","+ broNode.getId();
                }
            }
        }
        if (TextUtil.isEmpty(brotherNodeId)){
            brotherNodeId = "-1";
        }
        jsonObject.put("parentNodeCode", node.getNode_code());
        jsonObject.put("brotherNodeId", brotherNodeId);

        Log.e("addNode", "brotherNodeId:" + brotherNodeId);
        Log.e("addNode", "parentNodeCode:" + node.getNode_code());
        return jsonObject;
    }

    /**
     * 获取新增下级子任务Json对象
     * @param node
     * @return
     */
    public JSONObject getAddNextSubTaskData(NodeBean node) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", mActivity.projectId);
        jsonObject.put("bean", ProjectConstants.PROJECT_TASK_MOBULE_BEAN + mActivity.projectId);
        jsonObject.put("checkStatus", 0);
        jsonObject.put("checkMember", 0);
        jsonObject.put("associatesStatus", 0);
        jsonObject.put("endTime", 0);
        jsonObject.put("startTime", 0);
        jsonObject.put("executorId", 0);
        //jsonObject.put("data", new JSONObject());
        jsonObject.put("taskId", node.getData_id());

        String brotherNodeId = "";
        for (NodeBean broNode : node.getChild()) {
            if (node.getId() == broNode.getId()) {

                if (TextUtil.isEmpty(brotherNodeId)){
                    brotherNodeId = brotherNodeId + String.valueOf(broNode.getId()) + ",-1";
                }else {
                    brotherNodeId = brotherNodeId+"," + String.valueOf(broNode.getId()) + ",-1";
                }
            } else {
                if(TextUtil.isEmpty(brotherNodeId)){
                    brotherNodeId =brotherNodeId+ broNode.getId();
                }else {
                    brotherNodeId =brotherNodeId+","+ broNode.getId();
                }
            }
        }
        if (TextUtil.isEmpty(brotherNodeId)){
            brotherNodeId = "-1";
        }

        jsonObject.put("brotherNodeId", brotherNodeId);

        int parentTaskType = 0;
        if(node.getNode_type() == 2){
            parentTaskType =1;
        }else  if(node.getNode_type() == 3){
            parentTaskType =2;
        }
        jsonObject.put("parentTaskType",parentTaskType);

        Log.e("addNode", "brotherNodeId:" + brotherNodeId);
        Log.e("addNode", "parentTaskType:" + node.getNode_type());
        return jsonObject;
    }

    /**
     * 遍历所有节点获取父节点
     * @param node
     */
    public void getNodeParent(NodeBean node){
        if (id == node.getId()){
            parentNode =node;
        }else{
            for(NodeBean childNode : node.getChild()){
                getNodeParent(childNode);
            }
        }
    }

    /**
     * 编辑弹窗
     * @param mtype
     */
    public void showEditDialog(JSONObject data,int mtype,String name){
            editDialog = new CommomDialog(mActivity,0,name,mtype,new CommomDialog.OnCloseListener(){
                @Override
                public void onClick(CommomDialog dialog, boolean confirm,int type) {
                    if (confirm){
                       String editName = dialog.contentTxt.getText().toString();
                        if (TextUtil.isEmpty(editName)){

                            ToastUtils.showToast(mActivity,getResources().getString(R.string.project_selete_edit_node));
                            return;
                        }else{
                            if(type ==1){//编辑分类
                                long nodeId = (long) data.get("nodeId");
                                String nodeCode = (String) data.get("nodeCode");
                                editNode(editName,nodeId,nodeCode);
                            }else if (type ==2){//新增分类
                                data.put("node_name", editName);
                                addNodeClass(data);
                            }else if(type ==3){//新增主任务
                                data.put("taskName", editName);
                                addNodeTask(data);
                            }else if(type ==4){//编辑主任务
                                long taskId =(long) data.get("taskId");
                                queryTaskDetail(taskId,editName,viewDelegate.selectNode.getSelectNode());
                            }else if(type ==5){//新增子任务
                                data.put("taskName", editName);
                                addNodeSubTask(data);
                            }else if(type ==6){//编辑子任务
                                long taskId =(long) data.get("taskId");
                                querySubTaskDetail(taskId,editName,viewDelegate.selectNode.getSelectNode());
                            }
                        }
                        dialog.dismiss();
                    }

                }
            });
        if (editDialog != null && !editDialog.isShowing()){
            editDialog.show();
        }
    }

    public void showAddPopupWindow(List<String> dataList,View view,NodeBean node,int type){
            popupWindow = new CommonPopupWindow(mActivity,dataList,view,type,new CommonPopupWindow.OnClickListener(){

                @Override
                public void onClick(View view, int position,int type) {
                    if (type == 1){//新增分类
                        JSONObject jsonObject =null;
                        if (position == 0){
                            jsonObject = getAddSaveClassData(node);
                            showEditDialog(jsonObject,2,"");
                            popupWindow.dimiss();
                        }else if (position == 1){
                            jsonObject = getAddNextClassData(node,0);
                            showEditDialog(jsonObject,2,"");
                            popupWindow.dimiss();
                        }
                    }else if(type == 2){//新增任务
                        JSONObject jsonObject =null;
                        if (position == 0){//同级任务
                            if (node.getNode_type() == 1 || node.getNode_type() == 2){//新增主任务
                                jsonObject = getAddSaveTaskData(node);
                                showEditDialog(jsonObject,3,"");
                            }else{//新增同级子任务
                                jsonObject = getAddSaveTasSubkData(node);
                                showEditDialog(jsonObject,5,"");
                            }

                            popupWindow.dimiss();
                        }else if (position == 1){//下一级任务
                            if (node.getNode_type() == 1) {//新增主任务
                                jsonObject = getAddNextTaskData(node);
                                showEditDialog(jsonObject, 3,"");
                            }else{//新增下级子任务
                                jsonObject = getAddNextSubTaskData(node);
                                showEditDialog(jsonObject,5,"");
                            }
                            popupWindow.dimiss();
                        }
                    }
                }
            });
            popupWindow.show(view,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }if (requestCode == ProjectConstants.MAIN_NODE_SORT_REQUEST_CODE) {

        } else if (code == requestCode && Activity.RESULT_OK == resultCode && data != null) {
            ArrayList<EntryBean> list = (ArrayList<EntryBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            stateValue = getStateValue(list);
            remark = data.getStringExtra(Constants.DATA_TAG3);
            if (viewDelegate.selectNode.getSelectNode().getNode_type() ==2) {
                queryTaskDetail(0,null,viewDelegate.selectNode.getSelectNode());
            } else if (viewDelegate.selectNode.getSelectNode().getNode_type() ==3){
                querySubTaskDetail(0,null,viewDelegate.selectNode.getSelectNode());
            }

        }else if (Constants.REQUEST_CODE4 == requestCode){
            isRefreshData = false;
        }


    }

    public Object getStateValue(ArrayList<EntryBean> entrys) {
        List<Map<String, Object>> entryList = new ArrayList();
        for (int i = 0; i < entrys.size(); i++) {
            if (entrys.get(i).isCheck()) {
                JSONObject map = new JSONObject();
                map.put("value", entrys.get(i).getValue());
                map.put("label", entrys.get(i).getLabel());
                map.put("color", entrys.get(i).getColor());
                entryList.add(map);
            }
        }
        if (entryList.size() == 0) {
            return "";
        }
        return entryList;
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageBean event) {
        if (ProjectConstants.PROJECT_TASK_FILTER_CODE == event.getCode()) {
            Log.e("onMessage","value:"+event.getObject());
            Log.e("onMessage","tag:"+event.getTag());
            paramMap.clear();

            String sortField = "";
            String dateFormat = "";
            String bean = "";
            int indexType = 0;
            JSONObject queryWhere = new JSONObject();
            if (event.getTag() != null && !TextUtil.isEmpty(event.getTag()+"")){
                sortField = event.getTag();
            }
            Object obj = event.getObject();
            if(obj != null && obj instanceof Map){

                Map<String, Object> map = (Map<String, Object>) obj;
                if (map.get("dateFormat") != null){
                    dateFormat = map.get("dateFormat")+"";
                    map.remove("dateFormat");
                }
                if (map.get("bean") != null){
                    bean = map.get("bean")+"";
                    map.remove("bean");
                }
                if (map.get("queryType") != null && !TextUtil.isEmpty(map.get("queryType")+"")){
                    int queryType = TextUtil.parseInt(map.get("queryType")+"");
                    if (queryType == 0){
                        indexType = 1;
                    }else if (queryType == 1){
                        indexType = 3;
                    }else if (queryType == 2){
                        indexType = 2;
                    }
                }

                map.remove("queryType");
                String  jsonStr = JSONObject.toJSONString(event.getObject());
                if (!TextUtil.isEmpty(jsonStr)){
                    queryWhere = JSONObject.parseObject(jsonStr);
                }
                Log.e("queryWhere",jsonStr);
            }
            if (TextUtil.isEmpty(bean)){
                bean = "project_custom_"+mActivity.projectId;
            }
            paramMap.put("bean",bean);
            if (!TextUtil.isEmpty(dateFormat)){
                paramMap.put("dateFormat",dateFormat);
            }
            paramMap.put("queryType",indexType);
            paramMap.put("sortField",sortField);
            paramMap.put("queryWhere",queryWhere);
            String filter = JSONObject.toJSONString(paramMap);
            Log.e("filter",filter);
            getAllNode(filter);
        }
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
                Log.e("taskRole", taskRole);
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
    }



}

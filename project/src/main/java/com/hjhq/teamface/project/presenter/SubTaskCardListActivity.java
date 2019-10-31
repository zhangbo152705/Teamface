package com.hjhq.teamface.project.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CommonNewResultBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.ProjectPicklistStatusBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CloneUtils;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.customcomponent.widget2.select.PickListViewSelectActivity;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.SubPersionTaskCardApter;
import com.hjhq.teamface.project.adapter.SubTaskAdapter;
import com.hjhq.teamface.project.adapter.SubTaskCardApter;
import com.hjhq.teamface.project.adapter.TaskCardApter;
import com.hjhq.teamface.project.bean.AddPersonalSubTaskRequestBean;
import com.hjhq.teamface.project.bean.PersonalSubListResultBean;
import com.hjhq.teamface.project.bean.PersonalSubTaskBean;
import com.hjhq.teamface.project.bean.PersonalTaskDeatilListBean;
import com.hjhq.teamface.project.bean.PersonalTaskDetailResultBean;
import com.hjhq.teamface.project.bean.QueryTaskAuthResultBean;
import com.hjhq.teamface.project.bean.QueryTaskDetailResultBean;
import com.hjhq.teamface.project.bean.SavePersonalTaskLayoutRequestBean;
import com.hjhq.teamface.project.bean.SaveTaskLayoutRequestBean;
import com.hjhq.teamface.project.bean.SubListResultBean;
import com.hjhq.teamface.project.bean.SubTaskBean;
import com.hjhq.teamface.project.bean.TaskCardBean;
import com.hjhq.teamface.project.bean.TaskMemberListResultBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.presenter.task.PersonalTaskDetailActivity;
import com.hjhq.teamface.project.presenter.task.TaskDetailActivity;
import com.hjhq.teamface.project.ui.SubTaskCardDelegate;
import com.hjhq.teamface.project.ui.TaskCardDelegate;
import com.hjhq.teamface.project.view.CommomDialog;
import com.hjhq.teamface.project.widget.utils.ProjectCustomUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;

/**
 * 子任务列表
 * @author ZZH
 * @date 2018/4/10
 */
public class SubTaskCardListActivity extends ActivityPresenter<SubTaskCardDelegate, TaskModel> implements View.OnClickListener {
    protected int code = this.hashCode() % 10000;
    private CommomDialog editDialog;
    private String nodeCode ="0";
    private long taskId;//
    private long projectId;
    private String projectCustomId;
    private long taskType;//1;项目任务子任务  2:个人任务子任务
    private long parrentTaskType;//上级任务类型 1:主任务  2:子任务
    private SubTaskCardApter subTaskAdapter;//任务卡适配器
    private SubPersionTaskCardApter subPersionTaskAdapter;//任务卡适配器
    private List<TaskCardBean> mCardData = new ArrayList<>();//任务卡适配器数据
    private ArrayList<ProjectLabelBean> projectLables;
    private List<EntryBean> lableEntrys = new ArrayList<>();

    private String[] stateArr = null;

    private long currentTaskId;//编辑状态当前任务Id
    private long currentNodeId;//编辑状态当前nodeId
    private Object stateValue;
    private Map<String, Object> paramMap = new HashMap<>();

    private String projectStatus;//项目状态
    String  state = "";
    private String seleteLable;
    private String mEditNamr;
    /**
     * 任务权限
     */
    private List<QueryTaskAuthResultBean.DataBean> taskAuthList;
    /**
     * 任务权限角色
     */
    private List<String> taskRoles = new ArrayList<>();
    private boolean[] taskAuths;
    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);

        if(savedInstanceState == null){
            Intent intent = getIntent();
            if(intent != null){
                taskId = intent.getLongExtra(ProjectConstants.TASK_ID,0);
                nodeCode = intent.getStringExtra(ProjectConstants.MAIN_TASK_NODECODE);
                projectId = intent.getLongExtra(ProjectConstants.PROJECT_ID,0);
                taskType = intent.getLongExtra(ProjectConstants.TASK_FROM_TYPE,0);
                projectCustomId = intent.getStringExtra(ProjectConstants.PROJECT_CUSTOM_ID);
                projectStatus = intent.getStringExtra(ProjectConstants.PROJECT_STATUS);
                parrentTaskType =intent.getLongExtra(ProjectConstants.PARRENT_TASK_FROM_TYPE,1);
            }
        }
    }

    @Override
    public void init() {

        viewDelegate.setTitle(mContext.getResources().getString(R.string.project_sub_task));

        subTaskAdapter = new SubTaskCardApter(null);
        subPersionTaskAdapter = new SubPersionTaskCardApter(null);
        if (taskType == 1){
            viewDelegate.setAdapter(subTaskAdapter);
        }else if (taskType == 2){
            viewDelegate.setPersionTaskAdapter(subPersionTaskAdapter);
        }


        initData();
    }

    /***
     * 加载网络数据
     */
    public void initData(){
        initNetData();
    }

    public void initNetData(){
        if (taskType ==1){
            querySubList();
            queryTaskAuth();
        }else if (taskType ==2){
            queryPersionSubList();
        }

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
                permissionHandle(0,0);
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
    private void queryTaskRoles(long taskId,int qyeryType) {
        model.queryTaskMemberList(this,projectId, taskId, 2, 0, new ProgressSubscriber<TaskMemberListResultBean>(this) {
            @Override
            public void onNext(TaskMemberListResultBean taskMemberListResultBean) {
                super.onNext(taskMemberListResultBean);
                TaskMemberListResultBean.DataBean data = taskMemberListResultBean.getData();
                taskRoles.clear();
                if (!CollectionUtils.isEmpty(data.getDataList())) {
                    Observable.from(data.getDataList()).subscribe(dataBean -> taskRoles.add(dataBean.getProject_task_role()));
                }
                permissionHandle(1,qyeryType);
            }
        });
    }
    /**
     * 获取子任务
     */
    private void querySubList() {
        if (TextUtil.isEmpty(nodeCode)) {
            nodeCode = "";
        }
        model.querySubList(this, taskId, nodeCode, new ProgressSubscriber<SubListResultBean>(this) {
            @Override
            public void onNext(SubListResultBean baseBean) {
                super.onNext(baseBean);
                //子任务
                List<SubTaskBean> subTaskArr = baseBean.getData().getDataList();
                CollectionUtils.notifyDataSetChanged(subTaskAdapter, subTaskAdapter.getData(), subTaskArr);
            }
        });
    }

    /**
     * 获取个人任务子任务
     */
    private void queryPersionSubList() {
        model.queryPersonalSubList(this, taskId, new ProgressSubscriber<PersonalSubListResultBean>(this) {
            @Override
            public void onNext(PersonalSubListResultBean baseBean) {
                super.onNext(baseBean);
                //子任务
                List<PersonalSubTaskBean> subTaskArr = baseBean.getData();
                CollectionUtils.notifyDataSetChanged(subPersionTaskAdapter, subPersionTaskAdapter.getData(), subTaskArr);
            }
        });
    }

    /**
     *获取子任务项目任务布局信息
     */
    public void querySubTaskDetail(long taskId,long nodeId){
        model.querySubTaskDetailData(mContext,taskId, new ProgressSubscriber<QueryTaskDetailResultBean>(mContext,true) {
            @Override
            public void onNext(QueryTaskDetailResultBean taskAuthResultBean) {
                super.onNext(taskAuthResultBean);
                if (taskAuthResultBean != null && taskAuthResultBean.getData().getCustomArr() != null){
                    QueryTaskDetailResultBean.DataBean taskData = taskAuthResultBean.getData();
                    Object object = taskData.getCustomArr();
                    JSONObject customArr = (JSONObject) JSONObject.toJSON(object);
                    JSONObject editjsonObject = getEditTaskData(taskId,nodeId,customArr,0,taskData);
                    editTaskBoardSub(editjsonObject);
                }
            }
        });
    }

    /**
     *获取个人子任务任务任务布局信息
     */
    public void querySubPersionTaskDetail(long taskId){
        model.queryPersionSubDetaild(mContext,taskId, new ProgressSubscriber<PersonalTaskDetailResultBean>(mContext,true) {
            @Override
            public void onNext(PersonalTaskDetailResultBean taskAuthResultBean) {
                super.onNext(taskAuthResultBean);
                if (taskAuthResultBean != null && taskAuthResultBean.getData().getCustomLayout() != null){
                    PersonalTaskDetailResultBean.DataBean taskData = taskAuthResultBean.getData();
                    saveTaskLayoutData(taskData);
                }
            }
        });
    }

    /**
     *编辑子任务名称
     */
    public void editTaskBoardSub(JSONObject obj){
        model.editTaskBoardSub(mContext,obj, new ProgressSubscriber<BaseBean>(mContext,true) {
            @Override
            public void onNext(BaseBean bean) {
                super.onNext(bean);
                initNetData();
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
     * 编辑个人子任务
     * @param taskData
     */
    public void saveTaskLayoutData(PersonalTaskDetailResultBean.DataBean taskData){
        Object dataobject = taskData.getCustomLayout();
        JSONObject data = (JSONObject) JSONObject.toJSON(dataobject);
        JSONObject data2;
       if (data != null){

           data2 = (JSONObject) data.clone();

           SavePersonalTaskLayoutRequestBean personalBean = new SavePersonalTaskLayoutRequestBean();
           personalBean.setOldData(data2);

           if (data.get("attachment_customnumber") == null || TextUtil.isEmpty(data.get("attachment_customnumber").toString())){
                   data.put("attachment_customnumber",new ArrayList<Object>());
           }


           //关联组件,人员组件,部门组件处理 reference
           Set<String> Iterator = data.keySet();
           for (String key : Iterator) {
               if (!TextUtil.isEmpty(key) && key.indexOf("reference") != -1 && data.get(key) != null) {
                   final Object referenceExecution = data.get(key);
                   String referenceTag = getIdTag(referenceExecution);
                   data.put(key, referenceTag);
               }else if (!TextUtil.isEmpty(key) && key.indexOf("department") != -1 && data.get(key) != null){
                   final Object departmentExecution = data.get(key);
                   String departmentTag = getIdTag(departmentExecution);
                   data.put(key, departmentTag);
               }else if (!TextUtil.isEmpty(key) && key.indexOf("personnel") != -1 && data.get(key) != null){
                   final Object personnelExecution = data.get(key);
                   String personnelTag = getIdTag(personnelExecution);
                   data.put(key, personnelTag);
               }
           }

           //picklist_tag 处理
           String picklist_tag = "";
           if (data.get("picklist_tag") != null){
               String tagjsonArr = JSONObject.toJSONString(data.get("picklist_tag"));
               JSONArray tagja = JSONObject.parseArray(tagjsonArr);
               for(int i =0;i<tagja.size();i++){
                   JSONObject object = tagja.getJSONObject(i);
                   if (object.getLong("id") != 0){
                       if (TextUtil.isEmpty(picklist_tag)){
                           picklist_tag = picklist_tag + object.getLong("id");
                       }else {
                           picklist_tag = picklist_tag +","+ object.getLong("id");
                       }
                   }
               }
           }
           data.put("picklist_tag",picklist_tag);
           if (stateValue != null){
               data.put("picklist_status",stateValue);
           }
           personalBean.setCustomLayout(data);
           personalBean.setBean_name(ProjectConstants.PERSONAL_TASK_BEAN);
           personalBean.setId(TextUtil.parseLong(taskData.getId()));
           personalBean.setParticipants_only(taskData.getParticipants_only());
           personalBean.setRelation_id(taskData.getRelation_id());
           personalBean.setRelation_data(taskData.getRelation_data());
           personalBean.setName(taskData.getName());

           if (taskType == 2){
               model.editPersonalTaskSub(mContext, personalBean, new ProgressSubscriber<BaseBean>(mContext,false) {
                   @Override
                   public void onNext(BaseBean baseBean) {
                       super.onNext(baseBean);
                       ToastUtils.showSuccess(mContext, "编辑成功");
                        initNetData();
                       stateValue = null;
                   }

                   @Override
                   public void onError(Throwable e) {
                       super.onError(e);
                       stateValue = null;
                   }
               });
           }
       }


    }

    /**
     * 获取新增项目任务子任务Json对象
     * @return
     */
    public JSONObject getAddNextSubTaskData(String editName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", projectId);
        jsonObject.put("bean", ProjectConstants.PROJECT_TASK_MOBULE_BEAN + projectId);
        jsonObject.put("taskId", taskId);
        jsonObject.put("taskName", editName);
        jsonObject.put("name", editName);
        jsonObject.put("checkStatus", 0);
        jsonObject.put("checkMember", 0);
        jsonObject.put("associatesStatus", 0);
        jsonObject.put("endTime", 0);
        jsonObject.put("startTime", 0);
        jsonObject.put("executorId", 0);
        jsonObject.put("brotherNodeId", "");
        jsonObject.put("parentTaskType",parrentTaskType);
        return jsonObject;
    }
    /**
     * 处理数据
     */
    private void fillLabels() {
       stateArr =new String[]{mContext.getResources().getString(R.string.project_no_start),mContext.getResources().getString(R.string.project_ongoing)
                ,mContext.getResources().getString(R.string.project_suspended),mContext.getResources().getString(R.string.project_complete)};
        lableEntrys.clear();
        for(int i=0;i<stateArr.length;i++){
            EntryBean enter = new EntryBean();
            enter.setLabel(stateArr[i]);
            enter.setValue(i+"");
            enter.setFromType(Constants.STATE_FROM_PROJECR);
            lableEntrys.add(enter);
        }
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.rl_back, R.id.iv_add_bar);

        viewDelegate.recycler_view.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);

                if(adapter.getData() != null && adapter.getData().get(position) != null){
                    if (taskType == 1){//项目任务子任务
                        SubTaskBean item = (SubTaskBean) adapter.getItem(position);
                        Bundle bundle = new Bundle();
                        bundle.putLong(ProjectConstants.TASK_ID, item.getId());
                        bundle.putLong(ProjectConstants.TASK_FROM_TYPE, 2);
                        bundle.putString(ProjectConstants.TASK_NAME, item.getName());
                        bundle.putString(Constants.MODULE_BEAN, item.getBean_name());
                        bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
                        bundle.putLong(ProjectConstants.SUBNODE_ID, TextUtil.parseLong(item.getNode_id()));
                        bundle.putLong(ProjectConstants.MAIN_TASK_ID, taskId);
                        bundle.putString(ProjectConstants.MAIN_TASK_NODECODE, item.getNode_code());
                        CommonUtil.startActivtiy(SubTaskCardListActivity.this, TaskDetailActivity.class, bundle);
                    }else if (taskType == 2){//个人任务
                        PersonalSubTaskBean persionItem = (PersonalSubTaskBean) adapter.getItem(position);
                        Bundle bundle = new Bundle();
                        bundle.putLong(ProjectConstants.TASK_ID, persionItem.getId());
                        bundle.putInt(ProjectConstants.TASK_FROM_TYPE, 1);
                        bundle.putString(ProjectConstants.TASK_NAME, persionItem.getName());
                        bundle.putString(Constants.MODULE_BEAN,ProjectConstants.PERSONAL_TASK_BEAN);
                        CommonUtil.startActivtiy(SubTaskCardListActivity.this, PersonalTaskDetailActivity.class, bundle);
                    }

                }
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                ArrayList<ProjectPicklistStatusBean> status = null;
                if(adapter.getData() != null && adapter.getData().get(position) != null){
                    long id =0;
                    if (taskType == 1){
                        SubTaskBean item = (SubTaskBean) adapter.getItem(position);
                        status = item.getPicklist_status();
                        id = item.getId();
                        projectStatus = item.getProject_status();
                        currentTaskId = item.getId();
                        if(taskType == 1){
                            currentNodeId = TextUtil.parseLong(item.getNode_id());
                        }
                    }else if (taskType == 2){
                        PersonalSubTaskBean persionItem = (PersonalSubTaskBean) adapter.getItem(position);
                        status = persionItem.getPicklist_status();
                        currentTaskId = persionItem.getId();
                        if(taskType == 1){
                            currentNodeId = TextUtil.parseLong(persionItem.getNode_id());
                        }
                    }
                    if(!CollectionUtils.isEmpty(status)){
                        state =status.get(0).getLabel();
                    }

                    if (taskType == 1){
                        queryTaskRoles(id,0);
                    }else if (taskType == 2){
                        seleteStete();
                    }
                }


            }
        });

        //刷新
        viewDelegate.swipe_refresh_layout.setOnRefreshListener(() -> {
            initNetData();
            viewDelegate.swipe_refresh_layout.setRefreshing(false);
        });

    }

    public void seleteStete(){
        Bundle bundle = new Bundle();
        fillLabels();
        ArrayList<EntryBean> clone = CloneUtils.clone(((ArrayList<EntryBean>) lableEntrys));
        if(!TextUtil.isEmpty(state)){
            for(EntryBean enter: clone){
                if (enter.getLabel().equals(state)){
                    enter.setCheck(true);
                }
            }
        }
        bundle.putSerializable(Constants.DATA_TAG1, clone);
        bundle.putBoolean(Constants.DATA_TAG2, true);
        CommonUtil.startActivtiyForResult(mContext, PickListViewSelectActivity.class, code, bundle);
    }

    /**
     * 权限处理
     */
    private synchronized void permissionHandle(int type,int queryType) {
        if (CollectionUtils.isEmpty(taskRoles) || CollectionUtils.isEmpty(taskAuthList)) {
            if (type ==1){
                ToastUtils.showError(mContext, "没有权限");
            }
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
        if (queryType ==0){
            if (type ==1){
                if (taskAuths[0] && !projectStatus.equals("1") && !projectStatus.equals("2")){//编辑
                    seleteStete();
                }else {
                    ToastUtils.showError(mContext, "没有权限");
                }
            }
        }else if (queryType ==1){
            if (type ==1){
                if (taskAuths[7] && !projectStatus.equals("1") && !projectStatus.equals("2")){//新增子任务
                    addNodeSubTask(mEditNamr);
                }else {
                    ToastUtils.showError(mContext, "没有权限");
                }
            }
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_back) {
            finish();
        }  else if (id == R.id.iv_add_bar) {
                showEditDialog();
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
                    mEditNamr = dialog.contentTxt.getText().toString();
                    if (TextUtil.isEmpty(mEditNamr)){
                        ToastUtils.showToast(mContext,getResources().getString(R.string.project_selete_edit_node));
                        return;
                    }else {
                        if (taskType == 1){
                            queryTaskRoles(taskId,1);
                        }else if (taskType ==2){
                            addPersionSubTask(mEditNamr);
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


    /**
     * 增加子任务
     */
    private void addNodeSubTask( String editName) {
        JSONObject obj = getAddNextSubTaskData(editName);
        model.addNodeSubTask(mContext,obj, new ProgressSubscriber<BaseBean>(mContext, true) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                initNetData();
            }
        });
    }

    /**
     * 增加个人任务子任务
     */
    public void addPersionSubTask(String editName){
        AddPersonalSubTaskRequestBean bean = new AddPersonalSubTaskRequestBean();
        bean.setName(editName);
        bean.setEnd_time("");
        bean.setTask_id(taskId + "");
        bean.setExecutor_id("");
        bean.setProject_custom_id(projectCustomId);
        bean.setRelation_data("");
        bean.setRelation_id("");
        model.addPersonalSubTask(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mContext, "新增成功");
                initNetData();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (!viewDelegate.closeDrawer()) {
            super.onBackPressed();
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
                seleteLable = entrys.get(i).getLabel();
                entryList.add(map);
            }
        }
        if (entryList.size() == 0) {
            return "";
        }
        return entryList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (code == requestCode && Activity.RESULT_OK == resultCode && data != null) {
            ArrayList<EntryBean> list = (ArrayList<EntryBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            stateValue = getStateValue(list);
            if (taskType ==1){
                if (mContext.getResources().getString(R.string.project_complete).indexOf(seleteLable) != -1){
                    if (taskAuths[1]){//判断完成权限
                        querySubTaskDetail(currentTaskId,currentNodeId);
                    }else {
                        ToastUtils.showError(mContext, "没有权限");
                    }
                }else {
                    querySubTaskDetail(currentTaskId,currentNodeId);
                }

            }else if (taskType ==2){
                querySubPersionTaskDetail(currentTaskId);
            }
        }
    }

    /**
     * 获取编辑任务json对象
     */
    public JSONObject getEditTaskData(long taskId, long nodeId, JSONObject data, int type, QueryTaskDetailResultBean.DataBean taskData){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", projectId);
        jsonObject.put("taskId",taskData.getTask_id());
        jsonObject.put("id",taskData.getId());
        jsonObject.put("bean",ProjectConstants.PROJECT_TASK_MOBULE_BEAN + projectId);
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

        jsonObject.put("associatesStatus",taskData.getAssociates_status());

        jsonObject.put("taskName",data.get("text_name"));
        jsonObject.put("endTime",endTime);
        jsonObject.put("startTime",startTime);

        String personnel_principal = "";
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

        data.put("datetime_deadline",endTime);
        data.put("datetime_starttime",startTime);
        jsonObject.put("oldData",data);

        JSONObject cldata = (JSONObject) data.clone();
        cldata.put("personnel_principal",personnel_principal);
        cldata.put("picklist_tag",picklist_tag);
        if (stateValue != null){
            cldata.put("picklist_status",stateValue);
        }
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
        jsonObject.put("remark","");
        jsonObject.put("nodeId",currentNodeId);

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


}

package com.hjhq.teamface.project.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
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
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CloneUtils;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.JsonParser;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.select.PickListView;
import com.hjhq.teamface.customcomponent.widget2.select.PickListViewSelectActivity;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.TaskCardApter;
import com.hjhq.teamface.project.bean.NodeBean;
import com.hjhq.teamface.project.bean.PersonalTaskBean;
import com.hjhq.teamface.project.bean.PersonalTaskDeatilListBean;
import com.hjhq.teamface.project.bean.PersonalTaskDetailResultBean;
import com.hjhq.teamface.project.bean.PersonalTaskResultBean;
import com.hjhq.teamface.project.bean.ProjectLabelsListBean;
import com.hjhq.teamface.project.bean.QueryTaskAuthResultBean;
import com.hjhq.teamface.project.bean.QueryTaskDetailResultBean;
import com.hjhq.teamface.project.bean.SavePersonalTaskLayoutRequestBean;
import com.hjhq.teamface.project.bean.SaveTaskLayoutRequestBean;
import com.hjhq.teamface.project.bean.TaskCardBean;
import com.hjhq.teamface.project.bean.TaskMemberListResultBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.presenter.add.AddProjectActivity;
import com.hjhq.teamface.project.presenter.task.PersonalTaskDetailActivity;
import com.hjhq.teamface.project.presenter.task.TaskDetailActivity;
import com.hjhq.teamface.project.ui.ProjectDelegate;
import com.hjhq.teamface.project.ui.TaskCardDelegate;
import com.hjhq.teamface.project.view.CommomDialog;
import com.luojilab.router.facade.annotation.RouteNode;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.functions.Func1;

/**
 * 任务列表
 * @author ZZH
 * @date 2018/4/10
 */
public class TaskCardListActivity extends ActivityPresenter<TaskCardDelegate, ProjectModel> implements View.OnClickListener {
    protected int code = this.hashCode() % 10000;
    private String keyword;
    private CommomDialog editDialog;
    private String projectId ="0";
    private String title;
    private long taskType;//1:个人任务 2:项目任务
    private int indexType =0;//0:全部任务 1:我负责 2:我参与 3:我创建 4:已完成
    private int pageNum = 1;//当前页码
    private int pageSize =20;//
    private JSONObject queryWhere=null;//查询条件
    private String dateFormat="";
    private String sortField ="";

    private TaskCardApter mTaskCardAdapter;//任务卡适配器
    private List<TaskCardBean> mCardData = new ArrayList<>();//任务卡适配器数据
    private ArrayList<ProjectLabelBean> projectLables;
    private List<EntryBean> lableEntrys = new ArrayList<>();

    private String[] stateArr = null;

    private long currentTaskId;//编辑状态当前任务Id
    private long currentNodeId;//编辑状态当前nodeId
    private Object stateValue;
    private Map<String, Object> paramMap = new HashMap<>();
    private String remark;
    private String projectStatus;//项目状态
    private String  state = "";
    private String seleteLable;
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
                title = intent.getStringExtra(ProjectConstants.PROJECT_PERSION_TASK_CARD_TITLE);
                if(!TextUtil.isEmpty(intent.getStringExtra(ProjectConstants.PROJECT_ID))){
                    projectId = intent.getStringExtra(ProjectConstants.PROJECT_ID);
                }
                taskType = intent.getLongExtra(ProjectConstants.PROJECT_PERSION_TASK_CARD_TYPE,0);
                indexType = intent.getIntExtra(ProjectConstants.PROJECT_PERSION_TASK_CARD_INDEXT_TITLE,0);

            }
        }
    }

    @Override
    public void init() {

        if (taskType ==2){//项目任务
            viewDelegate.initFilter(TextUtil.parseLong(projectId),1);
            viewDelegate.hideAddBar(true);
        }else if (taskType == 1){//个人任务
            viewDelegate.initFilter(0,0);
            viewDelegate.hideAddBar(false);
        }
        viewDelegate.setTitle(title);

        mTaskCardAdapter = new TaskCardApter(null);
        viewDelegate.setAdapter(mTaskCardAdapter);

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
            requestPersionTaskList();
        }else if (taskType ==2){
            requestProjectTaskList();
            queryTaskAuth();
        }
    }


    /**
     * 获取任务权限
     */
    private void queryTaskAuth() {
        model.queryTaskAuthList(this, TextUtil.parseLong(projectId), new ProgressSubscriber<QueryTaskAuthResultBean>(this) {
            @Override
            public void onNext(QueryTaskAuthResultBean taskAuthResultBean) {
                super.onNext(taskAuthResultBean);
                taskAuthList = taskAuthResultBean.getData();
                permissionHandle(0);
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
    private void queryTaskRoles(long taskId) {
        model.queryTaskMemberList(this, TextUtil.parseLong(projectId), taskId, 1, 0, new ProgressSubscriber<TaskMemberListResultBean>(this) {
            @Override
            public void onNext(TaskMemberListResultBean taskMemberListResultBean) {
                super.onNext(taskMemberListResultBean);
                TaskMemberListResultBean.DataBean data = taskMemberListResultBean.getData();
                taskRoles.clear();
                if (!CollectionUtils.isEmpty(data.getDataList())) {
                    Observable.from(data.getDataList()).subscribe(dataBean -> taskRoles.add(dataBean.getProject_task_role()));
                }
                permissionHandle(1);
            }
        });
    }
    /**
     * 加载个人任务
     */
    private void requestPersionTaskList() {
        JSONObject jsonObject = getQuerryJsonData();
        model.queryPersonalTaskList(mContext, jsonObject, new ProgressSubscriber<PersonalTaskDeatilListBean>(mContext, true) {
            @Override
            public void onNext(PersonalTaskDeatilListBean bean) {
                super.onNext(bean);
                if(bean.getData() != null && bean.getData().getDataList() != null && !bean.getData().getDataList().isEmpty()){
                    mCardData = bean.getData().getDataList();
                    CollectionUtils.notifyDataSetChanged(mTaskCardAdapter, mTaskCardAdapter.getData(), bean.getData().getDataList());
                    //mTaskCardAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 加载项目任务
     */
    private void requestProjectTaskList() {
        JSONObject jsonObject = getQuerryJsonData();
        model.queryProjectTaskList(mContext, jsonObject, new ProgressSubscriber<PersonalTaskDeatilListBean>(mContext, true) {
            @Override
            public void onNext(PersonalTaskDeatilListBean bean) {
                super.onNext(bean);
                if(bean.getData() != null && bean.getData().getDataList() != null && !bean.getData().getDataList().isEmpty()){
                    mCardData = bean.getData().getDataList();
                    CollectionUtils.notifyDataSetChanged(mTaskCardAdapter, mTaskCardAdapter.getData(), bean.getData().getDataList());
                    //mTaskCardAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     *获取主任务布局信息
     */
    public void queryTaskDetail(long taskId,long nodeId){
        model.queryTaskDetail(mContext,taskId, new ProgressSubscriber<QueryTaskDetailResultBean>(mContext,true) {
            @Override
            public void onNext(QueryTaskDetailResultBean taskAuthResultBean) {
                super.onNext(taskAuthResultBean);
                if (taskAuthResultBean != null && taskAuthResultBean.getData().getCustomArr() != null){
                    QueryTaskDetailResultBean.DataBean taskData = taskAuthResultBean.getData();
                    Object object = taskData.getCustomArr();
                    JSONObject customArr = (JSONObject) JSONObject.toJSON(object);
                    JSONObject editjsonObject = getEditTaskData(taskId,nodeId,customArr,0,taskData);
                    editTaskBoard(editjsonObject);
                }
            }
        });
    }

    /**
     *获取个人任务任务布局信息
     */
    public void queryPersionTaskDetail(long taskId){
        model.PersonalTaskDetailData(mContext,taskId, new ProgressSubscriber<PersonalTaskDetailResultBean>(mContext,true) {
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
     *编辑主任务名称
     */
    public void editTaskBoard(JSONObject obj){
        model.editTaskBoard(mContext,obj, new ProgressSubscriber<BaseBean>(mContext,true) {
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
    /**
     * 获取网络请求数据
     * @return
     */
    public JSONObject getQuerryJsonData(){
        JSONObject obj = new JSONObject();
        obj.put("pageNum",pageNum);
        obj.put("pageSize",pageSize);
        if (queryWhere != null){
            obj.put("queryWhere",queryWhere);
        }
        if (!TextUtil.isEmpty(sortField)){
            obj.put("sortField",sortField);
        }
        if (!TextUtil.isEmpty(dateFormat)){
            obj.put("dateFormat",dateFormat);
        }
        obj.put("queryType",indexType);
        if (taskType != 1){
            obj.put("projectId",Long.parseLong(projectId));
            obj.put("bean",ProjectConstants.PROJECT_TASK_MOBULE_BEAN + projectId);
        }
        return obj;
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.rl_back, R.id.iv_add_bar, R.id.iv_filtrate_bar);

        viewDelegate.recycler_view.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                if(adapter.getData() != null && adapter.getData().get(position) != null){

                    TaskCardBean item = (TaskCardBean) adapter.getData().get(position);

                    Bundle bundle = new Bundle();
                    if (taskType == 1) {
                        bundle.putLong(ProjectConstants.TASK_ID, item.getId());
                        bundle.putString(ProjectConstants.TASK_NAME, item.getText_name());
                        bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PERSONAL_TASK_BEAN);
                        CommonUtil.startActivtiy(mContext, PersonalTaskDetailActivity.class, bundle);
                    } else if(taskType == 2) {
                        bundle.putLong(ProjectConstants.PROJECT_ID, TextUtil.parseLong(projectId));
                        bundle.putLong(ProjectConstants.TASK_ID,item.getId());
                        bundle.putString(ProjectConstants.TASK_NAME, item.getText_name());
                        bundle.putString(Constants.MODULE_BEAN, item.getBean_name());
                        bundle.putLong(ProjectConstants.TASK_FROM_TYPE,1);
                        bundle.putLong(ProjectConstants.MAIN_TASK_ID,0);
                        bundle.putString(ProjectConstants.MAIN_TASK_NODECODE,"");
                        bundle.putString(ProjectConstants.MAIN_TASK_NODE_ID,item.getSub_id());
                        bundle.putLong(ProjectConstants.SUBNODE_ID, TextUtil.parseLong(item.getSub_id()));
                        CommonUtil.startActivtiy(mContext, TaskDetailActivity.class, bundle);
                    }

                }

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);

                long id =0;
                if(adapter.getData() != null && adapter.getData().get(position) != null){
                            TaskCardBean item = (TaskCardBean) adapter.getData().get(position);
                    ArrayList<ProjectPicklistStatusBean> status = item.getPicklist_status();
                    projectStatus = item.getProject_status();
                    id = item.getId();

                    if(!CollectionUtils.isEmpty(status)){
                        state =status.get(0).getLabel();
                    }
                    currentTaskId = item.getId();
                    if (taskType == 2){
                        currentNodeId = TextUtil.parseLong(item.getSub_id());
                    }
                }
                if (taskType == 1){
                    seleteStete();
                }else if (taskType == 2){
                    queryTaskRoles(id);
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
        bundle.putInt(Constants.DATA_TAG3, 1);
        CommonUtil.startActivtiyForResult(mContext, PickListViewSelectActivity.class, code, bundle);
    }

    /**
     * 权限处理
     */
    private synchronized void permissionHandle(int type) {
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
        if (type ==1){
            if (taskAuths[0] && !projectStatus.equals("1") && !projectStatus.equals("2")){
                seleteStete();
            }else {
                ToastUtils.showError(mContext, "没有权限");
            }
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_back) {
            finish();
        }  else if (id == R.id.iv_add_bar) {
            if(taskType == 1){
                showEditDialog();
            }
         } else if (id == R.id.iv_filtrate_bar) {
               viewDelegate.openDrawer();
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
                        if (taskType ==1){//个人任务
                            addPersonTask(editName);
                        }else if (taskType ==2){//项目任务
                            addNodeTask(editName);
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
     * 新增项目任务
     */
    private void addNodeTask(String taskName) {
        JSONObject obj =getAddNextTaskData();
        obj.put("taskName", taskName);
        model.addNodeTask(mContext,obj, new ProgressSubscriber<BaseBean>(mContext, true) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                initNetData();
            }
        });
    }
    /**
     * 新增个人任务
     * @param taskName
     */
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
                    initNetData();
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
            remark = data.getStringExtra(Constants.DATA_TAG3);
            if (taskType ==1){
                queryPersionTaskDetail(currentTaskId);
            }else if (taskType ==2){
                if (mContext.getResources().getString(R.string.project_complete).indexOf(seleteLable) != -1) {
                    if (taskAuths[1]) {//判断完成权限
                        queryTaskDetail(currentTaskId, currentNodeId);
                    }else {
                        ToastUtils.showError(mContext, "没有权限");
                    }
                }else {
                    queryTaskDetail(currentTaskId, currentNodeId);
                }
            }
        }
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageBean event) {
        if (ProjectConstants.PERSONAL_TASK_FILTER_CODE == event.getCode() || ProjectConstants.PROJECT_TASK_FILTER_CODE == event.getCode()) {
            viewDelegate.closeDrawer();
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
                map.remove("bean");
                map.remove("queryType");
                String  jsonStr = JSONObject.toJSONString(event.getObject());
                queryWhere = JSONObject.parseObject(jsonStr);
            }
            initNetData();

        }
    }

    /**
     * 获取新增下级主任务Json对象
     * @return
     */
    public JSONObject getAddNextTaskData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", projectId);
        jsonObject.put("bean", ProjectConstants.PROJECT_TASK_MOBULE_BEAN + projectId);
        jsonObject.put("checkStatus", 0);
        jsonObject.put("checkMember", 0);
        jsonObject.put("associatesStatus", 0);
        jsonObject.put("endTime", 0);
        jsonObject.put("startTime", 0);
        jsonObject.put("executorId", 0);
        //jsonObject.put("parentNodeId", );
       // jsonObject.put("parentNodeCode", node.getNode_code());
        //jsonObject.put("brotherNodeId", brotherNodeId);

        //Log.e("addNode", "brotherNodeId:" + brotherNodeId);
       // Log.e("addNode", "parentNodeCode:" + node.getNode_code());
        return jsonObject;
    }


    /**
     * 获取编辑任务json对象
     */
    public JSONObject getEditTaskData(long taskId, long nodeId, JSONObject data, int type, QueryTaskDetailResultBean.DataBean taskData){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", projectId);
        jsonObject.put("taskId",taskId);
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



        data.put("datetime_deadline",endTime);
        data.put("datetime_starttime",startTime);
        jsonObject.put("oldData",data);

        JSONObject cldata = (JSONObject) data.clone();
        //cldata.put("personnel_principal",personnel_principal);

        //优先级处理
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
        if (stateValue != null){
            cldata.put("picklist_status",stateValue);
        }

        //关联组件,人员组件,部门组件处理 reference
        Set<String> referenceIterator = cldata.keySet();
        for (String key : referenceIterator) {
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
        //jsonObject.put("nodeId",nodeId);
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
     * 编辑个人任务
     * @param taskData
     */
    public void saveTaskLayoutData(PersonalTaskDetailResultBean.DataBean taskData) {

        Object dataobject = taskData.getCustomLayout();
        JSONObject data = (JSONObject) JSONObject.toJSON(dataobject);
        JSONObject data2;
        if (data != null) {
            data2 = (JSONObject) data.clone();
            SavePersonalTaskLayoutRequestBean personalBean = new SavePersonalTaskLayoutRequestBean();
            personalBean.setOldData(data2);

            if (data.get("attachment_customnumber") == null || TextUtil.isEmpty(data.get("attachment_customnumber").toString())) {
                data.put("attachment_customnumber", new ArrayList<Object>());
            }



            //关联组件,人员组件,部门组件处理 reference
            Set<String> iterator = data.keySet();
            for (String key : iterator) {
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
            if (!TextUtil.isEmpty(remark)){
                personalBean.setRemark(remark);
            }
            if (taskType ==1) {
                model.editPersonalTask(mContext, personalBean, new ProgressSubscriber<BaseBean>(mContext,false) {
                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                        ToastUtils.showSuccess(mContext, "编辑成功");
                        initNetData();
                        stateValue =null;
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
}

package com.hjhq.teamface.basis.bean;

import com.hjhq.teamface.basis.database.Member;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务信息
 * Created by Administrator on 2018/6/25.
 */

public class TaskInfoBean implements Serializable {
    /**
     * share_ids :
     * create_time : 1528103790851
     * remind_status : 1
     * modify_time :
     * dataType : 1
     * taskInfoId : 218
     * del_status : 0
     * modify_by :
     * title : cbcvbcvb
     * content : [{"num":0,"check":0,"type":1,"content":"cbcvbcvb"}]
     * create_by : 2
     * items_arr : []
     * remind_time : 0
     * completeStatus : 0
     * location : []
     * beanName : memo
     * id : 1151
     * pic_url :
     * beanId : 1151
     */
    //任务ID
    //private long taskInfoId;
    private long project_id;

    //各个类型数据的ID
    private long id;
   // private String beanName;
    private String bean_name;
    private long bean_id;
    //执行人头像地址
    private String pic_url;
    private String picture;
    private long create_time;
    //图标信息
    private String icon_url;
    private String icon_color;
    private String icon_type;

    //任务名称
    private String task_name;
    //标签名称
    private String lableName;
    //任务截止时间
    private String datetime_deadline;
    //任务开始时间
    private String datetime_starttime;
    // 1备忘录 2任务 3自定义模块数据 4审批数据
    private int dataType;
    //工作台 1审批 2项目主任务 3项目子任务 4个人主任务 5个人子任务
    private int beanType;
    //0项目任务 1个人任务
    private int from;
    // 关联任务id，如果没有关联为 -1
    private long quote_id;
    //private long quoteTaskId;
    //1关联,0未关联
    private String quote_status;
    //通过状态 0否 1是 2 驳回
    private String passed_status;
    //执行人
    private List<Member> personnel_principal;
    //任务描述
    private String multitext_desc;
    //任务名称
    private String text_name;
    // '是否完成 0否 1是';
    private String complete_status;
    //check_status 0：无需校验 1：需校验
    private String check_status;
    //激活次数
    private String complete_number;
    //任务标签
    private ArrayList<ProjectLabelBean> picklist_tag;
    private String sub_task_count;
    private String sub_task_complete_count;

    //时间工作台id
    private String timeId;

    // 备忘名称
    private String memoName;
    private int create_by;
    //备忘录创建人
    private Member createObj;
    private String content;
    private String title;

    //自定义模块
    private List<RowBean> row;
    private Row rows;
    //模块中文名
    private String module_name;

    //审批
    private String process_name;
    private String begin_user_name;
    private String process_status;
    private String project_status;
    //不为空子任务,为空主任务
    private String task_id;
    private String task_key;
    private String process_v;
    private String process_key;
    private String process_definition_id;
    private String process_field_v;
    private String approval_data_id;
    //选中
    private boolean check;

    private String project_name;//项目名称
    //private ArrayList<ProjectPicklistStatusBean> picklist_priority;//优先级
    private String sub_id;
    private String module_id;
    private String node_code;
    private int bean_type;
    private ArrayList<ProjectPicklistStatusBean> picklist_status;
    private ArrayList<ProjectPicklistStatusBean> picklist_priority;

    private List<Member> personnel_execution;

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public Row getRows() {
        return rows;
    }

    public void setRows(Row rows) {
        this.rows = rows;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getIcon_color() {
        return icon_color;
    }

    public void setIcon_color(String icon_color) {
        this.icon_color = icon_color;
    }

    public String getIcon_type() {
        return icon_type;
    }

    public void setIcon_type(String icon_type) {
        this.icon_type = icon_type;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLableName() {
        return lableName;
    }

    public void setLableName(String lableName) {
        this.lableName = lableName;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getDatetime_deadline() {
        return datetime_deadline;
    }

    public void setDatetime_deadline(String datetime_deadline) {
        this.datetime_deadline = datetime_deadline;
    }


    public List<ProjectLabelBean> getPicklist_tag() {
        return picklist_tag;
    }

    public void setPicklist_tag(ArrayList<ProjectLabelBean> picklist_tag) {
        this.picklist_tag = picklist_tag;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getBeanType() {
        return beanType;
    }

    public void setBeanType(int beanType) {
        this.beanType = beanType;
    }

    public String getMemoName() {
        return memoName;
    }

    public void setMemoName(String memoName) {
        this.memoName = memoName;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public int getCreate_by() {
        return create_by;
    }

    public void setCreate_by(int create_by) {
        this.create_by = create_by;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getText_name() {
        return text_name;
    }

    public void setText_name(String text_name) {
        this.text_name = text_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public List<RowBean> getRow() {
        return row;
    }

    public String getComplete_number() {
        return complete_number;
    }

    public void setComplete_number(String complete_number) {
        this.complete_number = complete_number;
    }

    public void setRow(List<RowBean> row) {
        this.row = row;
    }

    public String getProcess_name() {
        return process_name;
    }

    public void setProcess_name(String process_name) {
        this.process_name = process_name;
    }

    public String getBegin_user_name() {
        return begin_user_name;
    }

    public void setBegin_user_name(String begin_user_name) {
        this.begin_user_name = begin_user_name;
    }

    public String getProcess_status() {
        return process_status;
    }

    public void setProcess_status(String process_status) {
        this.process_status = process_status;
    }

    public String getProject_status() {
        return project_status;
    }

    public void setProject_status(String project_status) {
        this.project_status = project_status;
    }

    public String getMultitext_desc() {
        return multitext_desc;
    }

    public void setMultitext_desc(String multitext_desc) {
        this.multitext_desc = multitext_desc;
    }

    public String getQuote_status() {
        return quote_status;
    }

    public void setQuote_status(String quote_status) {
        this.quote_status = quote_status;
    }

    public String getPassed_status() {
        return passed_status;
    }

    public void setPassed_status(String passed_status) {
        this.passed_status = passed_status;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_key() {
        return task_key;
    }

    public void setTask_key(String task_key) {
        this.task_key = task_key;
    }

    public String getProcess_v() {
        return process_v;
    }

    public void setProcess_v(String process_v) {
        this.process_v = process_v;
    }

    public String getProcess_key() {
        return process_key;
    }

    public void setProcess_key(String process_key) {
        this.process_key = process_key;
    }

    public String getProcess_definition_id() {
        return process_definition_id;
    }

    public void setProcess_definition_id(String process_definition_id) {
        this.process_definition_id = process_definition_id;
    }

    public String getProcess_field_v() {
        return process_field_v;
    }

    public void setProcess_field_v(String process_field_v) {
        this.process_field_v = process_field_v;
    }

    public String getApproval_data_id() {
        return approval_data_id;
    }

    public void setApproval_data_id(String approval_data_id) {
        this.approval_data_id = approval_data_id;
    }

    public String getComplete_status() {
        return complete_status;
    }

    public void setComplete_status(String complete_status) {
        this.complete_status = complete_status;
    }

    public String getCheck_status() {
        return check_status;
    }

    public void setCheck_status(String check_status) {
        this.check_status = check_status;
    }

    public String getSub_task_count() {
        return sub_task_count;
    }

    public void setSub_task_count(String sub_task_count) {
        this.sub_task_count = sub_task_count;
    }

    public String getSub_task_complete_count() {
        return sub_task_complete_count;
    }

    public void setSub_task_complete_count(String sub_task_complete_count) {
        this.sub_task_complete_count = sub_task_complete_count;
    }

    public String getTimeId() {
        return timeId;
    }

    public void setTimeId(String timeId) {
        this.timeId = timeId;
    }

    public long getProject_id() {
        return project_id;
    }

    public void setProject_id(long project_id) {
        this.project_id = project_id;
    }

    public Member getCreateObj() {
        return createObj;
    }

    public void setCreateObj(Member createObj) {
        this.createObj = createObj;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public String getDatetime_starttime() {
        return datetime_starttime;
    }

    public void setDatetime_starttime(String datetime_starttime) {
        this.datetime_starttime = datetime_starttime;
    }

    public String getProject_name() {
        return project_name;
    }



    public String getSub_id() {
        return sub_id;
    }

    public String getModule_id() {
        return module_id;
    }

    public String getNode_code() {
        return node_code;
    }

    public int getBean_type() {
        return bean_type;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }



    public void setSub_id(String sub_id) {
        this.sub_id = sub_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public void setNode_code(String node_code) {
        this.node_code = node_code;
    }

    public void setBean_type(int bean_type) {
        this.bean_type = bean_type;
    }

    public ArrayList<ProjectPicklistStatusBean> getPicklist_status() {
        return picklist_status;
    }

    public void setPicklist_status(ArrayList<ProjectPicklistStatusBean> picklist_status) {
        this.picklist_status = picklist_status;
    }

    public List<Member> getPersonnel_principal() {
        return personnel_principal;
    }

    public List<Member> getPersonnel_execution() {
        return personnel_execution;
    }

    public void setPersonnel_principal(List<Member> personnel_principal) {
        this.personnel_principal = personnel_principal;
    }

    public void setPersonnel_execution(List<Member> personnel_execution) {
        this.personnel_execution = personnel_execution;
    }

    public ArrayList<ProjectPicklistStatusBean> getPicklist_priority() {
        return picklist_priority;
    }

    public void setPicklist_priority(ArrayList<ProjectPicklistStatusBean> picklist_priority) {
        this.picklist_priority = picklist_priority;
    }

    public long getBean_id() {
        return bean_id;
    }

    public void setBean_id(long bean_id) {
        this.bean_id = bean_id;
    }

    public String getBean_name() {
        return bean_name;
    }

    public void setBean_name(String bean_name) {
        this.bean_name = bean_name;
    }

    public long getQuote_id() {
        return quote_id;
    }

    public void setQuote_id(long quote_id) {
        this.quote_id = quote_id;
    }
}

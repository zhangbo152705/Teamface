package com.hjhq.teamface.project.bean;

import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.ProjectPicklistStatusBean;
import com.hjhq.teamface.basis.database.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/23.
 * Describe：
 */

public class TaskCardBean {

    private String relation_data;
    private String reference_relation;
    private String del_status;
    private Long project_custom_id;
    //项目截止时间
    private String datetime_deadline;
    //项目开始时间
    private String datetime_starttime;
    private String bean_name;
    private String picklist_tag_v;
    private int from;
    List<ProjectLabelBean> picklist_tag;
    private Long id;
    private String sub_id;
    private Long project_id;
    private String personnel_create_by;
    private String multitext_desc;
    private String complete_status;
    //个人任务字段 激活次数
    private int activate_number;
    //子任务总数
    private int subtotal;
    //子任务完成数量
    private int subfinishtotal;
    private String check_member;
    private String check_status;
    private String relation_id;
    private String participants_only;
    private List<Member> personnel_principal;
    private String employee_id;
    private String name;
    private String text_name;
    private ArrayList<ProjectPicklistStatusBean> picklist_priority;
    private ArrayList<ProjectPicklistStatusBean> picklist_status;
    private String project_status;

    public String getRelation_data() {
        return relation_data;
    }

    public String getReference_relation() {
        return reference_relation;
    }

    public String getDel_status() {
        return del_status;
    }

    public Long getProject_custom_id() {
        return project_custom_id;
    }

    public String getDatetime_deadline() {
        return datetime_deadline;
    }

    public String getDatetime_starttime() {
        return datetime_starttime;
    }

    public String getBean_name() {
        return bean_name;
    }

    public String getPicklist_tag_v() {
        return picklist_tag_v;
    }

    public int getFrom() {
        return from;
    }

    public List<ProjectLabelBean> getPicklist_tag() {
        return picklist_tag;
    }

    public Long getId() {
        return id;
    }

    public String getSub_id() {
        return sub_id;
    }

    public Long getProject_id() {
        return project_id;
    }

    public String getPersonnel_create_by() {
        return personnel_create_by;
    }

    public String getMultitext_desc() {
        return multitext_desc;
    }

    public String getComplete_status() {
        return complete_status;
    }

    public int getActivate_number() {
        return activate_number;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public int getSubfinishtotal() {
        return subfinishtotal;
    }

    public String getCheck_member() {
        return check_member;
    }

    public String getCheck_status() {
        return check_status;
    }

    public String getRelation_id() {
        return relation_id;
    }

    public String getParticipants_only() {
        return participants_only;
    }

    public List<Member> getPersonnel_principal() {
        return personnel_principal;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public String getName() {
        return name;
    }

    public String getText_name() {
        return text_name;
    }

    public void setRelation_data(String relation_data) {
        this.relation_data = relation_data;
    }

    public void setReference_relation(String reference_relation) {
        this.reference_relation = reference_relation;
    }

    public void setDel_status(String del_status) {
        this.del_status = del_status;
    }

    public void setProject_custom_id(Long project_custom_id) {
        this.project_custom_id = project_custom_id;
    }

    public void setDatetime_deadline(String datetime_deadline) {
        this.datetime_deadline = datetime_deadline;
    }

    public void setDatetime_starttime(String datetime_starttime) {
        this.datetime_starttime = datetime_starttime;
    }

    public void setBean_name(String bean_name) {
        this.bean_name = bean_name;
    }

    public void setPicklist_tag_v(String picklist_tag_v) {
        this.picklist_tag_v = picklist_tag_v;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setPicklist_tag(List<ProjectLabelBean> picklist_tag) {
        this.picklist_tag = picklist_tag;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSub_id(String sub_id) {
        this.sub_id = sub_id;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public void setPersonnel_create_by(String personnel_create_by) {
        this.personnel_create_by = personnel_create_by;
    }

    public void setMultitext_desc(String multitext_desc) {
        this.multitext_desc = multitext_desc;
    }

    public void setComplete_status(String complete_status) {
        this.complete_status = complete_status;
    }

    public void setActivate_number(int activate_number) {
        this.activate_number = activate_number;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public void setSubfinishtotal(int subfinishtotal) {
        this.subfinishtotal = subfinishtotal;
    }

    public void setCheck_member(String check_member) {
        this.check_member = check_member;
    }

    public void setCheck_status(String check_status) {
        this.check_status = check_status;
    }

    public void setRelation_id(String relation_id) {
        this.relation_id = relation_id;
    }

    public void setParticipants_only(String participants_only) {
        this.participants_only = participants_only;
    }

    public void setPersonnel_principal(List<Member> personnel_principal) {
        this.personnel_principal = personnel_principal;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText_name(String text_name) {
        this.text_name = text_name;
    }

    public ArrayList<ProjectPicklistStatusBean> getPicklist_priority() {
        return picklist_priority;
    }

    public void setPicklist_priority(ArrayList<ProjectPicklistStatusBean> picklist_priority) {
        this.picklist_priority = picklist_priority;
    }

    public ArrayList<ProjectPicklistStatusBean> getPicklist_status() {
        return picklist_status;
    }

    public void setPicklist_status(ArrayList<ProjectPicklistStatusBean> picklist_status) {
        this.picklist_status = picklist_status;
    }

    public String getProject_status() {
        return project_status;
    }

    public void setProject_status(String project_status) {
        this.project_status = project_status;
    }
}

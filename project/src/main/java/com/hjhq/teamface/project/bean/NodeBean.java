package com.hjhq.teamface.project.bean;

import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.database.Member;

import java.io.Serializable;
import java.util.List;

/**
 * 节点实体
 *
 * @author Administrator
 * @date 2018/4/25
 */

public class NodeBean implements Serializable {
    private long project_id;
    private long main_id;
    private String name;
    private long id;
    private String flow_id;
    private String temp_id;
    private String create_by;
    private List<NodeBean> subnodeArr;
    private String children_data_type;
    private boolean isCheck;

    //zzh->ad:改版新增字段
    private int node_type;
    private int node_level;
    private String data_id;
    private long parent_id;
    private String node_code;
    private String is_mileage;
    private List<NodeBean> child;
    private String node_name;
    private String task_name;
    private int sort;
    private int main_task_count;
    private int main_task_complete_num;
    private long main_task_min_start_time;
    private long main_task_max_end_time;
    private long datetime_deadline;
    private TaskInfoBean task_info;
    //执行人
    private List<Member> personnel_principal;



    public String getFlow_id() {
        return flow_id;
    }

    public void setFlow_id(String flow_id) {
        this.flow_id = flow_id;
    }

    public String getTemp_id() {
        return temp_id;
    }

    public void setTemp_id(String temp_id) {
        this.temp_id = temp_id;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public NodeBean() {
    }

    public NodeBean(long project_id, long main_id) {
        this.project_id = project_id;
        this.main_id = main_id;
    }

    public NodeBean(long project_id, long main_id, long id) {
        this.project_id = project_id;
        this.main_id = main_id;
        this.id = id;
    }

    public NodeBean(long project_id, long main_id, long id, String name) {
        this.project_id = project_id;
        this.main_id = main_id;
        this.id = id;
        this.name = name;
    }


    public long getProject_id() {
        return project_id;
    }

    public void setProject_id(long project_id) {
        this.project_id = project_id;
    }

    public long getMain_id() {
        return main_id;
    }

    public void setMain_id(long main_id) {
        this.main_id = main_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public List<NodeBean> getSubnodeArr() {
        return subnodeArr;
    }

    public void setSubnodeArr(List<NodeBean> subnodeArr) {
        this.subnodeArr = subnodeArr;
    }

    public String getChildren_data_type() {
        return children_data_type;
    }

    public void setChildren_data_type(String children_data_type) {
        this.children_data_type = children_data_type;
    }


    //zzh->ad:新增改版增加字段
    public int getNode_type() {
        return node_type;
    }

    public int getNode_level() {
        return node_level;
    }

    public String getData_id() {
        return data_id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public String getNode_code() {
        return node_code;
    }

    public String getIs_mileage() {
        return is_mileage;
    }

    public List<NodeBean> getChild() {
        return child;
    }

    public String getNode_name() {
        return node_name;
    }

    public String getTask_name() {
        return task_name;
    }

    public int getSort() {
        return sort;
    }

    public int getMain_task_count() {
        return main_task_count;
    }

    public int getMain_task_complete_num() {
        return main_task_complete_num;
    }

    public long getMain_task_min_start_time() {
        return main_task_min_start_time;
    }

    public long getMain_task_max_end_time() {
        return main_task_max_end_time;
    }

    public long getDatetime_deadline() {
        return datetime_deadline;
    }

    public TaskInfoBean getTask_info() {
        return task_info;
    }

    public void setNode_type(int node_type) {
        this.node_type = node_type;
    }

    public void setNode_level(int node_level) {
        this.node_level = node_level;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public void setNode_code(String node_code) {
        this.node_code = node_code;
    }

    public void setIs_mileage(String is_mileage) {
        this.is_mileage = is_mileage;
    }

    public void setChild(List<NodeBean> child) {
        this.child = child;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void setMain_task_count(int main_task_count) {
        this.main_task_count = main_task_count;
    }

    public void setMain_task_complete_num(int main_task_complete_num) {
        this.main_task_complete_num = main_task_complete_num;
    }

    public void setMain_task_min_start_time(long main_task_min_start_time) {
        this.main_task_min_start_time = main_task_min_start_time;
    }

    public void setMain_task_max_end_time(long main_task_max_end_time) {
        this.main_task_max_end_time = main_task_max_end_time;
    }

    public void setDatetime_deadline(long datetime_deadline) {
        this.datetime_deadline = datetime_deadline;
    }

    public void setTask_info(TaskInfoBean task_info) {
        this.task_info = task_info;
    }

    public List<Member> getPersonnel_principal() {
        return personnel_principal;
    }

    public void setPersonnel_principal(List<Member> personnel_principal) {
        this.personnel_principal = personnel_principal;
    }
}

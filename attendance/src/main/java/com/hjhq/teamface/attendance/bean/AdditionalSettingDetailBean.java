package com.hjhq.teamface.attendance.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.database.Member;

import java.util.List;

/**
 * Created by Administrator on 2019/3/6.
 * Describe：
 */

public class AdditionalSettingDetailBean extends BaseBean {

    /**
     * data : {"list_set_be_late":"5","admin_arr":[],"company_id":12,"create_time":1550725189603,"modify_time":1551436542141,"absenteeism_rule_be_late_minutes":"10","del_status":"0","remind_clock_after_work":"0","list_set_sort_type":"1","modify_by":18,"list_set_diligent":"10","create_by":19,"list_set_type":"0","humanization_allow_late_minutes":"15","id":1,"remind_clock_before_work":"10","humanization_allow_late_times":"3","late_nigth_walk_arr":[{"leaveingLateTime":"3","workLateTime":"3"}],"list_set_early_arrival":"3","absenteeism_rule_leave_early_minutes":"10"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * list_set_be_late : 5
         * admin_arr : []
         * company_id : 12
         * create_time : 1550725189603
         * modify_time : 1551436542141
         * absenteeism_rule_be_late_minutes : 10
         * del_status : 0
         * remind_clock_after_work : 0
         * list_set_sort_type : 1
         * modify_by : 18
         * list_set_diligent : 10
         * create_by : 19
         * list_set_type : 0
         * humanization_allow_late_minutes : 15
         * id : 1
         * remind_clock_before_work : 10
         * humanization_allow_late_times : 3
         * late_nigth_walk_arr : [{"leaveingLateTime":"3","workLateTime":"3"}]
         * list_set_early_arrival : 3
         * absenteeism_rule_leave_early_minutes : 10
         */

        private String list_set_be_late;
        private String company_id;
        private String create_time;
        private String modify_time;
        private String absenteeism_rule_be_late_minutes;
        private String del_status;
        private String remind_clock_after_work;
        private String list_set_sort_type;
        private String modify_by;
        private String list_set_diligent;
        private String create_by;
        private String list_set_type;
        private String humanization_allow_late_minutes;
        private String id;
        private String remind_clock_before_work;
        private String humanization_allow_late_times;
        private String list_set_early_arrival;
        private String absenteeism_rule_leave_early_minutes;
        private List<Member> admin_arr;
        private List<AddLeaveingLateBean> late_nigth_walk_arr;

        public String getList_set_be_late() {
            return list_set_be_late;
        }

        public void setList_set_be_late(String list_set_be_late) {
            this.list_set_be_late = list_set_be_late;
        }

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getModify_time() {
            return modify_time;
        }

        public void setModify_time(String modify_time) {
            this.modify_time = modify_time;
        }

        public String getAbsenteeism_rule_be_late_minutes() {
            return absenteeism_rule_be_late_minutes;
        }

        public void setAbsenteeism_rule_be_late_minutes(String absenteeism_rule_be_late_minutes) {
            this.absenteeism_rule_be_late_minutes = absenteeism_rule_be_late_minutes;
        }

        public String getDel_status() {
            return del_status;
        }

        public void setDel_status(String del_status) {
            this.del_status = del_status;
        }

        public String getRemind_clock_after_work() {
            return remind_clock_after_work;
        }

        public void setRemind_clock_after_work(String remind_clock_after_work) {
            this.remind_clock_after_work = remind_clock_after_work;
        }

        public String getList_set_sort_type() {
            return list_set_sort_type;
        }

        public void setList_set_sort_type(String list_set_sort_type) {
            this.list_set_sort_type = list_set_sort_type;
        }

        public String getModify_by() {
            return modify_by;
        }

        public void setModify_by(String modify_by) {
            this.modify_by = modify_by;
        }

        public String getList_set_diligent() {
            return list_set_diligent;
        }

        public void setList_set_diligent(String list_set_diligent) {
            this.list_set_diligent = list_set_diligent;
        }

        public String getCreate_by() {
            return create_by;
        }

        public void setCreate_by(String create_by) {
            this.create_by = create_by;
        }

        public String getList_set_type() {
            return list_set_type;
        }

        public void setList_set_type(String list_set_type) {
            this.list_set_type = list_set_type;
        }

        public String getHumanization_allow_late_minutes() {
            return humanization_allow_late_minutes;
        }

        public void setHumanization_allow_late_minutes(String humanization_allow_late_minutes) {
            this.humanization_allow_late_minutes = humanization_allow_late_minutes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRemind_clock_before_work() {
            return remind_clock_before_work;
        }

        public void setRemind_clock_before_work(String remind_clock_before_work) {
            this.remind_clock_before_work = remind_clock_before_work;
        }

        public String getHumanization_allow_late_times() {
            return humanization_allow_late_times;
        }

        public void setHumanization_allow_late_times(String humanization_allow_late_times) {
            this.humanization_allow_late_times = humanization_allow_late_times;
        }

        public String getList_set_early_arrival() {
            return list_set_early_arrival;
        }

        public void setList_set_early_arrival(String list_set_early_arrival) {
            this.list_set_early_arrival = list_set_early_arrival;
        }

        public String getAbsenteeism_rule_leave_early_minutes() {
            return absenteeism_rule_leave_early_minutes;
        }

        public void setAbsenteeism_rule_leave_early_minutes(String absenteeism_rule_leave_early_minutes) {
            this.absenteeism_rule_leave_early_minutes = absenteeism_rule_leave_early_minutes;
        }

        public List<Member> getAdmin_arr() {
            return admin_arr;
        }

        public void setAdmin_arr(List<Member> admin_arr) {
            this.admin_arr = admin_arr;
        }

        public List<AddLeaveingLateBean> getLate_nigth_walk_arr() {
            return late_nigth_walk_arr;
        }

        public void setLate_nigth_walk_arr(List<AddLeaveingLateBean> late_nigth_walk_arr) {
            this.late_nigth_walk_arr = late_nigth_walk_arr;
        }
    }
}

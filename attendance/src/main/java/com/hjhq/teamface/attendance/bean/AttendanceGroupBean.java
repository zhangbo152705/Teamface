package com.hjhq.teamface.attendance.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.database.Member;

import java.util.List;

/**
 * Created by Administrator on 2019/3/7.
 * Describe：
 */

public class AttendanceGroupBean extends BaseBean {

    /**
     * data : {"no_punchcard_date":null,"excluded_group_users":"","memeber_number":2,"create_time":1551947055908,"selected_class":[],"attendance_users":[{"sign_id":0,"name":"安卓","id":14,"type":1,"value":"1-14","picture":"http://192.168.1.181:8093/custom-gateway(New)/common/file/download?bean=user111&fileName=12/common/1548837284805/1548837284749.jpg&fileSize=19572"}],"outworker_status":0,"modify_time":"","must_punchcard_date":[],"face_status":0,"del_status":"0","modify_by":"","attendance_start_time":"","create_by":31,"holiday_auto_status":0,"name":"金家街2","work_day_list":[],"attendance_address":[],"effective_date":0,"id":48,"attendance_wifi":[],"excluded_users":[{"sign_id":0,"name":"测试6","id":43,"type":1,"value":"1-43","picture":""}],"attendance_type":2}
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
         * no_punchcard_date : null
         * excluded_group_users :
         * memeber_number : 2
         * create_time : 1551947055908
         * selected_class : []
         * attendance_users : [{"sign_id":0,"name":"安卓","id":14,"type":1,"value":"1-14","picture":"http://192.168.1.181:8093/custom-gateway(New)/common/file/download?bean=user111&fileName=12/common/1548837284805/1548837284749.jpg&fileSize=19572"}]
         * outworker_status : 0
         * modify_time :
         * must_punchcard_date : []
         * face_status : 0
         * del_status : 0
         * modify_by :
         * attendance_start_time :
         * create_by : 31
         * holiday_auto_status : 0
         * name : 金家街2
         * work_day_list : []
         * attendance_address : []
         * effective_date : 0
         * id : 48
         * attendance_wifi : []
         * excluded_users : [{"sign_id":0,"name":"测试6","id":43,"type":1,"value":"1-43","picture":""}]
         * attendance_type : 2
         */

        private Object no_punchcard_date;
        private String excluded_group_users;
        private String memeber_number;
        private String create_time;
        private String outworker_status;
        private String modify_time;
        private String face_status;
        private String del_status;
        private String modify_by;
        private String attendance_start_time;
        private String create_by;
        private String holiday_auto_status;
        private String name;
        private String effective_date;
        private String id;
        private String attendance_type;
        private List<?> selected_class;
        private List<Member> attendance_users;
        private List<?> must_punchcard_date;
        private List<?> work_day_list;
        private List<?> attendance_address;
        private List<?> attendance_wifi;
        private List<Member> excluded_users;

        public Object getNo_punchcard_date() {
            return no_punchcard_date;
        }

        public void setNo_punchcard_date(Object no_punchcard_date) {
            this.no_punchcard_date = no_punchcard_date;
        }

        public String getExcluded_group_users() {
            return excluded_group_users;
        }

        public void setExcluded_group_users(String excluded_group_users) {
            this.excluded_group_users = excluded_group_users;
        }

        public String getMemeber_number() {
            return memeber_number;
        }

        public void setMemeber_number(String memeber_number) {
            this.memeber_number = memeber_number;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getOutworker_status() {
            return outworker_status;
        }

        public void setOutworker_status(String outworker_status) {
            this.outworker_status = outworker_status;
        }

        public String getModify_time() {
            return modify_time;
        }

        public void setModify_time(String modify_time) {
            this.modify_time = modify_time;
        }

        public String getFace_status() {
            return face_status;
        }

        public void setFace_status(String face_status) {
            this.face_status = face_status;
        }

        public String getDel_status() {
            return del_status;
        }

        public void setDel_status(String del_status) {
            this.del_status = del_status;
        }

        public String getModify_by() {
            return modify_by;
        }

        public void setModify_by(String modify_by) {
            this.modify_by = modify_by;
        }

        public String getAttendance_start_time() {
            return attendance_start_time;
        }

        public void setAttendance_start_time(String attendance_start_time) {
            this.attendance_start_time = attendance_start_time;
        }

        public String getCreate_by() {
            return create_by;
        }

        public void setCreate_by(String create_by) {
            this.create_by = create_by;
        }

        public String getHoliday_auto_status() {
            return holiday_auto_status;
        }

        public void setHoliday_auto_status(String holiday_auto_status) {
            this.holiday_auto_status = holiday_auto_status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEffective_date() {
            return effective_date;
        }

        public void setEffective_date(String effective_date) {
            this.effective_date = effective_date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAttendance_type() {
            return attendance_type;
        }

        public void setAttendance_type(String attendance_type) {
            this.attendance_type = attendance_type;
        }

        public List<?> getSelected_class() {
            return selected_class;
        }

        public void setSelected_class(List<?> selected_class) {
            this.selected_class = selected_class;
        }

        public List<Member> getAttendance_users() {
            return attendance_users;
        }

        public void setAttendance_users(List<Member> attendance_users) {
            this.attendance_users = attendance_users;
        }

        public List<?> getMust_punchcard_date() {
            return must_punchcard_date;
        }

        public void setMust_punchcard_date(List<?> must_punchcard_date) {
            this.must_punchcard_date = must_punchcard_date;
        }

        public List<?> getWork_day_list() {
            return work_day_list;
        }

        public void setWork_day_list(List<?> work_day_list) {
            this.work_day_list = work_day_list;
        }

        public List<?> getAttendance_address() {
            return attendance_address;
        }

        public void setAttendance_address(List<?> attendance_address) {
            this.attendance_address = attendance_address;
        }

        public List<?> getAttendance_wifi() {
            return attendance_wifi;
        }

        public void setAttendance_wifi(List<?> attendance_wifi) {
            this.attendance_wifi = attendance_wifi;
        }

        public List<Member> getExcluded_users() {
            return excluded_users;
        }

        public void setExcluded_users(List<Member> excluded_users) {
            this.excluded_users = excluded_users;
        }
    }
}

package com.hjhq.teamface.attendance.adapter;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/12.
 * Describe：
 */

public class RangeListBean extends BaseBean {

    /**
     * data : {"dataList":[{"real_punchcard_time":1550720824236,"post_name":"java开发","employee_id":31,"department_name":"项目部","employee_name":"徐兵小号","row":1,"picture":"/common/file/imageDownload?bean=company&fileName=47/image/1548140400555/blob&fileSize=6852"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DataListBean> dataList;

        public List<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public static class DataListBean {
            /**
             * real_punchcard_time : 1550720824236
             * post_name : java开发
             * employee_id : 31
             * department_name : 项目部
             * employee_name : 徐兵小号
             * row : 1
             * picture : /common/file/imageDownload?bean=company&fileName=47/image/1548140400555/blob&fileSize=6852
             */

            private String real_punchcard_time;
            private String post_name;
            private String employee_id;
            private String department_name;
            private String employee_name;
            private String row;
            private String picture;
            private String late_time_number;
            private String late_count_number;
            private String month_work_time;

            public String getLate_time_number() {
                return late_time_number;
            }

            public void setLate_time_number(String late_time_number) {
                this.late_time_number = late_time_number;
            }

            public String getLate_count_number() {
                return late_count_number;
            }

            public void setLate_count_number(String late_count_number) {
                this.late_count_number = late_count_number;
            }

            public String getMonth_work_time() {
                return month_work_time;
            }

            public void setMonth_work_time(String month_work_time) {
                this.month_work_time = month_work_time;
            }

            public String getReal_punchcard_time() {
                return real_punchcard_time;
            }

            public void setReal_punchcard_time(String real_punchcard_time) {
                this.real_punchcard_time = real_punchcard_time;
            }

            public String getPost_name() {
                return post_name;
            }

            public void setPost_name(String post_name) {
                this.post_name = post_name;
            }

            public String getEmployee_id() {
                return employee_id;
            }

            public void setEmployee_id(String employee_id) {
                this.employee_id = employee_id;
            }

            public String getDepartment_name() {
                return department_name;
            }

            public void setDepartment_name(String department_name) {
                this.department_name = department_name;
            }

            public String getEmployee_name() {
                return employee_name;
            }

            public void setEmployee_name(String employee_name) {
                this.employee_name = employee_name;
            }

            public String getRow() {
                return row;
            }

            public void setRow(String row) {
                this.row = row;
            }

            public String getPicture() {
                return picture;
            }

            public void setPicture(String picture) {
                this.picture = picture;
            }
        }
    }
}

package com.hjhq.teamface.attendance.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.hjhq.teamface.basis.bean.BaseBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/12.
 * Describe：
 */

public class AttendanceMonthDataBean extends BaseBean {

    /**
     * data : {"attendancePersonNumber":4,"dataList":[{"number":0,"employeeList":[{"duration":11,"post_name":"员工","count":3,"attendanceList":[{"duration":5,"punchcardTime":1551917100000,"attendanceDate":1551916800000}],"employee_name":"建华","id":4,"picture":""}],"name":"OUT_WORKER","type":5}]}
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
         * attendancePersonNumber : 4
         * dataList : [{"number":0,"employeeList":[{"duration":11,"post_name":"员工","count":3,"attendanceList":[{"duration":5,"punchcardTime":1551917100000,"attendanceDate":1551916800000}],"employee_name":"建华","id":4,"picture":""}],"name":"OUT_WORKER","type":5}]
         */

        private String attendancePersonNumber;
        private List<DataListBean> dataList;
        private String groupName;

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getAttendancePersonNumber() {
            return attendancePersonNumber;
        }

        public void setAttendancePersonNumber(String attendancePersonNumber) {
            this.attendancePersonNumber = attendancePersonNumber;
        }

        public List<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public static class DataListBean implements Serializable {
            /**
             * number : 0
             * employeeList : [{"duration":11,"post_name":"员工","count":3,"attendanceList":[{"duration":5,"punchcardTime":1551917100000,"attendanceDate":1551916800000}],"employee_name":"建华","id":4,"picture":""}]
             * name : OUT_WORKER
             * type : 5
             */

            private String number;
            private String name;
            private String type;
            private ArrayList<EmployeeListBean> employeeList;
            private ArrayList<AttendanceListBean> attendanceList;

            public ArrayList<AttendanceListBean> getAttendanceList() {
                return attendanceList;
            }

            public void setAttendanceList(ArrayList<AttendanceListBean> attendanceList) {
                this.attendanceList = attendanceList;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public ArrayList<EmployeeListBean> getEmployeeList() {
                return employeeList;
            }

            public void setEmployeeList(ArrayList<EmployeeListBean> employeeList) {
                this.employeeList = employeeList;
            }


        }
    }

    public static class EmployeeListBean implements Serializable {
        /**
         * duration : 11
         * post_name : 员工
         * count : 3
         * attendanceList : [{"duration":5,"punchcardTime":1551917100000,"attendanceDate":1551916800000}]
         * employee_name : 建华
         * id : 4
         * picture :
         */

        private String duration;
        private String post_name;
        private String count;
        private String employee_name;
        private String id;
        private String picture;
        private ArrayList<AttendanceListBean> attendanceList;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getPost_name() {
            return post_name;
        }

        public void setPost_name(String post_name) {
            this.post_name = post_name;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getEmployee_name() {
            return employee_name;
        }

        public void setEmployee_name(String employee_name) {
            this.employee_name = employee_name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public ArrayList<AttendanceListBean> getAttendanceList() {
            return attendanceList;
        }

        public void setAttendanceList(ArrayList<AttendanceListBean> attendanceList) {
            this.attendanceList = attendanceList;
        }


    }

    public static class AttendanceListBean implements Serializable, MultiItemEntity {
        /**
         * duration : 5
         * punchcardTime : 1551917100000
         * attendanceDate : 1551916800000
         */

        private String duration;
        private String punchcardTime;
        private String attendanceDate;
        private int type;
        private String punchcardType;
        private String punchcardAddress;
        private String startTime;
        private String endTime;

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getPunchcardType() {
            return punchcardType;
        }

        public void setPunchcardType(String punchcardType) {
            this.punchcardType = punchcardType;
        }

        public String getPunchcardAddress() {
            return punchcardAddress;
        }

        public void setPunchcardAddress(String punchcardAddress) {
            this.punchcardAddress = punchcardAddress;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getPunchcardTime() {
            return punchcardTime;
        }

        public void setPunchcardTime(String punchcardTime) {
            this.punchcardTime = punchcardTime;
        }

        public String getAttendanceDate() {
            return attendanceDate;
        }

        public void setAttendanceDate(String attendanceDate) {
            this.attendanceDate = attendanceDate;
        }

        @Override
        public int getItemType() {
            return getType();
        }
    }

}

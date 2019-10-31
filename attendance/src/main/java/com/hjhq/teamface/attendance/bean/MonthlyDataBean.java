package com.hjhq.teamface.attendance.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/14.
 * Describe：
 */

public class MonthlyDataBean extends BaseBean {

    /**
     * data : {"month":3,"year":2019,"dateList":[{"date":14,"groupName":"采购部","attendanceList":[{"punchcardState":1,"punchcardType":1,"isWay":"0","isOutworker":"0","isWayInfo":"广东省深圳市南山区高新南一道58号靠近思创科技大厦","expectPunchcardTime":1552525200000,"realPunchcardTime":1552528900058},{"punchcardState":2,"punchcardType":2,"isWay":"0","isOutworker":"0","isWayInfo":"广东省深圳市南山区高新南一道58号靠近思创科技大厦","expectPunchcardTime":1552525200000,"realPunchcardTime":1552528907549}],"state":"1"}]}
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
         * month : 3
         * year : 2019
         * dateList : [{"date":14,"groupName":"采购部","attendanceList":[{"punchcardState":1,"punchcardType":1,"isWay":"0","isOutworker":"0","isWayInfo":"广东省深圳市南山区高新南一道58号靠近思创科技大厦","expectPunchcardTime":1552525200000,"realPunchcardTime":1552528900058},{"punchcardState":2,"punchcardType":2,"isWay":"0","isOutworker":"0","isWayInfo":"广东省深圳市南山区高新南一道58号靠近思创科技大厦","expectPunchcardTime":1552525200000,"realPunchcardTime":1552528907549}],"state":"1"}]
         */

        private String month;
        private String year;
        private String groupName;
        private List<MonthDataItem> dateList;

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public List<MonthDataItem> getDateList() {
            return dateList;
        }

        public void setDateList(List<MonthDataItem> dateList) {
            this.dateList = dateList;
        }


    }


}

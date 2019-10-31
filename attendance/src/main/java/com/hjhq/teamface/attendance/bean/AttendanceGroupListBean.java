package com.hjhq.teamface.attendance.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/12.
 * Describe：
 */

public class AttendanceGroupListBean extends BaseBean {

    /**
     * data : {"dataList":[{"id":3,"name":"研发考勤组"}],"list_set_type":0}
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
         * dataList : [{"id":3,"name":"研发考勤组"}]
         * list_set_type : 0
         */

        private String list_set_type;
        private List<DataListBean> dataList;

        public String getList_set_type() {
            return list_set_type;
        }

        public void setList_set_type(String list_set_type) {
            this.list_set_type = list_set_type;
        }

        public List<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public static class DataListBean {
            /**
             * id : 3
             * name : 研发考勤组
             */

            private String id;
            private String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}

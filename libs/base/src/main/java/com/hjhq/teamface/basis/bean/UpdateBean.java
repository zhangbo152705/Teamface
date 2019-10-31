package com.hjhq.teamface.basis.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/14.
 * Describe：
 */

public class UpdateBean extends BaseBean {

    /**
     * data : {"dataList":[{"id":1,"v_code":"1.0.0","v_date":"1551369600000","v_must":"0","v_content":"更新的内容"}]}
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
             * id : 1
             * v_code : 1.0.0
             * v_date : 1551369600000
             * v_must : 0
             * v_content : 更新的内容
             */

            private String id;
            private String v_code;
            private String v_date;
            private String v_must;
            private String v_content;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getV_code() {
                return v_code;
            }

            public void setV_code(String v_code) {
                this.v_code = v_code;
            }

            public String getV_date() {
                return v_date;
            }

            public void setV_date(String v_date) {
                this.v_date = v_date;
            }

            public String getV_must() {
                return v_must;
            }

            public void setV_must(String v_must) {
                this.v_must = v_must;
            }

            public String getV_content() {
                return v_content;
            }

            public void setV_content(String v_content) {
                this.v_content = v_content;
            }
        }
    }
}

package com.hjhq.teamface.basis.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 数据列表返回Bean
 *
 * @author lx
 * @date 2017/9/16
 */

public class DataTempResultBean extends BaseBean {


    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        private List<DataListBean> dataList;
        private PageInfo pageInfo;
        private RowBean operationInfo;
        //保密字段key,以","分隔
        private String seasPwdFields;

        public String getSeasPwdFields() {
            return seasPwdFields;
        }

        public void setSeasPwdFields(String seasPwdFields) {
            this.seasPwdFields = seasPwdFields;
        }

        public static class DataListBean implements Serializable {
            private Row row;
            private RowBean id;
            private String color;
            private boolean isCheck;

            public RowBean getId() {
                return id;
            }

            public void setId(RowBean id) {
                this.id = id;
            }

            public Row getRow() {
                return row;
            }

            public void setRow(Row row) {
                this.row = row;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public boolean isCheck() {
                return isCheck;
            }

            public void setCheck(boolean check) {
                isCheck = check;
            }


        }

        public List<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public PageInfo getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }

        public RowBean getOperationInfo() {
            return operationInfo;
        }

        public void setOperationInfo(RowBean operationInfo) {
            this.operationInfo = operationInfo;
        }
    }

}

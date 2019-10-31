package com.hjhq.teamface.basis.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 关联组件列表数据列表返回Bean
 *
 * @author lx
 * @date 2017/9/16
 */

public class ReferDataTempResultBean extends BaseBean {


    private DataBean data;

    public static class DataBean implements Serializable {
        private ArrayList<DataListBean> dataList;
        private PageInfo pageInfo;

        public ArrayList<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(ArrayList<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public PageInfo getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataListBean implements Serializable {
        private ArrayList<RowBean> row;
        private RowBean id;
        private HashMap<String, Object> relationField;
        private boolean check;

        public boolean isCheck() {
            return check;
        }

        public void setCheck(boolean check) {
            this.check = check;
        }

        public RowBean getId() {
            return id;
        }

        public void setId(RowBean id) {
            this.id = id;
        }

        public ArrayList<RowBean> getRow() {
            return row;
        }

        public void setRow(ArrayList<RowBean> row) {
            this.row = row;
        }

        public HashMap<String, Object> getRelationField() {
            return relationField;
        }

        public void setRelationField(HashMap<String, Object> relationField) {
            this.relationField = relationField;
        }
    }
}

package com.hjhq.teamface.basis.bean;

import java.util.List;

/**
 * Describe：插件
 */

public class PlugListBean extends BaseBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<PlugBean> dataList;

        public List<PlugBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<PlugBean> dataList) {
            this.dataList = dataList;
        }
    }




}

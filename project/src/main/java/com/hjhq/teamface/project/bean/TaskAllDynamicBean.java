package com.hjhq.teamface.project.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 全部节点
 * Created by Administrator on 2018/4/25.
 */

public class TaskAllDynamicBean extends BaseBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<TaskAllDynamicDetailBean> dataList;
        private int totalSize;

        public List<TaskAllDynamicDetailBean> getDataList() {
            return dataList;
        }

        public int getTotalSize() {
            return totalSize;
        }

        public void setDataList(List<TaskAllDynamicDetailBean> dataList) {
            this.dataList = dataList;
        }

        public void setTotalSize(int totalSize) {
            this.totalSize = totalSize;
        }
    }



}

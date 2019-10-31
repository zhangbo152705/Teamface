package com.hjhq.teamface.memo.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-12-26.
 * Describe：
 */

public class RevelantDataListBean extends BaseBean {
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private ArrayList<DataListBean> dataList;

        public ArrayList<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(ArrayList<DataListBean> dataList) {
            this.dataList = dataList;
        }
    }

    public static class DataListBean {
        private ArrayList<TaskInfoBean> moduleDataList;
        private String module_name;

        public ArrayList<TaskInfoBean> getModuleDataList() {
            return moduleDataList;
        }

        public void setModuleDataList(ArrayList<TaskInfoBean> moduleDataList) {
            this.moduleDataList = moduleDataList;
        }

        public String getModule_name() {
            return module_name;
        }

        public void setModule_name(String module_name) {
            this.module_name = module_name;
        }
    }
}

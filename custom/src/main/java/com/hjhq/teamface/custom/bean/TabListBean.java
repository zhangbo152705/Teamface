package com.hjhq.teamface.custom.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.RowBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/29.
 * Describe：
 */

public class TabListBean extends BaseBean implements Serializable {

    /**
     * data : {"dataList":[{"chinese_name":"动态函数","condition_type":0,"target_bean":"bean1537238514425","id":193,"sorce_bean":"bean1537405298796"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private ArrayList<DataListBean> dataList;
        private RowBean operationInfo;

        public RowBean getOperationInfo() {
            return operationInfo;
        }

        public void setOperationInfo(RowBean operationInfo) {
            this.operationInfo = operationInfo;
        }

        public ArrayList<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(ArrayList<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public static class DataListBean implements Serializable {
            /**
             * chinese_name : 动态函数
             * condition_type : 0
             * target_bean : bean1537238514425
             * id : 193
             * sorce_bean : bean1537405298796
             */

            private String chinese_name;
            private String condition_type;
            private String target_bean;
            private String id;
            private String sorce_bean;
            private String module_id;

            public String getModule_id() {
                return module_id;
            }

            public void setModule_id(String module_id) {
                this.module_id = module_id;
            }

            public String getChinese_name() {
                return chinese_name;
            }

            public void setChinese_name(String chinese_name) {
                this.chinese_name = chinese_name;
            }

            public String getCondition_type() {
                return condition_type;
            }

            public void setCondition_type(String condition_type) {
                this.condition_type = condition_type;
            }

            public String getTarget_bean() {
                return target_bean;
            }

            public void setTarget_bean(String target_bean) {
                this.target_bean = target_bean;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getSorce_bean() {
                return sorce_bean;
            }

            public void setSorce_bean(String sorce_bean) {
                this.sorce_bean = sorce_bean;
            }
        }
    }
}

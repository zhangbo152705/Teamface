package com.hjhq.teamface.memo.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/8/23.
 * Describe：
 */

public class AddRelevantBean {

    /**
     * id : 1
     * status :  0新增 1删除
     * beanArr : [{"bean":"bean1534928172882","ids":"2,3,4"}]
     */

    private String id;
    private String status;
    private List<BeanArrBean> beanArr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<BeanArrBean> getBeanArr() {
        return beanArr;
    }

    public void setBeanArr(List<BeanArrBean> beanArr) {
        this.beanArr = beanArr;
    }

    public static class BeanArrBean {
        /**
         * bean : bean1534928172882
         * ids : 2,3,4
         */
        @SerializedName("bean_name")
        private String bean;
        @SerializedName("relation_id")
        private String ids;
        @SerializedName("bean_type")
        private int type;
        private String projectId;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getBean() {
            return bean;
        }

        public void setBean(String bean) {
            this.bean = bean;
        }

        public String getIds() {
            return ids;
        }

        public void setIds(String ids) {
            this.ids = ids;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }
    }
}

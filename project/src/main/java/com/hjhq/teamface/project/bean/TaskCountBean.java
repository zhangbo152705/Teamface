package com.hjhq.teamface.project.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

/**
 * Created by Administrator on 2018/9/18.
 * Describe：
 */

public class TaskCountBean extends BaseBean {

    /**
     * data : {"taskCount":"1"}
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
         * taskCount : 1
         */

        private String taskCount;

        public String getTaskCount() {
            return taskCount;
        }

        public void setTaskCount(String taskCount) {
            this.taskCount = taskCount;
        }
    }
}

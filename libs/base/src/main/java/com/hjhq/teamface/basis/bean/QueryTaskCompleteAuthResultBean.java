package com.hjhq.teamface.basis.bean;

/**
 * Created by Administrator on 2018/9/3.
 */

public class QueryTaskCompleteAuthResultBean extends BaseBean {

    /**
     * data : {"finish_task_role":1,"project_complete_status":0,"project_time_status":0}
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
         * finish_task_role : 1
         * project_complete_status : 0
         * project_time_status : 0
         */

        //是否有权限 1有0无
        private String finish_task_role;
        //激活原因1必须0无
        private String project_complete_status;
        //修改截止时间原因1必须0无
        private String project_time_status;

        public String getFinish_task_role() {
            return finish_task_role;
        }

        public void setFinish_task_role(String finish_task_role) {
            this.finish_task_role = finish_task_role;
        }

        public String getProject_complete_status() {
            return project_complete_status;
        }

        public void setProject_complete_status(String project_complete_status) {
            this.project_complete_status = project_complete_status;
        }

        public String getProject_time_status() {
            return project_time_status;
        }

        public void setProject_time_status(String project_time_status) {
            this.project_time_status = project_time_status;
        }
    }
}

package com.hjhq.teamface.basis.bean;

/**
 * Created by Administrator on 2019/3/22.
 * Describe：
 */

public class AppriveInfo extends BaseBean {

    /**
     * data : {"task_name":"","module_bean":"bean1553073868933","module_data_id":9,"approval_data_id":7,"task_id":"","begin_user_id":13,"process_definition_id":"2501","process_field_v":1553150571350,"fromType":"-1","process_name":"12","process_status":2,"task_key":"endEvent","process_key":"process1553073884282","process_v":1,"begin_user_name":"徐兵","id":7}
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
         * task_name :
         * module_bean : bean1553073868933
         * module_data_id : 9
         * approval_data_id : 7
         * task_id :
         * begin_user_id : 13
         * process_definition_id : 2501
         * process_field_v : 1553150571350
         * fromType : -1
         * process_name : 12
         * process_status : 2
         * task_key : endEvent
         * process_key : process1553073884282
         * process_v : 1
         * begin_user_name : 徐兵
         * id : 7
         */

        private String task_name;
        private String module_bean;
        private String module_data_id;
        private String approval_data_id;
        private String task_id;
        private String begin_user_id;
        private String process_definition_id;
        private String process_field_v;
        private String fromType;
        private String process_name;
        private String process_status;
        private String task_key;
        private String process_key;
        private String process_v;
        private String begin_user_name;
        private String id;

        public String getTask_name() {
            return task_name;
        }

        public void setTask_name(String task_name) {
            this.task_name = task_name;
        }

        public String getModule_bean() {
            return module_bean;
        }

        public void setModule_bean(String module_bean) {
            this.module_bean = module_bean;
        }

        public String getModule_data_id() {
            return module_data_id;
        }

        public void setModule_data_id(String module_data_id) {
            this.module_data_id = module_data_id;
        }

        public String getApproval_data_id() {
            return approval_data_id;
        }

        public void setApproval_data_id(String approval_data_id) {
            this.approval_data_id = approval_data_id;
        }

        public String getTask_id() {
            return task_id;
        }

        public void setTask_id(String task_id) {
            this.task_id = task_id;
        }

        public String getBegin_user_id() {
            return begin_user_id;
        }

        public void setBegin_user_id(String begin_user_id) {
            this.begin_user_id = begin_user_id;
        }

        public String getProcess_definition_id() {
            return process_definition_id;
        }

        public void setProcess_definition_id(String process_definition_id) {
            this.process_definition_id = process_definition_id;
        }

        public String getProcess_field_v() {
            return process_field_v;
        }

        public void setProcess_field_v(String process_field_v) {
            this.process_field_v = process_field_v;
        }

        public String getFromType() {
            return fromType;
        }

        public void setFromType(String fromType) {
            this.fromType = fromType;
        }

        public String getProcess_name() {
            return process_name;
        }

        public void setProcess_name(String process_name) {
            this.process_name = process_name;
        }

        public String getProcess_status() {
            return process_status;
        }

        public void setProcess_status(String process_status) {
            this.process_status = process_status;
        }

        public String getTask_key() {
            return task_key;
        }

        public void setTask_key(String task_key) {
            this.task_key = task_key;
        }

        public String getProcess_key() {
            return process_key;
        }

        public void setProcess_key(String process_key) {
            this.process_key = process_key;
        }

        public String getProcess_v() {
            return process_v;
        }

        public void setProcess_v(String process_v) {
            this.process_v = process_v;
        }

        public String getBegin_user_name() {
            return begin_user_name;
        }

        public void setBegin_user_name(String begin_user_name) {
            this.begin_user_name = begin_user_name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

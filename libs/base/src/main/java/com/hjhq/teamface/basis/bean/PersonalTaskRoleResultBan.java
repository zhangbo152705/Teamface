package com.hjhq.teamface.basis.bean;

/**
 * Created by Administrator on 2018/8/13.
 */

public class PersonalTaskRoleResultBan extends BaseBean {

    /**
     * data : {"role":2}
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
         * role : 2
         */

        //0创建人1执行人2协作人
        private String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}

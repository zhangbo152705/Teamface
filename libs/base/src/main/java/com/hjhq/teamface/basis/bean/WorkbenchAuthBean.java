package com.hjhq.teamface.basis.bean;

/**
 * Created by Administrator on 2018/11/2.
 * Describe：
 */

public class WorkbenchAuthBean extends BaseBean {

    /**
     * data : {"haveChagnePrivilege":"2"}
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
         * haveChagnePrivilege : 2//1有权限2无权限
         */

        private String haveChagnePrivilege;

        public String getHaveChagnePrivilege() {
            return haveChagnePrivilege;
        }

        public void setHaveChagnePrivilege(String haveChagnePrivilege) {
            this.haveChagnePrivilege = haveChagnePrivilege;
        }
    }
}

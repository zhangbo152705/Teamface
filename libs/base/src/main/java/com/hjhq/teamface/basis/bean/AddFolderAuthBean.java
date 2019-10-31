package com.hjhq.teamface.basis.bean;

/**
 * Created by Administrator on 2018-12-11.
 * Describe：
 */

public class AddFolderAuthBean extends BaseBean {

    /**
     * data : {"admin":1}
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
         * admin : 1//，1：是；0：不是
         */

        private String admin;

        public String getAdmin() {
            return admin;
        }

        public void setAdmin(String admin) {
            this.admin = admin;
        }
    }
}

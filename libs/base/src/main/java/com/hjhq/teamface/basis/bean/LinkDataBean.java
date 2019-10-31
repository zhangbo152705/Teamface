package com.hjhq.teamface.basis.bean;

import java.util.List;

/**
 * Created by Administrator on 2018-11-29.
 * Describe：
 */

public class LinkDataBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * modifyBy : {"employee_name":"曹建华","id":"1","picture":"http://192.168.1.202:8080/custom-gateway/common/file/imageDownload?bean=company&fileName=21/company/1527214220478.1589644_3a721c3b7033a6fbd5622599.jpg"}
         * modifyTime : 1531798035730
         * publish : 0
         * accessAuth : 1
         * id : 5b4d62137e20ca265405d93c
         * title : 第一个web表单11111
         * expandLink : []
         * accessPassword :
         */

        private ModifyByBean modifyBy;
        private long modifyTime;
        private String publish;
        private String accessAuth;
        private String id;
        private String title;
        private String accessPassword;
        private List<?> expandLink;

        public ModifyByBean getModifyBy() {
            return modifyBy;
        }

        public void setModifyBy(ModifyByBean modifyBy) {
            this.modifyBy = modifyBy;
        }

        public long getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(long modifyTime) {
            this.modifyTime = modifyTime;
        }

        public String getPublish() {
            return publish;
        }

        public void setPublish(String publish) {
            this.publish = publish;
        }

        public String getAccessAuth() {
            return accessAuth;
        }

        public void setAccessAuth(String accessAuth) {
            this.accessAuth = accessAuth;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAccessPassword() {
            return accessPassword;
        }

        public void setAccessPassword(String accessPassword) {
            this.accessPassword = accessPassword;
        }

        public List<?> getExpandLink() {
            return expandLink;
        }

        public void setExpandLink(List<?> expandLink) {
            this.expandLink = expandLink;
        }

        public static class ModifyByBean {
            /**
             * employee_name : 曹建华
             * id : 1
             * picture : http://192.168.1.202:8080/custom-gateway/common/file/imageDownload?bean=company&fileName=21/company/1527214220478.1589644_3a721c3b7033a6fbd5622599.jpg
             */

            private String employee_name;
            private String id;
            private String picture;

            public String getEmployee_name() {
                return employee_name;
            }

            public void setEmployee_name(String employee_name) {
                this.employee_name = employee_name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPicture() {
                return picture;
            }

            public void setPicture(String picture) {
                this.picture = picture;
            }
        }
    }
}

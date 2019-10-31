package com.hjhq.teamface.basis.bean;

import java.util.List;

/**
 * Created by Administrator on 2018-11-29.
 * Describe：
 */

public class WebLinkDataListBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * modifyBy : {"employee_name":"徐兵小号","id":"16","picture":""}
         * modifyTime : 1542190669872
         * externalLink : TmV5lsop5
         * publish : 1
         * accessAuth : 1
         * id : 5bebf64d38ad4c54189259e5
         * title : 徐兵的web表单
         * expandLink : [{"name":"新浪","url":"https://www.huijuhuaqi.com/...?ex=sina"}]
         * accessPassword : 123456
         */

        private ModifyByBean modifyBy;
        private long modifyTime;
        private String externalLink;
        private String signInLink;
        private String publish;
        private String accessAuth;
        private String id;
        private String title;
        private String shareTitle;
        private String shareDescription;
        private String accessPassword;
        private List<ExpandLinkBean> expandLink;

        public String getShareTitle() {
            return shareTitle;
        }

        public void setShareTitle(String shareTitle) {
            this.shareTitle = shareTitle;
        }

        public String getShareDescription() {
            return shareDescription;
        }

        public void setShareDescription(String shareDescription) {
            this.shareDescription = shareDescription;
        }

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

        public String getExternalLink() {
            return externalLink;
        }

        public void setExternalLink(String externalLink) {
            this.externalLink = externalLink;
        }

        public String getSignInLink() {
            return signInLink;
        }

        public void setSignInLink(String signInLink) {
            this.signInLink = signInLink;
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

        public List<ExpandLinkBean> getExpandLink() {
            return expandLink;
        }

        public void setExpandLink(List<ExpandLinkBean> expandLink) {
            this.expandLink = expandLink;
        }

        public static class ModifyByBean {
            /**
             * employee_name : 徐兵小号
             * id : 16
             * picture :
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

        public static class ExpandLinkBean {
            /**
             * name : 新浪
             * url : https://www.huijuhuaqi.com/...?ex=sina
             */

            private String name;
            private String url;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}

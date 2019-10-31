package com.hjhq.teamface.custom.bean;

import java.util.List;

/**
 * Created by Administrator on 2018-12-7.
 * Describe：
 */

public class TransferDataReqBean {


    /**
     * bean : bean1543547600944
     * target : 9
     * data : [{"id":51,"name":"老李","principal":"1","picture":"http://192.168.1.180:8080/custom-gateway/common/file/imageDownload?bean=company&fileName=1534757029477\blob&fileSize=6226"}]
     * share : 1
     */

    private String bean;
    private String target;
    private int share;
    private List<DataBean> data;

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 51
         * name : 老李
         * principal : 1
         * picture : http://192.168.1.180:8080/custom-gateway/common/file/imageDownload?bean=company&fileName=1534757029477lob&fileSize=6226
         */

        private String id;
        private String name;
        private String principal;
        private String picture;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrincipal() {
            return principal;
        }

        public void setPrincipal(String principal) {
            this.principal = principal;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }
    }
}

package com.hjhq.teamface.basis.bean;


import java.util.ArrayList;

/**
 * Created by lx on 2017/8/25.
 */

public class CustomLayoutResultBean extends BaseBean {

    /**
     * data : {"entity":"kehu","title":"客户","version":"1","layout":[{"title":"客户信息","isSpread":"false","type":"default_drop","rows":[{"name":"company","width":"316px","label":"公司名称","type":"text","field":{"type":"text","required":"true","defaultValue":"颜职印象","length":"200"}},{"name":"area","width":"316px","label":"区域","type":"picklist","field":{"type":"int","required":"true","defaultValue":"0","length":"1","entrys":[{"value":"0","label":"深圳"},{"value":"","label":"广州"},{"value":"","label":"上海"},{"value":"","label":"北京"}]}},{"name":"address","width":"316px","label":"地址","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"1000"}},{"name":"typeCode","width":"316px","label":"重要等级","type":"picklist","field":{"type":"int","required":"true","defaultValue":"0","length":"1","entrys":[{"value":"0","label":"VIP客户"},{"value":"","label":"重点客户"},{"value":"","label":"普通客户"}]}},{"name":"sourceCode","width":"316px","label":"消息来源","type":"picklist","field":{"type":"int","required":"true","defaultValue":"0","length":"1","entrys":[{"value":"0","label":"朋友推荐"},{"value":"","label":"网络"},{"value":"","label":"广告"}]}},{"name":"totalMoney","width":"316px","label":"累积购买金额","type":"text","field":{"type":"money","unit":"$","required":"true","defaultValue":"0.0"}},{"name":"createdDate","width":"316px","label":"创建时间","type":"datetime","field":{"type":"date","required":"true","defaultValue":"$NOW$"}},{"name":"createdBy","width":"316px","label":"所属用户","type":"text","field":{"type":"text","required":"true","defaultValue":"$user$","length":"200"}},{"name":"market","width":"732px","label":"备注","type":"textarea","field":{"type":"textarea","required":"true","defaultValue":""}}]},{"title":"联系人信息","isSpread":"false","type":"default_drop","rows":[{"name":"userName","width":"316px","label":"姓名","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"200"}},{"name":"phone","width":"316px","label":"电话","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"200"}},{"name":"mail","width":"316px","label":"邮箱","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"200"}},{"name":"wechat","width":"316px","label":"微信","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"200"}}]}]}
     * response : {"code":1001,"describe":"执行成功"}
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
         * entity : kehu
         * title : 客户
         * version : 1
         * layout : [{"title":"客户信息","isSpread":"false","type":"default_drop","rows":[{"name":"company","width":"316px","label":"公司名称","type":"text","field":{"type":"text","required":"true","defaultValue":"颜职印象","length":"200"}},{"name":"area","width":"316px","label":"区域","type":"picklist","field":{"type":"int","required":"true","defaultValue":"0","length":"1","entrys":[{"value":"0","label":"深圳"},{"value":"","label":"广州"},{"value":"","label":"上海"},{"value":"","label":"北京"}]}},{"name":"address","width":"316px","label":"地址","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"1000"}},{"name":"typeCode","width":"316px","label":"重要等级","type":"picklist","field":{"type":"int","required":"true","defaultValue":"0","length":"1","entrys":[{"value":"0","label":"VIP客户"},{"value":"","label":"重点客户"},{"value":"","label":"普通客户"}]}},{"name":"sourceCode","width":"316px","label":"消息来源","type":"picklist","field":{"type":"int","required":"true","defaultValue":"0","length":"1","entrys":[{"value":"0","label":"朋友推荐"},{"value":"","label":"网络"},{"value":"","label":"广告"}]}},{"name":"totalMoney","width":"316px","label":"累积购买金额","type":"text","field":{"type":"money","unit":"$","required":"true","defaultValue":"0.0"}},{"name":"createdDate","width":"316px","label":"创建时间","type":"datetime","field":{"type":"date","required":"true","defaultValue":"$NOW$"}},{"name":"createdBy","width":"316px","label":"所属用户","type":"text","field":{"type":"text","required":"true","defaultValue":"$user$","length":"200"}},{"name":"market","width":"732px","label":"备注","type":"textarea","field":{"type":"textarea","required":"true","defaultValue":""}}]},{"title":"联系人信息","isSpread":"false","type":"default_drop","rows":[{"name":"userName","width":"316px","label":"姓名","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"200"}},{"name":"phone","width":"316px","label":"电话","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"200"}},{"name":"mail","width":"316px","label":"邮箱","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"200"}},{"name":"wechat","width":"316px","label":"微信","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"200"}}]}]
         */

        private String bean;
        private String title;
        private String processId;
        private String version;
        private String highSeasPool;
        private String commentControl;
        private String dynamicControl;
        private ArrayList<LayoutBean> layout;

        public String getBean() {
            return bean;
        }

        public void setBean(String bean) {
            this.bean = bean;
        }

        public String getTitle() {
            return title;
        }

        public String getProcessId() {
            return processId;
        }

        public void setProcessId(String processId) {
            this.processId = processId;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getHighSeasPool() {
            return highSeasPool;
        }

        public void setHighSeasPool(String highSeasPool) {
            this.highSeasPool = highSeasPool;
        }

        public ArrayList<LayoutBean> getLayout() {
            return layout;
        }

        public String getCommentControl() {
            return commentControl;
        }

        public void setCommentControl(String commentControl) {
            this.commentControl = commentControl;
        }

        public String getDynamicControl() {
            return dynamicControl;
        }

        public void setDynamicControl(String dynamicControl) {
            this.dynamicControl = dynamicControl;
        }

        public void setLayout(ArrayList<LayoutBean> layout) {
            this.layout = layout;
        }


    }

}

package com.hjhq.teamface.basis.bean;

import java.util.List;

public class LayoutBean {
    /**
     * title : 客户信息
     * isSpread : false
     * terminalApp : 1
     * rows : [{"name":"company","width":"316px","label":"公司名称","type":"text","field":{"type":"text","required":"true","defaultValue":"颜职印象","length":"200"}},{"name":"area","width":"316px","label":"区域","type":"picklist","field":{"type":"int","required":"true","defaultValue":"0","length":"1","entrys":[{"value":"0","label":"深圳"},{"value":"","label":"广州"},{"value":"","label":"上海"},{"value":"","label":"北京"}]}},{"name":"address","width":"316px","label":"地址","type":"text","field":{"type":"text","required":"true","defaultValue":"","length":"1000"}},{"name":"typeCode","width":"316px","label":"重要等级","type":"picklist","field":{"type":"int","required":"true","defaultValue":"0","length":"1","entrys":[{"value":"0","label":"VIP客户"},{"value":"","label":"重点客户"},{"value":"","label":"普通客户"}]}},{"name":"sourceCode","width":"316px","label":"消息来源","type":"picklist","field":{"type":"int","required":"true","defaultValue":"0","length":"1","entrys":[{"value":"0","label":"朋友推荐"},{"value":"","label":"网络"},{"value":"","label":"广告"}]}},{"name":"totalMoney","width":"316px","label":"累积购买金额","type":"text","field":{"type":"money","unit":"$","required":"true","defaultValue":"0.0"}},{"name":"createdDate","width":"316px","label":"创建时间","type":"datetime","field":{"type":"date","required":"true","defaultValue":"$NOW$"}},{"name":"createdBy","width":"316px","label":"所属用户","type":"text","field":{"type":"text","required":"true","defaultValue":"$user$","length":"200"}},{"name":"market","width":"732px","label":"备注","type":"textarea","field":{"type":"textarea","required":"true","defaultValue":""}}]
     */

    private String title;
    private String terminalApp;
    //1隐藏
    private String isHideInCreate;//新增时是否隐藏分栏
    private String isHideInDetail;//详情时是否隐藏分栏
    private String isHideColumnName;//是否隐藏分栏名称
    private String isSpread;//是否展开
    private String name;

    private List<CustomBean> rows;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsHideInCreate() {
        return isHideInCreate;
    }

    public void setIsHideInCreate(String isHideInCreate) {
        this.isHideInCreate = isHideInCreate;
    }

    public String getIsHideInDetail() {
        return isHideInDetail;
    }

    public void setIsHideInDetail(String isHideInDetail) {
        this.isHideInDetail = isHideInDetail;
    }

    public String getIsHideColumnName() {
        return isHideColumnName;
    }

    public void setIsHideColumnName(String isHideColumnName) {
        this.isHideColumnName = isHideColumnName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CustomBean> getRows() {
        return rows;
    }

    public void setRows(List<CustomBean> rows) {
        this.rows = rows;
    }

    public String getTerminalApp() {
        return terminalApp;
    }

    public void setTerminalApp(String terminalApp) {
        this.terminalApp = terminalApp;
    }

    public String getIsSpread() {
        return isSpread;
    }

    public void setIsSpread(String isSpread) {
        this.isSpread = isSpread;
    }
}
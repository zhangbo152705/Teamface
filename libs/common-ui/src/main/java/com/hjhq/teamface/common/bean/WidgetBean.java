package com.hjhq.teamface.common.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019-1-19.
 * Describe：
 */

public class WidgetBean implements com.chad.library.adapter.base.entity.MultiItemEntity, Serializable {
    int type;
    String name;
    String moduleId;
    String moduleName;
    String moduleBean;
    String icon_url;
    String icon_color;
    String icon_type;

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getIcon_color() {
        return icon_color;
    }

    public void setIcon_color(String icon_color) {
        this.icon_color = icon_color;
    }

    public String getIcon_type() {
        return icon_type;
    }

    public void setIcon_type(String icon_type) {
        this.icon_type = icon_type;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleBean() {
        return moduleBean;
    }

    public void setModuleBean(String moduleBean) {
        this.moduleBean = moduleBean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getItemType() {
        return type;
    }
}

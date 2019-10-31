package com.hjhq.teamface.basis.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */

public class ProjectPicklistStatusBean implements Serializable {
    private String color;
    private String label;
    private String value;

    public String getColor() {
        return color;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

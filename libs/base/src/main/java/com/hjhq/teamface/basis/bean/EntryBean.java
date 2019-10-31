package com.hjhq.teamface.basis.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 下拉选项 bean
 *
 * @author Administrator
 * @date 2017/11/30
 */

public class EntryBean implements Serializable {
    /**
     * value : #value#
     * label : #label#
     * color : #color#
     */

    private String value;
    private String label;
    private String color;
    private ArrayList<EntryBean> subList;
    private boolean isCheck;
    //依赖的选择组件字段
    private String controlField;
    //依赖的选择组件值
    private ArrayList<EntryBean> relyonList;
    //需要隐藏的组件
    private ArrayList<HidenFieldBean> hidenFields;

    private String colour;
    private String name;
    private String id;

    private int fromType;//来自哪个模块数据

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EntryBean() {
    }

    public EntryBean(String value, String label, String color) {
        this.value = value;
        this.label = label;
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ArrayList<EntryBean> getSubList() {
        return subList;
    }

    public void setSubList(ArrayList<EntryBean> subList) {
        this.subList = subList;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getControlField() {
        return controlField;
    }

    public void setControlField(String controlField) {
        this.controlField = controlField;
    }

    public ArrayList<EntryBean> getRelyonList() {
        return relyonList;
    }

    public void setRelyonList(ArrayList<EntryBean> relyonList) {
        this.relyonList = relyonList;
    }

    public ArrayList<HidenFieldBean> getHidenFields() {
        return hidenFields;
    }

    public void setHidenFields(ArrayList<HidenFieldBean> hidenFields) {
        this.hidenFields = hidenFields;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }
}

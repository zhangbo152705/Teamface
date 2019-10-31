package com.hjhq.teamface.basis.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018-12-24.
 * Describe：知识库分类
 */

public class KnowledgeClassBean implements Serializable {


    /**
     * name : 书籍
     * id : 1
     * labels : [{"name":"金枪鱼","classification_id":2,"id":1}]
     */

    private String name;
    private String id;
    private String classification_id;
    private ArrayList<KnowledgeClassBean> labels;
    private boolean check;

    public String getClassification_id() {
        return classification_id;
    }

    public void setClassification_id(String classification_id) {
        this.classification_id = classification_id;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
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

    public ArrayList<KnowledgeClassBean> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<KnowledgeClassBean> labels) {
        this.labels = labels;
    }


}

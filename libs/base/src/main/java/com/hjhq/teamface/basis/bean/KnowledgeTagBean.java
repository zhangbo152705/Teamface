package com.hjhq.teamface.basis.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018-12-24.
 * Describe：知识库标签
 */

public class KnowledgeTagBean implements Serializable {

    /**
     * name : 金枪鱼
     * classification_id : 2
     * id : 1
     */

    private String name;
    private String classification_id;
    private String id;
    private boolean check;

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

    public String getClassification_id() {
        return classification_id;
    }

    public void setClassification_id(String classification_id) {
        this.classification_id = classification_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

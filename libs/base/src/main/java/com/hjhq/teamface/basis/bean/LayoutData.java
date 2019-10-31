package com.hjhq.teamface.basis.bean;

import java.io.Serializable;

public class LayoutData implements Serializable {
    private Serializable layout;
    private String moduleId;
    private String title;
    private String processId;

    public Serializable getLayout() {
        return layout;
    }

    public void setLayout(Serializable layout) {
        this.layout = layout;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
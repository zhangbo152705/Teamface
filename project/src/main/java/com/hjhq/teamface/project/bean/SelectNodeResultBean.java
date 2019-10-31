package com.hjhq.teamface.project.bean;

/**
 * Created by Administrator on 2019/5/13.
 */

public class SelectNodeResultBean {

    private int actionType;
    private NodeBean selectNode;

    public int getActionType() {
        return actionType;
    }

    public NodeBean getSelectNode() {
        return selectNode;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public void setSelectNode(NodeBean selectNode) {
        this.selectNode = selectNode;
    }
}

package com.hjhq.teamface.common.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.KnowledgeClassBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-12-24.
 * Describe：
 */

public class KnowledgeClassListBean extends BaseBean {
    private ArrayList<KnowledgeClassBean> data;

    public ArrayList<KnowledgeClassBean> getData() {
        return data;
    }

    public void setData(ArrayList<KnowledgeClassBean> data) {
        this.data = data;
    }
}

package com.hjhq.teamface.memo.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.util.List;

/**
 * Created by Administrator on 2018-12-28.
 * Describe：
 */

public class AnswerListBean extends BaseBean {
    private List<KnowledgeBean> data;

    public List<KnowledgeBean> getData() {
        return data;
    }

    public void setData(List<KnowledgeBean> data) {
        this.data = data;
    }
}

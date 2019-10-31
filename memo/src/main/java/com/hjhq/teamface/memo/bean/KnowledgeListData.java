package com.hjhq.teamface.memo.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.PageInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-12-25.
 * Describe：
 */

public class KnowledgeListData extends BaseBean {
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private ArrayList<KnowledgeBean> dataList;
        private PageInfo pageInfo;

        public ArrayList<KnowledgeBean> getDataList() {
            return dataList;
        }

        public void setDataList(ArrayList<KnowledgeBean> dataList) {
            this.dataList = dataList;
        }

        public PageInfo getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }
    }

}
package com.hjhq.teamface.custom.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.RowBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/31.
 */

public class RepeatCheckResponseBean extends BaseBean {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private ArrayList<RowBean> row;

        public ArrayList<RowBean> getRow() {
            return row;
        }

        public void setRow(ArrayList<RowBean> row) {
            this.row = row;
        }
    }
}

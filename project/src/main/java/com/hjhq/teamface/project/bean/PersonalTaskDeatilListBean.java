package com.hjhq.teamface.project.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.util.List;

/**
 * Created by Administrator on 2018/7/24.
 */

public class PersonalTaskDeatilListBean extends BaseBean {
    private databean data;

    public databean getData() {
        return data;
    }

    public void setData(databean data) {
        this.data = data;
    }

    public static class databean{

        private List<TaskCardBean> dataList;
        public List<TaskCardBean> getData() {
            return dataList;
        }

        public void setData(List<TaskCardBean> data) {
            this.dataList = data;
        }

        public List<TaskCardBean> getDataList() {
            return dataList;
        }
    }


}

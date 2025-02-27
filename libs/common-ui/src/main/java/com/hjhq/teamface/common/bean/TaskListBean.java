package com.hjhq.teamface.common.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;

import java.util.List;

/**
 * 任务列表下的任务
 *
 * @author Administrator
 * @date 2018/6/13
 */

public class TaskListBean extends BaseBean {

    /**
     * data : {"dataList":[{"share_ids":"","create_time":1528103790851,"remind_status":"1","modify_time":"","dataType":1,"taskInfoId":218,"del_status":"0","modify_by":"","title":"cbcvbcvb","content":"[{\"num\":0,\"check\":0,\"type\":1,\"content\":\"cbcvbcvb\"}]","create_by":2,"items_arr":"[]","remind_time":0,"completeStatus":0,"location":[],"beanName":"memo","id":1151,"pic_url":"","beanId":1151}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<TaskInfoBean> dataList;

        public List<TaskInfoBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<TaskInfoBean> dataList) {
            this.dataList = dataList;
        }

    }
}

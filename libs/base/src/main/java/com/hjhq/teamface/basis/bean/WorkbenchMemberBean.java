package com.hjhq.teamface.basis.bean;

import com.hjhq.teamface.basis.database.Member;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/11/21.
 * Describe：
 */

public class WorkbenchMemberBean extends BaseBean {

    /**
     * data : {"dataList":[{"sign_id":10018,"employee_name":"赖五","id":9,"type":1,"value":"1_9","picture":"http://192.168.1.183:8081/custom-gateway/common/file/imageDownload?bean=company&fileName=1540280735531/blob&fileSize=1989"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private ArrayList<Member> dataList;

        public ArrayList<Member> getDataList() {
            return dataList;
        }

        public void setDataList(ArrayList<Member> dataList) {
            this.dataList = dataList;
        }


    }
}

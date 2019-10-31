package com.hjhq.teamface.common.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/19.
 * Describe：
 */

public class DynamicParamsBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : -1
         * identifer : personnel_principal
         * value : 3-personnel_principal
         * name : 负责人
         */

        private String id;
        private String identifer;
        private String value;
        private String name;
        private boolean check;

        public boolean isCheck() {
            return check;
        }

        public void setCheck(boolean check) {
            this.check = check;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIdentifer() {
            return identifer;
        }

        public void setIdentifer(String identifer) {
            this.identifer = identifer;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

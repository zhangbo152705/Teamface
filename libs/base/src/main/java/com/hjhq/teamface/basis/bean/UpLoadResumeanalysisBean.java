package com.hjhq.teamface.basis.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author lx
 * @date 2017/5/18
 */

public class UpLoadResumeanalysisBean extends BaseBean implements Serializable {

    private Resumeanalysis data;

    public Resumeanalysis getData() {
        return data;
    }

    public void setData(Resumeanalysis data) {
        this.data = data;
    }

    public class Resumeanalysis {
        private int code;
        private UploadFileBean obj;
        private HashMap<String, Object> subData;
        private HashMap<String, Object> mainData;

        public int getCode() {
            return code;
        }

        public UploadFileBean getObj() {
            return obj;
        }

        public HashMap<String, Object> getSubData() {
            return subData;
        }

        public HashMap<String, Object> getMainData() {
            return mainData;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setObj(UploadFileBean obj) {
            this.obj = obj;
        }

        public void setSubData(HashMap<String, Object> subData) {
            this.subData = subData;
        }

        public void setMainData(HashMap<String, Object> mainData) {
            this.mainData = mainData;
        }
    }

}

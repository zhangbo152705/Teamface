package com.hjhq.teamface.memo.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/24.
 * Describe：
 */

public class RelevantDataBean extends BaseBean {

    /**
     * data : {"moduleDataList":[{"module_id":815,"color":"","sub_id":null,"dataType":3,"taskInfoId":null,"beanName":"bean1534928165336","complete_status":0,"id":{"name":"id","label":"主键id","value":1},"row":[{"name":"text_1534928165336","label":"名称","value":"11111111"},{"name":"personnel_principal","label":"负责人","value":[{"post_name":"员工","name":"罗军","id":1,"picture":"http://192.168.1.180:8080/custom-gateway/common/file/imageDownload?bean=company&fileName=1534757029477\blob&fileSize=6226"}]},{"name":"personnel_create_by","label":"创建人","value":[{"post_name":"员工","name":"罗军","id":1,"picture":"http://192.168.1.180:8080/custom-gateway/common/file/imageDownload?bean=company&fileName=1534757029477\blob&fileSize=6226"}]},{"name":"datetime_create_time","label":"创建时间","value":1534928326715},{"name":"personnel_modify_by","label":"修改人","value":[{"post_name":"员工","name":"罗军","id":1,"picture":"http://192.168.1.180:8080/custom-gateway/common/file/imageDownload?bean=company&fileName=1534757029477\blob&fileSize=6226"}]},{"name":"datetime_modify_time","label":"修改时间","value":1534928326715},{"name":"del_status","label":"删除状态","value":"0"},{"name":"seas_pool_id","label":"公海池编号","value":0}],"module_name":"AAAAAAAA","lockedState":""},{"module_id":815,"color":"","sub_id":null,"dataType":3,"taskInfoId":null,"beanName":"bean1534928165336","complete_status":0,"id":{"name":"id","label":"主键id","value":2},"row":[{"name":"text_1534928165336","label":"名称","value":"2222222"},{"name":"personnel_principal","label":"负责人","value":[{"post_name":"员工","name":"罗军","id":1,"picture":"http://192.168.1.180:8080/custom-gateway/common/file/imageDownload?bean=company&fileName=1534757029477\blob&fileSize=6226"}]},{"name":"personnel_create_by","label":"创建人","value":[{"post_name":"员工","name":"罗军","id":1,"picture":"http://192.168.1.180:8080/custom-gateway/common/file/imageDownload?bean=company&fileName=1534757029477\blob&fileSize=6226"}]},{"name":"datetime_create_time","label":"创建时间","value":1534928330856},{"name":"personnel_modify_by","label":"修改人","value":[{"post_name":"员工","name":"罗军","id":1,"picture":"http://192.168.1.180:8080/custom-gateway/common/file/imageDownload?bean=company&fileName=1534757029477\blob&fileSize=6226"}]},{"name":"datetime_modify_time","label":"修改时间","value":1534928330856},{"name":"del_status","label":"删除状态","value":"0"},{"name":"seas_pool_id","label":"公海池编号","value":0}],"module_name":"AAAAAAAA","lockedState":""}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<TaskInfoBean> moduleDataList;

        @SerializedName("dataList")
        private List<DataListBean>  dataList;

        public List<TaskInfoBean> getModuleDataList() {
            return moduleDataList;
        }

        public void setModuleDataList(List<TaskInfoBean> moduleDataList) {
            this.moduleDataList = moduleDataList;
        }

        public List<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<DataListBean> dataList) {
            this.dataList = dataList;
        }
    }
    public static class DataListBean{
        @SerializedName("module_id")
        private Long moduleId;
        @SerializedName("project_id")
        private String projectId;
        @SerializedName("dataType")
        private Long dataType;
        @SerializedName("beanName")
        private String beanName;
        @SerializedName("from")
        private String  from;
        @SerializedName("moduleDataList")
        private List<TaskInfoBean> moduleDataList;

        public Long getModuleId() {
            return moduleId;
        }

        public void setModuleId(Long moduleId) {
            this.moduleId = moduleId;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public Long getDataType() {
            return dataType;
        }

        public void setDataType(Long dataType) {
            this.dataType = dataType;
        }

        public String getBeanName() {
            return beanName;
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public List<TaskInfoBean> getModuleDataList() {
            return moduleDataList;
        }

        public void setModuleDataList(List<TaskInfoBean> moduleDataList) {
            this.moduleDataList = moduleDataList;
        }
    }
}

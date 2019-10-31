package com.hjhq.teamface.project.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.PageInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/10/12.
 * Describe：
 */

public class ProjectRoleBean extends BaseBean implements Serializable {

    /**
     * data : {"dataList":[{"update_time":1539159517396,"priviledge_ids":"","update_by_name":"彭华娣","role_type":3,"create_time":1529634717912,"describle":"只读角色，可修改权限范围","name":"访客","id":3},{"update_time":"","priviledge_ids":"7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33","update_by_name":"超级张","role_type":1,"create_time":1529634717912,"describle":"拥有当前负责项目所有权限，可修改权限范围","name":"项目负责人","id":1},{"update_time":1533544376738,"priviledge_ids":"11,25,26,28,27,24","update_by_name":"洪得财","role_type":2,"create_time":1529634717912,"describle":"拥有参与项目享有的权限，可修改权限范围","name":"项目成员","id":2},{"update_time":1536892107607,"priviledge_ids":"7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33","update_by_name":"彭华娣","role_type":4,"create_time":1536892107607,"describle":"","name":"全部权限","id":20},{"update_time":1539229133709,"priviledge_ids":"","update_by_name":"赖测八","role_type":5,"create_time":1539226554700,"describle":"全部","name":"全部","id":21},{"update_time":1539229238221,"priviledge_ids":"","update_by_name":"赖测八","role_type":6,"create_time":1539229238221,"describle":"无","name":"无权限","id":22}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DataListBean> dataList;
        private PageInfo pageInfo;

        public PageInfo getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }

        public List<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public static class DataListBean {
            /**
             * update_time : 1539159517396
             * priviledge_ids :
             * update_by_name : 彭华娣
             * role_type : 3
             * create_time : 1529634717912
             * describle : 只读角色，可修改权限范围
             * name : 访客
             * id : 3
             */

            private String update_time;
            private String priviledge_ids;
            private String update_by_name;
            private String role_type;
            private String create_time;
            private String describle;
            private String name;
            private String id;
            private boolean check;

            public boolean isCheck() {
                return check;
            }

            public void setCheck(boolean check) {
                this.check = check;
            }

            public String getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(String update_time) {
                this.update_time = update_time;
            }

            public String getPriviledge_ids() {
                return priviledge_ids;
            }

            public void setPriviledge_ids(String priviledge_ids) {
                this.priviledge_ids = priviledge_ids;
            }

            public String getUpdate_by_name() {
                return update_by_name;
            }

            public void setUpdate_by_name(String update_by_name) {
                this.update_by_name = update_by_name;
            }

            public String getRole_type() {
                return role_type;
            }

            public void setRole_type(String role_type) {
                this.role_type = role_type;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getDescrible() {
                return describle;
            }

            public void setDescrible(String describle) {
                this.describle = describle;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}

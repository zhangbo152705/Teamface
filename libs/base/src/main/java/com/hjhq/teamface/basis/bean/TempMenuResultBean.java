package com.hjhq.teamface.basis.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 * @date 2017/11/3
 */

public class TempMenuResultBean extends BaseBean implements Serializable {


    private DataBean1 data;

    public DataBean1 getData() {
        return data;
    }

    public void setData(DataBean1 data) {
        this.data = data;
    }

    public static class DataBean1 {
        private List<DataBean> newSubmenu;
        private List<DataBean> defaultSubmenu;

        public List<DataBean> getNewSubmenu() {
            return newSubmenu;
        }

        public void setNewSubmenu(List<DataBean> newSubmenu) {
            this.newSubmenu = newSubmenu;
        }

        public List<DataBean> getDefaultSubmenu() {
            return defaultSubmenu;
        }

        public void setDefaultSubmenu(List<DataBean> defaultSubmenu) {
            this.defaultSubmenu = defaultSubmenu;
        }
    }

    public static class DataBean implements Serializable {
        public DataBean() {
        }

        public DataBean(String id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * id : 0
         * name : 全部市场活动
         */

        private String id;
        private String type;
        private String name;
        private String query_condition;
        private String is_seas_pool;//1是公海池
        private String is_seas_admin;//1是管理员，0成员

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIs_seas_pool() {
            return is_seas_pool;
        }

        public void setIs_seas_pool(String is_seas_pool) {
            this.is_seas_pool = is_seas_pool;
        }

        public String getIs_seas_admin() {
            return is_seas_admin;
        }

        public String getQuery_condition() {
            return query_condition;
        }

        public void setQuery_condition(String query_condition) {
            this.query_condition = query_condition;
        }

        public void setIs_seas_admin(String is_seas_admin) {
            this.is_seas_admin = is_seas_admin;
        }
    }
}

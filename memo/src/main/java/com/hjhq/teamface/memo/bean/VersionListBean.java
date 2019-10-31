package com.hjhq.teamface.memo.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

import java.util.List;

/**
 * Created by Administrator on 2018-12-28.
 * Describe：
 */

public class VersionListBean extends BaseBean {
    private List<VersionBean> data;

    public List<VersionBean> getData() {
        return data;
    }

    public void setData(List<VersionBean> data) {
        this.data = data;
    }

    public static class VersionBean {


        /**
         * name : 当前版本
         * id : 1
         * content : <p>Why does the sun gong shining?</p><p>fdafdfadfadfadfadfadfadfadf</p>
         */

        private String name;
        private String id;
        private String content;
        private boolean check;

        public boolean isCheck() {
            return check;
        }

        public void setCheck(boolean check) {
            this.check = check;
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

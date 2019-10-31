package com.hjhq.teamface.basis.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2019/3/21.
 * Describe：
 */

public class InsertSubformBean extends BaseBean implements Serializable {

    /**
     * data : {"dataList":[{"id":{"name":"id","value":"3"},"row":[{"name":"subform_text_1552900911709","label":"单行文本B1","type":"text","value":"A33333"}]}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private ArrayList<DataListBean> dataList;
        private PageInfo pageInfo;

        public PageInfo getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }

        public ArrayList<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(ArrayList<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public static class DataListBean implements Serializable {
            /**
             * id : {"name":"id","value":"3"}
             * row : [{"name":"subform_text_1552900911709","label":"单行文本B1","type":"text","value":"A33333"}]
             */

            private IdBean id;
            private ArrayList<RowBean> row;
            private boolean check;

            public boolean isCheck() {
                return check;
            }

            public void setCheck(boolean check) {
                this.check = check;
            }

            public IdBean getId() {
                return id;
            }

            public void setId(IdBean id) {
                this.id = id;
            }

            public ArrayList<RowBean> getRow() {
                return row;
            }

            public void setRow(ArrayList<RowBean> row) {
                this.row = row;
            }

            public static class IdBean implements Serializable {
                /**
                 * name : id
                 * value : 3
                 */

                private String name;
                private String value;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }
}

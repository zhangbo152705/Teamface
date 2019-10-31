package com.hjhq.teamface.basis.bean;

import java.util.Map;

/**
 * Created by Administrator on 2018/11/12.
 * Describe：
 */

public class MappingDataBean extends BaseBean {

    /**
     * data : {"text_1541987377587":"数据2-3","text_1541986882984":"数据2-2","text_1541987352555":"数据2-1"}
     */

    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}

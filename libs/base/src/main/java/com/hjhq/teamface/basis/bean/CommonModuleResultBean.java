package com.hjhq.teamface.basis.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/27.
 */

public class CommonModuleResultBean extends BaseBean {
    private List<AppModuleBean> data;

    public List<AppModuleBean> getData() {
        return data;
    }

    public void setData(List<AppModuleBean> data) {
        this.data = data;
    }

}

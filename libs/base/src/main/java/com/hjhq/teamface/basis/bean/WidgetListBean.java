package com.hjhq.teamface.basis.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2019-1-22.
 * Describe：
 */

public class WidgetListBean extends BaseBean implements Serializable {
    ArrayList<AppModuleBean> data;

    public ArrayList<AppModuleBean> getData() {
        return data;
    }

    public void setData(ArrayList<AppModuleBean> data) {
        this.data = data;
    }
}

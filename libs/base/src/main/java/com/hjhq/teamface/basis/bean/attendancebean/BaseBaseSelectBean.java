package com.hjhq.teamface.basis.bean.attendancebean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/15.
 * Describe：选择数据的基类(排班,地点,WiFi等)
 */

public class BaseBaseSelectBean implements MultiItemEntity, Serializable {
    boolean check;

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}

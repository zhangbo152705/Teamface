package com.hjhq.teamface.memo.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.database.Member;

import java.util.List;

/**
 * Created by Administrator on 2018-12-28.
 * Describe：
 */

public class MemberListBean extends BaseBean {
    private List<Member> data;

    public List<Member> getData() {
        return data;
    }

    public void setData(List<Member> data) {
        this.data = data;
    }
}

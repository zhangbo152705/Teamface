package com.hjhq.teamface.memo.bean;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.database.Member;

import java.util.List;

/**
 * Created by Administrator on 2018-12-28.
 * Describe：
 */

public class MemberReadListBean extends BaseBean {
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<Member> reads;
        private List<Member> learning;

        public List<Member> getReads() {
            return reads;
        }

        public void setReads(List<Member> reads) {
            this.reads = reads;
        }

        public List<Member> getLearning() {
            return learning;
        }

        public void setLearning(List<Member> learning) {
            this.learning = learning;
        }
    }


}

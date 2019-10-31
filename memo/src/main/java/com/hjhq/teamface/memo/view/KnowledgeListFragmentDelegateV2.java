package com.hjhq.teamface.memo.view;

import android.view.View;

import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.memo.R;


/**
 * Created by Administrator on 2018/3/21.
 * Describe：
 */

public class KnowledgeListFragmentDelegateV2 extends AppDelegate {
    @Override
    public int getRootLayoutId() {
        return R.layout.memo_knowledge_list_fragment_v2;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();

    }

    /**
     * 侧滑菜单条目点击事件
     *
     * @param listener
     */
    public void setMenuItemClickListener(View.OnClickListener listener) {
        get(R.id.rl1).setTag(1);
        get(R.id.rl2).setTag(2);
        get(R.id.rl3).setTag(3);
        get(R.id.rl4).setTag(4);
        get(R.id.rl1).setOnClickListener(listener);
        get(R.id.rl2).setOnClickListener(listener);
        get(R.id.rl3).setOnClickListener(listener);
        get(R.id.rl4).setOnClickListener(listener);
    }
}

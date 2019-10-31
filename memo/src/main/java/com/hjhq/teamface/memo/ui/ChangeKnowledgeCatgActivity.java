package com.hjhq.teamface.memo.ui;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.memo.MemoModel;
import com.hjhq.teamface.memo.R;
import com.hjhq.teamface.memo.view.AddKnowledgeDelegate;
import com.luojilab.router.facade.annotation.RouteNode;

@RouteNode(path = "/change_knowledge_catg", desc = "修改知识分类")
public class ChangeKnowledgeCatgActivity extends ActivityPresenter<AddKnowledgeDelegate, MemoModel> {


    @Override
    public void init() {
        viewDelegate.get(R.id.ll_title).setVisibility(View.GONE);
        viewDelegate.get(R.id.ll_content).setVisibility(View.GONE);
        viewDelegate.get(R.id.ll_relevant).setVisibility(View.GONE);
        viewDelegate.get(R.id.ll_attachment).setVisibility(View.GONE);
        viewDelegate.setTitle("移动到");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SoftKeyboardUtils.hide(viewDelegate.getRootView());

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();

        viewDelegate.get(R.id.rl_choose_catg).setOnClickListener(v -> {
            chooseCatg();
        });
        viewDelegate.get(R.id.rl_choose_tag).setOnClickListener(v -> {
            chooseTag();
        });


    }

    /**
     * 选择知识分类
     */
    private void chooseCatg() {
        CommonUtil.startActivtiyForResult(mContext, ChooseCatgActivity.class, Constants.REQUEST_CODE2);
    }

    /**
     * 选择标签
     */
    private void chooseTag() {
        CommonUtil.startActivtiyForResult(mContext, ChooseCatgActivity.class, Constants.REQUEST_CODE2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_CODE2:
                //选择知识分类
                break;
            case Constants.REQUEST_CODE3:
                //选择知识标签
                break;
            default:
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


}

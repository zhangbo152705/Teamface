package com.hjhq.teamface.memo.view;

import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.memo.R;

/**
 * Created by Administrator on 2017/11/9.
 * Describe：
 */

public class ChooseMemoDelegate extends AppDelegate {


    @Override
    public int getRootLayoutId() {
        return R.layout.memo_choose_memo_activity;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle(R.string.memo_main_title);
        setRightMenuTexts("新建","确定");
    }

    public void showMenus(int fromType){
        if (fromType ==1){
            showMenu(0,1);
        }else {
            showMenu(1);
        }
    }
}

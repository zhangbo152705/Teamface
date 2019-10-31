package com.hjhq.teamface.custom.ui.detail;

import android.widget.TextView;

import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.custom.R;

/**
 * 自定义跳转邮箱
 *
 * @author Administrator
 * @date 2018/3/20
 */

public class ChooseEmailDelegate extends AppDelegate {
    private TextView tvTag1;
    private TextView tvTag2;


    @Override
    public int getRootLayoutId() {
        return R.layout.custom_choose_email_activity;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setRightMenuTexts(R.color.app_blue, "确定");
        tvTag1 = get(R.id.tv_tag1);
        tvTag2 = get(R.id.tv_tag2);
        tvTag1.setSelected(true);
    }

    public void setText(int type, String text) {
        if (type == EmailConstant.RECEVER_BOX) {
            TextUtil.setText(tvTag1, text);
        }
        if (type == EmailConstant.SENDED_BOX) {
            TextUtil.setText(tvTag2, text);
        }
    }

    public void setSelect(int type) {
        if (type == EmailConstant.RECEVER_BOX) {
            tvTag1.setSelected(true);
            tvTag2.setSelected(false);
        }
        if (type == EmailConstant.SENDED_BOX) {
            tvTag1.setSelected(false);
            tvTag2.setSelected(true);
        }
    }

}

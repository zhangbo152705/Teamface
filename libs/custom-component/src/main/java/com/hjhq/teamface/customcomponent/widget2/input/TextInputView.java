package com.hjhq.teamface.customcomponent.widget2.input;

import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.base.InputCommonView;

/**
 * 单行文本
 *
 * @author lx
 * @date 2017/8/23
 */

public class TextInputView extends InputCommonView {

    public TextInputView(CustomBean bean) {
        super(bean);
    }

    @Override
    public int getLayout() {
        if ("0".equals(structure)) {
            return R.layout.custom_input_single_widget_layout;
        } else {
            return R.layout.custom_input_single_widget_row_layout;
        }
    }

    @Override
    public void initOption() {
        setRepeatCheckIcon();
    }

    @Override
    public boolean formatCheck() {
        return true;
    }
}

package com.hjhq.teamface.customcomponent.widget2.input;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.base.InputCommonView;

/**
 * 多行文本
 *
 * @author lx
 * @date 2017/8/23
 */

public class MultiTextInputView extends InputCommonView {
    TextView tvNum;

    public MultiTextInputView(CustomBean bean) {
        super(bean);
    }

    @Override
    public int getLayout() {
        if ("0".equals(structure)) {
            return R.layout.custom_input_multi_widget_layout;
        } else {
            return R.layout.custom_input_multi_widget_row_layout;
        }
    }

    @Override
    public void initOption() {
        tvNum = mView.findViewById(R.id.tv_num);
        if (state == CustomConstants.DETAIL_STATE || CustomConstants.FIELD_READ.equals(fieldControl)) {
            tvNum.setVisibility(View.GONE);
        } else {
            tvNum.setVisibility(View.VISIBLE);
            etInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (TextUtils.isEmpty(s)) {
                        tvNum.setText("0/500");
                    } else {
                        tvNum.setText(s.length() + "/500");

                    }
                }
            });
        }

    }

    @Override
    public boolean formatCheck() {
        return true;
    }
}

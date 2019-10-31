package com.hjhq.teamface.login.ui;

import android.graphics.Color;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.TextWatcherUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.login.R;
import com.hjhq.teamface.login.presenter.LoginSettingActivity;
import com.hjhq.teamface.login.view.verify.CountDownButton;

/**
 * 登录视图
 *
 * @author Administrator
 */

public class LoginDelegate extends AppDelegate {
    public EditText phoneEt;
    public EditText pwdEt;
    public EditText verifyEt;
    public Button loginBtn;
    public ImageView showPwdSelectIv;
    public TextView mForgetPassword;
    public TextView registerTv;
    private ImageView ivClearPhone;
    private ImageView loginSetting;
    public CountDownButton verifyCodeBtn;
    public RelativeLayout chooseRegionCode;
    public TextView tvRegionCode;

    @Override
    public int getRootLayoutId() {
        return R.layout.login_activity_login;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle(R.string.login);
        phoneEt = get(R.id.phone_et);
        verifyEt = get(R.id.verify_et);
        pwdEt = get(R.id.pwd_et);
        loginBtn = get(R.id.login_btn);
        ivClearPhone = get(R.id.clear_phone_iv);
        registerTv = get(R.id.tv_register);
        showPwdSelectIv = get(R.id.show_pwd_select_iv);
        mForgetPassword = get(R.id.tv_froget_password);
        verifyCodeBtn = get(R.id.timmer_btn);
        chooseRegionCode = get(R.id.rl_region);
        tvRegionCode = get(R.id.tv_region);
        loginSetting = get(R.id.login_setting);
        verifyCodeBtn.setCount(Constants.VERIFY_CODE_TIME);
        verifyCodeBtn.setEnableCountDown(false);
        TextWatcherUtil.setEdNoChinaese(pwdEt);
        ivClearPhone.setOnClickListener(v -> {
            phoneEt.setText("");
        });
        loginSetting.setOnClickListener(v->{//zzh->ad:增加本地化设置点击事件
            CommonUtil.startActivtiy(getActivity(), LoginSettingActivity.class, null);
        });
    }

    /**
     * 得到手机号
     */
    public String getPhone() {
        return phoneEt.getText().toString().trim();
    }

    /**
     * 设置手机号
     *
     * @param userAccount 帐号
     */
    public void setPhone(String userAccount) {
        TextUtil.setText(phoneEt, userAccount);
        if (!TextUtil.isEmpty(userAccount)) {
            phoneEt.setSelection(userAccount.length());
        }
    }

    /**
     * 得到密码
     */
    public String getPassword() {
        return pwdEt.getText().toString().trim();
    }


    /**
     * 得到验证码
     */
    public String getVerifyCode() {
        return verifyEt.getText().toString().trim();
    }


    /**
     * 验证码失败
     */
    public void verifyCodeFailed() {
        verifyCodeBtn.setTextColor(Color.WHITE);
        verifyCodeBtn.setEnabled(true);
        verifyCodeBtn.setEnableCountDown(false);
        verifyCodeBtn.setText("重新获取");
    }

    /**
     * 验证码成功
     */
    public void verifyCodeSuccess() {
        verifyCodeBtn.setTextColor(Color.WHITE);
        verifyCodeBtn.removeCountDown();
        verifyCodeBtn.setEnableCountDown(true);
        verifyCodeBtn.startCount();
    }

    /**
     * 设置密码隐藏显示
     */
    public void setPwdVisible() {
        if (InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == pwdEt.getInputType()) {
            showPwdSelectIv.setImageResource(R.drawable.login_show_pwd_no);
            pwdEt.setInputType((InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
        } else {
            showPwdSelectIv.setImageResource(R.drawable.login_icon_show_pwd);
            pwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        pwdEt.setSelection(getPassword().length());
    }

    /**
     * 移除倒计时
     */
    public void removeCountDown() {
        verifyCodeBtn.setTextColor(Color.WHITE);
        if (verifyCodeBtn.isCountDownNow()) {
            verifyCodeBtn.removeCountDown();
        }
    }


    /**
     * 是否可用
     */
    public boolean getEnable() {
        return verifyCodeBtn.getEnableCountDown() || !verifyCodeBtn.isEnabled();
    }

    /**
     * 发送验证码中
     */
    public void sendVerify() {
        verifyCodeBtn.setEnabled(false);
        verifyCodeBtn.setText("正在获取...");
    }
}

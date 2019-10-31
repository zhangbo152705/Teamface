package com.hjhq.teamface.oa.main;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.BaseTitleActivity;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.PasswrodSetBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.FormValidationUtils;
import com.hjhq.teamface.basis.util.MD5;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.TextWatcherUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.componentservice.login.LoginService;
import com.luojilab.component.componentlib.router.Router;
import com.luojilab.router.facade.annotation.RouteNode;

import butterknife.Bind;

/**
 * 修改密码
 *
 * @author lx
 * @date 2017/6/15
 */

@RouteNode(path = "/changePwd", desc = "修改密码")
public class ChangePasswordActivity extends BaseTitleActivity {
    @Bind(R.id.et_old_password)
    EditText etOldPassword;
    @Bind(R.id.et_new_password)
    EditText etNewPassword;
    @Bind(R.id.et_affirm_password)
    EditText etAffirmPassword;
    @Bind(R.id.btn_commit)
    Button btnCommit;
    @Bind(R.id.show_pwd_1)
    ImageView ivShow1;
    @Bind(R.id.show_pwd_2)
    ImageView ivShow2;
    @Bind(R.id.show_pwd_3)
    ImageView ivShow3;
    private LoginService service;
    /**
     * 密码策略
     */
    private int pwdComplex = -1;
    /**
     * 密码长度
     */
    private int pwdLength = FormValidationUtils.MIN_PWD_LENGTH;
    /**
     * 是否修改密码后直接登录
     */
    private boolean isLogin;

    @Override
    protected int getChildView() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void setListener() {
        setOnClicks(btnCommit, ivShow1, ivShow2, ivShow3);
    }

    @Override
    protected void initView() {
        super.initView();
        setActivityTitle(R.string.change_password);
        isLogin = getIntent().getBooleanExtra(Constants.DATA_TAG1, false);
        TextWatcherUtil.setEdNoChinaese(etOldPassword);
        TextWatcherUtil.setEdNoChinaese(etNewPassword);
        TextWatcherUtil.setEdNoChinaese(etAffirmPassword);
    }

    @Override
    protected void initData() {
        service = (LoginService) Router.getInstance().getService(LoginService.class.getSimpleName());
        String userAccount = SPHelper.getUserAccount();
        service.getCompanySet(this, userAccount, new ProgressSubscriber<PasswrodSetBean>(this) {
            @Override
            public void onNext(PasswrodSetBean baseBean) {
                super.onNext(baseBean);
                PasswrodSetBean.DataBean pwdSet = baseBean.getData();
                if (pwdSet != null) {
                    pwdComplex = pwdSet.getPwd_complex();
                    pwdLength = pwdSet.getPwd_length();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_pwd_1:
                setPwdVisible(1);
                break;
            case R.id.show_pwd_2:
                setPwdVisible(2);
                break;
            case R.id.show_pwd_3:
                setPwdVisible(3);
                break;
            case R.id.btn_commit:
                changePassword();
                break;
            default:
                break;

        }

    }

    /**
     * 设置密码隐藏显示
     */
    public void setPwdVisible(int type) {
        switch (type) {
            case 1:
                changeState(ivShow1, etOldPassword, 1);
                break;
            case 2:
                changeState(ivShow2, etNewPassword, 2);
                break;
            case 3:
                changeState(ivShow3, etAffirmPassword, 3);
                break;
        }
    }

    /**
     * 切换密码显示隐藏
     *
     * @param imageView
     * @param et
     * @param type
     */
    private void changeState(ImageView imageView, EditText et, int type) {
        if (InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == et.getInputType()) {
            imageView.setImageResource(R.drawable.login_hide_pwd);
            et.setInputType((InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
        } else {
            imageView.setImageResource(R.drawable.login_view_pwd);
            et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        et.setSelection(getPassword(type).length());
    }

    /**
     * 得到密码
     */
    public String getPassword(int type) {
        switch (type) {
            case 1:

                return etOldPassword.getText().toString().trim();
            case 2:

                return etNewPassword.getText().toString().trim();
            case 3:

                return etAffirmPassword.getText().toString().trim();
        }
        return "";
    }

    /**
     * 修改密码
     */
    private void changePassword() {
        if (!verification()) {
            return;
        }
        String oldPwd = etOldPassword.getText().toString();
        String newPwd = etNewPassword.getText().toString();

        service.editPassWord(this, MD5.encodePasswordOrigin(oldPwd), MD5.encodePasswordOrigin(newPwd), new ProgressSubscriber<BaseBean>(this,true) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mContext, "修改密码成功");
                SPHelper.setUserPassword(MD5.encodePasswordOrigin(newPwd));
                if (isLogin) {
                    service.login(ChangePasswordActivity.this, SPHelper.getUserAccount(), MD5.encodePasswordOrigin(newPwd), true);
                } else {
                    finish();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showSuccess(mContext, "修改密码失败");
            }
        });
    }

    /**
     * 校验
     */
    private boolean verification() {
        if (pwdComplex < 0) {
            ToastUtils.showError(this, "未获取到密码安全策略，请重新获取");
            return false;
        }
        String affirmPwd = etAffirmPassword.getText().toString();
        String oldPwd = etOldPassword.getText().toString();
        String newPwd = etNewPassword.getText().toString();

        if (TextUtil.isEmpty(oldPwd)) {
            ToastUtils.showToast(this, "原密码不能为空");
            return false;
        }
        if (TextUtil.isEmpty(newPwd)) {
            ToastUtils.showToast(this, "新密码不能为空");
            return false;
        }
        if (TextUtil.isEmpty(affirmPwd)) {
            ToastUtils.showToast(this, "确认密码不能为空");
            return false;
        }
        if (!newPwd.equals(affirmPwd)) {
            ToastUtils.showToast(this, "新密码和确认密码输入不一致");
            return false;
        }

        if (newPwd.length() < pwdLength) {
            ToastUtils.showError(this, "不符合密码最小长度" + pwdLength + "位");
            return false;
        }

        switch (pwdComplex) {
            case 0: {
                boolean validation = FormValidationUtils.isPassword(newPwd);
                if (!validation) {
                    ToastUtils.showError(this, "新密码只能由数字、字母或特殊字符组成");
                    return false;
                }
                break;
            }
            case 1: {
                boolean upperCase = (FormValidationUtils.isUpperCase(newPwd) || FormValidationUtils.isLowerCase(newPwd)) && FormValidationUtils.isDight(newPwd);
                boolean validation = FormValidationUtils.validation("^[a-zA-Z0-9]+$", newPwd);
                if (!upperCase || !validation) {
                    ToastUtils.showError(this, "需包含字母和数字字符");
                    return false;
                }
                break;
            }
            case 2: {
                boolean upperCase = (FormValidationUtils.isUpperCase(newPwd) || FormValidationUtils.isLowerCase(newPwd))
                        && FormValidationUtils.isDight(newPwd) && FormValidationUtils.isSpecialChar(newPwd);
                boolean validation = FormValidationUtils.validation("^[a-zA-Z0-9~`@#$%^&*-_=+|\\?/()<>\\[\\]\\{\\}\",.;'!]+$", newPwd);
                if (!upperCase || !validation) {
                    ToastUtils.showError(this, "需包含字母、数字和特殊字符");
                    return false;
                }
                break;
            }
            case 3: {
                boolean upperCase = FormValidationUtils.isUpperCase(newPwd) && FormValidationUtils.isLowerCase(newPwd)
                        && FormValidationUtils.isDight(newPwd);
                boolean validation = FormValidationUtils.validation("^[a-zA-Z0-9]+$", newPwd);
                if (!upperCase || !validation) {
                    ToastUtils.showError(this, "需包含数字和大小写字母");
                    return false;
                }
                break;
            }
            case 4: {
                boolean upperCase = FormValidationUtils.isUpperCase(newPwd) && FormValidationUtils.isLowerCase(newPwd)
                        && FormValidationUtils.isDight(newPwd) && FormValidationUtils.isSpecialChar(newPwd);
                boolean validation = FormValidationUtils.validation("^[a-zA-Z0-9~`@#$%^&*-_=+|\\?/()<>\\[\\]\\{\\}\",.;'!]+$", newPwd);
                if (!upperCase || !validation) {
                    ToastUtils.showError(this, "需包含数字、大小写字母和特殊字符");
                    return false;
                }
                break;
            }
            default:
                break;
        }
        return true;
    }
}

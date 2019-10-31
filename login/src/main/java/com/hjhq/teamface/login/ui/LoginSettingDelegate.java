package com.hjhq.teamface.login.ui;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.login.R;

/**
 * 登录视图
 *
 * @author Administrator
 */

public class LoginSettingDelegate extends AppDelegate {

    public EditText ip_et;
    public Button loginsetting_btn;
    public ImageView clear_ip_iv;
    public ImageView check_iv;
    public LinearLayout remenber_li;
    public ImageView more_ip_iv;
    public RelativeLayout rl_ip;


    @Override
    public int getRootLayoutId() {
        return R.layout.login_activity_setting;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setLeftIcon(R.drawable.icon_blue_back);
        setLeftText(R.color.app_blue, "返回");
        ip_et = get(R.id.ip_et);
        loginsetting_btn = get(R.id.loginsetting_btn);
        clear_ip_iv = get(R.id.clear_ip_iv);
        check_iv = get(R.id.check_iv);
        remenber_li = get(R.id.remenber_li);
        more_ip_iv = get(R.id.more_ip_iv);
        rl_ip =  get(R.id.rl_ip);
    }


}

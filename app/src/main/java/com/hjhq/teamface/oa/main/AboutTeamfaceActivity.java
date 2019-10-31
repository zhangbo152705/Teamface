package com.hjhq.teamface.oa.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.BaseTitleActivity;
import com.hjhq.teamface.basis.bean.UpdateBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.provider.MyFileProvider;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.common.activity.ViewUserProtocolActivity;
import com.hjhq.teamface.download.utils.FileTransmitUtils;
import com.hjhq.teamface.im.ImLogic;
import com.hjhq.teamface.oa.login.logic.SettingHelper;
import com.hjhq.teamface.util.CommonUtil;
import com.kyleduo.switchbutton.SwitchButton;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;

/**
 * 关于
 *
 * @author lx
 * @date 2017/6/15
 */
@RouteNode(path = "/about_teamface", desc = "关于Teamface")
public class AboutTeamfaceActivity extends BaseTitleActivity {
    @Bind(R.id.app_version)
    TextView app_version;
    @Bind(R.id.app_service)
    TextView app_service;
    @Bind(R.id.app_teams_version)
    TextView app_teams_version;
    @Bind(R.id.app_version_time)
    TextView app_version_time;
    @Bind(R.id.ll_score)
    LinearLayout ll_score;
    @Bind(R.id.ll_update_version)
    LinearLayout ll_update_version;
    @Bind(R.id.re_app_message)
    RelativeLayout re_app_message;





    @Override
    protected int getChildView() {
        return R.layout.activity_about_teamface;
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View view) {

    }


    @Override
    protected void initView() {
        super.initView();
        getWindow().setBackgroundDrawableResource(R.drawable.login_start_bg_no_text_v2);
        setActivityTitle(R.string.app_about_teamface);

        ll_update_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUpdateInfo();
            }
        });

        app_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DATA_TAG1, Constants.USER_AGGREMENT_URL);
                com.hjhq.teamface.common.utils.CommonUtil.startActivtiy(mContext, ViewUserProtocolActivity.class, bundle);
                UIRouter.getInstance().openUri(mContext, "DDComp://login/view_user_protocol", null);
            }
        });
    }



    @Override
    protected void initData() {
        TextUtil.setText(app_version,SystemFuncUtils.getVersionName(AboutTeamfaceActivity.this));
        Calendar cd = Calendar.getInstance();
        int year =  cd.get(Calendar.YEAR);
        String versionTime = String.format(getResources().getString(R.string.teams_of_version_time),year);
        app_version_time.setText(versionTime);
        app_service.setText("{ "+getResources().getString(R.string.teams_of_service)+" }");
    }


    private void getUpdateInfo() {
        ImLogic.getInstance().getVersionList(this, new ProgressSubscriber<UpdateBean>(mContext, true) {
            @Override
            public void onNext(UpdateBean baseBean) {
                super.onNext(baseBean);
                List<UpdateBean.DataBean.DataListBean> dataList = baseBean.getData().getDataList();
                if (dataList != null && dataList.size() > 0) {
                    if (!SystemFuncUtils.getVersionName(mContext).equals(dataList.get(dataList.size() - 1).getV_code())) {
                        DialogUtils.getInstance().sureOrCancel(mContext, "版本升级", dataList.get(dataList.size() - 1).getV_content(),
                                re_app_message,"升级" ,"取消" , new DialogUtils.OnClickSureOrCancelListener() {
                                    @Override
                                    public void clickSure() {
                                        Uri uri = Uri.parse("https://android.myapp.com/myapp/detail.htm?apkName=com.hjhq.teamface&ADTAG=mobile");
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void clickCancel() {

                                    }
                                });
                    }else {
                        ToastUtils.showToast(AboutTeamfaceActivity.this,getResources().getString(R.string.update_message));
                    }
                }else {
                    ToastUtils.showToast(AboutTeamfaceActivity.this,getResources().getString(R.string.no_update_message));
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showToast(AboutTeamfaceActivity.this,getResources().getString(R.string.get_update_message_error));
            }
        });
    }

}

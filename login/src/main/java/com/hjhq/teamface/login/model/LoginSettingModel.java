package com.hjhq.teamface.login.model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.UserInfoBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EventConstant;
import com.hjhq.teamface.basis.network.ApiManager;
import com.hjhq.teamface.basis.network.exception.HttpResultFunc;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.network.utils.TransformerHelper;
import com.hjhq.teamface.basis.util.AppManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.IModel;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.im.IM;
import com.hjhq.teamface.login.LoginApiService;
import com.hjhq.teamface.login.R;
import com.hjhq.teamface.login.bean.LoginResponseBean;
import com.hjhq.teamface.login.presenter.LoginActivity;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;


/**
 * 登录
 */
public class LoginSettingModel implements IModel<LoginApiService> {

    @Override
    public LoginApiService getApi() {
        return new ApiManager<LoginApiService>().getAPI(LoginApiService.class);
    }



}

package com.hjhq.teamface.login.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hjhq.teamface.basis.AppConst;
import com.hjhq.teamface.basis.bean.ToolMenu;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.ScreenUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.file.SPUtils;
import com.hjhq.teamface.basis.util.popupwindow.ListItemAdapter;
import com.hjhq.teamface.basis.util.popupwindow.MenuItemAdapter;
import com.hjhq.teamface.basis.view.CustomPopWindow;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.login.R;
import com.hjhq.teamface.login.model.LoginSettingModel;
import com.hjhq.teamface.login.ui.LoginSettingDelegate;
import java.util.ArrayList;
import java.util.List;



/**
 * 登录
 *
 * @author lx
 * @date 2017/3/22
 */

public class LoginSettingActivity extends ActivityPresenter<LoginSettingDelegate, LoginSettingModel> implements View.OnClickListener {

    private static String adress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.login_window_blank_bg);
    }

    @Override
    public void init() {
        String domain = SPHelper.getLoginSettingDomain();
        if(!TextUtil.isEmpty(domain)){
            viewDelegate.ip_et.setText(domain);
            viewDelegate.clear_ip_iv.setVisibility(View.VISIBLE);
            viewDelegate.setRightMenuTexts(R.color.app_blue, "重置");
        }else {
            viewDelegate.clear_ip_iv.setVisibility(View.GONE);
        }

        if (SPHelper.getIsRemenberIp()){
            viewDelegate.check_iv.setBackgroundResource(R.drawable.project_icon_check);
        }else {
            viewDelegate.check_iv.setBackgroundResource(R.drawable.project_icon_uncheck);
        }
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.loginsetting_btn.setOnClickListener(this);
        viewDelegate.clear_ip_iv.setOnClickListener(this);
        viewDelegate.remenber_li.setOnClickListener(this);
        viewDelegate.more_ip_iv.setOnClickListener(this);
        viewDelegate.ip_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                 String content = viewDelegate.ip_et.getText().toString();
                 if (TextUtil.isEmpty(content)){
                     viewDelegate.clear_ip_iv.setVisibility(View.GONE);
                 }else {
                     viewDelegate.clear_ip_iv.setVisibility(View.VISIBLE);
                 }

            }
        });
    }

    /**
     * 显示历史记录
     * @param activity
     * @param parent
     * @param list
     */
    public  void showAnswerSortMenu(Activity activity, View parent, List<ToolMenu> list) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.loginsetting_history_ip_item, null);
        RecyclerView rv = view.findViewById(R.id.ip_rv);
        ListItemAdapter menuItemAdapter = new ListItemAdapter(list);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.setAdapter(menuItemAdapter);
        int width = parent.getWidth();
        final CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(activity)
                .setView(view)//显示的布局，还可以通过设置一个View
                .setWidth(width)
                .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
                .setAnimationStyle(com.hjhq.teamface.basis.R.style.pop_born_anim)
                .setBgAlpha(1)
                .create()//创建PopupWindowpopWindow
                .showAsDropDown(parent,0,10);//显示PopupWindow
        //菜单列表被选中
        rv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (list.get(position) != null && !TextUtil.isEmpty(list.get(position).getTitle())){
                    viewDelegate.ip_et.setText(list.get(position).getTitle());
                    popWindow.dissmiss();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
            if (view.getId() ==R.id.loginsetting_btn ){
                saveAdress();
            }else if (view.getId() ==R.id.clear_ip_iv ){
                viewDelegate.ip_et.setText("");
            }else if (view.getId() == R.id.remenber_li){
                if (SPHelper.getIsRemenberIp()){
                    viewDelegate.check_iv.setBackgroundResource(R.drawable.project_icon_uncheck);
                    SPHelper.setIsRemenberIp(false);
                }else {
                    viewDelegate.check_iv.setBackgroundResource(R.drawable.project_icon_check);
                    SPHelper.setIsRemenberIp(true);
                }

            }else if (view.getId() == R.id.more_ip_iv){
                handleHistoryIp();
            }
    }

    /**
     * 处理历史IP数据
     */
    public void handleHistoryIp(){
        String historyIp = SPHelper.getHistorySocketUrl();
        if (!TextUtil.isEmpty(historyIp)){
            String[] historyArr = historyIp.split(",");
            if (historyArr != null && historyArr.length>0){
                List<ToolMenu> list = new ArrayList<>();
               for(String item : historyArr){
                   ToolMenu itemMenu = new ToolMenu();
                   itemMenu.setTitle(item);
                   list.add(itemMenu);
               }
                showAnswerSortMenu(this,viewDelegate.rl_ip,list);
            }
        }
    }

    /**
     * 清除本地设置IP
     */
    private void clearIp() {

        DialogUtils.getInstance().sureDialog(this, getString(R.string.loginsetting_delete), getString(R.string.loginsetting_delete_ip)
                , viewDelegate.getRootView(), new DialogUtils.OnClickSureListener() {
                    @Override
                    public void clickSure() {
                        viewDelegate.ip_et.setText("");
                        SPUtils.remove(LoginSettingActivity.this, AppConst.LOGINGSETTING_DOMAIN);
                        ResetUrl();
                    }
                });
    }
    public static void ResetUrl(){
        Constants.BASE_URL = "https://file.teamface.cn/custom-gateway/";
        Constants.SOCKET_URI = "wss://push.teamface.cn";
        Constants.STATISTIC_REPORT_URL = "https://page.teamface.cn/#/tables";
        Constants.STATISTIC_CHART_URL = "https://page.teamface.cn/#/echarts";
        Constants.EMAIL_DETAIL_URL = "https://page.teamface.cn/#/emailDetail";
        Constants.EMAIL_EDIT_URL = "https://page.teamface.cn/#/emailEdit";
        Constants.PRJECT_TASK_EDIT_URL = "https://page.teamface.cn/#/hierarchyPreview";
    }

    /**
     * 保存ip或域名
     */
    public void saveAdress() {
        if (viewDelegate.ip_et.getText() != null && !TextUtil.isEmpty(viewDelegate.ip_et.getText().toString())) {
            adress = viewDelegate.ip_et.getText().toString().trim();

            if (TextUtil.isIp(adress)) {
                SPHelper.setLoginSettingDomain(adress);
                initLocalDamain(adress);
                //保存历史记录
                if (SPHelper.getIsRemenberIp()){
                    String historyAdress = SPHelper.getHistorySocketUrl();
                    if (TextUtil.isEmpty(historyAdress)){
                        historyAdress = adress;
                        SPHelper.setHistorySocketUrl(historyAdress);
                    }else {
                        if(historyAdress.indexOf(adress) == -1){
                            historyAdress =adress+"," +historyAdress;
                            SPHelper.setHistorySocketUrl(historyAdress);
                        }
                    }

                }

                ToastUtils.showToast(this, "ip或域设置成功");
                finish();
            } else {
                ToastUtils.showToast(this, "ip或域名不合法");
            }


        } else {
            ToastUtils.showToast(this, "ip或域名输入不能为空");
        }

    }



    /**
     * 初始化本地ip和H5页面IP
     * @param localDamain
     */
    public void initLocalDamain(String localDamain){
        if (!TextUtil.isEmpty(localDamain)){
            Constants.BASE_URL = localDamain + "/custom-gateway/";
            Constants.EMAIL_DETAIL_URL = localDamain+"/dist/H5.html#/emailDetail";
            Constants.EMAIL_EDIT_URL = localDamain+"/dist/H5.html#/emailEdit";
            Constants.PRJECT_TASK_EDIT_URL = localDamain+"/dist/H5.html#/hierarchyPreview";
            Constants.STATISTIC_REPORT_URL = localDamain+"/dist/H5.html#/tables";
            Constants.STATISTIC_CHART_URL = localDamain+"/dist/H5.html#/echarts";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        clearIp();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
         finish();
    }
}

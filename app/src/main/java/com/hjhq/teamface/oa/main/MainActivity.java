package com.hjhq.teamface.oa.main;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hjhq.teamface.MyApplication;
import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.BaseActivity;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.CommonModuleResultBean;
import com.hjhq.teamface.basis.bean.EmployeeResultBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ProgressBean;
import com.hjhq.teamface.basis.bean.UpdateBean;
import com.hjhq.teamface.basis.constants.AppConstant;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.EventConstant;
import com.hjhq.teamface.basis.constants.FileConstants;
import com.hjhq.teamface.basis.constants.IMState;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.provider.MyFileProvider;
import com.hjhq.teamface.basis.util.AppManager;
import com.hjhq.teamface.basis.util.BadgeUtil;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.DeviceUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.JYFileHelper;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.FragmentAdapter;
import com.hjhq.teamface.common.view.NoScrollViewPager;
import com.hjhq.teamface.im.IM;
import com.hjhq.teamface.im.IMService;
import com.hjhq.teamface.im.ImLogic;
import com.hjhq.teamface.im.activity.TeamMessageFragment;
import com.hjhq.teamface.im.db.DBManager;
import com.hjhq.teamface.oa.approve.ui.ApproveActivity;
import com.hjhq.teamface.oa.login.logic.SettingHelper;
import com.hjhq.teamface.oa.main.logic.MainLogic;
import com.hjhq.teamface.util.CommonUtil;
import com.hjhq.teamface.view.quickaction.ActionItem;
import com.hjhq.teamface.view.quickaction.QuickAction;
import com.hjhq.teamface.viewmodels.MainViewmodel;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import rx.Observable;


@RouteNode(path = "/main", desc = "文件详情界面")
public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @Bind(R.id.main_vp)
    NoScrollViewPager mViewPager;
    @Bind(R.id.iv_data_analysis)
    ImageView ivDataAnalysis;
    @Bind(R.id.tv_data_analysis)
    TextView tvDataAnalysis;
    @Bind(R.id.workbench_iv)
    ImageView workbenchIv;
    @Bind(R.id.workbench_tv)
    TextView workbenchTv;
    @Bind(R.id.workbench_rl)
    RelativeLayout workbenchRl;
    @Bind(R.id.teamwork_rl)
    RelativeLayout teamworkRl;
    @Bind(R.id.addIv)
    ImageView addIv;
    @Bind(R.id.team_message_iv)
    ImageView teamMessageIv;
    @Bind(R.id.team_message_tv)
    TextView teamMessageTv;
    @Bind(R.id.team_message_rl)
    RelativeLayout teamMessageRl;
    @Bind(R.id.team_mine_rl)
    RelativeLayout teamMineRl;
    @Bind(R.id.mine_iv)
    ImageView mineIv;
    @Bind(R.id.mine_tv)
    TextView mineTv;
    @Bind(R.id.team_contact_rl)
    RelativeLayout teamContactRl;
    @Bind(R.id.contact_iv)
    ImageView contactIv;
    @Bind(R.id.contact_tv)
    TextView contactTv;
    @Bind(R.id.actionbar_ll)
    LinearLayout actionbarLl;
    @Bind(R.id.tv_im_total_unread_num)
    TextView imUnreadNum;
    @Bind(R.id.fl_statistics)
    FrameLayout mFlData;
    @Bind(R.id.fl_chat)
    FrameLayout mFlChat;
    @Bind(R.id.fl_cc)
    FrameLayout mFlCc;
    @Bind(R.id.activity_root)
    RelativeLayout rootView;

    private static NotificationManager mNotificationManager;
    private ScheduledExecutorService scheduledExecutorService;
    private Runnable progressRunnable;
    private MyReceiver mReceiver;
    //自定义模块快捷方式
    AppModuleBean mAppModuleBean;

    private long time;

    public static boolean canOpenFullscreenMode = true;


    private ImageView[] actionBarIvs = new ImageView[5];
    private TextView[] actionBarTvs = new TextView[5];
    private int[] normalActionBarDrawable = {R.drawable.actionbar_workbench_normal, R.drawable.actionbar_team_message_normal,R.drawable.actionbar_message_normal, R.drawable.actionbar_contacts_normal, R.drawable.actionbar_mine_normal};
    private int[] clickActionBarDrawable = {R.drawable.actionbar_workbench_click, R.drawable.actionbar_team_message_click, R.drawable.actionbar_message_click,R.drawable.actionbar_contacts_click, R.drawable.actionbar_mine_click};

    private static List<Fragment> fragments = new ArrayList<>(5);
    private static StatisticFragment mStatisticFragment;

    private QuickAction quickActionAdd;

    private int currentItem;
    private  MainViewmodel model;
    private boolean isChangeCompany = false;
    protected void onSetContentViewNext(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            currentItem = getIntent().getIntExtra(Constants.DATA_TAG1, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setBackgroundDrawable(null);
        startIMService();
        //检测企信服务是否运行
        checkIMServiceIsAlive();
        initReceiver();
        initIMEI();
        final Intent intent = getIntent();
        openModule(intent);
        //获取当前WiFi信息,并保存到IMState中.
        getCurrentWifiInfo();
         model = ViewModelProviders.of(this).get(MainViewmodel.class);
        model.setContext(this);
    }

    private void checkUpdate() {
        Long updateCheckTime = SPHelper.getUpdateCheckTime();
        if (updateCheckTime == null || updateCheckTime == 0L) {
            getUpdateInfo();
            SPHelper.setUpdateCheckTime(System.currentTimeMillis());

        } else {
            SPHelper.setUpdateCheckTime(System.currentTimeMillis());
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(updateCheckTime);
            if (c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) {
                getUpdateInfo();
            }
        }


    }

    private void getUpdateInfo() {
        ImLogic.getInstance().getVersionList(MainActivity.this, new ProgressSubscriber<UpdateBean>(mContext, false) {
            @Override
            public void onNext(UpdateBean baseBean) {
                super.onNext(baseBean);
                List<UpdateBean.DataBean.DataListBean> dataList = baseBean.getData().getDataList();
                if (dataList != null && dataList.size() > 0) {
                    if (!SystemFuncUtils.getVersionName(mContext).equals(dataList.get(dataList.size() - 1).getV_code())) {
                        DialogUtils.getInstance().sureOrCancel(mContext, "版本升级", dataList.get(dataList.size() - 1).getV_content(),
                                mViewPager,  "升级","取消", new DialogUtils.OnClickSureOrCancelListener() {
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
                    }

                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        openModule(intent);
    }

    private void openModule(Intent intent) {
        if (intent == null) {
            return;
        }
        //String moduleBean = intent.getStringExtra(Constants.DATA_TAG1);
        String moduleBean = "";//zzh->ad:Constants.DATA_TAG 获取的未int 数据
        try{
            if(intent != null && intent.getIntExtra(Constants.DATA_TAG1,-10) != -10){
                moduleBean = ""+intent.getIntExtra(Constants.DATA_TAG1,-10);
            }
        }catch (Exception e){
            e.printStackTrace();
            moduleBean = intent.getStringExtra(Constants.DATA_TAG1);
        }

        Log.e("openModule","moduleBean:"+moduleBean);
        String moduleId = intent.getStringExtra(Constants.DATA_TAG2);
        String moduleName = intent.getStringExtra(Constants.DATA_TAG3);
        String id = intent.getStringExtra(Constants.DATA_TAG4);
        if (!TextUtils.isEmpty(moduleBean)) {
            mAppModuleBean = new AppModuleBean();
            mAppModuleBean.setEnglish_name(moduleBean);
            mAppModuleBean.setModule_id(moduleId);
            mAppModuleBean.setChinese_name(moduleName);
            mAppModuleBean.setId(id);
            if (fragments.get(2) != null) {
                ((WorkbenchFragmentV2) fragments.get(2)).openModule(mAppModuleBean, mAppModuleBean.getId());
            }
        }

    }


    private void initIMEI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SystemFuncUtils.requestPermissions(this, Manifest.permission.READ_PHONE_STATE, aBoolean -> {
                if (aBoolean) {
                    saveIMEI();
                } else {
                    ToastUtils.showError(mContext, "必须获得必要的权限才能正常使用!");
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_CODE7);
                }
            });
        } else {
            saveIMEI();
        }

    }

    private void saveIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Activity.TELEPHONY_SERVICE);
        String imei = "";
        //在 Android 9 中，Build.SERIAL 始终设置为 "UNKNOWN" 以保护用户的隐私。
        //如果您的应用需要访问设备的硬件序列号，您应改为请求 READ_PHONE_STATE 权限，然后调用 getSerial()。
        if (Build.VERSION.SDK_INT >= 28) {
            final String serial = Build.getSerial();
            Log.e("serial", serial + "<<");
        }
        if (Build.VERSION.SDK_INT >= 26) {
            imei = telephonyManager.getImei();
        } else {
            imei = telephonyManager.getDeviceId();
        }
        SPHelper.setImei(Math.abs(imei.hashCode()) + "");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE7:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveIMEI();
                }
                break;
            default:
                break;
        }
    }


    private void initReceiver() {
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileConstants.FILE_DOWNLOAD_PROGRESS_ACTION);
        registerReceiver(mReceiver, intentFilter);
    }


    private void checkIMServiceIsAlive() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                startIMService(MainActivity.this);
            }
        };
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(progressRunnable, 10, 10, TimeUnit.SECONDS);
    }


    private void startIMService(Context context) {

        try {
            if (!SystemFuncUtils.isServiceRunning(mContext, IMService.class.getName())) {
                Intent intent = new Intent(context, IMService.class);
                context.startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        fragments.clear();
        fragments.add(new WorkbenchFragmentV3());
        fragments.add(new TeamMessageFragment());
        fragments.add(new WorkbenchFragmentV2());
        fragments.add(new ContactsFragment());
        fragments.add(new MineFragment());

        mViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments));
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(this);
        //禁止ViewPager滑动，以免造成逻辑混乱
        mViewPager.setNoScroll(true);

        actionBarIvs[0] = ivDataAnalysis;
        actionBarIvs[1] = teamMessageIv;
        actionBarIvs[2] = workbenchIv;
        actionBarIvs[3] = contactIv;
        actionBarIvs[4] = mineIv;

        actionBarTvs[0] = tvDataAnalysis;
        actionBarTvs[1] = teamMessageTv;
        actionBarTvs[2] = workbenchTv;
        actionBarTvs[3] = contactTv;
        actionBarTvs[4] = mineTv;

        changeActionBar(currentItem);
        mViewPager.setCurrentItem(currentItem);

        // 初始化快速添加菜单
        quickActionAdd = initAddMenu(this);
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUpdate();
            }
        }, 5000);


    }

    @Override
    protected void setListener() {
        setOnClicks(workbenchRl, teamworkRl, teamMessageRl, teamMineRl,teamContactRl,addIv);
        teamMineRl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Constants.IS_DEBUG) {
                    SettingHelper.logout();
                }
                return true;
            }
        });

    }

    @Override
    protected void initData() {
        initNotification();
        initContacts();
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "message";
            String channelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
            channelId = "push";
            channelName = "推送消息";
            importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        mNotificationManager.createNotificationChannel(channel);
    }

    private void initContacts() {
        MainLogic.getInstance().getEmployee(mContext, null, new ProgressSubscriber<EmployeeResultBean>(mContext, false) {
            @Override
            public void onNext(EmployeeResultBean employeeResultBean) {
                super.onNext(employeeResultBean);
                List<Member> list = employeeResultBean.getData();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setMyId(SPHelper.getUserId());
                    list.get(i).setCompany_id(SPHelper.getCompanyId());
                }
                DBManager.getInstance().saveAll(list);
                EventBusUtils.sendEvent(new MessageBean(0, MsgConstant.MEMBER_MAYBE_CHANGED_TAG, null));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.team_message_rl:
                mViewPager.setCurrentItem(0);
                ((WorkbenchFragmentV2) fragments.get(2)).setAutoPlay(false);
                if (mViewPager.getCurrentItem() == 0) {
                    ((WorkbenchFragmentV3) fragments.get(0)).hideDataList();
                }
                break;
            case R.id.teamwork_rl:
                mViewPager.setCurrentItem(1);
                ((WorkbenchFragmentV2) fragments.get(2)).setAutoPlay(false);
                break;
            case R.id.workbench_rl:
                mViewPager.setCurrentItem(2);
                ((WorkbenchFragmentV2) fragments.get(2)).setAutoPlay(true);
                // ((WorkbenchFragmentV2) fragments.get(1)).refreshDataAll();
                break;

            case R.id.team_contact_rl:
                mViewPager.setCurrentItem(3);
                ((WorkbenchFragmentV2) fragments.get(2)).setAutoPlay(false);
                break;
            case R.id.team_mine_rl:
                mViewPager.setCurrentItem(4);
                ((WorkbenchFragmentV2) fragments.get(2)).setAutoPlay(false);
                break;
            case R.id.addIv:
                quickAdd(view);
                break;
            default:
                break;
        }
    }

    public AppModuleBean getAppModuleBean() {
        return mAppModuleBean;
    }

    public void setAppModuleBean(AppModuleBean appModuleBean) {
        mAppModuleBean = appModuleBean;
    }


    private void quickAdd(View view) {
        MainLogic.getInstance().getQuicklyAdd(this, new ProgressSubscriber<CommonModuleResultBean>(this, false) {
            @Override
            public void onNext(CommonModuleResultBean appListResultBean) {
                super.onNext(appListResultBean);
                List<AppModuleBean> commonApplication = appListResultBean.getData();
                List<ActionItem> actionItems = new ArrayList<>();
                if (!CollectionUtils.isEmpty(commonApplication)) {
                    Observable.from(commonApplication)
                            .filter(app -> actionItems.size() < QuickAction.QUICK_MODULE_COUNT)
                            .subscribe(bean -> actionItems.add(new ActionItem(bean.getId(), bean.getEnglish_name(), bean.getChinese_name(), bean.getIcon_url(), bean.getIcon_color(), bean.getIcon_type())));
                } else {
                    actionItems.add(new ActionItem(ProjectConstants.PROJECT_BEAN_NAME, "协作"));
                    actionItems.add(new ActionItem(EmailConstant.BEAN_NAME, "邮件"));
                    actionItems.add(new ActionItem(FileConstants.BEAN_NAME, "文件库"));
                    actionItems.add(new ActionItem(MemoConstant.BEAN_NAME, "备忘录"));
                    actionItems.add(new ActionItem(ApproveConstants.APPROVAL_MODULE_BEAN, "审批"));
                }
                quickActionAdd.addActionItem(actionItems);
                quickActionAdd.show(view, addIv.getWidth() / 2,
                        (int) DeviceUtils.dpToPixel(mContext, -15));
            }
        });
    }


    private void changeActionBar(int position) {
        resetActionBar();
        actionBarIvs[position].setImageResource(clickActionBarDrawable[position]);
        actionBarTvs[position].setTextColor(ContextCompat.getColor(mContext, R.color.app_blue));

    }


    private void resetActionBar() {

        for (int i = 0; i < actionBarIvs.length; i++) {
            actionBarIvs[i].setImageResource(normalActionBarDrawable[i]);
            actionBarTvs[i].setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeActionBar(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public QuickAction initAddMenu(final Activity activity) {
        QuickAction quickActionAdd = new QuickAction(activity,
                QuickAction.HORIZONTAL);

        // 设置 Item点击监听事件
        quickActionAdd.setOnActionItemClickListener(item -> {
            String moduleId = item.getModuleId();
            String moduleBean = item.getModuleBean();
            if (moduleBean == null) moduleBean = "";
            //系统模块
            Bundle bundle = new Bundle();
            switch (moduleBean) {
                case ApproveConstants.APPROVAL_MODULE_BEAN:
                    bundle.putString(Constants.MODULE_BEAN, ApproveConstants.APPROVAL_MODULE_BEAN);
                    CommonUtil.startActivtiy(MainActivity.this, ApproveActivity.class);
                    break;
                case ProjectConstants.PROJECT_BEAN_NAME:
                    bundle.putString(Constants.MODULE_BEAN, moduleBean);
                    UIRouter.getInstance().openUri(mContext, "DDComp://project/addproject", bundle);
                    break;
                case MemoConstant.BEAN_NAME:
                    bundle.putString(Constants.MODULE_BEAN, moduleBean);
                    UIRouter.getInstance().openUri(mContext, "DDComp://memo/add", bundle);
                    break;
                case EmailConstant.BEAN_NAME:
                    bundle.putInt(Constants.DATA_TAG3, EmailConstant.ADD_NEW_EMAIL);
                    UIRouter.getInstance().openUri(mContext, "DDComp://email/new_email", bundle);
                    break;
                case FileConstants.BEAN_NAME:
                    UIRouter.getInstance().openUri(mContext, "DDComp://filelib/netdisk", bundle);
                    break;
                default:
                    if (TextUtil.isEmpty(moduleId)) {
                        ToastUtils.showError(mContext, "未获取到模块ID");
                        return;
                    }
                    bundle.putString(Constants.MODULE_BEAN, item.getModuleBean());
                    bundle.putString(Constants.MODULE_ID, moduleId);
                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/add", bundle);
                    break;
            }

        });
        return quickActionAdd;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //返回键不退出程序，改为显示桌面
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mFlData.getVisibility() == View.VISIBLE) {
                closeStatistics();
                return true;
            }
            //返回隐藏弹出工作台的任务列表
            if (mViewPager.getCurrentItem() == 0 && ((WorkbenchFragmentV3) fragments.get(0)).openedListIndex < 100) {
                ((WorkbenchFragmentV3) fragments.get(0)).hideDataList();
                return true;
            }
            if (System.currentTimeMillis() - time > 3000) {
                ToastUtils.showToast(mContext, "再次点击进入后台");
                time = System.currentTimeMillis();
            } else {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageBean bean) {
        if (bean.getCode() == Constants.REQUEST_SWITCH_COMPANY) {
            int currentItem = mViewPager.getCurrentItem();

            AppManager.getAppManager().finishAllActivity();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1, currentItem);
            CommonUtil.startActivtiy(this, MainActivity.class, bundle);
            isChangeCompany = true;
            mStatisticFragment =null;//zzh:切换公司时把数据分析表置null
            finish();//关闭自己
            overridePendingTransition(0, 0); //去掉Activity切换时的动画
            return;
        }
        if (bean.getTag() == null) {
            return;
        }
        switch (bean.getTag()) {
            case EventConstant.TYPE_ORGANIZATIONAL_AND_PERSONNEL_CHANGES:
                //人员或组织架构变化,更新数据
                initContacts();
                break;
            case MsgConstant.IM_TOTAL_UNREAD_NUM:
                //显示总的未读消息数
                setUnreadNum(bean.getCode());
                break;
            case AppConstant.SHOW_STATISTIC:
                //显示数据分析
                showStatistics();
                break;
            case AppConstant.HIDE_STATISTIC:
                //隐藏数据分析
                closeStatistics();
                break;
            case MsgConstant.IM_SERVICE_DYING:
                //企信服务关闭,重启
                Log.e("MainActivity", "正在重启企信服务");
                IM.getInstance().writeFileToSDCard(DateTimeUtil.longToStr(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss   ") + "正在重启企信服务");

                actionbarLl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, IMService.class);
                        try {
                            if (!SystemFuncUtils.isServiceRunning(mContext, IMService.class.getName())) {
                                ComponentName name = mContext.startService(intent);
                                IM.getInstance().writeFileToSDCard(new Gson().toJson(name));

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
                break;
            default:

                break;
        }
        if (CacheDataHelper.CUSTOM_MODULE_CACHE_DATA_NUM == bean.getCode()) {
            updateNum(bean.getTag(), TextUtil.parseInt(bean.getObject().toString(), 0));
        }
        if (CacheDataHelper.CUSTOM_MODULE_CACHE_DATA_NUM2 == bean.getCode()) {
            updateModule(bean.getTag(), TextUtil.parseInt(bean.getObject().toString(), 0));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateNum(String bean, int num) {
        //ImLogic.getInstance().updateNum(this, bean, num);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateModule(String bean, int num) {
        //ImLogic.getInstance().updateModule(this, bean, num + "");
    }

    private void showStatistics() {
        if (mStatisticFragment == null) {
            mStatisticFragment = new StatisticFragment();
            android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();
            supportFragmentManager.beginTransaction().replace(R.id.fl_statistics, mStatisticFragment).commit();
        }
        mFlData.setVisibility(View.VISIBLE);
    }

    private void closeStatistics() {
        mFlData.setVisibility(View.GONE);
    }


    private void setUnreadNum(int num) {
        if (num < 0) {
            num = 0;
        }
        showBadgeNum(num);
        if (num <= 0) {
            imUnreadNum.setVisibility(View.GONE);
            return;
        }
        imUnreadNum.setVisibility(View.VISIBLE);
        if (num > 0 && num <= MsgConstant.SHOW_MAX_EXACT_NUM) {
            if (num < 10) {
                imUnreadNum.setBackground(getResources().getDrawable(R.drawable.im_unread_num_round_bg));
            } else {
                imUnreadNum.setBackground(getResources().getDrawable(R.drawable.im_unread_num_bg));
            }
            imUnreadNum.setText(num + "");
        } else if (num > MsgConstant.SHOW_MAX_EXACT_NUM) {
            imUnreadNum.setBackground(getResources().getDrawable(R.drawable.im_unread_num_bg));
            imUnreadNum.setText("99+");
        }
    }


    private void showBadgeNum(int num) {
        BadgeUtil.setBadge(MainActivity.this, num);
    }

    private Notification getNotification() {
        Log.e("MainActivity", "启动前台服务");
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(mContext,
                100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notification = new NotificationCompat.Builder(mContext, "push")
                    .setContentTitle("TEAMFACE")
                    .setContentText("")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.status_bar_icon)
                    .setContentIntent(pi)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.logo))
                    .setAutoCancel(true)
                    .build();

        } else {
            notification = new Notification.Builder(mContext)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.logo))

                    .setSmallIcon(R.mipmap.status_bar_icon)

                    .setTicker(Constants.APP_NAME)

                    .setContentIntent(pi)

                    .setContentTitle("TEAMFACE")

                    .setContentText("")

                    .setWhen(System.currentTimeMillis())

                    .setPriority(Notification.PRIORITY_DEFAULT)

                    .setAutoCancel(true)

                    .setOngoing(false)

                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                    .setContentIntent(PendingIntent.getActivity(mContext, MsgConstant.NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .build();
        }
        return notification;
    }


    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (FileConstants.FILE_DOWNLOAD_PROGRESS_ACTION.equals(action)) {
                //文件下载进度
                ProgressBean bean = (ProgressBean) intent.getSerializableExtra(Constants.DATA_TAG1);
                if (bean != null && MsgConstant.NEW_VERSION_APK_ID.equals(bean.getFileId())) {
                    if (bean.isDone()
                            || bean.getBytesRead() == bean.getContentLength()
                            || (int) ((bean.getBytesRead() * 100) / bean.getContentLength()) == 100
                            ) {
                        File file = new File(JYFileHelper.getFileDir(context, Constants.PATH_DOWNLOAD), MsgConstant.NEW_VERSION_APK_ID + "12.apk");
                        installProcess(file);
                    }
                }
            }

        }

    }


    public void installProcess(File apk) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = isHasInstallPermissionWithO(mContext);
            if (!hasInstallPermission) {
                startInstallPermissionSettingActivity();
            } else {
                //有权限，开始安装应用程序
                installApk(apk);
            }
        } else {
            installApk(apk);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, Constants.REQUEST_CODE9);
    }


    private void installApk(File apk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        } else {
            //Android7.0之后获取uri要用contentProvider
            Uri uri = MyFileProvider.getUriForFile(getBaseContext(), Constants.FILE_PROVIDER, apk);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isHasInstallPermissionWithO(Context context) {
        if (context == null) {
            return false;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE8);
    }

    public void startIMService() {
        Intent intent = new Intent(mContext, IMService.class);
        try {
            if (!SystemFuncUtils.isServiceRunning(mContext, IMService.class.getName())) {
                //ComponentName name = mContext.startService(intent);
                //IM.getInstance().writeFileToSDCard(new Gson().toJson(name));
                if (Build.VERSION.SDK_INT >= 26) {
                    startForegroundService(intent);
                } else {
                    mContext.startService(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCharts() {
        CommonUtil.startActivtiyForResult(this, FullscreenChartActivity.class, Constants.REQUEST_CODE1);
        overridePendingTransition(0, 0);
    }

    public void viewFullscreenChart(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, type);
        CommonUtil.startActivtiyForResult(this, FullscreenChartActivity2.class, Constants.REQUEST_CODE1, bundle);
        overridePendingTransition(0, 0);
    }


    public static View getView() {
        if (canOpenFullscreenMode && mStatisticFragment != null) {//zzh:增加不为null判断
            return mStatisticFragment.getWebView();
        } else {
            return null;
        }
    }


    public static void setView(View view) {
        if (mStatisticFragment != null){//zzh:增加不为null判断
            mStatisticFragment.setWebView(view);
            canOpenFullscreenMode = true;
        }
    }


    private void closeFullScreen() {
        WebView v = (WebView) mFlData.getChildAt(0);
        v.post(new Runnable() {
            @Override
            public void run() {
                v.evaluateJavascript("javascript:exitFullScreen()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
        });
        v.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        mFlData.removeAllViews();
        if(mStatisticFragment != null){
            mStatisticFragment.setWebView(v);
        }
        mFlData.setVisibility(View.GONE);

    }


    @JavascriptInterface
    public void fullscreenMode(String value) {
        // viewFullscreenChart(value);
    }


    @JavascriptInterface
    public void fullscreenMode() {
        showCharts();
    }


    @JavascriptInterface
    public void quitFullscreenMode() {
        EventBusUtils.sendEvent(new MessageBean(0, MsgConstant.QUIT_FULLSCREEN_MODE, null));
    }

    private void getCurrentWifiInfo() {
        WifiManager wifiManager = (WifiManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();
        if (wifiEnabled) {
            WifiInfo info = wifiManager.getConnectionInfo();
            IMState.setWifiSsid(info.getSSID());
            IMState.setWifiMac(info.getBSSID());
            Log.e("WifiInfo", JSONObject.toJSONString(info));
        } else {
            IMState.setWifiSsid("");
            IMState.setWifiMac("");

        }

    }


    @JavascriptInterface
    public void closeLoading() {
        if (mStatisticFragment != null){
            mStatisticFragment.closeLoading();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode && requestCode == Constants.REQUEST_CODE9) {
            File file = new File(JYFileHelper.getFileDir(mContext, Constants.PATH_DOWNLOAD), MsgConstant.NEW_VERSION_APK_ID + "12.apk");
            installProcess(file);
        }
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE8) {
            File file = new File(JYFileHelper.getFileDir(mContext, Constants.PATH_DOWNLOAD), MsgConstant.NEW_VERSION_APK_ID + "12.apk");
            installProcess(file);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mViewPager.getCurrentItem() == 2) {
            ((WorkbenchFragmentV2) fragments.get(2)).setAutoPlay(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新未读数
        teamMessageRl.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBusUtils.sendEvent(new MessageBean(0, MsgConstant.UPDATE_UNREAD_MSG_NUM, null));
            }
        }, 200);
        if (mViewPager == null || fragments.get(2) == null) {

        }
        if (mViewPager.getCurrentItem() == 2) {
            ((WorkbenchFragmentV2) fragments.get(2)).setAutoPlay(true);
        } else {
            ((WorkbenchFragmentV2) fragments.get(2)).setAutoPlay(false);
        }
        checkLoginState();
        if(currentItem == 0 && model != null){
            model.startAttendance(rootView);
        }
    }


    private void checkLoginState() {
        if (IMState.isImCanLogin() && !IMState.getImOnlineState()) {
            IM.getInstance().login();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
        if (!isChangeCompany){
            //zzh:导致在本Activity调logout退出登录时 直接退出程序而不是跳转到登录页
            //System.exit(0);
        }

    }
}

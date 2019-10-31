package com.hjhq.teamface.attendance.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceApproveDetailBean;
import com.hjhq.teamface.attendance.bean.SaveAttendanceApprovalBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AddApprovalDelegate;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.CustomLayoutResultBean;
import com.hjhq.teamface.basis.bean.LayoutBean;
import com.hjhq.teamface.basis.bean.ModuleBean;
import com.hjhq.teamface.basis.bean.ModuleItemBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.popupwindow.OnMenuSelectedListener;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.ui.time.DateTimeSelectPresenter;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/add_attendance_approval", desc = "添加审批单")
public class AddApprovalActivity extends ActivityPresenter<AddApprovalDelegate, AttendanceModel> implements View.OnClickListener {
    String[] stateMenu;
    String[] unitMenu;
    int stateIndex = -1;
    int unitIndex = -1;
    String[] timeMenu;
    String[] timeVale;
    String[] timeMenu2;
    String[] timeVale2;
    private int startTimeIndex;
    private int endTimeIndex;
    private int timeIndex;


    ModuleItemBean mModuleItemBean;
    AppModuleBean appModeluBean;
    AttendanceApproveDetailBean mDataBean;
    // String datetimeType = "yyyy年MM月dd日 HH:mm";
    String datetimeType = "yyyy-MM-dd HH:mm";
    Calendar mStartCalendar;
    Calendar mStopCalendar;
    boolean isStartTimeChoosed = false;
    boolean isStopTimeChoosed = false;
    String startTimeName;
    String endTimeName;
    String timeName;
    boolean isEdit = false;
    private String dataId = "";

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        dataId = getIntent().getStringExtra(Constants.DATA_TAG1);

    }

    private void getDetailData() {
        model.getAttendanceApprovalDetail(mContext, dataId, new ProgressSubscriber<AttendanceApproveDetailBean>(mContext) {
            @Override
            public void onNext(AttendanceApproveDetailBean baseBean) {
                super.onNext(baseBean);
                mDataBean = baseBean;
                showData(baseBean);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

    }

    private void showData(AttendanceApproveDetailBean baseBean) {
        AttendanceApproveDetailBean.DataBean data = baseBean.getData();
        startTimeName = data.getStart_time_field();
        endTimeName = data.getEnd_time_field();
        String time = data.getDuration_field();
        /*viewDelegate.setDurationText(time);*/
        String unit = data.getDuration_unit();
        unitIndex = TextUtil.parseInt(unit, 0);
        if (unitMenu.length>unitIndex && unitIndex>-1){//zzh->获取时长计算单位的值,没有默认按小时
            viewDelegate.setUnitText(unitMenu[unitIndex]);
        }else {
            viewDelegate.setUnitText(unitMenu[0]);
        }
        appModeluBean = new AppModuleBean();
        appModeluBean.setChinese_name(data.getChinese_name());
        appModeluBean.setEnglish_name(data.getRelevance_bean());
        getCustomLayout(appModeluBean);
        viewDelegate.setApproveTitle(data.getChinese_name());
        stateIndex = TextUtil.parseInt(data.getRelevance_status(), 0);
        controlViews();
        viewDelegate.setStateText(stateMenu[stateIndex]);
    }

    @Override
    public void init() {
        stateMenu = getResources().getStringArray(R.array.attendance_state_menu);
        unitMenu = getResources().getStringArray(R.array.attendance_unit_menu);//zzh->ad:去除按天算选项
        mStartCalendar = Calendar.getInstance();
        mStopCalendar = Calendar.getInstance();
        controlViews();
        if (!TextUtils.isEmpty(dataId) && TextUtil.parseLong(dataId, 0L) > 0) {
            getDetailData();
            isEdit = true;
            viewDelegate.setTitle("编辑审批单");
        }
    }

    private void controlViews() {
        if (stateIndex == 0) {
            viewDelegate.setVisibility(R.id.rl4, false);
            viewDelegate.setVisibility(R.id.rl5, false);
            viewDelegate.setVisibility(R.id.rl6, false);
            viewDelegate.setStartTimeTitle(this.getResources().getString(R.string.attendance_patch_time_label));
        } else if(stateIndex == 2){//zzh->ad:设置加班字段显示
            viewDelegate.setVisibility(R.id.rl4, true);
            viewDelegate.setVisibility(R.id.rl5, true);
            viewDelegate.setVisibility(R.id.rl6, true);
            viewDelegate.setStartTimeTitle(this.getResources().getString(R.string.attendance_start_time_label));
        }else {
            viewDelegate.setVisibility(R.id.rl4, true);
            viewDelegate.setVisibility(R.id.rl5, false);
            viewDelegate.setVisibility(R.id.rl6, false);
            viewDelegate.setStartTimeTitle(this.getResources().getString(R.string.attendance_start_time_label));
        }
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.rl1, R.id.rl2, R.id.rl3, R.id.rl4, R.id.rl5, R.id.rl6);
        viewDelegate.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveApproval();
        return super.onOptionsItemSelected(item);
    }

    private void chooseApproveType() {
        /*Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, Constants.SELECT_FOR_TASK);
        UIRouter.getInstance().openUri(mContext, "DDComp://project/appModule", bundle, Constants.REQUEST_CODE3);*/
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.DATA_TAG1, true);
        UIRouter.getInstance().openUri(mContext, "DDComp://app/choose_approve_module", bundle, Constants.REQUEST_CODE3);
    }


    private void saveApproval() {
        if (appModeluBean == null) {
            ToastUtils.showToast(mContext, "请选择要关联的审批");
            return;
        }
        if (stateIndex == -1) {
            ToastUtils.showToast(mContext, "请选择要修正的状态");
            return;
        }
        /*if (unitIndex == -1) {
            ToastUtils.showToast(mContext, "请选择时长单位");
            return;
        }*/
        if (!isStartTimeChoosed) {
            ToastUtils.showToast(mContext, "请选择开始时间");
            return;
        }
        if(stateIndex != 0 && !isStopTimeChoosed){
            ToastUtils.showToast(mContext, "请选择结束时间");
            return;
        }

        SaveAttendanceApprovalBean bean = new SaveAttendanceApprovalBean();
        bean.setBeanName(appModeluBean.getEnglish_name());
        bean.setStartTimeField(startTimeName);
        bean.setEndTimeField(endTimeName);
        bean.setRelevanceStatus(stateIndex + "");
        if (stateIndex == 0) {
            bean.setDurationField("");
            bean.setDurationUnit("");
        } else {
            bean.setDurationField(timeName);
            bean.setDurationUnit(unitIndex + "");
        }

        if (isEdit) {
            bean.setId(TextUtil.parseLong(dataId));
            model.updateAttendanceApproval(mContext, bean, new ProgressSubscriber<ModuleBean>(mContext, true) {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }

                @Override
                public void onNext(ModuleBean moduleBean) {
                    super.onNext(moduleBean);
                    setResult(RESULT_OK);
                    finish();
                }

            });
        } else {
            model.saveAttendanceApproval(mContext, bean, new ProgressSubscriber<ModuleBean>(mContext, true) {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }

                @Override
                public void onNext(ModuleBean moduleBean) {
                    super.onNext(moduleBean);
                    setResult(RESULT_OK);
                    finish();
                }

            });
        }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl1) {
            //关联审批单
            chooseApproveType();

        } else if (v.getId() == R.id.rl2) {
            //修正状态
            selectStateMenu();
        } else if (v.getId() == R.id.rl3) {
            //开始时间
            selectDate(1);
        } else if (v.getId() == R.id.rl4) {
            //结束时间
            selectDate(2);

        } else if (v.getId() == R.id.rl5) {
            //时长
            viewDelegate.etDuration.requestFocus();
            selectDate(3);

        } else if (v.getId() == R.id.rl6) {
            //时长单位
            selectUnitMenu();
        }

    }

    private void selectDate(int i) {
        if (appModeluBean == null) {
            ToastUtils.showToast(mContext, "请选择模块");
            return;
        }

        if (i == 3) {
            if (timeMenu2 == null || timeMenu2.length <= 0) {
                ToastUtils.showToast(mContext, "所选模块无相关字段");
                return;
            }
        } else {
            if (timeMenu == null || timeMenu.length <= 0) {
                ToastUtils.showToast(mContext, "所选模块无相关字段");
                return;
            }
        }

        PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "选择字段", i == 3 ? timeMenu2 : timeMenu, new OnMenuSelectedListener() {
            @Override
            public boolean onMenuSelected(int p) {
                switch (i) {
                    case 1:
                        viewDelegate.setStartTime(timeMenu[p]);
                        startTimeIndex = p;
                        isStartTimeChoosed = true;
                        startTimeName = timeVale[p];
                        break;
                    case 2:
                        viewDelegate.setStopTime(timeMenu[p]);
                        endTimeIndex = p;
                        isStopTimeChoosed = true;
                        endTimeName = timeVale[p];
                        break;
                    case 3:
                        viewDelegate.setTime(timeMenu2[p]);
                        timeIndex = p;
                        timeName = timeVale2[p];
                        break;
                    default:

                        break;
                }
                return false;
            }
        });

    }

    /**
     * 修正状态
     */
    private void selectStateMenu() {
        PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(),
                getResources().getString(R.string.attendance_please_select_hint), stateMenu,
                new OnMenuSelectedListener() {
                    @Override
                    public boolean onMenuSelected(int p) {
                        stateIndex = p;
                        viewDelegate.setStateText(stateMenu[stateIndex]);
                        controlViews();
                        return true;
                    }
                });

    }

    /**
     * 修正状态
     */
    private void selectUnitMenu() {
        PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(),
                getResources().getString(R.string.attendance_please_select_hint), unitMenu,
                new OnMenuSelectedListener() {
                    @Override
                    public boolean onMenuSelected(int p) {
                        unitIndex = p;
                        viewDelegate.setUnitText(unitMenu[unitIndex]);
                        return true;
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_CODE1:
                //开始时间
                mStartCalendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
                viewDelegate.setStartTime(DateTimeUtil.longToStr(mStartCalendar.getTimeInMillis(), datetimeType));
                isStartTimeChoosed = true;
                break;
            case Constants.REQUEST_CODE2:
                //结束时间
                mStopCalendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
                viewDelegate.setStopTime(DateTimeUtil.longToStr(mStartCalendar.getTimeInMillis(), datetimeType));
                isStopTimeChoosed = true;
                break;
            case Constants.REQUEST_CODE3:
                //选择模块
                appModeluBean = (AppModuleBean) data.getSerializableExtra(Constants.DATA_TAG1);
                if (appModeluBean != null) {
                    viewDelegate.setApproveTitle(appModeluBean.getChinese_name());
                    getCustomLayout(appModeluBean);
                }
                break;
            default:

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 得到自定义布局
     */

    private void getCustomLayout(AppModuleBean appModeluBean) {
        Map<String, Object> map = new HashMap<>();
        map.put("bean", appModeluBean.getEnglish_name());
        map.put("operationType", CustomConstants.APPROVE_STATE);
        model.getCustomLayout(this, map, new ProgressSubscriber<CustomLayoutResultBean>(this) {
            @Override
            public void onNext(CustomLayoutResultBean customLayoutResultBean) {
                super.onNext(customLayoutResultBean);
                final List<LayoutBean> layout = customLayoutResultBean.getData().getLayout();
                if (layout == null || layout.size() <= 0) {
                    ToastUtils.showToast(mContext, "无相关字段");
                    return;
                }
                List<CustomBean> timeList = new ArrayList<CustomBean>();
                List<CustomBean> durationList = new ArrayList<CustomBean>();
                for (LayoutBean layoutBean : layout) {
                    boolean isTerminalApp = "1".equals(layoutBean.getTerminalApp());
                    boolean isHideInCreate = "1".equals(layoutBean.getIsHideInCreate());
                    boolean isSpread = "0".equals(layoutBean.getIsSpread());
                    boolean isHideColumnName = "1".equals(layoutBean.getIsHideColumnName());
                    if (!isTerminalApp || isHideInCreate) {
                        //不是app端或新建时隐藏
                        continue;
                    }

                    List<CustomBean> list = layoutBean.getRows();

                    for (int i = 0; i < list.size(); i++) {
                        if (CustomConstants.DATETIME.equals(list.get(i).getType())) {
                            final CustomBean customBean = list.get(i);
                            timeList.add(customBean);
                        }
                        if (CustomConstants.NUMBER.equals(list.get(i).getType())) {
                            final CustomBean customBean = list.get(i);
                            durationList.add(customBean);
                        }
                    }

                }
                timeMenu = new String[timeList.size()];
                timeVale = new String[timeList.size()];

                timeMenu2 = new String[durationList.size()];
                timeVale2 = new String[durationList.size()];
                for (int i = 0; i < durationList.size(); i++) {
                    timeMenu2[i] = durationList.get(i).getLabel();
                    timeVale2[i] = durationList.get(i).getName();
                }

                for (int i = 0; i < timeList.size(); i++) {//zzh->add:新增赋值
                    timeMenu[i] = timeList.get(i).getLabel();
                    timeVale[i] = timeList.get(i).getName();
                }
                if (mDataBean != null && mDataBean.getData() != null && timeVale2 != null && timeVale2.length > 0) {
                    String value = mDataBean.getData().getDuration_field();
                    for (int i = 0; i < timeVale2.length; i++) {
                        if (!TextUtils.isEmpty(value) && value.equals(timeVale2[i])) {
                            timeIndex = i;
                            timeName = timeVale2[i];
                            viewDelegate.setDurationText(timeMenu2[timeIndex]);
                            break;
                        }
                    }

                }
                if (!TextUtils.isEmpty(startTimeName) || !TextUtils.isEmpty(endTimeName)) {
                    initDataName();
                }
            }
        });
    }

    private void initDataName() {
        if (timeVale == null || timeVale.length <= 0) {
            return;
        }
        for (int i = 0; i < timeVale.length; i++) {
            if (!TextUtils.isEmpty(startTimeName)) {
                if (startTimeName.equals(timeVale[i])) {
                    startTimeIndex = i;
                    viewDelegate.setStartTime(timeMenu[i]);
                    isStartTimeChoosed = true;
                }
            }
            if (!TextUtils.isEmpty(endTimeName)) {
                if (endTimeName.equals(timeVale[i])) {
                    endTimeIndex = i;
                    viewDelegate.setStopTime(timeMenu[i]);
                    isStopTimeChoosed = true;
                }
            }

        }

    }

    @Override
    public void onBackPressed() {
        DialogUtils.getInstance().sureOrCancel(mContext, "", "返回内容会被清空！", viewDelegate.getRootView(), new DialogUtils.OnClickSureListener() {
            @Override
            public void clickSure() {
                finish();
            }
        });
    }
}

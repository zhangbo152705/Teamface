package com.hjhq.teamface.attendance.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.AttendanceRulesAdapter;
import com.hjhq.teamface.attendance.adapter.AttendanceSettingListAdapter;
import com.hjhq.teamface.attendance.bean.AddDateBean;
import com.hjhq.teamface.attendance.bean.AttendanceRulesListBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.SettingFragmentDelegate;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class SettingFragment extends FragmentPresenter<SettingFragmentDelegate, AttendanceModel> {

    List<String> strList = new ArrayList<>();
    private List<Map<String, Object>> data_list;
    private SimpleAdapter adapter;

    AttendanceRulesAdapter mAttendanceRulesAdapter;
    List<AddDateBean> dataList = new ArrayList<>();
    static SettingFragment newInstance(String tag) {
        SettingFragment myFragment = new SettingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, tag);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
    }

    @Override
    protected void init() {
        initSettingView();
        initRulesView();
        getNetData(true);
    }

    public void initSettingView(){
        strList.add(getActivity().getResources().getString(R.string.attendance_type_title));
        strList.add(getActivity().getResources().getString(R.string.attendance_class_manager_title));
        strList.add(getActivity().getResources().getString(R.string.attendance_work_time_detail_title2));
        strList.add(getActivity().getResources().getString(R.string.attendance_additional_setting_title));//"其他设置"
        strList.add(getActivity().getResources().getString(R.string.attendance_approval_title));
        strList.add(getActivity().getResources().getString(R.string.attendance_uinit_title));//
        int icno[] = { R.drawable.attendance_mode, R.drawable.attendance_class_manager, R.drawable.attendance_class_detail,
                R.drawable.attendance_other_setting, R.drawable.attendance_approve, R.drawable.attendance_unit_manager};
        data_list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i <icno.length; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("img", icno[i]);
            map.put("text",strList.get(i));
            data_list.add(map);
        }

        String[] from={"img","text"};
        int[] to={R.id.image,R.id.text};
        adapter=new SimpleAdapter(getActivity(), data_list, R.layout.attendance_setting_frangment_item, from, to);
        viewDelegate.mGridView.setAdapter(adapter);
        viewDelegate.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                switch (arg2) {
                    case 0:
                       /* //打卡规则
                        CommonUtil.startActivtiy(getActivity(), AttendanceRulesActivity.class);*/
                        //考勤方式
                        CommonUtil.startActivtiy(getActivity(), AttendanceTypeActivity.class);
                        break;
                    case 1:
                        //班次管理
                        CommonUtil.startActivtiy(getActivity(), WorkTimeManageActivity.class);
                        break;
                    case 2:
                        //班次详情
                        CommonUtil.startActivtiy(getActivity(), ScheduleDetailActivity.class);
                        break;
                    case 3:
                        //其他设置
                        CommonUtil.startActivtiy(getActivity(), AdditionalSettingsActivity.class);
                        break;
                    case 4:
                        //关联审批单
                        CommonUtil.startActivtiy(getActivity(), AttendanceApprovalActivity.class);
                        break;
                    case 5:
                        //插件管理
                        CommonUtil.startActivtiy(getActivity(), PlugManageActivity.class);
                        break;
                }
            }
        });
    }

    public void initRulesView(){
        viewDelegate.setTitle(R.string.attendance_rules_manage_title);
        viewDelegate.setRightMenuTexts(R.color.app_blue, getString(R.string.add));
        mAttendanceRulesAdapter = new AttendanceRulesAdapter(dataList);
        viewDelegate.setAdapter(mAttendanceRulesAdapter);
        viewDelegate.setItemListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                super.onItemClick(adapter, view, position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                if (view.getId() == R.id.edit_rules) {
                    bundle.putString(Constants.DATA_TAG1, dataList.get(position).getId());
                    bundle.putInt(Constants.DATA_TAG2, AttendanceConstants.TYPE_EDIT);
                    CommonUtil.startActivtiyForResult(getActivity(), AddRulesActivity.class, Constants.REQUEST_CODE2, bundle);
                } else if (view.getId() == R.id.edit_members) {
                    bundle.putString(Constants.DATA_TAG1, dataList.get(position).getId());
                    bundle.putInt(Constants.DATA_TAG2, AttendanceConstants.TYPE_EDIT);
                    CommonUtil.startActivtiyForResult(getActivity(), AddGroupActivity.class, Constants.REQUEST_CODE2, bundle);
                } else if (view.getId() == R.id.delete) {
                    deleteRules(position);
                }
                super.onItemChildClick(adapter, view, position);
            }
        });

    }

    /**
     * 获取打卡规则列表
     *
     * @param flag
     */
    public void getNetData(boolean flag) {
        model.getAttendanceRulesList(((ActivityPresenter) getActivity()), 1, 100000, new ProgressSubscriber<AttendanceRulesListBean>(getActivity(), flag) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(AttendanceRulesListBean baseBean) {
                super.onNext(baseBean);
                dataList.clear();
                if (baseBean.getData() != null && baseBean.getData().getDataList() != null) {
                    dataList.addAll(baseBean.getData().getDataList());

                }
                mAttendanceRulesAdapter.notifyDataSetChanged();

            }
        });

    }

    /**
     * 删除打卡规则
     *
     * @param position
     */
    private void deleteRules(int position) {

        DialogUtils.getInstance().sureOrCancel(getActivity(), "提示",
                "删除后，该考勤组成员的考勤将不会被系统智能标记迟到、早退等行为统计。是否继续？",
                viewDelegate.getRootView(), getString(R.string.confirm), getString(R.string.cancel),
                new DialogUtils.OnClickSureOrCancelListener() {
                    @Override
                    public void clickSure() {
                        model.delAttendanceRules(((ActivityPresenter) getActivity()), dataList.get(position).getId(),
                                new ProgressSubscriber<BaseBean>(getActivity()) {
                                    @Override
                                    public void onError(Throwable e) {
                                        super.onError(e);
                                    }

                                    @Override
                                    public void onNext(BaseBean baseBean) {
                                        super.onNext(baseBean);
                                        dataList.remove(position);
                                        mAttendanceRulesAdapter.notifyDataSetChanged();
                                    }
                                });
                    }

                    @Override
                    public void clickCancel() {


                    }
                });

    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.iv_add_rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.DATA_TAG2, AttendanceConstants.TYPE_ADD);
                CommonUtil.startActivtiyForResult(getActivity(), AddGroupActivity.class, Constants.REQUEST_CODE1, bundle);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_CODE1:
                getNetData(false);
                break;
            case Constants.REQUEST_CODE2:
                getNetData(false);
                break;
            default:

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

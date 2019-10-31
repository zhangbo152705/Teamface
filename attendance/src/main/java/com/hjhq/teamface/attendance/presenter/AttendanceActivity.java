package com.hjhq.teamface.attendance.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AttendanceDelegate;
import com.hjhq.teamface.basis.bean.LocalModuleBean;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.FragmentAdapter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.NoScrollViewPager;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/attendance_main", desc = "考勤主界面")
public class AttendanceActivity extends ActivityPresenter<AttendanceDelegate, AttendanceModel>
        implements ViewPager.OnPageChangeListener, View.OnClickListener {
    List<Fragment> fragments = new ArrayList<>(4);
    FragmentAdapter mAdapter;
    NoScrollViewPager mViewPager;
    private ImageView[] actionBarIvs = new ImageView[4];
    private TextView[] actionBarTvs = new TextView[4];
    private int[] normalActionBarDrawable = {R.drawable.attendance_daka_unselect,R.drawable.attendance_approve_nomal,
            R.drawable.attendance_data_unselect, R.drawable.attendance_setting_unselect};
    private int[] clickActionBarDrawable = {R.drawable.attendance_daka_selected,R.drawable.attendance_approve_click,
            R.drawable.attendance_data_selected, R.drawable.attendance_setting_selected};
    private int[] titleRes = {R.string.attendance_daka,R.string.attendance_approve, R.string.attendance_data, R.string.attendance_setting};
    private boolean trigger = false;
    RelativeLayout rlAction1;
    RelativeLayout rlAction2;
    RelativeLayout rlAction3;
    RelativeLayout rlAction4;
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment4;


    @Override
    public void init() {
        initView();
    }

    private void initView() {
        rlAction1 = viewDelegate.get(R.id.rl1);
        rlAction2 = viewDelegate.get(R.id.rl2);
        rlAction3 = viewDelegate.get(R.id.rl3);
        rlAction4= viewDelegate.get(R.id.rl4);
        rlAction1.setOnClickListener(this);
        rlAction2.setOnClickListener(this);
        rlAction3.setOnClickListener(this);
        rlAction4.setOnClickListener(this);
        TextView tv1 = viewDelegate.get(R.id.tv1);
        TextView tv2 = viewDelegate.get(R.id.tv2);
        TextView tv3 = viewDelegate.get(R.id.tv3);
        TextView tv4 = viewDelegate.get(R.id.tv4);
        ImageView iv1 = viewDelegate.get(R.id.iv1);
        ImageView iv2 = viewDelegate.get(R.id.iv2);
        ImageView iv3 = viewDelegate.get(R.id.iv3);
        ImageView iv4 = viewDelegate.get(R.id.iv4);
        actionBarIvs[0] = iv1;
        actionBarIvs[1] = iv2;
        actionBarIvs[2] = iv3;
        actionBarIvs[3] = iv4;
        actionBarTvs[0] = tv1;
        actionBarTvs[1] = tv2;
        actionBarTvs[2] = tv3;
        actionBarTvs[3] = tv4;

        mViewPager = viewDelegate.get(R.id.main_vp);
        fragment1 = new AttendanceFragment();
        fragments.add(fragment1);
        fragment2 = new AttendanceApproveFragment();
        fragments.add(fragment2);

        /*viewDelegate.getRootView().postDelayed(new Runnable() {
            @Override
            public void run() {
                new DataFragment();
                fragment2 = new DataFragment();
                fragment3 = new SettingFragment();
                fragments.add(fragment2);
                fragments.add(fragment3);
                mAdapter.notifyDataSetChanged();
            }
        }, 500);*/
        mAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);
        //禁止ViewPager滑动，以免造成逻辑混乱
        mViewPager.setNoScroll(true);
        initAuth();
    }

    private void initAuth() {
        if ("2".equals(SPHelper.getRole()) || "3".equals(SPHelper.getRole())) {
            rlAction4.setVisibility(View.VISIBLE);
        } else {
           /* model.getSettingDetail(mContext, new ProgressSubscriber<AdditionalSettingDetailBean>(mContext, false) {
                @Override
                public void onNext(AdditionalSettingDetailBean additionalSettingDetailBean) {
                    super.onNext(additionalSettingDetailBean);
                    final List<Member> admin_arr = additionalSettingDetailBean.getData().getAdmin_arr();
                    for (int i = 0; i < admin_arr.size(); i++) {
                        if (SPHelper.getEmployeeId().equals(admin_arr.get(i).getId() + "")) {
                            rlAction3.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });*/
            model.findRoles(mContext, new ProgressSubscriber<LocalModuleBean>(mContext, false) {
                @Override
                public void onNext(LocalModuleBean bean) {
                    super.onNext(bean);
                    if (bean != null && bean.getData() != null && bean.getData().size() > 0) {
                        final List<LocalModuleBean.DataBean> data = bean.getData();
                        for (int i = 0; i < data.size(); i++) {
                            if ("attendance".equals(data.get(i).getBean())) {//zzh->ad:如果有考勤模块则有设置权限
                                rlAction4.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });
        }
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        rlAction3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (trigger) {
                    rlAction4.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        rlAction1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                trigger = true;
                return true;
            }
        });
        rlAction4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CommonUtil.startActivtiy(mContext, AttendanceActivity2.class);
                return true;
            }
        });
    }

    private void changeActionBar(int position) {
        resetActionBar();
        viewDelegate.setTitle(titleRes[position]);
        mViewPager.setCurrentItem(position);
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

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl1) {
            changeActionBar(0);
            viewDelegate.showMenu();
        }else if (v.getId() == R.id.rl2) {
            changeActionBar(1);
            viewDelegate.showMenu();
        } else if (v.getId() == R.id.rl3) {
            if (fragment3 == null) {
                fragment3 = new DataFragment();
                fragments.add(2, fragment3);
                mAdapter.notifyDataSetChanged();
            }
            if (fragment4 == null) {
                fragment4 = new SettingFragment();
                fragments.add(3, fragment4);
                mAdapter.notifyDataSetChanged();
            }
            changeActionBar(2);
            viewDelegate.showMenu(0);
        } else if (v.getId() == R.id.rl4) {
            if (fragment3 == null) {
                fragment3 = new DataFragment();
                fragments.add(2, fragment3);
                mAdapter.notifyDataSetChanged();
            }
            if (fragment4 == null) {
                fragment4 = new SettingFragment();
                fragments.add(3, fragment4);
                mAdapter.notifyDataSetChanged();
            }
            changeActionBar(3);
            viewDelegate.showMenu();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CommonUtil.startActivtiy(mContext, AttendanceRangeActivity.class);
        return super.onOptionsItemSelected(item);
    }
}

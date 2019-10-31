package com.hjhq.teamface.customcomponent.util;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.KnowledgeClassBean;
import com.hjhq.teamface.basis.util.device.DeviceUtils;
import com.hjhq.teamface.basis.util.device.ScreenUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.adapter.KnowledgeItemAdapter;
import com.hjhq.teamface.customcomponent.adapter.WidgetItemAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Administrator on 2018/3/22.
 */

public class WidgetDialogUtil {

    /**
     * 多选接口
     */
    public interface OnMutilSelectListner {
        void mutilSelectSure(List<EntryBean> entryBeanList);
    }

    public interface OnMutilSelectListner2 {
        void mutilSelectSure(List<KnowledgeClassBean> entryBeanList);
    }

    /**
     * 多选 Dialog
     *
     * @param activity
     * @param multi    多选
     * @param root
     * @param listener
     */
    public static void mutilDialog(final Activity activity, boolean multi, List<EntryBean> datas,
                                   View root, OnMutilSelectListner listener) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        View mResendMailPopupView = LayoutInflater.from(activity).inflate(
                R.layout.custom_dialog_widget_with_search, null);
        ImageView ivClean = mResendMailPopupView.findViewById(R.id.iv_clean);

        RecyclerView mRecyclerView = mResendMailPopupView.findViewById(R.id.recycler_view);
        EditText etSearch = mResendMailPopupView.findViewById(R.id.et_search);
        etSearch.clearFocus();
        List<EntryBean> resultDatas = new ArrayList<>();
        List<EntryBean> checkedDatas = new ArrayList<>();
        resultDatas.addAll(datas);
        for (EntryBean bean : datas) {
            if (bean.isCheck()) {
                checkedDatas.add(bean);
            }
        }
        int maxHeight = (int) (ScreenUtils.getScreenHeight(activity) / 3 * 2);
        int height = (int) DeviceUtils.dpToPixel(activity, 40 * datas.size());
        height = height > maxHeight ? maxHeight : WindowManager.LayoutParams.WRAP_CONTENT;
        ViewGroup.LayoutParams lp = mRecyclerView.getLayoutParams();
        lp.height = height;
        mRecyclerView.setLayoutParams(lp);
        //动态设置最大高度
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        final WidgetItemAdapter adapter = new WidgetItemAdapter(resultDatas);
        mRecyclerView.setAdapter(adapter);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    ivClean.setVisibility(View.VISIBLE);
                } else {
                    ivClean.setVisibility(View.GONE);
                }
                getResultData(s.toString(), datas);
                adapter.notifyDataSetChanged();
            }

            private List<EntryBean> getResultData(String s, List<EntryBean> datas) {
                resultDatas.clear();
                if (TextUtils.isEmpty(s)) {
                    return datas;
                }
                for (EntryBean e : datas) {
                    if (!TextUtils.isEmpty(e.getLabel()) && e.getLabel().contains(s.toString())) {
                        resultDatas.add(e);
                    }
                }
                return resultDatas;
            }
        });
        ivClean.setOnClickListener(v -> {
            etSearch.setText("");
            ivClean.setVisibility(View.GONE);
        });

        PopupWindow mResendMailPopup = initDisPlay(activity, dm, mResendMailPopupView);

        mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                EntryBean entryBean = resultDatas.get(position);
                if (multi) {
                    entryBean.setCheck(!entryBean.isCheck());
                    adapter.notifyItemChanged(position);
                } else {
                    Observable.from(resultDatas).filter(data -> {
                        data.setCheck(false);
                        return data == entryBean;
                    }).subscribe(data -> data.setCheck(true));
                    listener.mutilSelectSure(resultDatas);

                    close(activity, mResendMailPopup, new ArrayList<>(), new ArrayList<>());
                }
                super.onItemClick(adapter, view, position);
            }
        });
        //关闭按钮
        mResendMailPopupView.findViewById(R.id.iv_del).setOnClickListener(view -> {
            close(activity, mResendMailPopup, checkedDatas, datas);
            //listener.mutilSelectSure(datas);
        });
        if (!multi) {
            mResendMailPopupView.findViewById(R.id.ll_option).setVisibility(View.GONE);
            mResendMailPopupView.findViewById(R.id.dialog_line).setVisibility(View.GONE);
        }
        //点击了确定
        mResendMailPopupView.findViewById(R.id.dialog_btnSure).setOnClickListener(view -> {
            if (mResendMailPopup != null && mResendMailPopup.isShowing()) {
                mResendMailPopup.dismiss();
                ScreenUtils.letScreenLight(activity);
                listener.mutilSelectSure(datas);
            }
        });
        // 点击了取消
        mResendMailPopupView.findViewById(R.id.dialog_btnRefuse).setOnClickListener(view -> {
            close(activity, mResendMailPopup, checkedDatas, datas);
            // listener.mutilSelectSure(datas);
        });
        mResendMailPopup.setOnDismissListener(() -> ScreenUtils.letScreenLight(activity));
        mResendMailPopup.showAtLocation(root, Gravity.CENTER, 0, 0);
    }

    public static void mutilDialog2(final Activity activity, boolean multi, List<KnowledgeClassBean> datas,
                                    View root, OnMutilSelectListner2 listener) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        View mResendMailPopupView = LayoutInflater.from(activity).inflate(
                R.layout.custom_dialog_widget_with_search, null);
        ImageView ivClean = mResendMailPopupView.findViewById(R.id.iv_clean);
        RecyclerView mRecyclerView = mResendMailPopupView.findViewById(R.id.recycler_view);
        EditText etSearch = mResendMailPopupView.findViewById(R.id.et_search);
        etSearch.clearFocus();
        List<KnowledgeClassBean> resultDatas = new ArrayList<>();
        List<KnowledgeClassBean> checkedDatas = new ArrayList<>();
        resultDatas.addAll(datas);
        for (KnowledgeClassBean bean : datas) {
            if (bean.isCheck()) {
                checkedDatas.add(bean);
            }
        }
        int maxHeight = (int) (ScreenUtils.getScreenHeight(activity) / 3 * 2);
        int height = (int) DeviceUtils.dpToPixel(activity, 40 * datas.size());
        height = height > maxHeight ? maxHeight : WindowManager.LayoutParams.WRAP_CONTENT;
        ViewGroup.LayoutParams lp = mRecyclerView.getLayoutParams();
        lp.height = height;
        mRecyclerView.setLayoutParams(lp);
        //动态设置最大高度
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        final KnowledgeItemAdapter adapter = new KnowledgeItemAdapter(resultDatas);
        mRecyclerView.setAdapter(adapter);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    ivClean.setVisibility(View.VISIBLE);
                } else {
                    ivClean.setVisibility(View.GONE);
                }
                getResultData(s.toString(), datas);
                adapter.notifyDataSetChanged();
            }

            private List<KnowledgeClassBean> getResultData(String s, List<KnowledgeClassBean> datas) {
                resultDatas.clear();
                if (TextUtils.isEmpty(s)) {
                    return datas;
                }
                for (KnowledgeClassBean e : datas) {
                    String name = e.getName();
                    if (!TextUtils.isEmpty(name) && name.contains(s.toString())) {
                        resultDatas.add(e);
                    }
                }
                return resultDatas;
            }
        });
        ivClean.setOnClickListener(v -> {
            etSearch.setText("");
            ivClean.setVisibility(View.GONE);
        });

        PopupWindow mResendMailPopup = initDisPlay(activity, dm, mResendMailPopupView);
        mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                KnowledgeClassBean entryBean = resultDatas.get(position);
                if (multi) {
                    entryBean.setCheck(!entryBean.isCheck());
                    adapter.notifyItemChanged(position);
                } else {
                    Observable.from(resultDatas).filter(data -> {
                        data.setCheck(false);
                        return data == entryBean;
                    }).subscribe(data -> data.setCheck(true));
                    listener.mutilSelectSure(resultDatas);

                    close(activity, mResendMailPopup, new ArrayList<>(), new ArrayList<>());
                }
                super.onItemClick(adapter, view, position);
            }
        });
        //关闭按钮
        mResendMailPopupView.findViewById(R.id.iv_del).setOnClickListener(view -> {
            close2(activity, mResendMailPopup, checkedDatas, datas);
            //listener.mutilSelectSure(datas);
        });
        if (!multi) {
            mResendMailPopupView.findViewById(R.id.ll_option).setVisibility(View.GONE);
            mResendMailPopupView.findViewById(R.id.dialog_line).setVisibility(View.GONE);
        }
        //点击了确定
        mResendMailPopupView.findViewById(R.id.dialog_btnSure).setOnClickListener(view -> {
            if (mResendMailPopup != null && mResendMailPopup.isShowing()) {
                mResendMailPopup.dismiss();
                ScreenUtils.letScreenLight(activity);
                listener.mutilSelectSure(datas);
            }
        });
        // 点击了取消
        mResendMailPopupView.findViewById(R.id.dialog_btnRefuse).setOnClickListener(view -> {
            close2(activity, mResendMailPopup, checkedDatas, datas);
            // listener.mutilSelectSure(datas);
        });
        mResendMailPopup.setOnDismissListener(() -> ScreenUtils.letScreenLight(activity));
        mResendMailPopup.showAtLocation(root, Gravity.CENTER, 0, 0);
    }

    private static void close(Activity activity, PopupWindow mResendMailPopup, List<EntryBean> checked, List<EntryBean> raw) {
        if (mResendMailPopup != null && mResendMailPopup.isShowing()) {
            mResendMailPopup.dismiss();
            ScreenUtils.letScreenLight(activity);
        }
        for (EntryBean bean : raw) {
            bean.setCheck(false);
        }
        for (EntryBean outer : checked) {
            for (EntryBean inner : raw) {
                if (!TextUtils.isEmpty(inner.getValue()) && inner.getValue().equals(outer.getValue())) {
                    inner.setCheck(true);
                }
            }
        }


    }

    private static void close2(Activity activity, PopupWindow mResendMailPopup, List<KnowledgeClassBean> checked, List<KnowledgeClassBean> raw) {
        if (mResendMailPopup != null && mResendMailPopup.isShowing()) {
            mResendMailPopup.dismiss();
            ScreenUtils.letScreenLight(activity);
        }
        for (KnowledgeClassBean bean : raw) {
            bean.setCheck(false);
        }
        for (KnowledgeClassBean outer : checked) {
            for (KnowledgeClassBean inner : raw) {
                if (!TextUtils.isEmpty(inner.getId()) && inner.getId().equals(outer.getId())) {
                    inner.setCheck(true);
                }
            }
        }
    }

    private static PopupWindow initDisPlay(Activity activity, DisplayMetrics dm, View mResendMailPopupView) {
        return initDisPlay(activity, dm, mResendMailPopupView, true);
    }

    private static PopupWindow initDisPlay(Activity activity, DisplayMetrics dm, View mResendMailPopupView, boolean bl) {
        PopupWindow mResendMailPopup = new PopupWindow(mResendMailPopupView,
                dm.widthPixels, dm.heightPixels,
                true);
        //解决华为闪屏
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //没有设置宽高显示不全的问题
        mResendMailPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mResendMailPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mResendMailPopup.setTouchable(true);
        mResendMailPopup.setOutsideTouchable(false);
        mResendMailPopup.setBackgroundDrawable(new ColorDrawable());
        ScreenUtils.letScreenGray(activity);
        if (bl) {
            mResendMailPopup.setAnimationStyle(R.style.AnimTools);
        }
        return mResendMailPopup;
    }
}

package com.hjhq.teamface.customcomponent.widget2.subfield;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.CustomUtil;

import java.util.ArrayList;
import java.util.List;


public class SubfieldView {
    private String moduleBean;
    private List<CustomBean> customBeanList;
    private boolean isSpread;
    private View mView;
    private TextView tvTitle;
    private LinearLayout llRoot;

    private Context context;
    private int state;
    private int stateEnv;
    private String title;
    //是否隐藏分栏名称
    private boolean isHideColumnName;
    protected boolean appTerminalHide;
    protected boolean hideColumn;
    private ImageView ivSpread;
    private ArrayList<BaseView> mViewList;
    private View llSubfield;
    private boolean fromApprove = false;

    public SubfieldView(List<CustomBean> list, int state, String title, boolean isSpread, String moduleBean) {
        this.customBeanList = list;
        this.state = state;
        this.title = title;
        this.isSpread = isSpread;
        this.moduleBean = moduleBean;
    }

    public SubfieldView(List<CustomBean> list, int state, String title, boolean isSpread, String moduleBean, boolean fromApprove) {
        this.customBeanList = list;
        this.state = state;
        this.title = title;
        this.isSpread = isSpread;
        this.moduleBean = moduleBean;
        this.fromApprove = fromApprove;
    }

    public void addView(LinearLayout parent) {
        context = parent.getContext();
        mView = View.inflate(context, R.layout.custom_item_text_subfield, null);

        llRoot = mView.findViewById(R.id.ll_layout);
        tvTitle = mView.findViewById(R.id.tv_title);
        llSubfield = mView.findViewById(R.id.ll_subfield);
        ivSpread = mView.findViewById(R.id.iv_spread);
        parent.addView(mView);

        initView();
        if (hideColumn || appTerminalHide) {
            mView.setVisibility(View.GONE);
        }
    }

    public int getStateEnv() {
        return stateEnv;
    }

    public void setStateEnv(int stateEnv) {
        this.stateEnv = stateEnv;
    }


    private void initView() {
        //同时隐藏分栏名称和收起分栏，优先级隐藏分栏名称
        if (isHideColumnName && !isSpread) {
            llSubfield.setVisibility(View.GONE);
        } else {
            TextUtil.setText(tvTitle, title);
            ivSpread.setSelected(isSpread);

            ivSpread.setOnClickListener(v -> {
                isSpread = !isSpread;
                if (isSpread) {
                    spread();
                } else {
                    shrink();
                }
            });
            llRoot.setVisibility(isSpread ? View.VISIBLE : View.GONE);
        }
        addWidget();
    }


    public void addWidget() {
        if (customBeanList == null) {
            return;
        }

        mViewList = new ArrayList<>();
        for (CustomBean customBean : customBeanList) {
            customBean.setModuleBean(moduleBean);
            int stateStr = state;
            if (fromApprove) {
                if (stateEnv == CustomConstants.APPROVE_DETAIL_STATE) {
                    if (CustomConstants.FORMULA.equals(customBean.getType())
                            || CustomConstants.FUNCTION_FORMULA.equals(customBean.getType())
                            || CustomConstants.REFERENCE_FORMULA.equals(customBean.getType())
                            || CustomConstants.SENIOR_FORMULA.equals(customBean.getType())) {
                        stateStr = CustomConstants.DETAIL_STATE;
                    } else {
                        stateStr = CustomConstants.EDIT_STATE;
                    }
                } else {
                    stateStr = CustomConstants.DETAIL_STATE;
                }
            }
            customBean.setState(stateStr);
            customBean.setStateEnv(stateEnv);
            //子表单
            if (customBean.getComponentList() != null && customBean.getComponentList().size() > 0) {
                final List<CustomBean> componentList = customBean.getComponentList();
                for (CustomBean bean : componentList) {
                    int stateStr2 = state;
                    if (fromApprove) {
                        if (stateEnv == CustomConstants.APPROVE_DETAIL_STATE) {
                            if (CustomConstants.FORMULA.equals(bean.getType())
                                    || CustomConstants.FUNCTION_FORMULA.equals(bean.getType())
                                    || CustomConstants.REFERENCE_FORMULA.equals(bean.getType())
                                    || CustomConstants.SENIOR_FORMULA.equals(bean.getType())) {
                                stateStr2 = CustomConstants.DETAIL_STATE;
                            } else {
                                stateStr2 = CustomConstants.EDIT_STATE;
                            }
                        } else {
                            stateStr2 = CustomConstants.DETAIL_STATE;
                        }
                    }
                    bean.setState(stateStr2);
                    bean.setStateEnv(stateEnv);
                }
            }
            // BaseView baseView = CustomUtil.drawLayout(llRoot, customBean, stateStr);
            BaseView baseView = CustomUtil.drawLayout(stateEnv, llRoot, customBean);
            if (baseView != null) {
                baseView.setParentName(title);
                mViewList.add(baseView);
            }
        }
    }


    private void spread() {
        ivSpread.setSelected(true);
        llRoot.setVisibility(View.VISIBLE);
    }


    private void shrink() {
        ivSpread.setSelected(false);
        llRoot.setVisibility(View.GONE);
    }


    public ArrayList<BaseView> getViewList() {
        return mViewList;
    }

    public void setHideColumnName(boolean hideColumnName) {
        isHideColumnName = hideColumnName;
    }

    public void setAppTerminalHide(boolean appTerminalHide) {
        this.appTerminalHide = appTerminalHide;
    }

    public void setHideColumn(boolean hideColumn) {
        this.hideColumn = hideColumn;
    }
}

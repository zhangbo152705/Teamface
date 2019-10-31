package com.hjhq.teamface.custom.ui.add;

import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.CustomLayoutResultBean;
import com.hjhq.teamface.basis.bean.LayoutBean;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.customcomponent.widget2.subfield.SubfieldView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddCustomDelegate extends AppDelegate {
    List<SubfieldView> mViewList = new ArrayList<>();
    LinearLayout llContent;
    ScrollView svRoot;
    int top = 0;
    int height = 0;
    private int isCopyState = 0;

    @Override
    public int getRootLayoutId() {
        return R.layout.custom_layout_linear;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setLeftIcon(-1);
        setLeftText(R.color.gray_69, "取消");
        setRightMenuIcons(R.drawable.icon_web_link);
        setRightMenuTexts(R.color.main_green, "保存");
        llContent = get(R.id.ll_custom_layout);
        svRoot = get(R.id.sv_root);
        svRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                top = svRoot.getTop();
                height = svRoot.getHeight();
                svRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    public void drawLayout(CustomLayoutResultBean.DataBean layoutBeanList, HashMap detailMap, int state, String moduleBean) {
        if (layoutBeanList == null) {
            return;
        }
        String title = layoutBeanList.getTitle();
        setTitle(title);
        List<LayoutBean> layout = layoutBeanList.getLayout();
        if (layout == null) {
            return;
        }
        mViewList.clear();
        for (LayoutBean layoutBean : layout) {

            boolean isTerminalApp = "1".equals(layoutBean.getTerminalApp());
            boolean isHideInCreate = "1".equals(layoutBean.getIsHideInCreate());
            boolean isSpread = "0".equals(layoutBean.getIsSpread());
            boolean isHideColumnName = "1".equals(layoutBean.getIsHideColumnName());

            List<CustomBean> list = layoutBean.getRows();
            if (detailMap != null) {
                for (CustomBean customBean : list) {
                    customBean.setIsCopyState(isCopyState);
                    if (!isTerminalApp) {
                        customBean.getField().setTerminalApp(layoutBean.getTerminalApp());
                        customBean.getField().setFieldControl("0");
                    }
                    if (isHideInCreate) {
                        customBean.getField().setAddView("0");
                        customBean.getField().setEditView("0");
                        customBean.getField().setFieldControl("0");
                    }
                    Object o = detailMap.get(customBean.getName());
                    customBean.setValue(o);
                    if (customBean.getRelevanceField() != null && !TextUtils.isEmpty(customBean.getRelevanceField().getFieldName())) {
                        Object o2 = detailMap.get(customBean.getRelevanceField().getFieldName());
                        if (getActivity() instanceof AddCustomActivity) {
                            ((AddCustomActivity) getActivity()).setRelevaneData(customBean.getName());
                        }

                        Map<String, Object> map = new HashMap<>();
                        map.put("id", detailMap.get("id"));
                        map.put("name", o2);
                        if (customBean.getValue() == null) {
                            customBean.setValue(map);
                        }
                    }
                    // Log.e("名称>>", customBean.getLabel() + ">>>>" + customBean.getField().getTerminalApp());
                }
            }

            SubfieldView subfieldView = new SubfieldView(list, state, layoutBean.getTitle(), isSpread, moduleBean);
            subfieldView.setHideColumnName(isHideColumnName);
            subfieldView.setHideColumn(isHideInCreate);
            subfieldView.setAppTerminalHide(!isTerminalApp);
            subfieldView.addView(llContent);
            mViewList.add(subfieldView);
        }
    }

    public void setIsCopyData(int state){
        isCopyState = state;
    }

}


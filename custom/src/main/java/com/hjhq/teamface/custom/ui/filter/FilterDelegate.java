package com.hjhq.teamface.custom.ui.filter;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.FilterFieldResultBean;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.util.log.LogUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.weight.BaseFilterView;
import com.hjhq.teamface.common.weight.filter.AreaFilterView;
import com.hjhq.teamface.common.weight.filter.ItemFilterView;
import com.hjhq.teamface.common.weight.filter.KeywordFilterView;
import com.hjhq.teamface.common.weight.filter.MemberFilterView2;
import com.hjhq.teamface.common.weight.filter.NumberFilterView;
import com.hjhq.teamface.common.weight.filter.TimeFilterView;
import com.hjhq.teamface.custom.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lx on 2017/9/4.
 */

public class FilterDelegate extends AppDelegate {
    private LinearLayout llContent;
    public List<BaseFilterView> viewList;

    @Override
    public int getRootLayoutId() {
        //组件显示
        return R.layout.custom_filter_fragment;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        viewList = new ArrayList<>();
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getActivity());
        get(R.id.rl_title_text).setPadding(0, statusBarHeight, 0, 0);
        llContent = get(R.id.ll_container);
    }


    public void getData(JSONObject json) {
        for (int i = 0; i < viewList.size(); i++) {
            try {
                viewList.get(i).put(json);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("GoodsFilterDelegate", "添加过滤条件失败!");
            }

        }
    }

    public void initFilerData(FragmentActivity activity, FilterFieldResultBean dataBean) {
        if (dataBean == null || dataBean.getData() == null) {
            return;
        }
        viewList.clear();
        llContent.removeAllViews();
        drawLayout(activity, dataBean.getData());


    }

    public void drawLayout(Activity activity, List<FilterFieldResultBean.DataBean> data) {
        for (FilterFieldResultBean.DataBean bean : data) {
            BaseFilterView view = null;
            switch (bean.getType()) {
                case "text"://单行文本
                case "location"://定位
                case "textarea"://多行文本
                case "multitext"://富文本
                case "phone"://电话
                case "email"://邮箱
                case "url"://超链接
                case "barcode"://条形码
                case "identifier"://自动编号
                case "serialnum"://流水号
                case "formula"://公式
                case "citeformula"://引用公式
                case "functionformula"://函数公式
                case "seniorformula"://高级公式
                case "reference"://引用
                    view = new KeywordFilterView(bean);
                    break;
                case "number"://数字
                    view = new NumberFilterView(bean);
                    break;
                case "picklist"://下拉选项
                    view = new ItemFilterView(bean);
                    break;
                case "tag"://标签
                    List<FilterFieldResultBean.DataBean.TagBean> tag = bean.getEntry();
                    List<FilterFieldResultBean.DataBean.EntrysBean> entry = new ArrayList<>();
                    if (tag != null && tag.size() > 0) {
                        for (FilterFieldResultBean.DataBean.TagBean t : tag) {
                            FilterFieldResultBean.DataBean.EntrysBean e = new FilterFieldResultBean.DataBean.EntrysBean();
                            e.setValue(t.getId());
                            e.setColor(t.getColour());
                            e.setLabel(t.getName());
                            e.setType(1);
                            entry.add(e);
                        }
                        bean.setEntrys(entry);
                    }
                    view = new ItemFilterView(bean);
                    break;
                case "multi"://复选框
                    view = new ItemFilterView(bean);
                    break;
                case "personnel":
                    //人员
                case "department":
                    //部门
                    view = new MemberFilterView2(bean);
                    break;
                case "date":
                    view = new TimeFilterView(bean);
                    break;
                case "datetime"://时间
                    view = new TimeFilterView(bean);
                    break;
                case "area"://省市区
                    view = new AreaFilterView(bean);
                    break;
                case "attachment"://附件
                    break;
                case "picture"://图片
                    break;
                case "subform"://子表单
                    break;
                default:
                    break;
            }

            if (view != null) {
                view.addView(llContent, activity);
                viewList.add(view);
            }
        }
    }

    public List<BaseFilterView> getViewList() {
        return viewList;
    }
}

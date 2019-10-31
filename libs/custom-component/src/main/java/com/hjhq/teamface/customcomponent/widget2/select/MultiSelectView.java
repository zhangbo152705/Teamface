package com.hjhq.teamface.customcomponent.widget2.select;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.JsonParser;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.adapter.MultiItemAdapter;
import com.hjhq.teamface.customcomponent.widget2.BaseView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;


/**
 * 复选
 *
 * @author lx
 * @date 2017/8/23
 */

public class MultiSelectView extends BaseView {
    private List<EntryBean> defaultEntrys;
    /**
     * 当前下拉选项集合
     */
    private List<EntryBean> entrys;

    /**
     * 是否多选  选择类型：0单选、1多选
     */
    private boolean isMulti;
    private TextView tvTitle;
    private View llRoot;
    private RecyclerView mRecyclerView;
    private TextView tvContent;

    public MultiSelectView(CustomBean bean) {
        super(bean);
        CustomBean.FieldBean field = bean.getField();
        this.entrys = bean.getEntrys();
        if (field != null) {
            defaultEntrys = field.getDefaultEntrys();
            isMulti = "1".equals(field.getChooseType());
        }
    }

    @Override
    public void addView(LinearLayout parent, Activity activity, int index) {
        if ("0".equals(structure)) {
            mView = View.inflate(activity, R.layout.custom_multi_select_widget_layout, null);
        } else {
            mView = View.inflate(activity, R.layout.custom_multi_select_widget_row_layout, null);
        }
        parent.addView(mView);

        tvTitle = mView.findViewById(R.id.tv_title);
        llRoot = mView.findViewById(R.id.ll_content);
        tvContent = mView.findViewById(R.id.tv_content);
        mRecyclerView = mView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new MultiItemAdapter(entrys));
        initOption();
    }


    public void initOption() {
        if (keyName.startsWith(CustomConstants.SUBFORM)) {
            //关联映射
            RxManager.$(aHashCode).on(keyName + subFormIndex, this::setContent);
            //联动
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, this::setContent);
        } else {
            //关联映射
            RxManager.$(aHashCode).on(keyName, this::setContent);
            //联动
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG, this::setContent);
        }
        RxManager.$(aHashCode).on(bean.getName(), tag -> {
            //关联组件返回的值
            if (tag != null && tag instanceof List) {
                try {
                    final List<JSONObject> tag1 = (List<JSONObject>) tag;
                    if (tag1.size() > 0) {
                        for (int i = 0; i < tag1.size(); i++) {
                            final JSONObject map = tag1.get(i);
                            if (map != null && map.get("label") != null) {
                                String label = map.getString("label");
                                for (EntryBean bean : entrys) {
                                    if (label.equals(bean.getLabel())) {//zzh:value比较改为lable比较
                                        bean.setCheck(true);
                                    }
                                }
                            }
                        }
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            tvContent.setVisibility(View.VISIBLE);
            //mRecyclerView.setVisibility(View.VISIBLE);
            tvContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (entrys == null || entrys.size() == 0) {
                        ToastUtils.showError(mView.getContext(), "数据为空");
                        return;
                    }
                    List<EntryBean> clone22 = new ArrayList<EntryBean>(entrys);
                    /*for (int i = 0; i < entrys.size(); i++) {
                        clone22.add(entrys.get(i));
                    }*/
                    final List<EntryBean> value = getValue();
                    if (value.size() > 0) {
                        for (int i = 0; i < value.size(); i++) {
                            for (int j = 0; j < clone22.size(); j++) {
                                if (value.get(i).getLabel().equals(clone22.get(j).getLabel())) {//zzh:value比较改为lable比较
                                    clone22.get(j).setCheck(true);
                                }
                            }
                        }
                    }
                    DialogUtils.getInstance().bottomSelectDialog(((Activity) mView.getContext()), mView, clone22, isMulti, new DialogUtils.OnClickSureOrCancelListener() {
                        @Override
                        public void clickSure() {
                            List<EntryBean> list = new ArrayList<EntryBean>();
                            for (int i = 0; i < clone22.size(); i++) {
                                if (clone22.get(i).isCheck()) {
                                    list.add(clone22.get(i));
                                }
                            }

                            setStringValue(list);
                        }

                        @Override
                        public void clickCancel() {

                        }
                    });
                }
            });
        } else {
            tvContent.setVisibility(View.VISIBLE);
            //mRecyclerView.setVisibility(View.GONE);
            // tvContent.setBackgroundResource(R.drawable.diy_bg);
        }
        if (state == CustomConstants.ADD_STATE) {
            setDefaultValue();
        }
        setTitle(tvTitle, title);

        Object value = bean.getValue();
        if (value != null && !"".equals(value)) {
            setData(value);
        } else {
            if (isDetailState()) {
                tvContent.setText("未选择");
                tvContent.setTextColor(getContext().getResources().getColor(R.color._999999));
            }
        }

        mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                RxManager.$(aHashCode).post(CustomConstants.MESSAGE_CLEAR_FOCUS_CODE, "");
                if (isDetailState()) {
                } else if (CustomConstants.FIELD_READ.equals(fieldControl)) {
                    ToastUtils.showError(getContext(), "只读属性不可更改");
                } else {
                    EntryBean entry = entrys.get(position);
                    if (isMulti) {
                        entry.setCheck(!entry.isCheck());
                        adapter.notifyItemChanged(position);
                    } else {
                        Observable.from(entrys).filter(EntryBean::isCheck).subscribe(entryBean -> entryBean.setCheck(false));
                        entry.setCheck(true);
                        adapter.notifyDataSetChanged();
                        if (!checkNull() && isLinkage) {
                            linkageData();
                        }
                    }
                }
            }
        });
    }

    private void setContent(Object object) {
        Log.e(MultiSelectView.class.getName(), JSONObject.toJSONString(object));
        List<EntryBean> list = new ArrayList<>();
        if (object instanceof List) {
            for (Object o : ((List) object)) {
                try {
                    EntryBean m = JSONObject.parseObject(JSONObject.toJSONString(o), EntryBean.class);
                    if (m != null) {
                        list.add(m);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (list.size() > 0) {
                if (entrys != null && entrys.size() > 0) {
                    for (EntryBean outer : list) {
                        for (EntryBean inner : entrys) {
                            if (outer.getLabel().equals(inner.getLabel())) {//zzh:value改为lable
                                inner.setCheck(true);
                            }
                        }
                    }
                }

            } else {
                for (int i = 0; i < entrys.size(); i++) {
                    entrys.get(i).setCheck(false);
                }

            }

        } else {
            for (int i = 0; i < entrys.size(); i++) {
                entrys.get(i).setCheck(false);
            }
        }
        setStringValue(list);
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void setData(Object value) {
        List<EntryBean> checkEntrys;
        if (value instanceof String){
            JSONArray arr = JSONArray.parseArray(value+"");
            checkEntrys = new JsonParser<EntryBean>().jsonFromList(arr, EntryBean.class);
        }else {
            checkEntrys = new JsonParser<EntryBean>().jsonFromList(value, EntryBean.class);
        }
        setStringValue(checkEntrys);
    }

    /**
     * 显示选中内容
     *
     * @param checkEntrys
     */
    private void setStringValue(List<EntryBean> checkEntrys) {
        if (isDetailState() && (checkEntrys == null || checkEntrys.size() == 0)) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText("未选择");
            tvContent.setTextColor(getContext().getResources().getColor(R.color._999999));
            return;
        }
        StringBuilder checkEntrysSb = new StringBuilder();
        //设置成选中
        for (EntryBean entry : entrys) {
            Observable.from(checkEntrys)
                    .filter(checkEntry -> entry.getLabel().equals(checkEntry.getLabel()))//zzh:value改为lable
                    .subscribe(checkEntry -> {
                        entry.setCheck(true);
                        if (TextUtil.isEmpty(checkEntrysSb)) {
                            checkEntrysSb.append(entry.getLabel());
                        } else {
                            checkEntrysSb.append("、" + entry.getLabel());
                        }
                    });
        }
        if (!TextUtils.isEmpty(checkEntrysSb)) {
            TextUtil.setText(tvContent, checkEntrysSb.toString());
            if (!checkNull() && isLinkage) {//zzh->ad:增加联动设置
                linkageData();
            }
        }
    }

    public void setDefaultValue() {
        if (CollectionUtils.isEmpty(entrys) || CollectionUtils.isEmpty(defaultEntrys)) {
            return;
        }
        for (EntryBean entry : entrys) {
            Observable.from(defaultEntrys).filter(bean -> bean.getLabel().equals(entry.getLabel())).subscribe(bean -> {//zzh:value改为lable比较
                entry.setCheck(true);
            });
        }
        setStringValue(defaultEntrys);
    }

    @Override
    public void put(JSONObject jsonObj) {
        List<Map<String,Object>> mapList = new ArrayList<>();
        JSONArray jsonArr = new JSONArray();
        if (!CollectionUtils.isEmpty(getValue())){
             jsonArr = filterIscheck(getValue());
        }
        jsonObj.put(keyName, jsonArr.isEmpty() ? "" : jsonArr);//zzh->:过滤字段check jsonObj.put(keyName, CollectionUtils.isEmpty( getValue()) ? "" : getValue())
    }

    /**
     * zzh->ad:过滤check字段
     * @return
     */
    public JSONArray filterIscheck(List<EntryBean> beanList){
         String arrString = JSON.toJSONString(beanList, profilter);
        JSONArray jsonArr = new JSONArray();
         if (arrString != null){
             jsonArr = JSON.parseArray(arrString);
         }
        return jsonArr;
    }

    /**
     * zzh->ad:过滤字段选择器
     */
    PropertyFilter profilter = new PropertyFilter(){
        @Override
        public boolean apply(Object object, String name, Object value) {
            if(name.equalsIgnoreCase("check") || name.equalsIgnoreCase("fromType")){
                return false;
            }
            return true;
        }
    };


    @Override
    public boolean checkNull() {
        return CollectionUtils.isEmpty(getValue());
    }

    @Override
    protected void linkageData() {
        super.linkageData();
    }

    private List<EntryBean> getValue() {
        List<EntryBean> checkEntrys = new ArrayList<>();
        Observable.from(entrys).filter(EntryBean::isCheck).subscribe(entryBean -> checkEntrys.add(entryBean));
        return checkEntrys;
    }

    @Override
    public boolean formatCheck() {
        return true;
    }


}

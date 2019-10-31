package com.hjhq.teamface.customcomponent.widget2;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.HidenFieldBean;
import com.hjhq.teamface.basis.bean.LayoutBean;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.RegionUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.customcomponent.widget2.barcode.BarcodeView;
import com.hjhq.teamface.customcomponent.widget2.file.AttachmentView;
import com.hjhq.teamface.customcomponent.widget2.file.PictureView;
import com.hjhq.teamface.customcomponent.widget2.file.ResumeanalysisView;
import com.hjhq.teamface.customcomponent.widget2.input.AutoNumberView;
import com.hjhq.teamface.customcomponent.widget2.input.EmailInputView;
import com.hjhq.teamface.customcomponent.widget2.input.FormulaView;
import com.hjhq.teamface.customcomponent.widget2.input.LocationInputView;
import com.hjhq.teamface.customcomponent.widget2.input.MultiTextInputView;
import com.hjhq.teamface.customcomponent.widget2.input.NumberInputView;
import com.hjhq.teamface.customcomponent.widget2.input.PhoneInputView;
import com.hjhq.teamface.customcomponent.widget2.input.TextInputView;
import com.hjhq.teamface.customcomponent.widget2.input.UrlInputView;
import com.hjhq.teamface.customcomponent.widget2.reference.ReferenceView;
import com.hjhq.teamface.customcomponent.widget2.select.DepartmentView;
import com.hjhq.teamface.customcomponent.widget2.select.LocationSelectView;
import com.hjhq.teamface.customcomponent.widget2.select.MemberView;
import com.hjhq.teamface.customcomponent.widget2.select.MultiPickListSelectView;
import com.hjhq.teamface.customcomponent.widget2.select.MultiSelectView;
import com.hjhq.teamface.customcomponent.widget2.select.PickListView;
import com.hjhq.teamface.customcomponent.widget2.select.TimeSelectView;
import com.hjhq.teamface.customcomponent.widget2.subfield.SubfieldView;
import com.hjhq.teamface.customcomponent.widget2.subforms.CommonSubFormsView;
import com.hjhq.teamface.customcomponent.widget2.web.RichTextWebView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class CustomUtil {
    private static Pattern mPattern;


    public static BaseView drawLayout(LinearLayout llContent, CustomBean bean, int state) {
        if (bean == null) {
            return null;
        }
        CustomBean.FieldBean field = bean.getField();


        BaseView view = null;
        switch (bean.getType()) {
            case CustomConstants.IDENTIFIER:
            case CustomConstants.SERIAL_NUMBER:
                view = new AutoNumberView(bean);
                break;
            case CustomConstants.TEXT:
                view = new TextInputView(bean);
                break;
            case CustomConstants.PICKLIST:
                view = new PickListView(bean);
                break;
            case CustomConstants.MULTI:
                view = new MultiSelectView(bean);
                break;
            case CustomConstants.MUTLI_PICKLIST:
                view = new MultiPickListSelectView(bean);
                break;
            case CustomConstants.LOCATION:
                view = new LocationInputView(bean);
                break;
            case CustomConstants.AREA:
                view = new LocationSelectView(bean);
                break;
            case CustomConstants.TEXTAREA:
                view = new MultiTextInputView(bean);
                break;
            case CustomConstants.DATETIME:
                view = new TimeSelectView(bean);
                break;
            case CustomConstants.NUMBER:
                view = new NumberInputView(bean);
                break;
            case CustomConstants.PHONE:
                view = new PhoneInputView(bean);
                break;
            case CustomConstants.EMAIL:
                view = new EmailInputView(bean);
                break;
            case CustomConstants.REFERENCE:
                view = new ReferenceView(bean);
                Context context = llContent.getContext();
                if (llContent.getContext() instanceof ReferenceViewInterface) {
                    ((ReferenceView) view).setReferenceViewInterface((ReferenceViewInterface) context);
                }
                break;
            case CustomConstants.URL:
                view = new UrlInputView(bean);
                break;
            case CustomConstants.ATTACHMENT:
                view = new AttachmentView(bean);
                break;
            case CustomConstants.PICTURE:
                view = new PictureView(bean);
                break;
            case CustomConstants.FORMULA:
            case CustomConstants.SENIOR_FORMULA:
            case CustomConstants.FUNCTION_FORMULA:
            case CustomConstants.REFERENCE_FORMULA:
                view = new FormulaView(bean);
                break;
            case CustomConstants.PERSONNEL:
                view = new MemberView(bean);
                break;
            case CustomConstants.SUBFORM:
                view = new CommonSubFormsView(bean);
                break;
            case CustomConstants.RICH_TEXT:
                view = new RichTextWebView(bean);
                break;
            case CustomConstants.DEPARTMENT:
                view = new DepartmentView(bean);
                break;
            case CustomConstants.BARCODE:
                view = new BarcodeView(bean);
                break;
            default:
                break;
        }

        if (view != null) {
            view.setState(state);
            view.addView(llContent, (Activity) llContent.getContext());
            view.setStateVisible();
        }
        return view;
    }


    public static BaseView drawLayout(int stateEnv, LinearLayout llContent, CustomBean bean) {
        if (bean == null) {
            return null;
        }
        CustomBean.FieldBean field = bean.getField();

        Log.e("drawLayout","Type:"+bean.getType());
        Log.e("drawLayout","lable:"+bean.getLabel());
        Log.e("drawLayout","relyonFields:"+bean.getRelyonFields());
        BaseView view = null;
        switch (bean.getType()) {
            case CustomConstants.IDENTIFIER:
            case CustomConstants.SERIAL_NUMBER:
                view = new AutoNumberView(bean);
                break;
            case CustomConstants.TEXT:
                view = new TextInputView(bean);
                break;
            case CustomConstants.PICKLIST:
                view = new PickListView(bean);
                break;
            case CustomConstants.MULTI:
                view = new MultiSelectView(bean);
                break;
            case CustomConstants.MUTLI_PICKLIST:
                view = new MultiPickListSelectView(bean);
                break;
            case CustomConstants.LOCATION:
                view = new LocationInputView(bean);
                break;
            case CustomConstants.AREA:
                view = new LocationSelectView(bean);
                break;
            case CustomConstants.TEXTAREA:
                view = new MultiTextInputView(bean);
                break;
            case CustomConstants.DATETIME:
                view = new TimeSelectView(bean);
                break;
            case CustomConstants.NUMBER:
                view = new NumberInputView(bean);
                break;
            case CustomConstants.PHONE:
                view = new PhoneInputView(bean);
                break;
            case CustomConstants.EMAIL:
                view = new EmailInputView(bean);
                break;
            case CustomConstants.REFERENCE:
                view = new ReferenceView(bean);
                Context context = llContent.getContext();
                if (llContent.getContext() instanceof ReferenceViewInterface) {
                    ((ReferenceView) view).setReferenceViewInterface((ReferenceViewInterface) context);
                }
                break;
            case CustomConstants.URL:
                view = new UrlInputView(bean);
                break;
            case CustomConstants.ATTACHMENT:
                view = new AttachmentView(bean);
                break;
            case CustomConstants.RESUMEANALYSIS://zzh:简历解析组件
                view = new ResumeanalysisView(bean);
                break;
            case CustomConstants.PICTURE:
                view = new PictureView(bean);
                break;
            case CustomConstants.FORMULA:
            case CustomConstants.SENIOR_FORMULA:
            case CustomConstants.FUNCTION_FORMULA:
            case CustomConstants.REFERENCE_FORMULA:
                view = new FormulaView(bean);
                break;
            case CustomConstants.PERSONNEL:
                view = new MemberView(bean);
                break;
            case CustomConstants.SUBFORM:
                view = new CommonSubFormsView(bean);
                break;
            case CustomConstants.RICH_TEXT:
                view = new RichTextWebView(bean);
                break;
            case CustomConstants.DEPARTMENT:
                view = new DepartmentView(bean);
                break;
            case CustomConstants.BARCODE:
                view = new BarcodeView(bean);
                break;
            default:
                break;
        }

        if (view != null) {
            view.setState(bean.getState());
            view.setStateEnv(stateEnv);
            view.addView(llContent, (Activity) llContent.getContext());
            view.setStateVisible();
        }
        return view;
    }


    public static void putDefaultValue(List<LayoutBean> layoutBeanList, JSONObject json) {
        if (layoutBeanList != null && layoutBeanList.size() > 0) {
            for (int i = 0; i < layoutBeanList.size(); i++) {
                List<CustomBean> rowList = layoutBeanList.get(i).getRows();
                for (int k = 0; k < rowList.size(); k++) {
                    CustomBean kBean = rowList.get(k);
                    if (CustomConstants.SUBFORM.equals(kBean.getType())) {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        List<CustomBean> componentList = kBean.getComponentList();
                        for (int j = 0; j < componentList.size(); j++) {
                            CustomBean jBean = componentList.get(j);
                            jsonObject.put(jBean.getName(), getDefaultVale(jBean));
                        }
                        jsonArray.add(jsonObject);
                        json.put(kBean.getName(), jsonArray);
                    } else {
                        json.put(kBean.getName(), getDefaultVale(kBean));
                    }
                }
            }
        }
    }


    public static Object getDefaultVale(CustomBean bean) {
        Object o = null;
        List<EntryBean> defaultEntrys = bean.getField().getDefaultEntrys();
        CustomBean.DefaultEntryBean defaultEntryBean = bean.getDefaultEntrys();
        List<Member> department = bean.getField().getDefaultDepartment();
        List<Member> personnel = bean.getField().getDefaultPersonnel();
        switch (bean.getType()) {
            case CustomConstants.IDENTIFIER:
            case CustomConstants.SUBFORM + "_" + CustomConstants.IDENTIFIER:
            case CustomConstants.SERIAL_NUMBER:
            case CustomConstants.SUBFORM + "_" + CustomConstants.SERIAL_NUMBER:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.TEXT:
            case CustomConstants.SUBFORM + "_" + CustomConstants.TEXT:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.PICKLIST:
            case CustomConstants.SUBFORM + "_" + CustomConstants.PICKLIST:
            case CustomConstants.SUBFORM + "_" + CustomConstants.MULTI:
            case CustomConstants.MULTI:
                if (defaultEntrys == null || defaultEntrys.size() == 0) {
                    o = "";
                } else {
                    JSONArray jArray = new JSONArray();
                    for (int i = 0; i < defaultEntrys.size(); i++) {
                        JSONObject jo = new JSONObject();
                        jo.put("color", defaultEntrys.get(i).getColor());
                        jo.put("value", defaultEntrys.get(i).getValue());
                        jo.put("label", defaultEntrys.get(i).getLabel());
                        jArray.add(jo);
                    }
                    o = jArray;
                }
                break;
            case CustomConstants.MUTLI_PICKLIST:
            case CustomConstants.SUBFORM + "_" + CustomConstants.MUTLI_PICKLIST:
                if (defaultEntryBean == null) {
                    o = "";
                } else {
                    JSONArray jArray = new JSONArray();
                    if (!TextUtils.isEmpty(defaultEntryBean.getOneDefaultValueId())) {
                        JSONObject jo1 = new JSONObject();
                        jo1.put("color", defaultEntryBean.getOneDefaultValueColor());
                        jo1.put("value", defaultEntryBean.getOneDefaultValueId());
                        jo1.put("label", defaultEntryBean.getOneDefaultValue());
                        jArray.add(jo1);
                    }
                    if (!TextUtils.isEmpty(defaultEntryBean.getThreeDefaultValueId()) && jArray.size() == 1) {
                        JSONObject jo2 = new JSONObject();
                        jo2.put("color", defaultEntryBean.getOneDefaultValueColor());
                        jo2.put("value", defaultEntryBean.getOneDefaultValueId());
                        jo2.put("label", defaultEntryBean.getOneDefaultValue());
                        jArray.add(jo2);
                    }
                    if (!TextUtils.isEmpty(defaultEntryBean.getThreeDefaultValueId()) && jArray.size() == 2) {
                        JSONObject jo3 = new JSONObject();
                        jo3.put("color", defaultEntryBean.getOneDefaultValueColor());
                        jo3.put("value", defaultEntryBean.getOneDefaultValueId());
                        jo3.put("label", defaultEntryBean.getOneDefaultValue());
                        jArray.add(jo3);
                    }
                    if (jArray.size() == 0) {
                        o = "";
                    } else {
                        o = jArray;
                    }

                }
                break;
            case CustomConstants.LOCATION:
            case CustomConstants.SUBFORM + "_" + CustomConstants.LOCATION:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.AREA:
            case CustomConstants.SUBFORM + "_" + CustomConstants.AREA:
                o = "";
                break;
            case CustomConstants.TEXTAREA:
            case CustomConstants.SUBFORM + "_" + CustomConstants.TEXTAREA:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.DATETIME:
            case CustomConstants.SUBFORM + "_" + CustomConstants.DATETIME:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.NUMBER:
            case CustomConstants.SUBFORM + "_" + CustomConstants.NUMBER:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.PHONE:
            case CustomConstants.SUBFORM + "_" + CustomConstants.PHONE:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.EMAIL:
            case CustomConstants.SUBFORM + "_" + CustomConstants.EMAIL:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.REFERENCE:
            case CustomConstants.SUBFORM + "_" + CustomConstants.REFERENCE:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.URL:
            case CustomConstants.SUBFORM + "_" + CustomConstants.URL:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.ATTACHMENT:
            case CustomConstants.SUBFORM + "_" + CustomConstants.ATTACHMENT:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.PICTURE:
            case CustomConstants.SUBFORM + "_" + CustomConstants.PICTURE:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.FORMULA:
            case CustomConstants.SENIOR_FORMULA:
            case CustomConstants.FUNCTION_FORMULA:
            case CustomConstants.REFERENCE_FORMULA:
            case CustomConstants.SUBFORM + "_" + CustomConstants.FORMULA:
            case CustomConstants.SUBFORM + "_" + CustomConstants.SENIOR_FORMULA:
            case CustomConstants.SUBFORM + "_" + CustomConstants.FUNCTION_FORMULA:
            case CustomConstants.SUBFORM + "_" + CustomConstants.REFERENCE_FORMULA:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.PERSONNEL:
            case CustomConstants.SUBFORM + "_" + CustomConstants.PERSONNEL:
                if (personnel != null && personnel.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < personnel.size(); i++) {
                        if (personnel.get(i).getId() > 0) {
                            if (TextUtils.isEmpty(sb)) {
                                sb.append(personnel.get(i).getId() + "");
                            } else {
                                sb.append("," + personnel.get(i).getId());
                            }
                        }
                        if (personnel.get(i).getId() == -1) {
                            if (TextUtils.isEmpty(sb)) {
                                sb.append(SPHelper.getEmployeeId());
                            } else {
                                sb.append("," + SPHelper.getEmployeeId());
                            }
                        }
                    }
                    o = sb.toString();
                } else {
                    o = "";
                }
                break;
            case CustomConstants.SUBFORM:
                break;
            case CustomConstants.RICH_TEXT:
            case CustomConstants.SUBFORM + "_" + CustomConstants.RICH_TEXT:
                o = bean.getField().getDefaultValue();
                break;
            case CustomConstants.DEPARTMENT:
            case CustomConstants.SUBFORM + "_" + CustomConstants.DEPARTMENT:
                if (department != null && department.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < department.size(); i++) {
                        if (department.get(i).getId() > 0) {
                            if (TextUtils.isEmpty(sb)) {
                                sb.append(department.get(i).getId() + "");
                            } else {
                                sb.append("," + department.get(i).getId());
                            }
                        }
                        if (department.get(i).getId() == -1) {
                            if (TextUtils.isEmpty(sb)) {
                                sb.append(SPHelper.getDepartmentId());
                            } else {
                                sb.append("," + SPHelper.getDepartmentId());
                            }
                        }
                    }
                    o = sb.toString();
                } else {
                    o = "";
                }

                break;
            case CustomConstants.BARCODE:
            case CustomConstants.SUBFORM + "_" + CustomConstants.BARCODE:
                o = bean.getField().getDefaultValue();
                break;
            default:
                break;
        }
        return o;
    }


    public static boolean editPut(List<SubfieldView> mViewList, Activity activity, JSONObject json) {
        for (SubfieldView view : mViewList) {
            ArrayList viewList = view.getViewList();
            boolean put = editPut(activity, viewList, json);
            if (!put) {
                return put;
            }
        }
        return true;
    }


    public static boolean addPut(List<SubfieldView> mViewList, Activity activity, JSONObject json) {
        for (SubfieldView view : mViewList) {
            ArrayList viewList = view.getViewList();
            boolean put = addPut(activity, viewList, json);
            if (!put) {
                return put;
            }
        }
        return true;
    }

    public static boolean getValue(List<SubfieldView> mViewList, Activity activity, JSONObject json) {
        for (SubfieldView view : mViewList) {
            ArrayList viewList = view.getViewList();
            boolean put = getValue(activity, viewList, json);
            if (!put) {
                return put;
            }
        }
        return true;
    }

    //zzh->ad:新增根据index获取指定栏目或第一个栏目数据
    public static boolean getValue(List<SubfieldView> mViewList, Activity activity, JSONObject json,int index) {
        for (SubfieldView view : mViewList) {
            ArrayList viewList = view.getViewList();
            boolean put = getValue(activity, viewList, json,index);
            if (!put) {
                return put;
            }
        }
        return true;
    }


    public static boolean editPut(Activity activity, List<BaseView> mViewList, JSONObject json) {
        for (BaseView view : mViewList) {
            if (view.controlHide) {
                json.put(view.getKeyName(), "");
                continue;
            }
            boolean must = CustomConstants.FIELD_MUST.equals(view.getFieldControl());
            if (view.checkNull() && must) {
                ToastUtils.showToast(activity, view.getBean().getLabel() + "不能为空");
                return false;
            } else if (view.formatCheck()) {
                view.put(json);
            } else {
                return false;
            }
        }
        return true;
    }


    public static boolean addPut(Activity activity, List<BaseView> mViewList, JSONObject json) {
        for (BaseView view : mViewList) {
            if (view.controlHide) {
                json.put(view.getKeyName(), "");
                continue;
            }

            boolean must = CustomConstants.FIELD_MUST.equals(view.getFieldControl());
            if (view.checkNull() && must) {
                if (view instanceof CommonSubFormsView) {
                    if (((CommonSubFormsView) view).getViewList().size() <= 0) {
                        ToastUtils.showToast(activity, view.getBean().getLabel() + "不能为空");
                        return false;
                    }
                } else {
                    ToastUtils.showToast(activity, view.getBean().getLabel() + "不能为空");
                    return false;
                }

            } else if (view.formatCheck()) {
                view.put(json);
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean getValue(Activity activity, List<BaseView> mViewList, JSONObject json) {
        for (BaseView view : mViewList) {
            if (view.controlHide) {
                json.put(view.getKeyName(), "");
                continue;
            }
            if (view instanceof CommonSubFormsView) {
                ((CommonSubFormsView) view).put(json, false);
            } else {
                view.put(json);
            }
        }
        return true;
    }

    //zzh->ad:新增根据index获取指定栏目或第一个栏目数据
    public static boolean getValue(Activity activity, List<BaseView> mViewList, JSONObject json,int index) {
        for (BaseView view : mViewList) {
            if (view.controlHide) {
                json.put(view.getKeyName(), "");
                continue;
            }
            if (view instanceof CommonSubFormsView) {
                ((CommonSubFormsView) view).put(json, false,index);
            } else {
                view.put(json);
            }
        }
        return true;
    }


    public static boolean putNoCheck(List<BaseView> mViewList, JSONObject json) {
        for (BaseView view : mViewList) {
            if (!view.getVisibility()) {
                if (view.isHidenField()) {
                    json.put(view.getKeyName(), "");
                }
                continue;
            }
            view.put(json);
        }
        return true;
    }


    public static boolean putAndCheck(List<BaseView> mViewList, JSONObject json, boolean showNotify) {
        for (BaseView view : mViewList) {
            if (view instanceof CommonSubFormsView) {
                JSONArray ja = new JSONArray();
                final boolean subFormsValue = ((CommonSubFormsView) view).getSubFormsValue(ja, showNotify);
                if (subFormsValue) {
                    json.put(view.getKeyName(), ja);
                    continue;
                } else {
                    return false;
                }
            }
            if (view.controlHide) {
                json.put(view.getKeyName(), "");
                continue;
            }
            if (view.state == CustomConstants.EDIT_STATE || view.state == CustomConstants.APPROVE_DETAIL_STATE) {
                if (view.checkNull()) {
                    if ("2".equals(view.getFieldControl())) {
                        if (showNotify) {
                            ToastUtils.showToast(view.mView.getContext(), view.getTitle() + "必填项");
                        }
                        return false;
                    } else {
                        json.put(view.getKeyName(), "");
                    }
                } else {
                    view.put(json);
                }
            } else {
                view.put(json);
            }

        }
        return true;
    }


    public static boolean putReference(List<SubfieldView> mViewList, JSONObject json) {
        for (SubfieldView subfieldView : mViewList) {
            for (BaseView view : subfieldView.getViewList()) {
                if (view instanceof ReferenceView && view.getVisibility()) {
                    view.put(json);
                }
            }
        }
        return true;
    }


    public static void setReferenceTempValue(TextView textView, RowBean rowBean) {
        setTempValue(textView, rowBean, false);
    }


    public static void setDetailHeaderValue(TextView textView, RowBean rowBean) {
        try {
            setTempValue(textView, rowBean, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setTempValue(TextView textView, RowBean rowBean, boolean isColor) {
        if (rowBean == null) {
            return;
        }
        GradientDrawable drawable = null;
        try {
            drawable = (GradientDrawable) textView.getBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String name = rowBean.getName();
        String value = rowBean.getValue();
        if (TextUtil.isEmpty(value)) {
            textView.setText("");
            return;
        }

        boolean isSubform = name.startsWith(CustomConstants.SUBFORM);
        boolean isPicture = name.startsWith(CustomConstants.PICTURE);
        boolean isAttachment = name.startsWith(CustomConstants.ATTACHMENT);
        if (isPicture || isAttachment) {//zzh->ad: 去掉isSubform ||
            return;
        }

        StringBuilder text = new StringBuilder();
        String color = null;

        //zzh->ad: name.startsWith 改为 name.indexOf != -1
        if (name.indexOf(CustomConstants.PERSONNEL) != -1 || name.indexOf(CustomConstants.REFERENCE) != -1 || name.indexOf(CustomConstants.DEPARTMENT) !=-1) {
            text.append(getJsonArrayValue(value, "name"));
        } else if (name.indexOf(CustomConstants.PICKLIST) != -1 || name.indexOf(CustomConstants.MULTI) != -1 || name.indexOf(CustomConstants.MUTLI_PICKLIST) != -1) {
            text.append(getJsonArrayValue(value, "label"));
            if (isColor) {
                String[] split = getJsonArrayValue(value, "color").split(",");
                if (split.length > 0) {
                    color = split[0];
                }
            }
        } else if (name.indexOf(CustomConstants.DATETIME) != -1) {
            String format = rowBean.getOther();
            if (TextUtil.isEmpty(format)) {
                format = "yyyy-MM-dd";
            }
            long time = TextUtil.parseLong(value);
            String s = DateTimeUtil.longToStr(time, format);
            text.append(s);
        } else if (name.indexOf(CustomConstants.AREA) != -1) {
            text.append(RegionUtil.code2String(value));
        } else if (name.indexOf(CustomConstants.LOCATION) != -1) {
            text.append(getJsonObjectValue(value, "value"));
        } else {
            text.append(value);
        }

        TextUtil.setText(textView, text.toString());
        boolean checkColor = ColorUtils.checkColor(color);
        if (isColor) {
            if (checkColor && !color.toUpperCase().startsWith("#FFFFFF")) {
                if (drawable != null) {
                    drawable.setColor(ColorUtils.hexToColor(color));
                }
                textView.setTextColor(ColorUtils.hexToColor("#FFFFFF"));
            } else {
                if (drawable != null) {
                    drawable.setColor(ColorUtils.hexToColor("#FFFFFF"));
                }
                textView.setTextColor(ColorUtils.hexToColor("#4a4a4a"));
            }
        }
    }


    public static void setTempValue(TextView textView, RowBean rowBean, boolean isColor, boolean isKeepSecret) {
        if (rowBean == null) {
            return;
        }
        GradientDrawable drawable = null;
        try {
            drawable = (GradientDrawable) textView.getBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String name = rowBean.getName();
        String value = rowBean.getValue();
        if (TextUtil.isEmpty(value)) {
            textView.setText("");
            return;
        }

        boolean isSubform = name.startsWith(CustomConstants.SUBFORM);
        boolean isPicture = name.startsWith(CustomConstants.PICTURE);
        boolean isAttachment = name.startsWith(CustomConstants.ATTACHMENT);
        if (isSubform || isPicture || isAttachment) {
            return;
        }

        StringBuilder text = new StringBuilder();
        String color = null;

        if (name.startsWith(CustomConstants.PERSONNEL) || name.startsWith(CustomConstants.REFERENCE) || name.startsWith(CustomConstants.DEPARTMENT)) {
            text.append(getJsonArrayValue(value, "name"));
        } else if (name.startsWith(CustomConstants.PICKLIST) || name.startsWith(CustomConstants.MULTI) || name.startsWith(CustomConstants.MUTLI_PICKLIST)) {
            text.append(getJsonArrayValue(value, "label"));
            if (isColor) {
                String[] split = getJsonArrayValue(value, "color").split(",");
                if (split.length > 0 && split.length ==1) {
                    color = split[0];
                }else {
                    color = "#FFFFFF";
                }
            }
        } else if (name.startsWith(CustomConstants.DATETIME)) {
            String format = rowBean.getOther();
            if (TextUtil.isEmpty(format)) {
                format = rowBean.getField_param().getFormatType();
            }
            long time = TextUtil.parseLong(value);
            String s = DateTimeUtil.longToStr(time, format);
            text.append(s);
        } else if (name.startsWith(CustomConstants.AREA)) {
            text.append(RegionUtil.code2String(value));
        } else if (name.startsWith(CustomConstants.NUMBER)) {
            if (formatNumber(rowBean, text)) ;
        } else if (name.contains(CustomConstants.FORMULA)) {
            if (formatNumber(rowBean, text)) ;
        } else if (name.startsWith(CustomConstants.LOCATION)) {
            String location = getJsonObjectValue(value, "value");
            if (TextUtils.isEmpty(location)) {
                text.append("");
                return;
            }
            if (isKeepSecret) {
                text.append(getConcealLocation(location));
            } else {

                text.append(location);

            }

        } else if (name.startsWith(CustomConstants.PHONE)) {
            if (isKeepSecret) {
                text.append(getConcealPhone(value));
            } else {
                text.append(value);
            }
        } else if (name.startsWith(CustomConstants.EMAIL)) {
            if (isKeepSecret) {
                text.append(getConcealEmailAddress(value));
            } else {
                text.append(value);
            }
        } else {
            text.append(value);
        }

        TextUtil.setText(textView, text.toString());
        boolean checkColor = ColorUtils.checkColor(color);
        if (isColor) {
            if (checkColor && !color.toUpperCase().startsWith("#FFFFFF")) {
                if (drawable != null) {
                    drawable.setColor(ColorUtils.hexToColor(color));
                }
                textView.setTextColor(ColorUtils.hexToColor("#FFFFFF"));
            } else {
                if (drawable != null) {
                    drawable.setColor(ColorUtils.hexToColor("#FFFFFF"));
                }
                textView.setTextColor(ColorUtils.hexToColor("#4a4a4a"));
            }
        }
    }


    public static boolean formatNumber(CustomBean.FieldBean fieldBean, String value, StringBuilder text) {
        RowBean rowBean = new RowBean();
        rowBean.setField_param(fieldBean);
        rowBean.setValue(value);
        return formatNumber(rowBean, text);

    }

    public static boolean formatNumber(RowBean rowBean, StringBuilder text) {
        int numberType = TextUtil.parseInt(rowBean.getField_param().getNumberType());
        int numberLenth = 0;
        if (numberType == 0 || numberType == 1 || numberType == 2) {
            if (rowBean.getName().startsWith(CustomConstants.NUMBER)) {
                numberLenth = TextUtil.parseInt(rowBean.getField_param().getNumberLenth());
            } else {
                numberLenth = TextUtil.parseInt(rowBean.getField_param().getDecimalLen());
            }
            String contentStr = rowBean.getValue();
            if (TextUtils.isEmpty(contentStr)) {
                return true;
            }
            int length = contentStr.length();
            int pointIndex = contentStr.indexOf(".");
            int size;
            if (pointIndex == -1) {
                contentStr += ".";
                size = numberLenth;
            } else {
                size = numberLenth - (length - pointIndex - 1);
            }
            for (int j = 0; j < size; j++) {
                contentStr += "0";
            }
            if (2 == numberType) {
                text.append(contentStr + "%");
            } else if (0 == numberType) {
                text.append(contentStr);
            } else {
                text.append(TextUtil.parseIntText(rowBean.getValue()));
            }
        } else if (3 == numberType) {
            text.append(rowBean.getValue());
        } else if (4 == numberType) {
            final String date = DateTimeUtil.longToStr(TextUtil.parseLong(rowBean.getValue()), rowBean.getField_param().getChooseType());
            text.append(date);
        }
        return false;
    }


    private static String getJsonArrayValue(String json, String field) {
        StringBuilder text = new StringBuilder();
        try {
            if (TextUtil.isEmpty(json) || TextUtil.isEmpty(field)) {
                return json;
            }
            final Object parse = JSON.parse(json);

            if (parse instanceof JSONArray) {
                JSONArray jsonArray = JSONArray.parseArray(json);
                if (!CollectionUtils.isEmpty(jsonArray)) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        text.append(jsonObject.getString(field) + ",");
                    }
                    if (!TextUtil.isEmpty(text)) {
                        text.deleteCharAt(text.length() - 1);
                    }
                }
                return text.toString();
            } else if (parse instanceof JSONObject) {
                JSONObject jsonObject = JSONObject.parseObject(json);
                text.append(jsonObject.getString(field));
                return text.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return json;
        }
        return json;
    }


    private static String getJsonObjectValue(String json, String field) {
        String text = "";
        try {
            if (TextUtil.isEmpty(json) || TextUtil.isEmpty(field)) {
                return text;
            }
            JSONObject jsonObject = JSONObject.parseObject(json);
            if (jsonObject != null) {
                text = jsonObject.getString(field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }


    public static void handleHidenFields(int hashCode, Object tag, List<SubfieldView> mViewList) {
        RxManager.$(hashCode).onSticky(tag, hides -> {
            List<HidenFieldBean> hidenFields = (List<HidenFieldBean>) hides;
            HidenFieldBean controlField = hidenFields.get(0);
            hidenFields.remove(0);
            boolean after = false;
            for (SubfieldView subfieldView : mViewList) {
                for (BaseView baseView : subfieldView.getViewList()) {
                    if (!baseView.getVisibility() && !baseView.isHidenField()) {
                        continue;
                    }
                    String keyName = baseView.getKeyName();
                    if (after) {
                        boolean isShow = true;
                        for (HidenFieldBean hidenField : hidenFields) {
                            if (keyName.equals(hidenField.getName())) {
                                isShow = false;
                                break;
                            }
                        }
                        baseView.setHidenFieldVisible(isShow);
                    }
                    if (keyName.equals(controlField.getName())) {
                        if (!baseView.getVisibility() && baseView.isHidenField()) {
                            return;
                        }
                        after = true;
                    }
                }
            }
        });
        RxManager.$(hashCode).onSticky(CustomConstants.CLEAR_FIELD_VALUE_TAG + tag, hides -> {
            List<HidenFieldBean> hidenFields = (List<HidenFieldBean>) hides;
            HidenFieldBean controlField = hidenFields.get(0);
            hidenFields.remove(0);
            boolean after = false;
            for (SubfieldView subfieldView : mViewList) {
                for (BaseView baseView : subfieldView.getViewList()) {
                    if (!baseView.getVisibility() && !baseView.isHidenField()) {
                        continue;
                    }
                    String keyName = baseView.getKeyName();
                    if (after) {
                        boolean isShow = true;
                        for (HidenFieldBean hidenField : hidenFields) {
                            if (keyName.equals(hidenField.getName())) {
                                isShow = false;
                                break;
                            }
                        }
                        baseView.setHidenFieldVisible(isShow);
                        if (baseView instanceof PickListView) {
                            PickListView pickListView = (PickListView) baseView;
                            if (!pickListView.isMulti() && !CollectionUtils.isEmpty(pickListView.getCheckEntrys())) {
                                if (!CollectionUtils.isEmpty(pickListView.getCheckEntrys().get(0).getHidenFields())) {
                                    pickListView.clear(true);
                                }
                            }
                        }
                    }
                    if (keyName.equals(controlField.getName())) {
                        if (!baseView.getVisibility() && baseView.isHidenField()) {
                            return;
                        }
                        after = true;
                    }
                }
            }
        });

        RxManager.$(hashCode).onSticky(CustomConstants.CLEAR_FIELD_HIDE_TAG + tag, hides -> {
            List<HidenFieldBean> hidenFields = (List<HidenFieldBean>) hides;

            List<BaseView> baseViewList = new ArrayList<>();
            for (SubfieldView subfieldView : mViewList) {
                baseViewList.addAll(subfieldView.getViewList());
            }
            for (HidenFieldBean hidenField : hidenFields) {
                for (BaseView baseView : baseViewList) {
                    if ("0".equals(baseView.terminalApp) || !baseView.isHidenField()) {
                        continue;
                    }
                    if (baseView.getKeyName().equals(hidenField.getName())) {
                        baseView.setHidenFieldVisible(true);
                        break;
                    }
                }
            }

        });
    }


    public static String getEmail(Map<String, Object> detailMap) {
        StringBuilder sb = new StringBuilder();
        for (String next : detailMap.keySet()) {
            if (next.startsWith(CustomConstants.EMAIL)) {
                String email = detailMap.get(next) + "";
                if (TextUtils.isEmpty(sb)) {
                    sb.append(email);
                } else {
                    sb.append(",").append(email);
                }
            }
        }
        return sb.toString();
    }


    public static String getConcealLocation(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length() - 4; i++) {
            sb.append("*");
        }
        return sb.toString();
    }


    public static String getConcealEmailAddress(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        final int lastIndexOf = text.lastIndexOf("@");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lastIndexOf + 1; i++) {
            sb.append("*");
        }
        text = sb + text.substring(lastIndexOf, text.length() - 1);
        return text;
    }


    public static String getConcealPhone(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        final int length = text.length();
        if (length <= 4) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append("*");
            }
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length - 4; i++) {
                sb.append("*");
            }
            return sb + text.substring(length - 4, length);
        }
    }
}

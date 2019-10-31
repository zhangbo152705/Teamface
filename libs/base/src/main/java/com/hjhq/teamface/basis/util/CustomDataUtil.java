package com.hjhq.teamface.basis.util;

import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.bean.RowDataBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.database.Member;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/24.
 * Describe：
 */

public class CustomDataUtil {
    /**
     * 从json字符串获取自定义数据的值
     *
     * @param rawValue
     * @param json
     * @param field
     * @return
     */
    private static String getJsonArrayValue(String rawValue, String json, String field) {
        StringBuilder text = new StringBuilder();
        if (TextUtil.isEmpty(json) || TextUtil.isEmpty(field)) {
            return text.toString();
        }
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            return rawValue;
        }
        return text.toString();
    }

    /**
     * 从json字符串获取自定义数据的值
     *
     * @param json
     * @param field
     * @return
     */
    private static String getJsonObjectValue(String json, String field) {
        String text = "";
        if (TextUtil.isEmpty(json) || TextUtil.isEmpty(field)) {
            return text;
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        if (jsonObject != null) {
            text = jsonObject.getString(field);
        }
        return text;
    }

    /**
     * 获取数据
     *
     * @param rowBean
     * @return
     */
    public static String getDataValue(RowDataBean rowBean) {
        boolean isColor = false;
        if (rowBean == null) {
            return "";
        }
        String name = rowBean.getName();
        String value = JSONObject.toJSONString(rowBean.getValue());
        String rawValue = rowBean.getValue().toString();

        if (TextUtil.isEmpty(value)) {

            return "";
        }

        //附件、子表、图片 不显示
        boolean isSubform = name.startsWith(CustomConstants.SUBFORM);
        boolean isPicture = name.startsWith(CustomConstants.PICTURE);
        boolean isAttachment = name.startsWith(CustomConstants.ATTACHMENT);
        if (isSubform || isPicture || isAttachment) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        String color = null;
        if (name.startsWith(CustomConstants.PERSONNEL) || name.startsWith(CustomConstants.REFERENCE) || name.startsWith(CustomConstants.DEPARTMENT)) {
            //人员、关联
            text.append(getJsonArrayValue(rawValue, value, "name"));
        } else if (name.startsWith(CustomConstants.PICKLIST) || name.startsWith(CustomConstants.MULTI) || name.startsWith(CustomConstants.MUTLI_PICKLIST)) {
            //下拉
            text.append(getJsonArrayValue(rawValue, value, "label"));
            if (isColor) {
                String[] split = getJsonArrayValue(rawValue, value, "color").split(",");
                if (split.length > 0) {
                    color = split[0];
                }
            }
        } else if (name.startsWith(CustomConstants.DATETIME)) {
            //时间
            String format = rowBean.getOther();
            if (TextUtil.isEmpty(format)) {
                format = "yyyy-MM-dd";
            }
            long time = TextUtil.parseLong(value);
            String s = DateTimeUtil.longToStr(time, format);
            text.append(s);
        } else if (name.startsWith(CustomConstants.AREA)) {
            //省市区
            text.append(RegionUtil.code2String(value));
        } else if (name.startsWith(CustomConstants.LOCATION)) {
            //定位
            text.append(getJsonObjectValue(value, "value"));
        } else {
            text.append(value);
        }
        return text.toString();
    }

    /**
     * 设置显示列表的值
     *
     * @param textView 文本控件
     * @param rowBean  内容对象
     * @param isColor  是否显示解析颜色
     */
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

        //附件、子表、图片 不显示
        boolean isSubform = name.startsWith(CustomConstants.SUBFORM);
        boolean isPicture = name.startsWith(CustomConstants.PICTURE);
        boolean isAttachment = name.startsWith(CustomConstants.ATTACHMENT);
        if (isSubform || isPicture || isAttachment) {
            return;
        }

        StringBuilder text = new StringBuilder();
        String color = null;
        if (name.startsWith(CustomConstants.PERSONNEL) || name.startsWith(CustomConstants.REFERENCE) || name.startsWith(CustomConstants.DEPARTMENT)) {
            //人员、关联
            text.append(getJsonArrayValue("", value, "name"));
        } else if (name.startsWith(CustomConstants.PICKLIST) || name.startsWith(CustomConstants.MULTI) || name.startsWith(CustomConstants.MUTLI_PICKLIST)) {
            //下拉
            text.append(getJsonArrayValue("", value, "label"));
            if (isColor) {
                String[] split = getJsonArrayValue("", value, "color").split(",");
                if (split.length > 0) {
                    color = split[0];
                }
            }
        } else if (name.startsWith(CustomConstants.DATETIME)) {
            //时间
            String format = rowBean.getOther();
            if (TextUtil.isEmpty(format)) {
                format = "yyyy-MM-dd";
            }
            long time = TextUtil.parseLong(value);
            String s = DateTimeUtil.longToStr(time, format);
            text.append(s);
        } else if (name.startsWith(CustomConstants.AREA)) {
            //省市区
            text.append(RegionUtil.code2String(value));
        } else if (name.startsWith(CustomConstants.LOCATION)) {
            //定位
            text.append(getJsonObjectValue(value, "value"));
        } else {
            text.append(value);
        }

        TextUtil.setText(textView, text.toString());
        boolean checkColor = ColorUtils.checkColor(color);
        //是否要显示颜色
        if (isColor) {
            //是否有颜色 且 不能是白色
            if (checkColor && !color.toUpperCase().startsWith("#FFFFFF")) {
                //textView.setBackgroundColor(ColorUtils.hexToColor(color));
                if (drawable != null) {
                    drawable.setColor(ColorUtils.hexToColor(color));
                }
                textView.setTextColor(ColorUtils.hexToColor("#FFFFFF"));
            } else {
                //textView.setBackgroundColor(ColorUtils.hexToColor("#FFFFFF"));
                if (drawable != null) {
                    drawable.setColor(ColorUtils.hexToColor("#FFFFFF"));
                }
                textView.setTextColor(ColorUtils.hexToColor("#4a4a4a"));
            }
        }
    }

    public static List<Member> getMmeberValue(RowDataBean rowBean) {
        List<Member> list = new ArrayList<>();
        Object value = rowBean.getValue();
        String jsonString = JSONObject.toJSONString(value);
        if (rowBean.getValue() instanceof List) {
            try {
                List<Member> memberList = (List<Member>) rowBean.getValue();
                if (memberList != null && memberList.size() > 0) {
                    list.addAll(memberList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public static String getTextValue(RowDataBean rowBean) {
        String name = rowBean.getName();
        StringBuilder sb = new StringBuilder();
        String label = rowBean.getLabel();
        String jsonString = JSONObject.toJSONString(rowBean.getValue());
        org.json.JSONObject jo = null;
        org.json.JSONArray ja = null;
        if (!TextUtils.isEmpty(name)) {
            name = name.substring(0, name.indexOf("_"));
            switch (name) {
                case CustomConstants.IDENTIFIER:
                case CustomConstants.SERIAL_NUMBER:
                case CustomConstants.TEXT:
                case CustomConstants.TEXTAREA:
                case CustomConstants.AREA:
                case CustomConstants.NUMBER:
                case CustomConstants.PHONE:
                case CustomConstants.EMAIL:
                case CustomConstants.MAIL_BOX_SCOPE:
                case CustomConstants.URL:
                case CustomConstants.DEPARTMENT:
                    sb.append(label + ":" + rowBean.getValue());
                    break;
                case CustomConstants.PERSONNEL:
                    sb.append(label + ":");
                    try {
                        ja = new org.json.JSONArray(jsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (ja != null) {
                        for (int i = 0; i < ja.length(); i++) {
                            sb.append(ja.optJSONObject(i).optString("name") + ",");
                        }
                        if (sb.toString().endsWith(",")) {
                            sb = new StringBuilder(sb.toString().substring(0, sb.length() - 1));
                        }
                    }
                    break;
                case MsgConstant.TYPE_PICKLIST:
                    sb.append("[下拉列表]");
                    break;
                case MsgConstant.TYPE_DATETIME:
                    sb.append(label + ":" + DateTimeUtil.longToStr(TextUtil.parseLong(rowBean.getValue() + ""), "yyyy-MM-dd H:mm"));
                    break;
                case CustomConstants.RICH_TEXT:
                    //富文本
                    sb.append(label + ":" + Html.fromHtml(rowBean.getValue() + ""));
                    break;
                case CustomConstants.LOCATION:
                    // TODO: 2018/6/22 位置信息解析
                    sb.append("[位置]");
                    break;
                case CustomConstants.REFERENCE:
                    sb.append("[引用]");
                    break;
                case CustomConstants.PICTURE:
                    sb.append("[图片]");
                    break;
                case CustomConstants.FORMULA:
                    sb.append("[公式]");
                    break;
                case CustomConstants.FUNCTION_FORMULA:
                case CustomConstants.REFERENCE_FORMULA:
                    sb.append("[函数公式]");
                    break;
                case CustomConstants.SENIOR_FORMULA:
                    sb.append("[高级公式]");
                    break;
                case CustomConstants.SUBFORM:
                    sb.append("[子表单]");
                    break;
                default:
                    sb.append(label + ":" + jo.optString("value"));
                    break;
            }
            return sb.toString();
        } else {
            return sb.toString();
        }
    }

    /**
     * 获取人员类型数据
     *
     * @param rowBean
     * @return
     */
    public static List<Member> getMemberValue(RowDataBean rowBean) {
        String name = rowBean.getName();
        StringBuilder sb = new StringBuilder();
        String label = rowBean.getLabel();
        String jsonString = JSONObject.toJSONString(rowBean.getValue());
        org.json.JSONArray ja = null;
        List<Member> list = new ArrayList<>();

        if (!TextUtils.isEmpty(name) && name.startsWith(CustomConstants.PERSONNEL)) {
            try {
                ja = new org.json.JSONArray(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (ja != null) {
                for (int i = 0; i < ja.length(); i++) {
                    try {
                        final org.json.JSONObject jsonObject = ja.getJSONObject(i);
                        Member m = new Member();
                        m.setName(jsonObject.optString("name"));
                        m.setPost_name(jsonObject.optString("post_name"));
                        m.setId(jsonObject.optLong("id"));
                        m.setPicture(jsonObject.optString("picture"));
                        list.add(m);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }


    /**
     * 格式化数字与公式
     *
     * @param fieldBean
     * @param text
     * @return
     */
    public static boolean formatNumber(CustomBean.FieldBean fieldBean, String value, StringBuilder text, String type) {
        RowBean rowBean = new RowBean();
        rowBean.setField_param(fieldBean);
        rowBean.setValue(value);
        rowBean.setName(type);
        return formatNumber(rowBean, text);

    }

    public static boolean formatNumber(RowBean rowBean, StringBuilder text) {
        //数字类型：0数字、1整数、2百分比、3文本、4日期
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
            //小数或百分比
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
}

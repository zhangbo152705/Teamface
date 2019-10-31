package com.hjhq.teamface.basis.util;


import android.text.TextUtils;
import android.widget.TextView;

import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.FieldInfoBean;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.bean.SortToken;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.MsgConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * @author Administrator
 * @date 2017/11/23
 */

public class ParseUtil {
    /**
     * 中文字符串匹配
     */
    public static final String chReg = "[\\u4E00-\\u9FA5]+";

    /**
     * 解析sort_key,封装简拼,全拼
     *
     * @param sortKey
     * @return
     */
    public static SortToken parseSortKey(String sortKey) {
        SortToken token = new SortToken();
        if (sortKey != null && sortKey.length() > 0) {
            //其中包含的中文字符
            String[] enStrs = sortKey.replace(" ", "").split(chReg);
            for (int i = 0, length = enStrs.length; i < length; i++) {
                if (enStrs[i].length() > 0) {
                    //拼接简拼
                    token.simpleSpell += enStrs[i].charAt(0);
                    token.wholeSpell += enStrs[i];
                }
            }
        }
        return token;
    }

    /**
     * 解析sort_key,封装简拼,全拼。
     * Android 5.0 以上使用
     *
     * @param sortKey
     * @return
     */
    public static SortToken parseSortKeyLollipop(String sortKey) {
        SortToken token = new SortToken();
        if (sortKey != null && sortKey.length() > 0) {
            boolean isChinese = sortKey.matches(chReg);
            // 分割条件：中文不分割，英文以大写和空格分割
            String regularExpression = isChinese ? "" : "(?=[A-Z])|\\s";

            String[] enStrs = sortKey.split(regularExpression);

            for (int i = 0, length = enStrs.length; i < length; i++)
                if (enStrs[i].length() > 0) {
                    //拼接简拼
                    token.simpleSpell += getSortLetter(String.valueOf(enStrs[i].charAt(0)));
                    token.wholeSpell += CharacterParser.getInstance().getSelling(enStrs[i]);
                }
        }
        return token;
    }


    /**
     * 名字转拼音,取首字母
     *
     * @param name
     * @return
     */
    public static String getSortLetter(String name) {
        String letter = "#";
        if (name == null) {
            return letter;
        }
        //汉字转换成拼音
        String pinyin = CharacterParser.getInstance().getSelling(name);
        String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /**
     * 取sort_key的首字母
     *
     * @param sortKey
     * @return
     */
    public static String getSortLetterBySortKey(String sortKey) {
        String letter = "#";
        if (sortKey == null || "".equals(sortKey.trim())) {
            return letter;
        }
        //汉字转换成拼音
        String sortString = sortKey.trim().substring(0, 1).toUpperCase(Locale.CHINESE);
        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        } else
//			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {// 5.0以上需要判断汉字
            if (sortString.matches("^[\u4E00-\u9FFF]+$"))// 正则表达式，判断是否为汉字
                letter = getSortLetter(sortString.toUpperCase(Locale.CHINESE));
//		}
        return letter;
    }

    /**
     * 从自定义的数据获取格式化的字符串
     *
     * @param list
     * @return
     */
    public static String getStringValue(List<FieldInfoBean> list) {
        if (list == null || list.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            FieldInfoBean item = list.get(i);
            if (item == null || (TextUtils.isEmpty(item.getField_label()) && TextUtils.isEmpty(item.getField_value()))) {
                continue;
            }
            switch (item.getType()) {
                case CustomConstants.IDENTIFIER:
                case CustomConstants.SERIAL_NUMBER:
                case CustomConstants.TEXT:
                case CustomConstants.TEXTAREA://zzh:增加多行文本限制字符长度为20个字符
                    String textareaValue = item.getField_label() + ":" + item.getField_value();
                    if (!TextUtil.isEmpty(textareaValue) && textareaValue.length()>17){
                        textareaValue = textareaValue.substring(0,17)+"...";
                    }
                    textareaValue = textareaValue.replaceAll("\n","");
                    sb.append(textareaValue);
                    break;
                case CustomConstants.BARCODE:
                case CustomConstants.PHONE:
                case CustomConstants.EMAIL:
                case CustomConstants.MAIL_BOX_SCOPE:
                case CustomConstants.URL:
                case CustomConstants.PERSONNEL:
                    sb.append(item.getField_label() + ":" + item.getField_value());
                    break;
                case CustomConstants.DEPARTMENT:
                    //部门
                    String departmentValue = item.getField_value();
                    if (TextUtils.isEmpty(departmentValue)) {
                        sb.append(item.getField_label() + ":");
                    } else {
                        try {
                            JSONArray ja = new JSONArray(departmentValue);
                            StringBuilder departmentSb = new StringBuilder();
                            for (int k = 0; k < ja.length(); k++) {
                                org.json.JSONObject jb = ((org.json.JSONObject) ja.get(k));
                                if (TextUtils.isEmpty(departmentSb)) {
                                    departmentSb.append(jb.optString("name"));
                                } else {
                                    departmentSb.append("、");
                                    departmentSb.append(jb.optString("name"));
                                }

                            }
                            sb.append(item.getField_label() + ":" + departmentSb.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            sb.append(item.getField_label() + ":");
                        }
                    }
                    break;
                case CustomConstants.AREA:
                    //省市区
                    String area = item.getField_value();
                    if (TextUtils.isEmpty(area)) {
                        sb.append(item.getField_label() + ":");
                    } else {
                        final String[] split = area.split(",");
                        StringBuilder areaSb = new StringBuilder();
                        if (split.length > 0) {
                            for (String s : split) {
                                final String[] split1 = s.split(":");
                                if (split1.length == 2) {
                                    if (TextUtils.isEmpty(areaSb)) {
                                        areaSb.append(split1[1]);
                                    } else {
                                        areaSb.append(",");
                                        areaSb.append(split1[1]);
                                    }
                                }
                            }

                        }
                        sb.append(item.getField_label() + ":" + areaSb.toString());
                    }
                    break;
                //下拉/复选
                case MsgConstant.TYPE_PICKLIST:
                case CustomConstants.MUTLI_PICKLIST:
                case CustomConstants.MULTI:
                    String field_value = item.getField_value();
                    if (TextUtils.isEmpty(field_value)) {
                        sb.append(item.getField_label() + ":");
                    } else {
                        StringBuilder filedValue = new StringBuilder("");
                        try {
                            JSONArray ja = new JSONArray(field_value);
                            for (int k = 0; k < ja.length(); k++) {
                                org.json.JSONObject jb = ((org.json.JSONObject) ja.get(k));
                                if (TextUtils.isEmpty(filedValue)) {
                                    filedValue.append(jb.optString("label"));
                                } else {
                                    filedValue.append("、");
                                    filedValue.append(jb.optString("label"));
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sb.append(item.getField_label() + ":" + filedValue.toString());
                    }
                    break;
                case MsgConstant.TYPE_DATETIME:
                    sb.append(item.getField_label() + ":");
                    String value = item.getField_value();
                    long time = TextUtil.parseLong(value, 0L);
                    if (time > 0) {
                        final String fieldString = com.alibaba.fastjson.JSONObject.toJSONString(item.getField());
                        if (!TextUtils.isEmpty(fieldString)) {
                            CustomBean.FieldBean field = com.alibaba.fastjson.JSONObject.parseObject(fieldString, CustomBean.FieldBean.class);
                            if (field != null) {
                                String datetime = DateTimeUtil.longToStr(time, field.getFormatType());
                                sb.append(datetime);
                            } else {
                                String datetime = DateTimeUtil.longToStr(time, "yyyy-MM-dd HH:mm");
                                sb.append(datetime);
                            }
                        }
                    }
                    break;
                case CustomConstants.RICH_TEXT:
                    //富文本
                    String content = item.getField_value();
                    sb.append(item.getField_label() + "" + TextUtil.processHtmlText(content, ""));
                    break;
                case CustomConstants.LOCATION:
                    try {
                        String locationValue = item.getField_value();
                        JSONObject jb = new JSONObject(locationValue);
                        final Object value1 = jb.opt("value");
                        if (value1 == null) {
                            sb.append(item.getField_label() + ":");
                        } else {
                            sb.append(item.getField_label() + ":" + jb.opt("value"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case CustomConstants.REFERENCE:
                    String field_value2 = item.getField_value();
                    StringBuilder filedValue2 = new StringBuilder();
                    if (TextUtils.isEmpty(field_value2)) {
                        sb.append(item.getField_label() + ":");
                    } else {
                        try {
                            JSONArray ja = new JSONArray(field_value2);
                            for (int k = 0; k < ja.length(); k++) {
                                org.json.JSONObject jb = ((org.json.JSONObject) ja.get(k));
                                if (!TextUtils.isEmpty(filedValue2)) {
                                    filedValue2.append("," + jb.optString("name"));
                                } else {
                                    filedValue2.append(jb.optString("name"));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sb.append(item.getField_label() + ":" + filedValue2.toString());
                    }
                    break;
                case CustomConstants.PICTURE:
                    sb.append(item.getField_label() + ":[图片]");
                case CustomConstants.ATTACHMENT:
                    sb.append(item.getField_label() + ":[附件]");
                    break;
                case CustomConstants.NUMBER:
                case CustomConstants.FORMULA:
                case CustomConstants.FUNCTION_FORMULA:
                case CustomConstants.REFERENCE_FORMULA:
                case CustomConstants.SENIOR_FORMULA:
                    sb.append(item.getField_label() + ":");
                    final String fieldString = com.alibaba.fastjson.JSONObject.toJSONString(item.getField());
                    if (!TextUtils.isEmpty(fieldString)) {
                        CustomBean.FieldBean field = com.alibaba.fastjson.JSONObject.parseObject(fieldString, CustomBean.FieldBean.class);
                        if (field != null) {
                            CustomDataUtil.formatNumber(field, item.getField_value(), sb, item.getType());
                        } else {
                            sb.append(item.getField_value());
                        }

                    } else {
                        sb.append(item.getField_value());
                    }
                    break;
                case CustomConstants.SUBFORM:
                    sb.append(item.getField_label() + ":[子表单]");
                    break;
                default:
                    sb.append(item.getField_label() + ":" + item.getField_value());
                    break;
            }
            if (i < list.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 将字节数组转为long
     *
     * @param b
     * @return
     */
    public static long bytes2Long(byte[] b) {
        long s = 0;
        try {
            for (int i = 0; i < 7; ++i) {
                if (b[7 - i] >= 0) {
                    s = s + b[7 - i];
                } else {
                    s = s + 256 + b[7 - i];
                }

                s = s * 256;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (b[0] >= 0) {
            s = s + b[0];
        } else {
            s = s + 256 + b[0];
        }
        return s;
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
            text.append(getJsonArrayValue(value, "name"));
        } else if (name.startsWith(CustomConstants.PICKLIST) || name.startsWith(CustomConstants.MULTI) || name.startsWith(CustomConstants.MUTLI_PICKLIST)) {
            //下拉
            text.append(getJsonArrayValue(value, "label"));
            if (isColor) {
                String[] split = getJsonArrayValue(value, "color").split(",");
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
                textView.setBackgroundColor(ColorUtils.hexToColor(color));
                textView.setTextColor(ColorUtils.hexToColor("#FFFFFF"));
            } else {
                textView.setBackgroundColor(ColorUtils.hexToColor("#FFFFFF"));
                textView.setTextColor(ColorUtils.hexToColor("#4a4a4a"));
            }
        }
    }

    /**
     * 解析 JSONArray的json字符串
     *
     * @param json  json字符串
     * @param field 解析字段
     * @return 字段的值
     */
    private static String getJsonArrayValue(String json, String field) {
        StringBuilder text = new StringBuilder();
        if (TextUtil.isEmpty(json) || TextUtil.isEmpty(field)) {
            return text.toString();
        }
        try {
            com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(json);
            if (!CollectionUtils.isEmpty(jsonArray)) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                    text.append(jsonObject.getString(field) + ",");
                }
                if (!TextUtil.isEmpty(text)) {
                    text.deleteCharAt(text.length() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text.toString();
    }


    /**
     * 解析 JSONObject
     *
     * @param json  json字符串
     * @param field 解析字段
     * @return 字段的值
     */
    private static String getJsonObjectValue(String json, String field) {
        String text = "";
        if (TextUtil.isEmpty(json) || TextUtil.isEmpty(field)) {
            return text;
        }
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(json);
        if (jsonObject != null) {
            text = jsonObject.getString(field);
        }
        return text;
    }
}

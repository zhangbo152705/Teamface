package com.hjhq.teamface.basis.util;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hjhq.teamface.basis.listener.LinkClickableSpan;

import org.json.JSONArray;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 文本工具类
 *
 * @author lx
 * @date 2017/6/30
 */

public class TextUtil {
    /**
     * 判断文本内容是否为null或空字符串
     *
     * @param str 文本
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 为文本控件填充内容
     *
     * @param view 文本控件
     * @param str  文本
     */
    public static void setText(TextView view, CharSequence str) {
        if (str != null && view != null) {
            view.setText(str);
        }
    }

    /**
     * 为文本控件填充内容
     *
     * @param view 文本控件
     * @param num  数字
     */
    public static void setText(TextView view, Integer num) {
        if (view != null) {
            view.setText(num + "");
        }
    }

    /**
     * 为文本控件填充内容
     *
     * @param view 文本控件
     * @param num  数字
     */
    public static void setTextRes(TextView view, @StringRes int num) {
        if (num != 0 && view != null) {
            view.setText(view.getContext().getResources().getString(num));
        }
    }

    /**
     * 为文本控件填充内容
     *
     * @param view 文本控件
     * @param num  数字
     */
    public static void setText(TextView view, Long num) {
        if (view != null) {
            view.setText(num + "");
        }
    }

    /**
     * 为文本控件填充内容
     *
     * @param view 文本控件
     * @param num  数字
     */
    public static void setText(TextView view, Float num) {
        if (view != null) {
            view.setText(num + "");
        }
    }

    /**
     * 为文本控件填充内容
     *
     * @param view 文本控件
     * @param num  数字
     */
    public static void setText(TextView view, Double num) {
        if (view != null) {
            view.setText(num + "");
        }
    }

    /**
     * 为文本控件填充非空内容
     *
     * @param view 文本控件
     * @param str  文本
     */
    public static void setNonEmptyText(TextView view, String str) {
        if (!isEmpty(str) && view != null) {
            view.setText(str);
        }
    }

    /**
     * 为编辑控件填充内容
     *
     * @param view 编辑控件
     * @param str  文本
     */
    public static void setText(EditText view, String str) {
        if (str != null && view != null) {
            view.setText(str);
        }
    }

    /**
     * 为编辑控件填充内容
     *
     * @param view 编辑控件
     * @param str  文本
     */
    public static void setTextDouble(TextView view, String str) {
        if (str != null && view != null) {
            view.setText(str);
        }
    }

    /**
     * 设置内容，没有就隐藏TextView
     *
     * @param view 文本控件
     * @param str  文本
     */
    public static void setTextorVisibility(TextView view, CharSequence str) {
        if (view == null) {
            return;
        }
        if (isEmpty(str)) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            setText(view, str);
        }
    }

    /**
     * 为文本控件设置提示
     *
     * @param view 文本控件
     * @param hint 提示
     */
    public static void setHint(TextView view, String hint) {
        if (hint != null && view != null) {
            view.setHint(hint);
        }
    }

    /**
     * 返回非空的值
     *
     * @param val1 值1
     * @param val2 值2
     */
    public static String getNonNull(String val1, String val2) {
        if (val1 != null) {
            return val1;
        }
        if (val2 != null) {
            return val2;
        }
        return "";
    }

    /**
     * 比较两个值是否一样，都为null返回false
     *
     * @param str String类型
     * @param i   Integer类型
     */
    public static boolean equals(String str, Integer i) {
        if (i == null || str == null) {
            return false;
        } else if (str.equals(i + "")) {
            return true;
        }
        return false;
    }

    /**
     * 比较两个值是否一样，都为null返回false
     *
     * @param str  String类型
     * @param str2 String类型
     */
    public static boolean equals(String str, String str2) {
        if (str2 == null || str == null) {
            return false;
        } else if (str.equals(str2)) {
            return true;
        }
        return false;
    }

    /**
     * 将文本解析成int类型数字，默认为0
     *
     * @param str 文本
     * @return 数字
     */
    public static int parseInt(String str) {
        return parseInt(str, 0);
    }

    /**
     * 将文本解析成int类型数字
     *
     * @param str 文本
     * @param def 默认数字
     * @return 数字
     */
    public static int parseInt(String str, int def) {
        try {
            if (!TextUtil.isEmpty(str)) {
                def = Integer.parseInt(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    /**
     * 将文本解析成long类型数字，默认为0
     *
     * @param str 文本
     * @return 数字
     */
    public static long parseLong(String str) {
        return parseLong(str, 0L);
    }

    /**
     * 将文本解析成long类型数字
     *
     * @param str 文本
     * @param def 默认数字
     * @return 数字
     */
    public static long parseLong(String str, Long def) {
        if (isEmpty(str)) {
            return def;
        }
        try {
            def = Long.parseLong(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    /**
     * 将文本解析成double类型数字，默认为0
     *
     * @param str 文本
     * @return 数字
     */
    public static double parseDouble(String str) {
        return parseDouble(str, 0);
    }

     /**
      * 判断是否为浮点数，包括double和float
      * @param str 传入的字符串
      * @return 是浮点数返回true,否则返回false
      */
    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断字符串是否可以转化为JSON数组
     * @param content
     * @return
     */
    public static boolean isJsonArray(String content) {
        try {
            JSONArray jsonStr = new JSONArray(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 将字符串解析为double类型数字字符串
     *
     * @param str
     * @return
     */
    public static String parseDoubleText(String str) {
        double def;
        try {
            def = Double.parseDouble(str);
            return def + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将字符串解析为整型数字字符串
     *
     * @param str
     * @return
     */
    public static String parseIntText(String str) {
        double def;
        try {
            def = Double.parseDouble(str);
            return ((int) def) + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将文本解析成double类型数字
     *
     * @param str 文本
     * @param def 默认数字
     * @return 数字
     */
    public static double parseDouble(String str, double def) {
        try {
            def = Double.parseDouble(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    /**
     * 根据关键字匹配内容
     *
     * @param keyword 关键字
     * @param content 内容
     * @return
     */
    public static SpannableString getSpannableString(String keyword, String content) {
        if (TextUtils.isEmpty(keyword)) {
            if (TextUtils.isEmpty(content)) {
                return new SpannableString("");
            }
            return new SpannableString(content);
        }
        SpannableString ss = new SpannableString(content);
        Pattern p = Pattern.compile(keyword);
        Matcher matcher = p.matcher(content);
        while (matcher.find()) {
            String str = matcher.group();
            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#3689E9"));
            ss.setSpan(foregroundColorSpan, matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    public static SpannableString getClickSpannableString(Context context, String keyword, String content) {
        SpannableString ss = new SpannableString(content);
        if (!TextUtils.isEmpty(keyword)) {
            Pattern p = Pattern.compile(keyword);
            Matcher matcher = p.matcher(content);
            while (matcher.find()) {
                String str = matcher.group();
                int matcherStart = matcher.start();
                int matcherEnd = matcher.end();
//                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#3689E9"));
//                ss.setSpan(foregroundColorSpan, matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ClickableSpan clickableSpan = new LinkClickableSpan(context);
                ss.setSpan(clickableSpan, matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ss;
    }

    /**
     * 根据关键字匹配内容
     *
     * @param keyword 关键字
     * @param content 内容
     * @param color   颜色
     * @return
     */
    public static SpannableString getSpannableString(String keyword, String content, @ColorInt int color) {
        if (TextUtils.isEmpty(keyword)) {
            if (TextUtils.isEmpty(content)) {
                return new SpannableString("");
            }
            return new SpannableString(content);
        }
        SpannableString ss = new SpannableString(content);
        if (!TextUtils.isEmpty(keyword)) {
            Pattern p = Pattern.compile(keyword);
            Matcher matcher = p.matcher(content);
            while (matcher.find()) {
                String str = matcher.group();
                int matcherStart = matcher.start();
                int matcherEnd = matcher.end();
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(color);
                ss.setSpan(foregroundColorSpan, matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ss;
    }

    /**
     * 将double类型字符串转换成int类型字符串
     *
     * @param text
     * @return
     */
    public static String doubleParseInt(String text) {
        double v = parseDouble(text, -99899);
        if (v != -99899) {
            text = ((int) v) + "";
        }
        return text;
    }

    /**
     * 将html转为文本
     *
     * @param content     html内容
     * @param defaultText html内容为空时的返回值
     * @return
     */
    public static String processHtmlText(String content, String defaultText) {
        if (TextUtils.isEmpty(content)) {
            return defaultText;
        } else {
            content = Html.fromHtml(content).toString();
            //web端添加的标记
            content = content.replace("++--+!!!+--++", "");
            if (content.length() > 25) {
                content = content.substring(0, 25);
            }
            content = content.replace("  ", "").replace("\n", "").replace("\r\n", "").replace("\t", "");
            content = content.replace("￼", "[图]");
            return content;
        }
    }

    /**
     * 判断字符串是否为IP
     * @param content
     * @return
     */
    public static boolean isIp(String content){
        boolean res = false;
       if(!TextUtil.isEmpty(content) && content.startsWith("http")){
           res=Patterns.WEB_URL.matcher(content).matches();
       }
        return res;
    }

    /**
     * 判断字符串是否为域名
     * @param content
     * @return
     */
    public static boolean isDomain(String content){
        boolean res = false;
        if(!TextUtil.isEmpty(content)){
            res = Patterns.DOMAIN_NAME.matcher(content).matches();
        }
        return res;
    }

}

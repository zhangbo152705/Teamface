package com.hjhq.teamface.customcomponent.widget2.input;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.base.InputCommonView;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 公式组件
 *
 * @author lx
 * @date 2017/8/23
 */

public class FormulaView extends InputCommonView {
    private String numberType;//类型(0数字 1整数 2百分比)
    /**
     * 百分比 小数位
     */
    private int decimalLen;//小数位
    private int numberDelimiter;//分隔符 1:千分位分隔符  2:万分位分隔符

    public FormulaView(CustomBean bean) {
        super(bean);
        CustomBean.FieldBean field = bean.getField();
        if (field != null) {
            decimalLen = TextUtil.parseInt(field.getDecimalLen(), 0);
            numberType = field.getNumberType();
            numberDelimiter = TextUtil.parseInt(field.getNumberDelimiter());
            if (decimalLen < 1 || decimalLen > 4) {
                decimalLen = 2;
            }
        }
    }

    @Override
    public int getLayout() {
        if ("0".equals(structure)) {
            return R.layout.custom_input_single_widget_layout;
        } else {
            return R.layout.custom_input_single_widget_row_layout;
        }
    }

    @Override
    public void setVisibility(int visibility) {
        //在新增和编辑状态中，不能改变其状态
        if (CustomConstants.ADD_STATE == state || CustomConstants.EDIT_STATE == state) {
            if (visibility == View.VISIBLE) {
                return;
            }
        }
        super.setVisibility(visibility);
    }

    @Override
    public void initOption() {
        //类型：0数字、1整数、2百分比、3文本、4日期
        ImageView ivLeft = mView.findViewById(R.id.iv_left);
        ivLeft.setVisibility(View.VISIBLE);
        if (CustomConstants.SENIOR_FORMULA.equals(type)) {
            setLeftImage(R.drawable.custom_icon_formula);
        } else {
            setLeftImage(R.drawable.custom_icon_function);
        }
        etInput.setClickable(false);
        etInput.setFocusable(false);

        if (CustomConstants.ADD_STATE == state || CustomConstants.EDIT_STATE == state){//新建和编辑状态
            TextUtil.setText(etInput,mContext.getResources().getString(R.string.formula_save));
            return;
        }
        //详情状态
        String contentStr = etInput.getText().toString();
        int numberTypeInt = TextUtil.parseInt(bean.getField().getNumberType());
        int numberLenth = TextUtil.parseInt(bean.getField().getDecimalLen());
        if (numberTypeInt == 0 || numberTypeInt == 2) {
            if (TextUtils.isEmpty(contentStr)) {
                contentStr ="0";
            }
            int length = contentStr.length();
            //小数或百分比
            int pointIndex = contentStr.indexOf(".");
            int size;
            if (pointIndex == -1 && numberLenth>0) {//zzh:增加判断小数位大于0
                contentStr += ".";
                size = numberLenth;
            } else {
                size = numberLenth - (length - pointIndex - 1);
            }
            for (int j = 0; j < size; j++) {
                contentStr += "0";
            }
            if (2 == numberTypeInt) {
                TextUtil.setText(etInput, contentStr + "%");
            } else if (0 == numberTypeInt) {
                TextUtil.setText(etInput, contentStr);
            } else {
                TextUtil.setText(etInput, TextUtil.parseIntText(contentStr));
            }
        }else if (1 == numberTypeInt){//zzh:增加整数判断
            if (TextUtils.isEmpty(contentStr)) {
                etInput.setText("0");
            }else {
                if (contentStr.indexOf(".") != -1){
                    contentStr = contentStr.substring(0,contentStr.indexOf("."));
                }
                TextUtil.setText(etInput, contentStr);
            }
        } else if (3 == numberTypeInt) {
            TextUtil.setText(etInput, contentStr);
        } else if (4 == numberTypeInt) {
            final String date = DateTimeUtil.longToStr(TextUtil.parseLong(contentStr), bean.getField().getChooseType());
            TextUtil.setText(etInput, date);
        }
    }

    @Override
    public void put(JSONObject jsonObj) {
        if (jsonObj != null) {
            try {
                jsonObj.remove(keyName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean formatCheck() {
        return true;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    @Override
    public void setContent(String content) {
        if (content == null) {
            content = "";
        }
        if (!TextUtil.isEmpty(content) && decimalLen > 0) {
            if ("0".equals(numberType)) {
                String formatStr = "0.";
                for (int i = 0; i < decimalLen; i++) {
                    formatStr = formatStr + "0";
                }
                content = new DecimalFormat(formatStr).format(TextUtil.parseDouble(content));
                content = getSpliteNumber(content);//zzh:增加分隔符
            } else if ("2".equals(numberType)) {
                //百分比
                //content += "%";
            }
        }
        //整形去除小数点
        if ("1".equals(numberType) && !TextUtils.isEmpty(content)) {
            content = TextUtil.doubleParseInt(content);
            content = getSpliteNumber(content);//zzh:增加分隔符
        }
        TextUtil.setText(etInput, content);
    }


    /**
     * 给数字增加分割符
     */
    public String getSpliteNumber(String content){
        if (numberDelimiter == 1){
            content = splite(1,content);
        }else if (numberDelimiter == 2){
            content = splite(2,content);
        }
        return content;
    }

    public String splite(int type,String content){
        if (type == 1){
            if (content.indexOf(".") == -1){//整数
                long def = 0;
                if (TextUtil.parseLong(content,def)>0){
                    def = TextUtil.parseLong(content,def);
                    DecimalFormat df4 = new DecimalFormat("#,###");
                    content = df4.format(new BigDecimal(def));
                }
            }else {//小数
                String numContent = content.substring(0,content.indexOf("."));
                long def = 0;
                if (TextUtil.parseLong(numContent,def)>0){
                    def = TextUtil.parseLong(numContent,def);
                    DecimalFormat df4 = new DecimalFormat("#,###");
                    numContent = df4.format(new BigDecimal(def));
                    content = numContent+content.substring(content.indexOf("."),content.length());
                }
            }
        }else if (type ==2){
            if (content.indexOf(".") == -1){//整数
                long def = 0;
                if (TextUtil.parseLong(content,def)>0){
                    def = TextUtil.parseLong(content,def);
                    DecimalFormat df4 = new DecimalFormat("#,####");
                    content = df4.format(new BigDecimal(def));
                }
            }else {//小数
                String numContent = content.substring(0,content.indexOf("."));
                long def = 0;
                if (TextUtil.parseLong(numContent,def)>0){
                    def = TextUtil.parseLong(numContent,def);
                    DecimalFormat df4 = new DecimalFormat("#,####");
                    numContent = df4.format(new BigDecimal(def));
                    content = numContent+content.substring(content.indexOf("."),content.length());
                }
            }
        }
        return  content;
    }
}

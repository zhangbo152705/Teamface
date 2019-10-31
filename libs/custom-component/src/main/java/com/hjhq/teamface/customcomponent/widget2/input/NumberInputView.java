package com.hjhq.teamface.customcomponent.widget2.input;

import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.base.InputCommonView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 数值组件
 *
 * @author lx
 * @date 2017/8/23
 */
public class NumberInputView extends InputCommonView {
    private int numberType;//数字类型：0数字、1整数、2百分比
    private int betweenMin;//范围最小值
    private int betweenMax;//范围最大值
    private int numberLenth;//小数位  位数(1 2 3 4)
    private Pattern mPattern;
    private int numberDelimiter;

    public NumberInputView(CustomBean bean) {
        super(bean);
        CustomBean.FieldBean field = bean.getField();
        if (field != null) {
            numberType = TextUtil.parseInt(field.getNumberType());
            betweenMin = TextUtil.parseInt(field.getBetweenMin(), Integer.MIN_VALUE);
            betweenMax = TextUtil.parseInt(field.getBetweenMax(), Integer.MAX_VALUE);
            numberLenth = TextUtil.parseInt(field.getNumberLenth());
            numberDelimiter = TextUtil.parseInt(field.getNumberDelimiter());
            if (numberLenth < 1 || numberLenth > 4) {
                numberLenth = 2;
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
    public void initOption() {
        setLeftImage(R.drawable.custom_icon_number);
        if (1 == numberType) {//整数
            mPattern = Pattern.compile("([0-9])*");
            etInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            if (numberType == 2) {
                if (state == 2 || state == 3) {
                    ImageView ivRight = mView.findViewById(R.id.iv_right);
                    ivRight.setVisibility(View.VISIBLE);
                    ivRight.setImageResource(R.drawable.custom_icon_percent2);
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append(1);
            if (numberLenth != 1) {
                sb.append("," + numberLenth);
            }
            etInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            mPattern = Pattern.compile("(([,0-9]*)|([,0-9]*\\.)|([,0-9]*\\.\\d{" + sb.toString() + "}))");
            String contentStr = etInput.getText().toString();

            if (TextUtils.isEmpty(contentStr) && state == CustomConstants.ADD_STATE) {
                contentStr = defaultValue;
            }
            if (TextUtils.isEmpty(contentStr)) {
                return;
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
            if (size <0 && length>=pointIndex+numberLenth+1){
                contentStr = contentStr.substring(0,pointIndex+numberLenth+1);
            }

            //Log.e("contentStr>>>", contentStr + "<<<");
            if (2 == numberType) {
                if (state == 0 || state == 2) {
                    ImageView ivRight = mView.findViewById(R.id.iv_right);
                    ivRight.setVisibility(View.VISIBLE);
                    ivRight.setImageResource(R.drawable.custom_icon_percent2);
                    etInput.setText(contentStr);
                } else if (state == 4) {
                    etInput.setText(contentStr + "%");
                }

            } else if (0 == numberType) {
                etInput.setText(contentStr);
            }
        }
        InputFilter[] filter = {(source, start, end, dest, dstart, dend) -> {
            String sourceText = source.toString();
            //验证删除等按键
            if (TextUtils.isEmpty(sourceText)) {
                return "";
            }
            String text = dest.subSequence(0, dstart) + sourceText + dest.subSequence(dend, dest.length());
            Matcher matcher = mPattern.matcher(text);
            if (!matcher.matches()) {
               return "";
            }

            return source;
        }};
        etInput.setFilters(filter);

    }

    @Override
    public Object getValue() {
        String content = etInput.getText().toString().trim();
        int length = content.length();
        if (length == 0) {
            return "";
        }
        if (content.indexOf(",") != -1){
            content = content.replaceAll(",","").trim();//去除数字里的逗号分割符
        }

        if (1 != numberType) {
            //小数或百分比
            int pointIndex = content.indexOf(".");
            int size;
            if (pointIndex == -1) {
                content += ".";
                size = numberLenth;
            } else {
                size = numberLenth - (length - pointIndex - 1);
            }
            for (int j = 0; j < size; j++) {
                content += "0";
            }
        }
        return content;
    }

    @Override
    public boolean formatCheck() {
        String content = getContent();
        if (TextUtil.isEmpty(content)) {
            return true;
        }
        if (content.indexOf(",") != -1){
            content = content.replaceAll(",","").trim();//去除数字里的逗号分割符
        }
        double number = TextUtil.parseDouble(content);
        if ((betweenMin != Integer.MIN_VALUE && betweenMax != Integer.MAX_VALUE) && (number > betweenMax || number < betweenMin)) {
            ToastUtils.showError(getContext(), title + "输入范围" + betweenMin + "-" + betweenMax);
            return false;
        }
        boolean matches = mPattern.matcher(content).matches();
        if (!matches) {
            ToastUtils.showError(getContext(), title + "格式错误");
        }
        return matches;
    }

    @Override
    public void setContent(String content) {
        super.setContent(content);
        setNumData(content);
    }

    @Override
    public void setContent(Object content) {
        super.setContent(content);

        if (content instanceof String) {
            String mcontent = content.toString();
            setNumData(mcontent);
        }
    }

    /**
     * 根据numberType和numBerLenght设置数字的小数位
     */
    public void setNumData(String content){
        if (type.equals(CustomConstants.NUMBER) &&!TextUtils.isEmpty(content)){//整数
            if (numberType  == 1){
                if (content.indexOf(".") != -1){
                    content = content.substring(0,content.indexOf("."));
                    content = getSpliteNumber(content);
                    TextUtil.setText(etInput, content);
                }
            }else if (numberType  == 0){
                if (content.indexOf(".") != -1 && numberLenth>0){
                    int contentLenght = content.length();
                    int pointLenght = contentLenght -content.indexOf(".");
                    if (pointLenght>numberLenth && contentLenght >= content.indexOf(".")+numberLenth+1){
                        content = content.substring(0,content.indexOf(".")+numberLenth+1);
                    }else {
                        int size = numberLenth-pointLenght;
                        for (int i=0;i<size;i++){
                            content= content+"0";
                        }
                    }
                    content = getSpliteNumber(content);
                    TextUtil.setText(etInput, content);
                }
            }

        }
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

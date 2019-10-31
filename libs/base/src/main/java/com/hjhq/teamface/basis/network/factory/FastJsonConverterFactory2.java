package com.hjhq.teamface.basis.network.factory;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.utils.AESUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 使用fastjson转换数据格式
 *
 * @author lx
 * @date 2017/10/18
 */

public class FastJsonConverterFactory2 extends Converter.Factory {

    public static FastJsonConverterFactory2 create() {
        return new FastJsonConverterFactory2();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new FastJsonRequestBodyConverter<>();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new FastJsonResponseBodyConverter<>(type);
    }


    private class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, BaseBean> {
        Type type;

        public FastJsonResponseBodyConverter(Type type) {
            this.type = type;
        }

        @Override
        public BaseBean convert(ResponseBody value) throws IOException {
            BufferedSource bufferedSource = Okio.buffer(value.source());
            String tempStr = bufferedSource.readUtf8();
            bufferedSource.close();
            if (Constants.IS_AES){
                try {
                    tempStr = AESUtil.aesDecryptByBytes(tempStr,AESUtil.KEY);//AES解密
                } catch (Exception e) {
                    Log.e("FastJsonConverter2","FastJsonResponseBodyConverter:"+ e.getLocalizedMessage());
                }
            }
            BaseBean o = JSON.parseObject(tempStr, type, new PatchParserConfig());
            o.setRaw(tempStr);
            return o;
        }
    }

    private class FastJsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

        @Override
        public RequestBody convert(T value) throws IOException {
            if (Constants.IS_AES){

                try {
                    return RequestBody.create(MEDIA_TYPE, AESUtil.aesEncryptByteToBytes(JSON.toJSONBytes(value),AESUtil.KEY));////AES加密
                } catch (Exception e) {
                    Log.e("FastJsonConverter2","FastJsonRequestBodyConverter:"+ e.getLocalizedMessage());
                }
            }
            return RequestBody.create(MEDIA_TYPE, JSON.toJSONBytes(value));
        }
    }

}



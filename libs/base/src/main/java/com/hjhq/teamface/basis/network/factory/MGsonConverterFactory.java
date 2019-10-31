package com.hjhq.teamface.basis.network.factory;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 使用fastjson转换数据格式
 *
 * @author lx
 * @date 2017/10/18
 */

public class MGsonConverterFactory extends Converter.Factory {

    private boolean isAes;
    private Gson gson;
    public static MGsonConverterFactory create(boolean isAes,Gson gson) {
        return new MGsonConverterFactory(isAes,gson);
    }


    private MGsonConverterFactory(boolean isAes,Gson mgson) {
        this.isAes = isAes;
        if (mgson != null){
            gson = mgson;
        }else {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
        }
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new RequestBodyConverter<>(isAes, gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new ResponseBodyConverter<>(type, gson);
    }
    /**
     * 自定义请求RequestBody
     */
    public static class RequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private Gson gson;
        public RequestBodyConverter(boolean isAes, Gson gson) {
            this.gson = gson;
        }
        @Override
        public RequestBody convert(T value) throws IOException {//T就是传入的参数
            if (Constants.IS_AES) {//这里加上你自己的加密处理
                try {
                    return RequestBody.create(MEDIA_TYPE, AESUtil.aesDecryptByBytes(gson.toJson(value),AESUtil.KEY));
                } catch (Exception e) {
                    Log.e("MGsonConverterFactory","RequestBodyConverter:"+ e.getLocalizedMessage());
                }
            }
            Log.e("MGsonConverterFactory","RequestBodyConverter");
            return RequestBody.create(MEDIA_TYPE, gson.toJson(value));
        }

    }

    /**
     * 自定义响应ResponseBody
     */
    public class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private Gson gson;
        private Type type;

        public ResponseBodyConverter(Type type, Gson gson) {
            this.gson = gson;
            this.type = type;
        }

        /**
         * @param value
         * @return T
         * @throws IOException
         */
        @Override
        public T convert(ResponseBody value) throws IOException {
                BufferedSource bufferedSource = Okio.buffer(value.source());
                String tempStr = bufferedSource.readUtf8();
                bufferedSource.close();
                if (Constants.IS_AES){
                    try {
                        tempStr = AESUtil.aesDecryptByBytes(tempStr,AESUtil.KEY);//AES解密
                    } catch (Exception e) {
                        Log.e("MGsonConverterFactory","ResponseBodyConverter:"+ e.getLocalizedMessage());
                    }
                }
            Log.e("MGsonConverterFactory","ResponseBodyConverter");
                return gson.fromJson(tempStr,type);
        }
    }


}



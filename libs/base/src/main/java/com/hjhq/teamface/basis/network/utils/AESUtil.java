package com.hjhq.teamface.basis.network.utils;



import android.util.Base64;
import android.util.Log;

import com.hjhq.teamface.basis.AppConst;
import com.hjhq.teamface.basis.util.AppManager;
import com.hjhq.teamface.basis.util.file.SPUtils;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Description:与前端配合加解密(team.js)
 * @author: Administrator
 * @date: 2019年6月19日 下午3:35:12
 * @version: 1.0
 */

public class AESUtil
{
    // 密钥 (需要前端和后端保持一致)
    public static final String KEY = "teamface2019team";
    
    // 算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";
    
    /**
     * AES加密
     * 
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的内容
     * @throws Exception
     */
    public static String aesEncryptToBytes(String content, String encryptKey) {
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes("utf-8"), "AES"));
            return new String(Base64.encode(cipher.doFinal(content.getBytes("utf-8")), Base64.DEFAULT),"UTF-8");
        }catch (Exception e){
            return content;
        }

    }

    /**
     * AES加密
     *
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的内容
     * @throws Exception
     */
    public static byte[] aesEncryptByteToBytes(byte[] content, String encryptKey){
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes("utf-8"), "AES"));
            return cipher.doFinal(content);
        }catch(Exception e){
            return content;
        }
    }
    
    /**
     * AES解密
     * 
     * @param encryptStr 待解密内容
     * @param decryptKey 解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(String encryptStr, String decryptKey)
        throws Exception
    {
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes("utf-8"), "AES"));
        byte[] decryptBytes = cipher.doFinal(Base64.decode(encryptStr,Base64.DEFAULT));
        return new String(decryptBytes);
    }

    public static String getSign(){
        Random random = new Random();
        int num = random.nextInt(1000);
        String sign = System.currentTimeMillis()+","+ SPUtils.getString(AppManager.application, AppConst.IMEI)+","
                +AppConst.CLIENT_FLAG+","+num;
        Log.e("SIGN",sign);
        try {
            sign = AESUtil.aesEncryptToBytes(sign,AESUtil.KEY).trim();
            Log.e("SIGN",sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }
    
    /**
     * 测试
     */
  /*  public static void main(String[] args)
        throws Exception
    {
        String content = "teamface192.168.1.9teamface0teamface0";
        System.out.println("加密前：" + content);
        System.out.println("加密密钥和解密密钥：" + KEY);
        String encrypt = aesEncryptToBytes(content, KEY);
        System.out.println("加密后：" + encrypt);
        String decrypt = aesDecryptByBytes(encrypt, KEY);
        System.out.println("解密后：" + decrypt);
    }*/
}
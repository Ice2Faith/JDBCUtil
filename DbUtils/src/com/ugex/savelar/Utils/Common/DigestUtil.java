package com.ugex.savelar.Utils.Common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtil {
    /**
     * 获取二进制数据的MD5值
     * 对于字符串请如下使用
     * String md5=makeMD5("aa".getBytes());
     * @param data 字节数组，二进制值
     * @return MD5串
     */
    public static String makeMD5(byte[] data){
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16).toUpperCase();
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
}

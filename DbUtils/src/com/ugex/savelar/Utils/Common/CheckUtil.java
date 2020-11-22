package com.ugex.savelar.Utils.Common;

public class CheckUtil {
    public static boolean isNull(Object obj){
        return obj==null;
    }

    /**
     * 检查是否是null或者是否是空串，根据需要进行trim之后比较是否空串
     * @param str 串
     * @param needTrimed 是否需要trim判断
     * @return
     */
    public static boolean isNullOrEmptyStr(String str,boolean needTrimed){
        if(str==null)
            return true;
        if(needTrimed){
            return "".equals(str.trim());
        }
        return "".equals(str);
    }

    //一些常用的正则表达式串，这些串都可以作为参数进行匹配
    public static final String REGEX_INT_NUMBER="^-?[1-9]\\d*$";
    public static final String REGEX_FLOAT_NUMBER="^-?[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$";
    public static final String REGEX_MOBILE_11="^[1][0-9]{10}$";
    public static final String REGEX_EMAIL="^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static final String REGEX_ID_NUMBER="^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X)$";
    public static final String REGEX_URL="[a-zA-Z]+://[^\\s]*";

    /**
     * 匹配字符串，通过正则
     * @param str 字符串
     * @param regex 正则
     * @return 是否满足正则
     */
    public static boolean isMatched(String str,String regex){
        return str.matches(regex);
    }

}

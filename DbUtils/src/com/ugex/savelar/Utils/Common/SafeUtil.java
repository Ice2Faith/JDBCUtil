package com.ugex.savelar.Utils.Common;

public class SafeUtil {
    /**
     * 获取安全的字符串，非null，按需trim
     * 如果为null，返回空串
     * @param str 源字符串
     * @param needTrimed 是否需要trim返回
     * @return 安全串
     */
    public static String safeStr(String str,boolean needTrimed){
        if(str==null)
            return "";
        if(needTrimed)
            return str.trim();
        return str;
    }

    /**
     * 如果入参为null,则用默认值替换
     * @param obj 入参
     * @param whenNull 默认值
     * @param <T> 类型
     * @return 安全值
     */
    public static <T> T nullTo(T obj,T whenNull){
        if(obj==null)
            return whenNull;
        return obj;
    }

}

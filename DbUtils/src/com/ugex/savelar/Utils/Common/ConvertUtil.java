package com.ugex.savelar.Utils.Common;

public class ConvertUtil {
    public static Integer s2i(String num){
        return Integer.parseInt(num);
    }
    public static Integer s2ix(String num,int base){
        return Integer.parseInt(num,base);
    }
    public static Double s2d(String num){
        return Double.parseDouble(num);
    }
    public static Long s2l(String num){
        return Long.parseLong(num);
    }
    public static Float s2f(String num){
        return  Float.parseFloat(num);
    }

}

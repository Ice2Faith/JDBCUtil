package com.ugex.savelar.Utils.Common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {
    /**
     * 将对象转换为Json串
     * 用法：
     * String js=toJson(new Admin());
     * @param obj 对象
     * @param <T> 对象类型
     * @return Json串
     */
    public static<T> String toJson(T obj){
        Gson gson=new Gson();
        return gson.toJson(obj);
    }

    /**
     * 将一个Json串解析为对象
     * 用法：
     * Admin admin=fromJson(js,Admin.class);
     * @param json Json串
     * @param clazz 类类型
     * @param <T> 类型
     * @return 类对象
     */
    public static <T> T fromJson(String json,Class<T> clazz){
        Gson gson=new Gson();
        return gson.fromJson(json,clazz);
    }

    /**
     * 将一个Json串解析为对象集合
     * 用法：
     * Lis<Admin> list=fromJsonArray(js);
     * @param json Json串
     * @param <T> 类型
     * @return 对象集合
     */
    public static <T> T fromJsonArray(String json){
        Gson gson=new Gson();
        return gson.fromJson(json,new TypeToken<T>(){}.getType());
    }
}

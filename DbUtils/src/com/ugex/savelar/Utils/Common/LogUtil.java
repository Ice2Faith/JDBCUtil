package com.ugex.savelar.Utils.Common;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    public static volatile String LOG_FILE;
    private static volatile PrintWriter out;

    /**
     * must invoke it before use this Util
     * @param logFileName you log file location path
     */
    public static void Initial(String logFileName){
        try {
            LOG_FILE=logFileName;
            out=new PrintWriter(new FileWriter(LOG_FILE));
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    if(out!=null){
                        out.close();
                        out=null;
                    }
                }
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void checkState(){
        if(out==null){
            throw new RuntimeException("LogUtil's Initial() method must be invoked before use it.");
        }
    }
    /**
     * get log PrintWriter object to out put log info
     * @return
     */
    public static PrintWriter getWriter(){
        checkState();
        return out;
    }
    private static volatile SimpleDateFormat fmt=new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss SSS] : ");
    private static volatile String sep=",";

    /**
     * 你可以设置多参数的分隔符
     * 默认分割符：","
     * @param str
     */
    public static void setSep(String str){
        sep=str;
    }

    private static String getParamsStr(Object ... objs){
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<objs.length;i++){
            if(i!=0){
                builder.append(sep);
            }
            builder.append(objs[i]);
        }
        return builder.toString();
    }
    /**
     * 按照给定的分隔符进行分割并输出为一行数据
     * 默认分割符：","
     * @param params
     */
    public static void println(Object ... params){
        logInfo(params);
    }
    /**
     * 默认已经提供了一个日期格式，如果你需要使用自己的日期格式，你也可以进行设置
     * 默认："[yyyy-MM-dd HH:mm:ss SSS] : "
     * @param format
     */
    public static void setSimpleDateFormat(SimpleDateFormat format){
        fmt=format;
    }
    public static void logInfo(Object ... params){
        checkState();
        out.println("[INFO]    "+fmt.format(new Date())+getParamsStr(params));
    }
    public static void logWarning(Object ... params){
        checkState();
        out.println("[WARNING] "+fmt.format(new Date())+getParamsStr(params));
    }
    public static void logError(Object ... params){
        checkState();
        out.println("[ERROR]   "+fmt.format(new Date())+getParamsStr(params));
    }
    private static String getClassFullName(Object obj){
        if(Class.class.equals(obj.getClass())){
            return ((Class<?>) obj).getName();
        }else{
            return obj.getClass().getName();
        }
    }

    /**
     * 第一个参数表示这条日志打印的类或者绑定对象的类
     * 使用方式例如：
     * logInfo(Student.class,"this is log info");
     * logInfo(new Student(),"this is log info");
     * 以上两种都是可以的，这将会在你打印日志的时候附带上第一个参数的类名
     * @param obj
     * @param params
     */
    public static void logLocalInfo(Object obj,Object ... params){
        checkState();
        out.println("[INFO]    "+fmt.format(new Date())+" Class:["+getClassFullName(obj)+"] : "+getParamsStr(params));
    }
    public static void logLocalWarning(Object obj,Object params){
        checkState();
        out.println("[WARNING] "+fmt.format(new Date())+" Class:["+getClassFullName(obj)+"] : "+getParamsStr(params));
    }
    public static void logLocalError(Object obj,Object params){
        checkState();
        out.println("[ERROR]   "+fmt.format(new Date())+" Class:["+getClassFullName(obj)+"] : "+getParamsStr(params));
    }
}

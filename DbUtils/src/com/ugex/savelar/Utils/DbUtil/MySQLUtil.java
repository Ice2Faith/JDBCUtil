package com.ugex.savelar.Utils.DbUtil;

import com.ugex.savelar.Utils.UtilException;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MYSQL 辅助类
 * 帮助执行SQL语句以及连接操作
 * 如果你使用到解析返回结果为实体类的方法时，实体类必须具有默认构造
 * 人生建议：
 * 数据库时间字段一律使用datetime,java实体类使用对用的时间java.sql.Timestamp类型
 * 本工具提供转换时间转换函数convert2族函数
 */
public class MySQLUtil {
    public static String MYSQL_DRIVER_CLASS_NAME="com.mysql.cj.jdbc.Driver";
    //80之前：com.mysql.jdbc.Driver
    //注册连接驱动类，80之前，名字去掉cj段
    public static void setMysqlDriverClassName(String driverClassName){
        MYSQL_DRIVER_CLASS_NAME=driverClassName;
    }
    public static boolean doTrans(Connection conn, ITransaction trans, Object ... params) throws UtilException {
        try {
            conn.setAutoCommit(false);
            trans.doTrans(conn,params);
            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,conn,"rollback transaction error");
            }
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,conn,"sql transaction commit error");
        }finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception e) {
                throw new UtilException(e,UtilException.ErrCode.SQL_CONN_ERR,conn,"connection set auto commit to true error");
            }
        }
        //return false;
    }
    private static boolean isFirstLoad=true;
    public static void registerDriver() throws UtilException {
        try {
            Class.forName(MYSQL_DRIVER_CLASS_NAME);
            isFirstLoad=false;
        } catch (ClassNotFoundException e) {
            throw new UtilException(e,UtilException.ErrCode.OTHER_ERR,null,"class not found:"+MYSQL_DRIVER_CLASS_NAME);
        }

    }
    /**
     * 默认3306端口的链接
     */
    public static Connection getConnect(String host, String dbName, String user, String password) throws UtilException {
        return getConnect(host,3306,dbName,user,password);
    }
    //链接东8区的指定数据库使用UTF8编码Unicode解析
    public static Connection getConnect(String host,int port,String dbName,String user,String password) throws UtilException {
        return getConnect(host,port,dbName,user,password,"useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8");
    }
    //注意：params参数，也就是连接字符串的参数部分，例如：useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8
    public static Connection getConnect(String host,int port,String dbName,String user,String password,String params) throws UtilException {
        //params:useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8
        String url="jdbc:mysql://"+host+":"+port+"/"+dbName+"?"+params;
        try{
            if(isFirstLoad){
                registerDriver();
            }
            Connection conn= DriverManager.getConnection(url,user,password);
            return conn;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_CONN_ERR,url,"DriverManager.getConnection() error");
        }
    }
    public static Connection getConnect(String url,String user,String password) throws UtilException {
        //params:useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8
        try{
            if(isFirstLoad){
                registerDriver();
            }
            Connection conn= DriverManager.getConnection(url,user,password);
            return conn;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_CONN_ERR,url,"DriverManager.getConnection() error");
        }
    }
    //断开连接
    public static void disConnect(Connection conn) throws UtilException {
        if(conn!=null) {
            try {
                conn.close();
                conn=null;
            } catch (SQLException e) {
                throw new UtilException(e,UtilException.ErrCode.SQL_CONN_ERR,conn,"close Connection error");
            }
        }
    }
    //直接执行过程语句，（建表，存储过程，函数等）
    public static boolean executeDirect(Connection conn, String sql) throws UtilException {
        Statement stat=null;
        try{
            stat=conn.createStatement();
            boolean success=stat.execute(sql);
            //return success;
            //实际上，只要执行到这里没有发生异常，那就是执行成功了，他的返回值是没有参考意义的
            //因为是返回第一条语句的执行结果
            return true;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,stat,"execute sql error:"+sql);
        }finally {
            if(stat!=null){
                try {
                    stat.close();
                } catch (SQLException e) {
                    throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,stat,"close Statement error");
                }
            }
        }
    }

    /**
     * 以下方法，用于直接获取原始结果数据集
     * 重新封装成为一个数据集
     * 方便调用和使用，这样你可以按照行列索引进行访问数据
     * 也可以通过行索引和列名进行访问数据
     * 用法：
     * DBResultData datas=getQueryRawData(conn,"select * from Admin",true);
     * 这样便得到了结果集，并且列名都已经转换为小写
     * @param conn 数据库连接对象
     * @param sql SQL语句
     * @param useLowerCase 是否转换列名为小写
     * @return 返回查询结果集
     * @throws UtilException
     */
    public static DBResultData queryRawData(Connection conn, String sql, boolean useLowerCase) throws UtilException {
        DBResultData ret=new DBResultData();
        try{
            Statement stat=conn.createStatement();
            ResultSet rs=stat.executeQuery(sql);
            ret=getRawDataProxy(rs,useLowerCase);
            rs.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,sql,"query raw data error:"+sql);
        }
        return ret;
    }
    public static DBResultData queryRawData(PreparedStatement stat, boolean closeStat, boolean useLowerCase) throws UtilException {
        DBResultData ret=new DBResultData();
        try{
            ResultSet rs=stat.executeQuery();
            ret=getRawDataProxy(rs,useLowerCase);
            rs.close();
            if(closeStat)
                stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,stat,"query raw data error:"+stat.toString());
        }
        return ret;
    }
    public static DBResultData queryRawData(PreparedStatement stat) throws UtilException {
        return queryRawData(stat,true,false);
    }
    public static DBResultData queryRawData(PreparedStatement stat, boolean closeStat) throws UtilException {
        return queryRawData(stat,closeStat,false);
    }
    public static DBResultData queryRawData(Connection conn, String sql) throws UtilException {
        return queryRawData(conn,sql,false);
    }
    /**
     * 获取原始数据，用预处理方式，自带参数
     * 附带标记，将结果列名转换为小写
     * 用法：
     * DBResultData result=getQueryRawData(conn,false,"select * from Admin where name like ? and age>?;","'Ad%'",20);
     * 这样便进行了预处理并返回来目标结果
     * @param conn 数据库连接对象
     * @param useLowerCase 是否将列名转换为小写
     * @param prepareSql 预处理SQL语句
     * @param objs 预处理参数列表
     * @return 数据库返回结果集
     * @throws UtilException
     */
    public static DBResultData queryRawData(Connection conn, boolean useLowerCase, String prepareSql, Object ... objs) throws UtilException {
        DBResultData ret=new DBResultData();
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,objs);
            ResultSet rs=stat.executeQuery();
            ret=getRawDataProxy(rs,useLowerCase);
            rs.close();
            stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,"query raw data error:"+prepareSql);
        }
        return ret;
    }
    public static DBResultData getRawDataProxy(ResultSet rs,boolean useLowerCase) throws UtilException {
        List<String> cols=new ArrayList<>();
        List<Map<String,Object>> datas=new ArrayList<>();
        try{
            ResultSetMetaData meta=rs.getMetaData();
            int colCount=meta.getColumnCount();
            for(int i=1;i<=colCount;i++){
                String colName=meta.getColumnName(i);
                if(useLowerCase){
                    colName=colName.toLowerCase();
                }
                cols.add(colName);
            }
            while(rs.next()){
                Map<String,Object> row=new HashMap<>();
                for(int i=1;i<=colCount;i++){
                    String colName=meta.getColumnName(i);
                    Object colValue=rs.getObject(colName);

                    if(useLowerCase){
                        colName=colName.toLowerCase();
                    }

                    row.put(colName,colValue);
                }
                datas.add(row);
            }


        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.ILLEGAL_STATE_ERR,null,"read DataSet error");
        }

        return new DBResultData(cols,datas);
    }

    /**
     * 以下两个方法，用于将查询结果的一行数据转换为对应的实体类
     * 适用于：查询结果只有一条或者只要查询结果的第一条数据的时候，常见的根据主键查找，根据唯一列查找等
     * 前提：实体类的属性和查询返回的列名存在重叠
     * 如果，返回结果中出现属性中不存在的列名，这个结果将会被忽略
     * 或者，列名中不存在的属性，也会被忽略
     * 也就是说，当属性名和列名一致时才会被赋值
     * 用法：
     * Admin admin=getQueryBean(conn,Admin.class,"select * from Admin where id=1001;",true);
     * @param conn
     * @param clazz
     * @param sql
     * @param ignoreCase
     * @param <T>
     * @return
     * @throws UtilException
     */
    public static <T> T queryBean(Connection conn, Class<T> clazz, String sql, boolean ignoreCase) throws UtilException {
        T ret=null;
        try{
            Statement stat=conn.createStatement();
            ResultSet rs=stat.executeQuery(sql);
            ret=getBeanProxy(rs,clazz,ignoreCase);
            rs.close();
            stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,sql,"query bean error:"+clazz.getName()+":"+sql);
        }
        return ret;
    }
    //区别：传入的是预处理的PreparedStatement
    public static <T> T queryBean(Class<T> clazz, PreparedStatement stat, boolean closeStat, boolean ignoreCase) throws UtilException {
        T ret=null;
        try{
            ResultSet rs=stat.executeQuery();
            ret=getBeanProxy(rs,clazz,ignoreCase);
            rs.close();
            if(closeStat)
                stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,stat,"query bean error:"+clazz.getName()+":"+stat.toString());
        }
        return ret;
    }
    //区别：自动忽略属性与列名的大小写
    public static <T> T queryBean(Connection conn, Class<T> clazz, String sql) throws UtilException {
        return queryBean(conn,clazz,sql,true);
    }
    public static <T> T queryBean(Class<T> clazz, PreparedStatement stat, boolean closeStat) throws UtilException {
        return queryBean(clazz,stat,closeStat,true);
    }
    public static <T> T queryBean(Class<T> clazz, PreparedStatement stat) throws UtilException {
        return queryBean(clazz,stat,true,true);
    }
    public static <T> T queryBean(Connection conn, Class<T> clazz, boolean ignoreCase, String prepareSql,Object ... objs) throws UtilException {
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,objs);
            ResultSet rs=stat.executeQuery();
            T ret=getBeanProxy(rs,clazz,ignoreCase);
            rs.close();
            stat.close();
            return ret;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,"query bean error:"+clazz.getName()+":"+prepareSql);
        }
    }
    private static <T> T getBeanProxy(ResultSet rs,Class<T> clazz,boolean ignoreCase) throws UtilException {
        T ret=null;
        try{
            ResultSetMetaData meta=rs.getMetaData();
            int colCount=meta.getColumnCount();
            Field[] fields=clazz.getDeclaredFields();
            while(rs.next()){
                ret=clazz.newInstance();
                for(int i=1;i<=colCount;i++){
                    String colName=meta.getColumnName(i);
                    Object colValue=rs.getObject(colName);

                    for(Field field : fields){
                        field.setAccessible(true);
                        String attName=field.getName();
                        try{
                            if(ignoreCase){
                                if(colName.equalsIgnoreCase(attName) && colValue!=null){
                                    //field.set(ret,colValue);
                                    setObjectFieldValue(field,ret,colValue);
                                }
                            }else{
                                if(colName.equals(attName) && colValue!=null){
                                    //field.set(ret,colValue);
                                    setObjectFieldValue(field,ret,colValue);
                                }
                            }
                        }catch(Exception e){
                            //ignore error
                        }

                    }

                }
                break;
            }


        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.ILLEGAL_STATE_ERR,null,
                    "read ResultSet error or make class Instance error(class Must have none-arg Constructor Method):"+clazz.getName());
        }
        return ret;
    }
    //也就是获取所有列，其余的和上面的一样
    public static <T> T queryBeanFullColumn(Connection conn,String tableName,Class<T> clazz,boolean ignoreCase,String where,Object ... params) throws UtilException {
        String prepareSql="select * from "+tableName+" WHERE "+where+";";
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,params);
            ResultSet rs=stat.executeQuery();
            T ret=getBeanProxy(rs,clazz,ignoreCase);
            rs.close();
            stat.close();
            return ret;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,
                    "query bean full column error:"+prepareSql);
        }
    }
    public static <T> T queryBeanFullColumn(Connection conn,Class<T> clazz,boolean ignoreCase,String where,Object ... params) throws UtilException {
        String tableName=DBClassUtil.getLastClassName(clazz.getName());
        String prepareSql="select * from "+tableName+" WHERE "+where+";";
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,params);
            ResultSet rs=stat.executeQuery();
            T ret=getBeanProxy(rs,clazz,ignoreCase);
            rs.close();
            stat.close();
            return ret;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,
                    "query bean full column error:"+clazz.getName()+":"+prepareSql);
        }
    }

    /**
     * 以下两个方法用于将查询结果转换为一个实体类列表
     * 前提：实体类的属性和查询返回的列名存在重叠
     * 如果，返回结果中出现属性中不存在的列名，这个结果将会被忽略
     * 或者，列名中不存在的属性，也会被忽略
     * 也就是说，当属性名和列名一致时才会被赋值
     * 用法示例：
     * List<Admin> list=getQueryBeans(conn,Admin.class,"select * from Admin where name like '%Li%';",true);
     * @param conn 数据库连接对象
     * @param clazz 实体类类型
     * @param sql SQL语句
     * @param ignoreCase 是否忽略列名与属性名的大小写比较
     * @param <T> 实体类
     * @return 对应的实体类列表
     * @throws UtilException
     */
    public static <T> List<T> queryBeans(Connection conn, Class<T> clazz, String sql, boolean ignoreCase) throws UtilException {
        List<T> ret=new ArrayList<>();
        try{
            Statement stat=conn.createStatement();
            ResultSet rs=stat.executeQuery(sql);
            ret=getBeansProxy(rs,clazz,ignoreCase);
            rs.close();
            stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,sql,
                    "query beans error:"+clazz.getName()+":"+sql);
        }
        return ret;
    }
    public static <T> List<T> queryBeans(Class<T> clazz, PreparedStatement stat, boolean closeStat, boolean ignoreCase) throws UtilException {
        List<T> ret=new ArrayList<>();
        try{
            ResultSet rs=stat.executeQuery();
            ret=getBeansProxy(rs,clazz,ignoreCase);
            rs.close();
            if(closeStat)
                stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,stat,
                    "query beans error:"+clazz.getName()+":"+stat.toString());
        }
        return ret;
    }
    public static <T> List<T> queryBeans(Connection conn, Class<T> clazz, String sql) throws UtilException {
        return queryBeans(conn,clazz,sql,true);
    }
    public static <T> List<T> queryBeans(Class<T> clazz, PreparedStatement stat, boolean closeStat) throws UtilException {
        return queryBeans(clazz,stat,closeStat,true);
    }
    public static <T> List<T> queryBeans(Class<T> clazz, PreparedStatement stat) throws UtilException {
        return queryBeans(clazz,stat,true,true);
    }
    public static <T> List<T> queryBeans(Connection conn,Class<T> clazz, boolean ignoreCase,String prepareSql,Object ... objs) throws UtilException {
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,objs);
            ResultSet rs=stat.executeQuery();
            List<T> ret=getBeansProxy(rs,clazz,ignoreCase);
            rs.close();
            stat.close();
            return ret;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,
                    "query beans error:"+clazz.getName()+":"+prepareSql);
        }
    }
    private static <T> List<T> getBeansProxy(ResultSet rs,Class<T> clazz,boolean ignoreCase) throws UtilException {
        List<T> ret=new ArrayList<>();
        try{
            ResultSetMetaData meta=rs.getMetaData();
            int colCount=meta.getColumnCount();
            Field[] fields=clazz.getDeclaredFields();
            while(rs.next()){
                T obj=clazz.newInstance();
                for(int i=1;i<=colCount;i++){
                    String colName=meta.getColumnName(i);
                    Object colValue=rs.getObject(colName);
                    for(Field field : fields){
                        field.setAccessible(true);
                        String attName=field.getName();
                        try{
                            if(ignoreCase){
                                if(colName.equalsIgnoreCase(attName) && colValue!=null){
                                    //field.set(obj,colValue);
                                    setObjectFieldValue(field,obj,colValue);
                                }
                            }else{
                                if(colName.equals(attName) && colValue!=null){
                                    //field.set(obj,colValue);
                                    setObjectFieldValue(field,obj,colValue);
                                }
                            }
                        }catch(Exception e){
                            //ignore error
                        }

                    }

                }
                ret.add(obj);
            }


        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.ILLEGAL_STATE_ERR,null,
                    "read ResultSet error or make class Instance error(class Must have none-arg Constructor Method):"+clazz.getName());
        }
        return ret;
    }
    //也就是获取所有列，其余的和上面一样
    public static <T> List<T> queryBeansFullColumn(Connection conn,String tableName,Class<T> clazz,boolean ignoreCase,String where,Object ... params) throws UtilException {
        String prepareSql="select * from "+tableName+" WHERE "+where+";";
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,params);
            ResultSet rs=stat.executeQuery();
            List<T> ret=getBeansProxy(rs,clazz,ignoreCase);
            rs.close();
            stat.close();
            return ret;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR, prepareSql,
                    "query beans full column error:"+clazz.getName()+":"+prepareSql);
        }
    }
    public static <T> List<T> queryBeansFullColumn(Connection conn,Class<T> clazz,boolean ignoreCase,String where,Object ... params) throws UtilException {
        String tableName=DBClassUtil.getLastClassName(clazz.getName());
        String prepareSql="select * from "+tableName+" WHERE "+where+";";
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,params);
            ResultSet rs=stat.executeQuery();
            List<T> ret=getBeansProxy(rs,clazz,ignoreCase);
            rs.close();
            stat.close();
            return ret;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,
                    "query beans full column error:"+clazz.getName()+":"+prepareSql);
        }
    }

    public static <T> void setObjectFieldValue(Field field,T obj,Object value) throws IllegalAccessException {
        Class fieldType=field.getType();
        Class valueType=value.getClass();
        if(fieldType.equals(valueType)){//类型匹配
            field.set(obj,value);
        }
        else if(fieldType.equals(java.sql.Timestamp.class)){//实体类为java.sql.Timestamp类型，却传过来可转换的时间类型时：java.util.Date,java.sql.Date,java.sql.Time
            if(valueType.equals(java.util.Date.class)){
                field.set(obj,new java.sql.Timestamp(((java.util.Date)value).getTime()));
            }else if(valueType.equals(java.sql.Date.class)){
                field.set(obj,new java.sql.Timestamp(((java.sql.Date)value).getTime()));
            }else if(valueType.equals(java.sql.Time.class)){
                field.set(obj,new java.sql.Timestamp(((java.sql.Time)value).getTime()));
            }
        }
        else if(fieldType.equals(java.util.Date.class)){//实体类为java.util.Date类型，传过来的也是可转换的时间类型时，java.sql.Timestamp，java.sql.Date,java.sql.Time
            if(valueType.equals(java.sql.Timestamp.class)){
                field.set(obj,new java.util.Date(((java.sql.Timestamp)value).getTime()));
            }else if(valueType.equals(java.sql.Date.class)){
                field.set(obj,new java.util.Date(((java.sql.Date)value).getTime()));
            }else if(valueType.equals(java.sql.Time.class)){
                field.set(obj,new java.util.Date(((java.sql.Time)value).getTime()));
            }
        }
        else{
            field.set(obj,value);
        }
    }

    /**
     * 以下两个方法用于查询结果唯一的情况，获取唯一值，也就是数据库查询出的结果
     * 因此，返回类型都只会是数据库里支持的类型，并没有自定义的实体类类似的数据类型
     * 前提：基本数据类型需要使用对应的包装类，否则如果遇到空指针会异常
     * 比如：
     * int a=null;将会错误
     * Integer a=null;则不会发生错误
     * 这是由于泛型编程之上，并不会自动进行装箱和拆箱操作
     * 调用示例：
     * Double d=getQueryObject(conn,"select money from Account where id=1001;");
     * 但是，如果你的查询结果没有数据的话，依旧会发生空指针异常，
     * 原因类似：double a=(Double)null;
     * 虽然，语法上不会报错，但是运行时会发生空指针异常
     * @param conn 数据库连接对象
     * @param sql SQL语句
     * @param <T> 接受的数据类型
     * @return
     * @throws UtilException
     */
    public static <T> T queryObject(Connection conn, String sql) throws UtilException {
        T ret=null;
        try{
            Statement stat=conn.createStatement();
            ResultSet rs=stat.executeQuery(sql);
            ret=getObjectProxy(rs);
            rs.close();
            stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,sql,
                    "query object error:"+sql);
        }
        return ret;
    }
    public static <T> T queryObject(PreparedStatement stat, boolean closeStat) throws UtilException {
        T ret=null;
        try{
            ResultSet rs=stat.executeQuery();
            ret=getObjectProxy(rs);
            rs.close();
            if(closeStat)
                stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,stat,
                    "query object error:"+stat.toString());
        }
        return ret;
    }
    public static <T> T queryObject(PreparedStatement stat) throws UtilException {
        return queryObject(stat,true);
    }
    public static <T> T queryObject(Connection conn,String prepareSql,Object ... objs) throws UtilException {
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,objs);
            ResultSet rs=stat.executeQuery();
            T ret=getObjectProxy(rs);
            rs.close();
            stat.close();
            return ret;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,
                    "query object error:"+prepareSql);
        }
    }
    private static <T> T getObjectProxy(ResultSet rs) throws UtilException {
        T ret=null;
        try{
            ResultSetMetaData meta=rs.getMetaData();
            int colCount=meta.getColumnCount();
            while(rs.next()){
                for(int i=1;i<=colCount;i++){
                    String colName=meta.getColumnName(i);
                    Object colValue=rs.getObject(colName);
                    ret=(T)colValue;
                    break;
                }
                break;
            }
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,null,
                    "read ResultSet error");
        }
        return ret;
    }

    /**
     * 处理更新类型的SQL语句，以预处理的方式进行（更新，修改，删除）
     * @param conn 数据库连接
     * @param prepareSql 预处理语句
     * @param objs 预处理语句中的参数
     * @return 更新影响的行数
     */
    public static int updatePrepared(Connection conn, String prepareSql, Object ... objs) throws UtilException {
        int ret=-1;
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,objs);
            ret=stat.executeUpdate();
            stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,
                    "update prepared error:"+prepareSql);
        }
        return ret;
    }
    //直接执行更新语句（包含没有返回结果集的所有语句，包含 更新，修改，删除等）
    public static int updateDirect(Connection conn, String sql) throws UtilException {
        Statement stat=null;
        try{
            stat=conn.createStatement();
            int effectLines=stat.executeUpdate(sql);
            return effectLines;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,sql,
                    "update direct error:"+sql);
        }finally {
            if(stat!=null){
                try {
                    stat.close();
                } catch (SQLException e) {
                    throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,null,
                            "close PreparedStatement error");
                }
            }
        }
    }

    //以准备方式执行更新语句（包含 更新 删除 修改 过程 函数等），查询对象需要自己关闭
    public static int updatePrepared(PreparedStatement stat) throws UtilException {
        return updatePrepared(stat,false);
    }
    //以准备方式执行更新语句，查询对象根据closeStat指定是否需要关闭
    public static int updatePrepared(PreparedStatement stat, boolean closeStat) throws UtilException {
        try{
            int effectLines=stat.executeUpdate();
            return effectLines;
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,stat,
                    "update prepared error:"+stat.toString());
        }finally {
            if(closeStat){
                try {
                    stat.close();
                } catch (Exception e) {
                    throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,null,
                            "close PreparedStatement error");
                }
            }
        }
    }


    /**
     * 一下方法，均是以准备方式进行相应的操作，
     * 其中cols表示列名
     * values表示预处理使用的参数
     * whereValues表示where条件中使用的预处理参数
     * @param conn 数据库连接对象
     * @param tableName 表名
     * @param cols 列名
     * @param where where条件部分
     * @param whereValues where条件的预处理参数
     * @return 查询结果集
     * @throws UtilException
     */
    public static DBResultData query(Connection conn,String tableName,String[] cols,String where,Object ... whereValues) throws UtilException {
        DBResultData ret=new DBResultData();
        StringBuilder colsBuilder=new StringBuilder();
        for(int i=0;i<cols.length;i++){
            colsBuilder.append(cols[i]);
            if(i!=cols.length-1){
                colsBuilder.append(",");
            }
        }
        String prepareSql="SELECT "+colsBuilder.toString()+" FROM "+tableName+" WHERE "+where+";";
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,whereValues);
            ResultSet rs=stat.executeQuery();
            ret=getRawDataProxy(rs,false);
            stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,
                    "query error:"+prepareSql);
        }
        return ret;
    }
    public  static int insert(Connection conn,String tableName,String[] cols,Object ... values) throws UtilException {
        int ret=-1;
        StringBuilder colsBuilder=new StringBuilder();
        StringBuilder valuesBuilder=new StringBuilder();
        for(int i=0;i<cols.length;i++){
            colsBuilder.append(cols[i]);
            valuesBuilder.append("?");
            if(i!=cols.length-1){
                colsBuilder.append(",");
                valuesBuilder.append(",");
            }
        }
        String prepareSql="INSERT INTO "+tableName+"("+colsBuilder.toString()+") VALUES("+valuesBuilder.toString()+");";
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,values);
            ret=stat.executeUpdate();
            stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,
                    "insert error:"+prepareSql);
        }
        return ret;
    }
    public static int update(Connection conn,String tableName,String[] cols,String where,Object ... values) throws UtilException {
        int ret=-1;
        StringBuilder colsBuilder=new StringBuilder();
        for(int i=0;i<cols.length;i++){
            colsBuilder.append(cols[i]);
            colsBuilder.append("=?");
            if(i!=cols.length-1){
                colsBuilder.append(",");
            }
        }
        String prepareSql="UPDATE "+tableName+" SET "+colsBuilder+" WHERE "+where+";";
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,values);
            ret=stat.executeUpdate();
            stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,
                    "update error:"+prepareSql);
        }
        return ret;
    }
    public static int delete(Connection conn,String tableName,String where,Object ... whereValues) throws UtilException {
        int ret=-1;
        String prepareSql="DELETE FROM "+tableName+" WHERE "+where+";";
        try{
            PreparedStatement stat=PreparedStatementBuilder.make(conn,prepareSql,whereValues);
            ret=stat.executeUpdate();
            stat.close();
        }catch(Exception e){
            throw new UtilException(e,UtilException.ErrCode.SQL_EXEC_ERR,prepareSql,
                    "delete error:"+prepareSql);
        }
        return ret;
    }

    /**
     * 以类的方式，替代直接写表名的方式，防止因为表名写错而产生错误
     * 进行删除操作
     * 用法：
     * int effecline=delete(conn,Admin.class,"id=?",1001);
     * @param conn 数据库连接对象
     * @param clazz 类类型
     * @param where 删除条件
     * @param whereValues 条件的值
     * @param <T> 类型
     * @return 影响的行数
     * @throws UtilException
     */
    public static<T> int delete(Connection conn,Class<T> clazz,String where,Object ... whereValues) throws UtilException {
        String tableName=DBClassUtil.getLastClassName(clazz.getName());
        return delete(conn,tableName,where,whereValues);
    }

    /**
     * 以类的方式，替代直接写表名的方式，防止因为表名写错而产生错误
     * 进行查询操作
     * 用法：
     * DBResultData rsd=query(conn,Admin.class,new String[]{"id","name","type"},"age>?",17);
     * @param conn 数据库连接对象
     * @param clazz 类类型
     * @param cols 要查询的列
     * @param where 查询条件
     * @param whereValues 条件值
     * @param <T> 类型
     * @return 查询结果集
     * @throws UtilException
     */
    public static<T> DBResultData query(Connection conn,Class<T> clazz,String[] cols,String where,Object ... whereValues) throws UtilException {
        String tableName=DBClassUtil.getLastClassName(clazz.getName());
        return query(conn,tableName,cols,where,whereValues);
    }


    /**
     * 根据键值对插入到指定的表中
     * 如果某个键的值为null，将不会出现在语句中被执行，而是被跳过
     * @param conn 数据库连接对象
     * @param tableName 表名
     * @param values 键值对，键为列名，值为数据
     * @return 受影响的行数
     * @throws UtilException
     */
    public static int insert(Connection conn,String tableName,Map<String,Object> values) throws UtilException {
        int count=values.size();
        String[] scols=new String[count];
        Object[] svals=new Object[count];
        int i=0;
        for(String key : values.keySet()){
            scols[i]=key;
            svals[i]=values.get(key);
            if(svals[i]==null){
                continue;
            }
            if(String.class.equals(svals[i].getClass()) && "".equals(svals[i])){
                continue;
            }
            i++;
        }
        int trueCount=i;
        String[] cols=new String[trueCount];
        Object[] vals=new Object[trueCount];
        for(int j=0;j<trueCount;j++){
            cols[j]=scols[j];
            vals[j]=svals[j];
        }
        return insert(conn,tableName,cols,vals);
    }

    /**
     * 将一个实体类对象插入到对应的表中
     * 前提是，实体类类名和表名对应
     * 属性名和列名对应
     * 用法：
     * int effecline=insert(conn,new Admin(),"id","other");
     * @param conn 数据库连接对象
     * @param obj 实体类对象
     * @param removeAttrs 不需要插入的属性名列表，也就是不需要插入的那些列，常见的有自动增长的id主键列需要移除
     * @param <T> 类类型
     * @return 影响的行数
     * @throws UtilException
     */
    public static<T> int insert(Connection conn,T obj,String ... removeAttrs) throws UtilException {
        Class clazz=obj.getClass();
        String tableName=DBClassUtil.getLastClassName(clazz.getName());
        Map<String,Object> maps=getKeyValuesMapFromBean(obj);
        for(String attr : removeAttrs){
            maps.remove(attr);
        }
        return insert(conn,tableName,maps);
    }
    /**
     * 根据键值对和条件更新记录
     * 如果某个键的值为null，将不会出现在语句中被执行，而是被跳过
     * @param conn 数据库连接对象
     * @param tableName 表名
     * @param values 键值对，都是会被更新的键值对
     * @param where 更新条件语句
     * @param whereValues 更新条件语句的参数
     * @return 受影响的行数
     * @throws UtilException
     */
    public static int update(Connection conn,String tableName,Map<String,Object> values,String where,Object ... whereValues) throws UtilException {
        int count=values.size();
        String[] scols=new String[count];
        Object[] svals=new Object[count];
        int i=0;
        for(String key : values.keySet()){
            scols[i]=key;
            svals[i]=values.get(key);
            if(svals[i]==null){
                continue;
            }
            if(String.class.equals(svals[i].getClass()) && "".equals(svals[i])){
                continue;
            }
            i++;
        }

        int trueCount=i;
        int wcount=whereValues.length;

        String[] cols=new String[trueCount];
        Object[] vals=new Object[trueCount+wcount];
        for(int j=0;j<trueCount;j++){
            cols[j]=scols[j];
            vals[j]=svals[j];
        }

        for(int j=0;j<wcount;j++){
            vals[trueCount+j]=whereValues[j];

        }

        return update(conn,tableName,cols,where,vals);
    }

    /**
     * 实现将一个实体类数据更新到数据库
     * 前提是，实体类类名和表名对应
     * 属性名和列名对应
     * 用法：
     * int effecline=update(conn,new Admin(),new String[]{"id","other"},"id=?",1001);
     * @param conn 数据库连接对象
     * @param obj 要保存的实体类对象
     * @param removeAttrs 不要更新的属性名数组，这些将不会被更新
     * @param where 更新的预处理条件语句
     * @param whereValues 更新条件语句的预处理值
     * @param <T> 对象类型
     * @return 影响的行数
     * @throws UtilException
     */
    public static<T> int update(Connection conn,T obj,String[] removeAttrs,String where,Object  ... whereValues) throws UtilException {
        Class clazz=obj.getClass();
        String tableName=DBClassUtil.getLastClassName(clazz.getName());
        Map<String,Object> maps=getKeyValuesMapFromBean(obj);
        for(String attr : removeAttrs){
            maps.remove(attr);
        }
        return update(conn,tableName,maps,where,whereValues);
    }

    /**
     * 将实体类对象保存到数据库
     * 如果插入失败，则意味着可能已经存在，那么尝试进行更新，也就是上面两个方法的整合
     * 前提是，实体类类名和表名对应
     * 属性名和列名对应
     * 如果你不确定，那么请依旧使用原始的方式进行
     * 用法：
     * int effecline=saveTo(conn,new Admin(),new String[]{"id"},new String[]{"id,"other"},"id=?",1001);
     * @param conn 数据库连接对象
     * @param obj 实体类对象
     * @param removeInsertAttrs 执行插入时要移除的属性名
     * @param removeUpdateAttrs 执行更新时要移除的属性名
     * @param whereUpdate 更新的条件
     * @param whereUpdateValues 更新条件的值
     * @param <T> 实体类类型
     * @return 影响的行数
     * @throws UtilException
     */
    public static<T> int saveTo(Connection conn,T obj,String[] removeInsertAttrs,String[] removeUpdateAttrs,String whereUpdate,Object ... whereUpdateValues) throws UtilException {
        int effecline=-1;
        try{
           effecline=insert(conn,obj,removeInsertAttrs);
        }catch(Exception e){
            effecline=update(conn,obj,removeUpdateAttrs,whereUpdate,whereUpdateValues);
        }
        return effecline;
    }

    /**
     * 一下两个方法，是用于将实体类的属性和值与Map进行相互转换
     * 这样的话，结合上面的方法，即可方便地将一个实体类和数据库直接对接
     * 用法：
     * 创建一个对象
     * Role role=new Role(1,"test",100,1,"descInfo",new Timestamp(0),new Timestamp(0),1,1,"other");
     * 获得键值对
     * Map<String,Object> maps=MySQLUtil.getKeyValuesMapFromBean(role);
     * 移除自动增长的主键后直接插入
     * maps.remove("id");
     * MySQLUtil.insert(conn,"Role",maps);
     * 由于id已经移除，并且name在条件中，因此再移除name,并执行更新
     * maps.remove("names");
     * MySQLUtil.update(conn,"Role",maps,"id=? and names like ?",555,"%kkg%");
     *
     * @param obj 对象
     * @param <T> 类型
     * @return 键值对，键为属性名，值为属性值
     */
    public static<T> Map<String,Object> getKeyValuesMapFromBean(T obj){
        return DBClassUtil.getFieldsMapByObject(obj);
    }

    /**
     * 将一个Map映射为一个实体类对象
     * 前提：
     * 实体类对象具有默认构造
     * 数据集中列名和实体类对象属性名存在交集
     * 并且交集的数据类型一致
     * 用法：
     * DBResultData ret=MySQLUtil.queryRawData(stat,true);
     * List<Map<String,Object>> lines=ret.getDatas();
     * Admin admin=MySQLUtil.getBeanFromKeyValueMap(lines.get(0),Admin.class,false);
     * @param maps 键值对
     * @param clazz 类类型
     * @param ignoreCase 是否忽略列名与属性名大小写进行比较
     * @param <T> 类型
     * @return 实体对象
     */
    public static <T> T getBeanFromKeyValueMap(Map<String,Object> maps,Class<T> clazz,boolean ignoreCase){
        return DBClassUtil.getObjectByFieldsMap(maps,clazz,ignoreCase);
    }
    //转换为数据库时间戳Timestamp，数据库字段：datetime
    public static java.sql.Timestamp convert2Timestamp(java.util.Date date){
        return new java.sql.Timestamp(date.getTime());
    }
    public static java.sql.Timestamp convert2Timestamp(java.sql.Date date){
        return new java.sql.Timestamp(date.getTime());
    }
    public static java.sql.Timestamp convert2Timestamp(java.sql.Time time){
        return new java.sql.Timestamp(time.getTime());
    }
    //转换为一般使用的Date,从常用的数据库时间类型转换
    public static java.util.Date convert2Date(java.sql.Timestamp date){
        return new java.util.Date(date.getTime());
    }
    public static java.util.Date convert2Date(java.sql.Date date){
        return new java.util.Date(date.getTime());
    }
    public static java.util.Date convert2Date(java.sql.Time time){
        return new java.util.Date(time.getTime());
    }

    public static java.sql.Timestamp Now(){
        return new Timestamp(new java.util.Date().getTime());
    }

    //将类名转换为表名
    public static<T> String convert2TableName(Class<T> clazz){
        return DBClassUtil.getLastClassName(clazz.getName());
    }

    /**
     * 分第pageIndex[0-n]页(每页最多pageLimit条数据)查询表tableName，根据where条件查询按照指定的orderBy查询
     * 返回值：
     *      DBPageData
     *          此方法中，返回值中的data属性保存的是DBResultData对象，需要注意
     * 用法：
     * //查询年龄大于22岁的Admin，每页最多显示5条数据，本次查询第0页数据
     * @param conn 数据库连接对象
     * @param tableName 表名
     * @param pageIndex 页索引，从0开始
     * @param pageLimit 页数据量
     * @param orderBy SQL排序拼接部分
     * @param where SQL条件,允许为空，那就是查询全表
     * @param whereParams 条件参数，当where为空时，无效
     * @return DBPageData 对象
     * @throws UtilException
     */
    public static DBPageData<DBResultData> queryPageRawData(Connection conn,String tableName,
                                                   int pageIndex,int pageLimit,
                                                   String orderBy,
                                                   String where,Object ... whereParams) throws UtilException {
        DBPageData<DBResultData> ret=null;
        String sql=null;
        List params=new ArrayList();
        if(where==null){
            sql="select * from "+tableName+" order by "+orderBy+" limit ?,?;";
        }else{
            sql="select * from "+tableName+" where "+where+" order by "+orderBy+" limit ?,?;";
            for(Object obj : whereParams){
                params.add(obj);
            }
        }

        params.add(pageIndex*pageLimit);
        params.add(pageLimit);
        PreparedStatement stat=PreparedStatementBuilder.makeByList(conn,sql,params);
        DBResultData rs=queryRawData(stat,true);

        sql=null;
        if(where==null){
            sql="select * from "+tableName+";";
            stat=PreparedStatementBuilder.make(conn,sql);
        }else{
            sql="select * from "+tableName+" where "+where+";";
            stat=PreparedStatementBuilder.make(conn,sql,whereParams);
        }
        Integer count=queryObject(stat,true);

        ret=new DBPageData<DBResultData>(pageIndex,pageLimit,count,rs);
        return ret;
    }

    /**
     * 方法同上，区别是返回值
     * data属性被赋值为List<T>对象
     * @param conn 数据库连接对象
     * @param clazz 实体类类类型
     * @param tableName 表名
     * @param pageIndex 页索引
     * @param pageLimit 页大小
     * @param orderBy 排序部分
     * @param where 条件部分
     * @param whereParams 条件参数
     * @param <T> 实体类类型
     * @return DBPageData对象
     * @throws UtilException
     */
    public static<T> DBPageData<List<T>> queryPageBeans(Connection conn,Class<T> clazz,String tableName,
                                              int pageIndex,int pageLimit,
                                              String orderBy,
                                              String where,Object ... whereParams) throws UtilException {
        DBPageData<List<T>> ret=null;
        String sql=null;
        List params=new ArrayList();
        if(where==null){
            sql="select * from "+tableName+" order by "+orderBy+" limit ?,?;";
        }else{
            sql="select * from "+tableName+" where "+where+" order by "+orderBy+" limit ?,?;";
            for(Object obj : whereParams){
                params.add(obj);
            }
        }

        params.add(pageIndex*pageLimit);
        params.add(pageLimit);
        PreparedStatement stat=PreparedStatementBuilder.makeByList(conn,sql,params);
        List<T> rs=queryBeans(clazz,stat,true);

        sql=null;
        if(where==null){
            sql="select * from "+tableName+";";
            stat=PreparedStatementBuilder.make(conn,sql);
        }else{
            sql="select * from "+tableName+" where "+where+";";
            stat=PreparedStatementBuilder.make(conn,sql,whereParams);
        }
        Integer count=queryObject(stat,true);

        ret=new DBPageData<List<T>>(pageIndex,pageLimit,count,rs);
        return ret;
    }
    //方法同上，区别在于直接使用类名作为表名
    public static<T> DBPageData<List<T>> queryPageBeans(Connection conn,Class<T> clazz,
                                                        int pageIndex,int pageLimit,
                                                        String orderBy,
                                                        String where,Object ... whereParams) throws UtilException {
        return queryPageBeans(conn,clazz,
                convert2TableName(clazz),
                pageIndex,pageLimit,
                orderBy,
                where,whereParams);
    }

    public static DBResultData queryInRawData(Connection conn,String tableName,String inColName,Object ... inParams) throws UtilException {
        if(inParams.length==0){
            throw new UtilException(UtilException.ErrCode.ARGUMENT_ERR,"sql in segment least need one arg");
        }
        StringBuilder inBuilder=new StringBuilder();
        for(int i=0;i<inParams.length;i++){
            if(i!=0){
                inBuilder.append(",");
            }
            inBuilder.append("?");
        }
        String sql="select * from "+tableName+" where "+inColName+" in ("+inBuilder.toString()+");";
        return queryRawData(conn,true,sql,inParams);
    }
    public static<T> List<T> queryInBeans(Connection conn,Class<T> clazz,String inColName,Object ... inParams) throws UtilException {
        if(inParams.length==0){
            throw new UtilException(UtilException.ErrCode.ARGUMENT_ERR,"sql in segment least need one arg");
        }
        StringBuilder inBuilder=new StringBuilder();
        for(int i=0;i<inParams.length;i++){
            if(i!=0){
                inBuilder.append(",");
            }
            inBuilder.append("?");
        }
        String tableName=DBClassUtil.getLastClassName(clazz.getName());
        String sql="select * from "+tableName+" where "+inColName+" in ("+inBuilder.toString()+");";
        return queryBeans(conn,clazz,true,sql,inParams);
    }
}

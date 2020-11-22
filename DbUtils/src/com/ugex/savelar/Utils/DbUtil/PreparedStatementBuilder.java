package com.ugex.savelar.Utils.DbUtil;

import com.ugex.savelar.Utils.UtilException;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.List;

/**
 * 常用数据库类型预处理设置建造者
 * 实现流式调用,下标自动增长
 * PreparedStatementBuilder builder=new PreparedStatementBuilder(conn,sql);
 * PreparedStatement stat=builder.setInt(1).setDouble(1.25).setString("aaa").build();
 * 当然，这个类并不包含全部的类型建造，因此，你依然可以在调用build()之后，
 * 获取到下一个下标，通过方法：getIndex()
 * 如果还想使用此类进行构建，你可以使用其他构造函数
 * 新增方法：addObj(Obj val);
 * 提供无需考虑类型的进行设置值，之后你可以这样使用了，但是支持的类型有限，但是基本覆盖常用类型，具体看方法注释
 * PreparedStatement stat=builder.addObj(1).addObj(1.25).addObj("aaa").build();
 */
public class PreparedStatementBuilder {
    private PreparedStatement stat=null;
    private int index=1;
    public PreparedStatementBuilder(Connection conn, String sql) throws UtilException {
        try {
            stat=conn.prepareStatement(sql);
            index=1;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.OTHER_ERR,sql,
                    "make PreparedStatement error:"+sql);
        }

    }
    public PreparedStatementBuilder(PreparedStatement stat){
        this.stat=stat;
        index=1;
    }
    //注意，下标从1开始
    public PreparedStatementBuilder(PreparedStatement stat,int beginIndex){
        this.stat=stat;
        index=beginIndex;
    }
    public PreparedStatement build(){
        return stat;
    }
    public int getIndex(){
        return index;
    }
    public PreparedStatementBuilder setInt(int val) throws UtilException {
        try {
            stat.setInt(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setInt("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setString(String val) throws UtilException {
        try {
            stat.setString(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setString("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setBigDecimal(BigDecimal val) throws UtilException {

        try {
            stat.setBigDecimal(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setBigDecimal("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setBoolean(boolean val) throws UtilException {

        try {
            stat.setBoolean(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setBoolean("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setDouble(double val) throws UtilException {

        try {
            stat.setDouble(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setDouble("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setFloat(float val) throws UtilException {

        try {
            stat.setFloat(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setFloat("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setLong(long val) throws UtilException {

        try {
            stat.setLong(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setLong("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setShort(short val) throws UtilException {

        try {
            stat.setShort(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setShort("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setBytes(byte[] val) throws UtilException {

        try {
            stat.setBytes(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setBytes("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setByte(byte val) throws UtilException {

        try {
            stat.setByte(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setByte("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setDate(java.sql.Date val) throws UtilException {

        try {
            stat.setDate(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setDate("+index+","+val+") error");
        }
        return this;
    }

    public PreparedStatementBuilder setTimestamp(Timestamp val) throws UtilException {

        try {
            stat.setTimestamp(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setTimestamp("+index+","+val+") error");
        }
        return this;
    }
    //不建议使用，因为这个Object实际上需要时一个：java.sql.Date
    public PreparedStatementBuilder setObject(Object val) throws UtilException {

        try {
            stat.setObject(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setObject("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setURL(URL val) throws UtilException {

        try {
            stat.setURL(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setURL("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setArray(Array val) throws UtilException {

        try {
            stat.setArray(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setArray("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setBlob(Blob val) throws UtilException {

        try {
            stat.setBlob(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setBlob("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setClob(Clob val) throws UtilException {

        try {
            stat.setClob(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setClob("+index+","+val+") error");
        }
        return this;
    }
    public PreparedStatementBuilder setSQLXML(SQLXML val) throws UtilException {
        try {
            stat.setSQLXML(index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatement setSQLXML("+index+","+val+") error");
        }
        return this;
    }

    //支持类型参见：setAnyObj()方法注释
    public PreparedStatementBuilder addAnyObj(Object val) throws UtilException {
        try {
            setAnyObj(stat,index,val);
            index++;
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatementBuilder addAnyObj("+index+","+val+") error");
        }
        return this;
    }

    /**
     * 给PreparedStatement的index个参数赋值为val
     * 注意，这里的index是和PreparedStatement一致的，也就是都是从1开始
     * 支持类型：int double BigDecimal String float Timestamp boolean java.sql.Date Array byte[] Blob SQLXML Long short
     * java.util.Date(虽然支持，但是对应的是数据库中的Timestamp类型，这个需要注意)
     * @param stat
     * @param index
     * @param val
     * @return
     * @throws UtilException
     */
    public static PreparedStatement setAnyObj(PreparedStatement stat,int index,Object val) throws UtilException {
        try {
            Class clazz = val.getClass();
            if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
                stat.setInt(index, (Integer) val);
            } else if (clazz.equals(String.class)) {
                stat.setString(index, (String) val);
            } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
                stat.setDouble(index, (Double) val);
            } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
                stat.setFloat(index, (Float) val);
            } else if (clazz.equals(BigDecimal.class)) {
                stat.setBigDecimal(index, (BigDecimal) val);
            } else if (clazz.equals(Timestamp.class)) {
                stat.setTimestamp(index, (Timestamp) val);
            } else if (clazz.equals(java.util.Date.class)) {
                stat.setTimestamp(index, new Timestamp(((java.util.Date) val).getTime()));
            } else if (clazz.equals(java.sql.Time.class)) {
                stat.setTimestamp(index, new Timestamp(((java.sql.Time) val).getTime()));
            } else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
                stat.setBoolean(index, (Boolean) val);
            } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
                stat.setLong(index, (Long) val);
            } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
                stat.setShort(index, (Short) val);
            } else if (clazz.equals(java.sql.Date.class)) {
                stat.setDate(index, (java.sql.Date) val);
            } else if (clazz.equals(URL.class)) {
                stat.setURL(index, (URL) val);
            } else if (clazz.equals(Array.class)) {
                stat.setArray(index, (Array) val);
            } else if (clazz.equals(byte[].class) || clazz.equals(Byte[].class)) {
                stat.setBytes(index, (byte[]) val);
            } else if (clazz.equals(Blob.class)) {
                stat.setBlob(index, (Blob) val);
            } else if (clazz.equals(SQLXML.class)) {
                stat.setSQLXML(index, (SQLXML) val);
            } else {
                throw new UtilException(UtilException.ErrCode.ILLEGAL_STATE_ERR,"Unsupported Object Type of " + clazz.getName()+":"+val);
            }
        }catch (SQLException e){
            throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,val,
                    "PreparedStatementBuilder setAnyObj("+index+","+val+") error:"+stat.toString());
        }
        return stat;
    }

    /**
     * 直接生成预处理的执行对象，使用可变参数实现
     * 预处理语句中，有几个占位符，就需要几个参数，可以少于，但是不能多于
     * 注意，并不支持所有的数据类型，仅仅支持常见的SQL对象作为参数，否则将会被跳过参数
     * 支持类型：int double BigDecimal String float Timestamp boolean java.sql.Date Array byte[] Blob SQLXML Long short
     * java.util.Date(虽然支持，但是对应的是数据库中的Timestamp类型，这个需要注意)
     * 用法：
     * PreparedStatement stat=makePreparedStatement(conn,"update Admin set name=?,age=?,money=?,weight=? where id=?","Mr.Li",27,new BigDecimal(1200.52),78.25,55);
     * 相比较于上面的流式调用来说，这样更为直接，但是支持的类型是有限的，虽然对于日常数据库使用来说已经足够了
     * @param conn 数据库连接对象
     * @param prepareSql 预处理的SQL语句
     * @param objs 预处理参数
     * @return 预处理执行对象
     * @throws UtilException
     */
    public static PreparedStatement make(Connection conn, String prepareSql, Object ... objs) throws UtilException {
        PreparedStatement stat=null;
        try{
            stat=conn.prepareStatement(prepareSql);
            int iindex=1;
            for(int i=0;i<objs.length;i++){
                try {
                    setAnyObj(stat, iindex, objs[i]);
                    iindex++;
                }catch (UtilException e){
                    stat.close();
                    stat=null;
                    throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,prepareSql,
                            "PreparedStatementBuilder make error:"+prepareSql);
                }
            }
        }catch(Exception e){
            try {
                stat.close();
                stat=null;
            } catch (SQLException ex) {
                throw new UtilException(ex,UtilException.ErrCode.SQL_EXEC_ERR,stat,
                        "close PreparedStatement error:"+prepareSql);
            }
            throw new UtilException(e,UtilException.ErrCode.SQL_CONN_ERR,conn,
                    "create PreparedStatement error or other:"+prepareSql);
        }
        return stat;
    }
    //和上面的方法功能一样，只是使用数组作为预处理参数的值
    public static PreparedStatement makeByArray(Connection conn, String prepareSql, Object[] objs) throws UtilException {
        PreparedStatement stat=null;
        try{
            stat=conn.prepareStatement(prepareSql);
            int iindex=1;
            for(int i=0;i<objs.length;i++){
                try {
                    setAnyObj(stat, iindex, objs[i]);
                    iindex++;
                }catch (UtilException e){
                    stat.close();
                    stat=null;
                    throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,prepareSql,
                            "PreparedStatementBuilder make error:"+prepareSql);
                }
            }
        }catch(Exception e){
            try {
                stat.close();
                stat=null;
            } catch (SQLException ex) {
                throw new UtilException(ex,UtilException.ErrCode.SQL_EXEC_ERR,stat,
                        "close PreparedStatement error:"+prepareSql);
            }
            throw new UtilException(e,UtilException.ErrCode.SQL_CONN_ERR,conn,
                    "create PreparedStatement error or other:"+prepareSql);
        }
        return stat;
    }
    //和上面的方法功能一样，只是使用List作为预处理参数的值
    public static PreparedStatement makeByList(Connection conn, String prepareSql, List objs) throws UtilException {
        PreparedStatement stat=null;
        try{
            stat=conn.prepareStatement(prepareSql);
            int iindex=1;
            for(int i=0;i<objs.size();i++){
                try {
                    setAnyObj(stat, iindex, objs.get(i));
                    iindex++;
                }catch (UtilException e){
                    stat.close();
                    stat=null;
                    throw new UtilException(e,UtilException.ErrCode.ARGUMENT_ERR,prepareSql,
                            "PreparedStatementBuilder make error:"+prepareSql);
                }
            }
        }catch(Exception e){
            try {
                stat.close();
                stat=null;
            } catch (SQLException ex) {
                throw new UtilException(ex,UtilException.ErrCode.SQL_EXEC_ERR,stat,
                        "close PreparedStatement error:"+prepareSql);
            }
            throw new UtilException(e,UtilException.ErrCode.SQL_CONN_ERR,conn,
                    "create PreparedStatement error or other:"+prepareSql);
        }
        return stat;
    }
}

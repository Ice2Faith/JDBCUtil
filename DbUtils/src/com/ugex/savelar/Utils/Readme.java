package com.ugex.savelar.Utils;

import com.ugex.savelar.Utils.DbUtil.DBResultData;
import com.ugex.savelar.Utils.DbUtil.ITransaction;
import com.ugex.savelar.Utils.DbUtil.MySQLUtil;
import com.ugex.savelar.Utils.DbUtil.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 下面是关于本工具包的使用简介范例
 * 前提：
 * 包含以下JAR到项目中：
 * commons-pool2-2.5.0.jar
 * gson-2.8.6.jar
 * jedis-2.9.0.jar
 * mysql-connector-java-8.0.17.jar
 */
public class Readme {
    public static void testDB_MYSQL_Util() throws UtilException {
        try{
            ///////////////////////////////////////////////////////////
            //数据库的链接和断开示例
            //以及一般语句的简单使用
            Connection conn= MySQLUtil.getConnect("localhost","testDB","root","123456");
            //1.语句的使用方式，直接使用语句，或者直接拼接语句
            int effecLine=MySQLUtil.updateDirect(conn,"delete from Test where id=1001;");
            //2.使用简单预处理
            PreparedStatement stat=new PreparedStatementBuilder(conn,"update Test set title=?,size=?,ptime=? where id=?;")
                    .setString("test Title")
                    .setInt(12)
                    .setTimestamp(new Timestamp(0))
                    .setInt(1001)
                    .build();
            effecLine=MySQLUtil.updatePrepared(stat,true);
            //3.使用快捷预处理
            effecLine=MySQLUtil.updatePrepared(conn,
                    "update Test set title=?,size=?,ptime=? where id=?;",
                    "test Title",12,new Timestamp(0),1001);
            //4.使用快捷的针对单一表的预处理
            effecLine=MySQLUtil.update(conn,"Test",new String[]{"title","size","ptime"},"id=?",
                    "test Title",12,new Timestamp(0),1001);
            //5.使用简单查询
            DBResultData resultData=MySQLUtil.queryRawData(conn,"select * from Test where id=1001;");
            //6.使用简单预处理查询
            stat=new PreparedStatementBuilder(conn,"select * from Test where name like ? and size>?;")
                    .setString("%Title%")
                    .setInt(12)
                    .build();
            resultData=MySQLUtil.queryRawData(stat,true);
            //7.使用快捷预处理查询
            resultData=MySQLUtil.queryRawData(conn,false,
                    "select * from Test where name like ? and size>?;",
                    "%Title%", 12);
            //8.单一表查询
            resultData=MySQLUtil.query(conn,"Test",new String[]{"title","size"},
                    "name like ? and size>?","%Title%", 12);
            //9.实体类属性名和数据表列名存在交集时查询,
            List<Test> result=MySQLUtil.queryBeans(conn,Test.class,true,
                    "select * from Test where name like ? and size>?;",
                    "%Title%", 12);
            //10.已经有了一个实体类对象，并且数据库表结构一样，这个时候执行插入或者更新
            Test obj=new Test();
            Map<String,Object> maps=MySQLUtil.getKeyValuesMapFromBean(obj);
            //11.如果主键是id并且自动增长，那么移除主键，执行插入
            Integer id=(Integer) maps.remove("id");
            effecLine=MySQLUtil.insert(conn,"Test",maps);
            //12.执行更新，注意更新的时候，如果还有其他列作为更新条件，请先移除
            //maps.remove("name");
            effecLine=MySQLUtil.update(conn,"Test",maps,"id=?",id);
            //13.按照事务进行处理
            MySQLUtil.doTrans(conn, new ITransaction() {
                public void doTrans(Connection conn, Object ... params) throws Exception {
                    //这里填写你要在同一个事务中做的事情
                    //比如，插入的同时做更新等操作
                    //前提是，你必须使用传入的Connection,而不是其他全局的Connection
                    //否则事务是不会成功的
                    //一旦这里出现异常，请直接抛出，除非你能够解决
                    //如果抛出异常，将会自动回滚操作
                }
            });
            //14.最后，还提供了大量的重载函数以供不同情况下使用，你可以尽情探索
            MySQLUtil.update(conn,new Test(),new String[]{"id"},"id=?",1001);



            MySQLUtil.disConnect(conn);
        }catch(Exception e){
            throw new UtilException(e);
        }
    }
}

class Test{
    private int id;
    private String name;
    private String title;
    private int size;
    public Test(){

    }
}
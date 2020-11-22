package com.ugex.savelar.Demos.mybatisDemo.test;

import com.ugex.savelar.Demos.mybatisDemo.dao.StudentDao;
import com.ugex.savelar.Demos.mybatisDemo.model.Student;
import com.ugex.savelar.Utils.UtilException;
import com.ugex.savelar.Utils.Common.LogUtil;
import com.ugex.savelar.Utils.DbUtil.MySQLUtil;
import com.ugex.savelar.Utils.MyBatis.IMyBatisDao;
import com.ugex.savelar.Utils.MyBatis.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.util.List;

public class Test {
    static {
        Initials();
    }

    public static void Initials(){
        MyBatisUtil.Initial("com/ugex/savelar/Demos/mybatisDemo/resources/mybatis.xml");
        LogUtil.Initial("D:\\test_log.log");
    }
    public static void main(String[] args){
        //myBatisTest();
        //myBatisInterfaceTest();
        logPrintWriteTest();
        //mysqlInTest();
    }
    public static void myBatisTest(){
        SqlSession sqlSession=MyBatisUtil.getSqlSession();
        StudentDao dao=MyBatisUtil.getDao(sqlSession, StudentDao.class);
        List<Student> list=dao.findAll();
        for(Student stu : list){
            System.out.println(stu);
        }
        MyBatisUtil.commitAndClose(sqlSession);
    }
    public static void myBatisInterfaceTest(){
        List<Student> list=MyBatisUtil.Do(StudentDao.class, new IMyBatisDao<StudentDao, List<Student>>() {
            @Override
            public List<Student> toDo(StudentDao dao,Object ... params) {
                return dao.findAll();
            }
        });
        for(Student stu : list){
            System.out.println(stu);
        }
    }
    public static void logPrintWriteTest(){
        LogUtil.logInfo("hahah");
        LogUtil.logError("errof:111");
        LogUtil.logWarning("some warn occurred");
        LogUtil.logLocalInfo(Test.class,"test info");
        LogUtil.logLocalError(new Student(101),"test");
    }

    public static Connection getConn() throws UtilException {
        return MySQLUtil.getConnect("localhost","MybaitsTestDB","root","ltb12315");
    }
    public static void mysqlInTest(){
        try {
            List<Student> list=MySQLUtil.queryInBeans(getConn(),Student.class,"id",1,3,5,7,9,10);
            for(Student stu : list){
                System.out.println(stu);
            }
        } catch (UtilException e) {
            e.printStackTrace();
        }
    }
}

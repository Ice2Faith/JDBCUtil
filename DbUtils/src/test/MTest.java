package test;

import com.ugex.savelar.Utils.DbUtil.MySQLUtil;
import com.ugex.savelar.Utils.DbUtil.SqlBuilder.DeleteBuilder;
import com.ugex.savelar.Utils.DbUtil.SqlBuilder.InsertBuilder;
import com.ugex.savelar.Utils.DbUtil.SqlBuilder.SelectBuilder;
import com.ugex.savelar.Utils.DbUtil.SqlBuilder.UpdateBuilder;
import com.ugex.savelar.Utils.UtilException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class MTest {
    public static Connection getConn() throws UtilException {
        return MySQLUtil.getConnect("localhost","MybaitsTestDB","root","ltb12315");
    }
    public static void main(String[] args){
        SelectBuilder builder=new SelectBuilder("Student","*");
        List<Integer> ids=new ArrayList<>();
        ids.add(1);
        ids.add(3);
        ids.add(5);
        List<String> names=new ArrayList<>();
//        names.add("aa");
//        names.add("bb");
        String str=builder
                .addInnerJoin("Role","Student.RoleId=Role.id")
                .addLeftJoin("Permisson","Permission.RoleId=Role.id")
                .addRightJoin("Admin","Student.opeId=Admin.id")
                .addWhereRoot("age>?",12)
                .addWhereAnd("name like ?","%a%",false)
                .addWhereOr("tall>?",1.78,true)
                .addWhereAndIn("id",ids,"?")
                .addWhereOrIn("name",names,"?")
                .addOrderBy("id desc")
                .addGroupBy("age")
                .addHaving("age>?",0)
                .addLimit("?",0,10)
                .build();
        System.out.println(str);

        try{
            Connection conn=getConn();
            PreparedStatement stat=builder.buildStat(conn);
            System.out.println(stat.toString());

            InsertBuilder insertBuilder=new InsertBuilder("Student","?",
                    new String[]{"name","age","sex"},
                    "张三",20,"man");
            stat=insertBuilder.buildStat(conn);
            System.out.println(stat.toString());

            UpdateBuilder updateBuilder=new UpdateBuilder("Student","?",
                    (String) null,null,
                    new String[]{"name","age","sex"},
                    "张三",20,"man");
            stat=updateBuilder.buildStat(conn);
            System.out.println(stat.toString());

            DeleteBuilder deleteBuilder=new DeleteBuilder("Student",null);
            stat=deleteBuilder.buildStat(conn);
            System.out.println(stat.toString());

            MySQLUtil.disConnect(conn);
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }



    }
}

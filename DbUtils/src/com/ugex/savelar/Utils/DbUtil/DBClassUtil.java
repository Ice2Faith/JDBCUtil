package com.ugex.savelar.Utils.DbUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class DBClassUtil {
    /**
     * 数据库常用关键字常量定义
     */

    public static final String NOT_NULL="NOT NULL";
    public static final String UNIQUE="UNIQUE";
    public static final String AUTO_INCREMENT="AUTO_INCREMENT";

    public static final String PRIMARY_KEY="PRIMARY KEY";

    public static final String FOREIGN_KEY="FOREIGN KEY";
    public static final String REFERENCES="REFERENCES";

    public static final String GROUP_BY="GROUP BY";
    public static final String HAVING="HAVING";

    public static final String ORDER_BY="ORDER BY";
    public static final String DESC="DESC";
    public static final String ASC="ASC";

    public static final String INNER_JOIN="INNER JOIN";
    public static final String ON="ON";
    public static final String LEFT_JOIN="LEFT JOIN";
    public static final String RIGHT_JOIN="RIGHT JOIN";

    /**
     * 数据库预处理占位符
     */
    public static final String PREPARE_SYMBOL="?";

    /**
     * Java常用类型与数据库类型对照表
     */
    private static final Map<String,String> typeMaps =new HashMap<>();
    static{
        typeMaps.put("int","INT");
        typeMaps.put("Integer","INT");
        typeMaps.put("String","VARCHAR(200)");
        typeMaps.put("double","DOUBLE");
        typeMaps.put("Double","DOUBLE");
        typeMaps.put("float","FLOAT");
        typeMaps.put("Float","FLOAT");
        typeMaps.put("BigDecimal","DECIMAL(16,4)");
        typeMaps.put("boolean","BIT");
        typeMaps.put("Boolean","BIT");
        typeMaps.put("DateTime","DATETIME");
        typeMaps.put("Timestamp","DATETIME");
        typeMaps.put("Date","DATETIME");
        typeMaps.put("BigInteger","BIGINT");
        typeMaps.put("byte[]","BLOB");
    }

    /**
     * 从类根据属性和注解，解析出建表语句
     * 用法：
     * String sql=getCreateTableFromObject(Admin.class);
     * @param clz 类类型
     * @return 建表语句
     */
    public static String getCreateTableFromObject(Class clz){
        StringBuilder builder=new StringBuilder();
        builder.append("CREATE TABLE ");
        String clsName=getLastClassName(clz.getName());
        StringBuilder autoBeginNumberBuilder=new StringBuilder();

        DBTable tbann= (DBTable) clz.getDeclaredAnnotation(DBTable.class);
        if(tbann!=null){
            if("".equals(tbann.tableName())==false){
                clsName=tbann.tableName();
            }
        }

        StringBuilder foreignBuilder=new StringBuilder();

        builder.append(clsName);
        builder.append("\n(\n");

        Field[] fields=clz.getDeclaredFields();
        for(int i=0;i<fields.length;i++){
            Field item=fields[i];
            boolean isLastOne=(i==fields.length-1);

            item.setAccessible(true);

            String type=getLastClassName(item.getType().getName());
            String name=item.getName();
            String restrict="";

            boolean hasAnnType=false;
            boolean hasAnnRestrict=false;

            DBColumn ann=item.getAnnotation(DBColumn.class);
            if(ann!=null)
            {
                String annName=ann.colName();
                String annType=ann.colType();
                String annRestrict=ann.colRestrict();
                boolean annIgnore=ann.colIgnore();
                if(annIgnore){
                    continue;
                }

                boolean annPrimaryKey=ann.colPrimaryKey();
                boolean annAutoIncrementPrimaryKey=ann.colAutoIncrementPrimaryKey();

                if(annAutoIncrementPrimaryKey){
                    restrict=AUTO_INCREMENT+" "+PRIMARY_KEY;
                    String annAutoBeginNum=ann.colAutoIncrementBeginNumber();
                    if("".equals(annAutoBeginNum)==false){
                        autoBeginNumberBuilder.append(AUTO_INCREMENT);
                        autoBeginNumberBuilder.append("=");
                        autoBeginNumberBuilder.append(annAutoBeginNum);
                    }
                }
                else if(annPrimaryKey){
                    restrict=PRIMARY_KEY;
                }

                boolean annNotNull=ann.colNotNUllRestrict();
                boolean annUnique=ann.colUniqueRestrict();
                String annDefault=ann.colDefault();
                if(annNotNull){
                    restrict=restrict+" "+NOT_NULL;
                }
                if(annUnique){
                    restrict=restrict+" "+UNIQUE;
                }
                if("".equals(annDefault)==false){
                    restrict=restrict+" "+"DEFAULT "+annDefault;
                }

                if("".equals(annName)==false)
                    name=annName;
                if("".equals(annType)==false) {
                    type = annType;
                    hasAnnType=true;
                }
                if("".equals(annRestrict)==false) {
                    restrict = annRestrict;
                    hasAnnRestrict=true;
                }

                String annForeignKey=ann.colForeignKey();
                if("".equals(annForeignKey)==false){
                    foreignBuilder.append("\t");
                    foreignBuilder.append(FOREIGN_KEY);
                    foreignBuilder.append("(");
                    foreignBuilder.append(name);
                    foreignBuilder.append(")");
                    foreignBuilder.append(" ");
                    foreignBuilder.append(REFERENCES);
                    foreignBuilder.append(" ");
                    foreignBuilder.append(annForeignKey);
                    foreignBuilder.append(",\n");
                }
            }

            builder.append("\t");
            builder.append(name);

            builder.append(" ");
            if(hasAnnType){
                builder.append(type);
            }else{
                if(typeMaps.containsKey(type))
                    builder.append(typeMaps.get(type));
                else
                    builder.append(type);
            }

            if(hasAnnRestrict==false){
                if(name.equalsIgnoreCase("id")){
                    restrict=AUTO_INCREMENT+" "+PRIMARY_KEY;
                }
            }

            builder.append(" ");
            builder.append(restrict);

            if(isLastOne) {
                String foreignKeyStr=foreignBuilder.toString();
                if("".equals(foreignKeyStr)==false){
                    foreignKeyStr=foreignKeyStr.substring(0,foreignKeyStr.lastIndexOf(','))+"\n";
                    builder.append(",\n");
                    builder.append(foreignKeyStr);
                }else {
                    builder.append("\n");
                }
            }
            else
                builder.append(",\n");
        }

        builder.append(")");
        String autoInCreNum=autoBeginNumberBuilder.toString();
        if("".equals(autoInCreNum)==false){
            builder.append(autoInCreNum);
        }
        builder.append(";\n");
        String ret=builder.toString();
        return ret;
    }

    /**
     * 从类返回删表语句
     * 用法：
     * String sql=getDropTableFromObject(Admin.class);
     * @param clz 类类型
     * @return 删表语句
     */
    public static String getDropTableFromObject(Class clz){
        return "DROP TABLE "+getLastClassName(clz.getName())+";";
    }


    public static <T> String getInsertFromObject(T obj,boolean useValue){
        return getInsertFromObject(obj,useValue,false);
    }

    /**
     * 从类对象，生成全字段解析的SQL语句
     * 可直接生成完整SQL、预处理SQL、填值SQL
     * 用法：
     * String sql=getInsertFromObject(new Admin(),true,false);
     * 这样就生成一条完整值的语句
     * @param obj 对象
     * @param useValue 是否使用对象中的值填空
     * @param makePrepare 是否生成预处理语句，true时前一个参数被忽视
     * @param <T> 类型
     * @return SQL语句
     */
    public static <T> String getInsertFromObject(T obj,boolean useValue,boolean makePrepare){
        Class clz=obj.getClass();
        StringBuilder builder=new StringBuilder();

        builder.append("INSERT INTO ");

        builder.append(getLastClassName(clz.getName()));

        StringBuilder colBuilder=new StringBuilder();
        StringBuilder valBuilder=new StringBuilder();

        Field[] fields=clz.getDeclaredFields();
        for(int i=0;i<fields.length;i+=1){
            boolean isLastOne=(i==fields.length-1);

            Field item=fields[i];
            item.setAccessible(true);

            String type=getLastClassName(item.getType().getName());
            String name=item.getName();

            String colName=name;
            String colValue = getFieldValueString(obj, useValue, item);


            DBColumn ann=item.getAnnotation(DBColumn.class);
            if(ann!=null) {
                String annName = ann.colName();
                boolean annIgnore = ann.colIgnore();
                boolean annAutoIncrementPrimaryKey=ann.colAutoIncrementPrimaryKey();
                if (annAutoIncrementPrimaryKey || annIgnore) {
                    continue;
                }

                if("".equals(annName)==false)
                    name=annName;

                colValue=getFieldValueStringAnno(obj,colValue,ann,item);

            }
            colBuilder.append(name);

            if(makePrepare) {
                colValue=PREPARE_SYMBOL;
                valBuilder.append(colValue);
            }
            else{
                if(isStrValType(type)){
                    valBuilder.append("\'");
                    valBuilder.append(colValue);
                    valBuilder.append("\'");
                }else{
                    valBuilder.append(colValue);
                }
            }



            if(isLastOne==false){
                colBuilder.append(",");
                valBuilder.append(",");
            }
        }

        builder.append("(");
        builder.append(colBuilder.toString());
        builder.append(")");

        builder.append(" VALUES(");
        builder.append(valBuilder.toString());
        builder.append(");");

        String ret=builder.toString();
        return ret;
    }

    public static <T> String getSelectFromObject(T obj,boolean useValue) {
        return getSelectFromObject(obj,useValue,false,null);
    }
    public static <T> String getSelectFromObject(T obj,boolean useValue,boolean makePrepare) {
        return getSelectFromObject(obj,useValue,makePrepare,null);
    }

    /**
     * 生成查询语句，含完整字段的语句
     * @param obj 对象
     * @param useValue 使用主键作为查询值
     * @param makePrepare 主键作为预处理，true时前一个参数被忽视
     * @param where 是否自己定义where条件句，如果不为null,则前面两个参数被忽视
     * @param <T>
     * @return
     */
    public static <T> String getSelectFromObject(T obj,boolean useValue,boolean makePrepare,String where){
        Class clz=obj.getClass();
        StringBuilder builder=new StringBuilder();

        StringBuilder colBuilder=new StringBuilder();
        StringBuilder whereBuilder=new StringBuilder();

        builder.append("SELECT ");

        Field[] fields=clz.getDeclaredFields();
        for(int i=0;i<fields.length;i+=1){
            boolean isLastOne=(i==fields.length-1);
            boolean isFirstOne=(i==0);

            Field item=fields[i];
            item.setAccessible(true);

            String type=getLastClassName(item.getType().getName());
            String name=item.getName();

            String colValue = getFieldValueString(obj, useValue, item);

            DBColumn ann=item.getAnnotation(DBColumn.class);
            if(ann!=null) {
                String annName = ann.colName();
                boolean annIgnore = ann.colIgnore();
                if (annIgnore) {
                    continue;
                }

                if("".equals(annName)==false)
                    name=annName;

                boolean annPrimaryKey=ann.colPrimaryKey();
                boolean annAutoIncrementPrimaryKey=ann.colAutoIncrementPrimaryKey();
                if(annPrimaryKey || annAutoIncrementPrimaryKey) {
                    if(isFirstOne==false){
                        whereBuilder.append(" AND ");
                    }
                    whereBuilder.append(name);
                    whereBuilder.append("=");
                    if(makePrepare) {
                        colValue=PREPARE_SYMBOL;
                        whereBuilder.append(colValue);
                    }else{
                        if(isStrValType(type)){
                            whereBuilder.append("\'");
                            whereBuilder.append(colValue);
                            whereBuilder.append("\'");
                        }else{
                            whereBuilder.append(colValue);
                        }
                    }

                }

                colValue=getFieldValueStringAnno(obj,colValue,ann,item);
            }
            colBuilder.append(name);


            if(isLastOne==false){
                colBuilder.append(",");
            }
        }

        builder.append(colBuilder.toString());

        builder.append(" FROM ");
        builder.append(getLastClassName(clz.getName()));

        builder.append(" WHERE ");
        if(where==null) {
            builder.append(whereBuilder.toString());
        }else{
            builder.append(where);
        }
        builder.append(";");

        String ret=builder.toString();
        return ret;
    }

    public static <T> String getUpdateFromObject(T obj,boolean useValue){
        return getUpdateFromObject(obj,useValue,false,null);
    }
    public static <T> String getUpdateFromObject(T obj,boolean useValue,boolean makePrepare){
        return getUpdateFromObject(obj,useValue,makePrepare,null);
    }
    public static <T> String getUpdateFromObject(T obj,boolean useValue,boolean makePrepare,String where){
        Class clz=obj.getClass();
        StringBuilder builder=new StringBuilder();

        StringBuilder upBuilder=new StringBuilder();

        StringBuilder whereBuilder=new StringBuilder();

        builder.append("UPDATE ");
        builder.append(getLastClassName(clz.getName()));
        builder.append(" SET ");

        Field[] fields=clz.getDeclaredFields();
        for(int i=0;i<fields.length;i+=1){
            boolean isLastOne=(i==fields.length-1);
            boolean isFirstOne=(i==0);

            Field item=fields[i];
            item.setAccessible(true);

            String type=getLastClassName(item.getType().getName());
            String name=item.getName();

            String colValue = getFieldValueString(obj, useValue, item);

            DBColumn ann=item.getAnnotation(DBColumn.class);
            if(ann!=null) {
                String annName = ann.colName();
                boolean annIgnore = ann.colIgnore();
                if (annIgnore) {
                    continue;
                }

                if("".equals(annName)==false)
                    name=annName;

                boolean annPrimaryKey=ann.colPrimaryKey();
                boolean annAutoIncrementPrimaryKey=ann.colAutoIncrementPrimaryKey();
                if(annPrimaryKey || annAutoIncrementPrimaryKey) {
                    if(isFirstOne==false){
                        whereBuilder.append(" AND ");
                    }
                    whereBuilder.append(name);
                    whereBuilder.append("=");
                    if(makePrepare) {
                        colValue=PREPARE_SYMBOL;
                        whereBuilder.append(colValue);
                    }else{
                        if("String".equals(type)){
                            whereBuilder.append("\'");
                            whereBuilder.append(colValue);
                            whereBuilder.append("\'");
                        }else{
                            whereBuilder.append(colValue);
                        }
                    }

                }
                if(annAutoIncrementPrimaryKey){
                    continue;
                }else{
                    colValue=getFieldValueStringAnno(obj,colValue,ann,item);
                }
            }

            upBuilder.append(name);
            upBuilder.append("=");
            if(makePrepare) {
                colValue=PREPARE_SYMBOL;
                upBuilder.append(colValue);
            }else{
                if(isStrValType(type)){
                    upBuilder.append("\'");
                    upBuilder.append(colValue);
                    upBuilder.append("\'");
                }else{
                    upBuilder.append(colValue);
                }
            }



            if(isLastOne==false){
                upBuilder.append(",");
            }
        }

        builder.append(upBuilder.toString());

        builder.append(" WHERE ");
        if(where==null){
            builder.append(whereBuilder.toString());
        }else{
            builder.append(where);
        }

        builder.append(";");

        String ret=builder.toString();
        return ret;
    }

    public static <T> String getDeleteFromObject(T obj,boolean useValue){
        return getDeleteFromObject(obj,useValue,false,null);
    }
    public static <T> String getDeleteFromObject(T obj,boolean useValue,boolean makePrepare){
        return getDeleteFromObject(obj,useValue,makePrepare,null);
    }
    public static <T> String getDeleteFromObject(T obj,boolean useValue,boolean makePrepare,String where){
        Class clz=obj.getClass();
        StringBuilder builder=new StringBuilder();

        builder.append("DELETE FROM ");
        builder.append(getLastClassName(clz.getName()));
        builder.append(" WHERE ");

        if(where!=null){
            builder.append(where);
            builder.append(";");
            return builder.toString();
        }

        Field[] fields=clz.getDeclaredFields();
        for(int i=0;i<fields.length;i+=1){
            boolean isFirstOne=(i==0);

            Field item=fields[i];
            item.setAccessible(true);

            String type=getLastClassName(item.getType().getName());
            String name=item.getName();

            String colValue = getFieldValueString(obj, useValue, item);


            DBColumn ann=item.getAnnotation(DBColumn.class);
            if(ann!=null) {
                String annName = ann.colName();
                boolean annIgnore = ann.colIgnore();
                if (annIgnore) {
                    continue;
                }

                if("".equals(annName)==false)
                    name=annName;

                boolean annPrimaryKey=ann.colPrimaryKey();
                boolean annAutoIncrementPrimaryKey=ann.colAutoIncrementPrimaryKey();
                if(annPrimaryKey || annAutoIncrementPrimaryKey){
                    if(isFirstOne==false){
                        builder.append(" AND ");
                    }
                    builder.append(name);
                    builder.append("=");
                    if(makePrepare) {
                        colValue=PREPARE_SYMBOL;
                        builder.append(colValue);
                    }else{
                        if(isStrValType(type)){
                            builder.append("\'");
                            builder.append(colValue);
                            builder.append("\'");
                        }else{
                            builder.append(colValue);
                        }
                    }

                }
                if(annAutoIncrementPrimaryKey){
                    continue;
                }else{
                    colValue=getFieldValueStringAnno(obj,colValue,ann,item);
                }

            }
        }


        builder.append(";");

        String ret=builder.toString();
        return ret;
    }


    /**
     * 返回带单引号包裹的字符串，用于SQL的字符串类型参数
     * @param str
     * @return
     */
    public static String makeSqlString(String str){
        return "'"+str+"'";
    }

    /**
     * 以下用于返回 like 的字符串，也即是百分号包含
     * left则表示固定串在左边，right一样的规律
     * @param str
     * @return
     */
    public static String makeSqlFullLikeString(String str){
        return "'%"+str+"%'";
    }
    public static String makeSqlLeftLikeString(String str){
        return "'"+str+"%'";
    }
    public static String makeSqlRightLikeString(String str){
        return "'%"+str+"'";
    }

    /**
     * 用于生成Order By 子句
     * 根据每个字段的升降序进行组合返回串
     * 如果desc的长度不等于cols的长度，将按照cols来决定
     * desc长度小于则多余的cols部分不指明排序方式，根据数据库自身决定
     * desc长度大于等于cols，则直接根据desc来设置升降序
     * 结果=ORDER BY col1 desc,col2 asc...
     * @param desc 每个字段是否降序，反之就是升序
     * @param cols 每个字段的字段名
     * @return order by子句
     */
    public static String makeOrderByString(boolean[] desc,String[] cols){
        StringBuilder colBuilder=new StringBuilder();
        for(int i=0;i<cols.length;i++){
            if(i==0){
                colBuilder.append(" ");
            }
            colBuilder.append(cols[i]);
            if(i<desc.length){
                colBuilder.append(" ");
                colBuilder.append((desc[i]?DESC:ASC));
            }
            if(i!=cols.length-1){
                colBuilder.append(",");
            }
        }
        return ORDER_BY+colBuilder.toString();
    }

    /**
     * 生成group by子句
     * 结果=GROUP BY col1,col2,...
     * @param cols 分组的列们
     * @return group by子句
     */
    public static String makeGroupByString(String ... cols){
        StringBuilder colBuilder=new StringBuilder();
        for(int i=0;i<cols.length;i++){
            if(i==0){
                colBuilder.append(" ");
            }
            colBuilder.append(cols[i]);
            if(i!=cols.length-1){
                colBuilder.append(",");
            }
        }
        return GROUP_BY+colBuilder.toString();
    }

    /**
     * 生成连接子串
     * 规则：
     * 结果=INNER JOIN tableName on whereOn
     * @param tableName
     * @param whereOn
     * @return
     */
    public static String makeInnerJoinString(String tableName,String whereOn){
        return INNER_JOIN+" "+tableName+" "+ON+" "+whereOn;
    }
    public static String makeLeftJoinString(String tableName,String whereOn){
        return LEFT_JOIN+" "+tableName+" "+ON+" "+whereOn;
    }
    public static String makeRightJoinString(String tableName,String whereOn){
        return RIGHT_JOIN+" "+tableName+" "+ON+" "+whereOn;
    }

    /**
     * 获取一个对象的所有属性和属性值，形成键值对返回
     * @param obj 对象
     * @param <T> 类型
     * @return 键值对
     */
    public static<T> Map<String,Object> getFieldsMapByObject(T obj){
        Map<String,Object>  ret=new HashMap<>();
        Class clazz=obj.getClass();
        Field[] fields=clazz.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            try {
                ret.put(field.getName(),field.get(obj));
            } catch (IllegalAccessException e) {
                //e.printStackTrace();
                ret.put(field.getName(),null);
            }
        }
        return ret;
    }

    /**
     * 根据键值对生成目标类对象
     * 用法：
     * Admin admin=getObjectByFieldsMap(adminMap,Admin.class,true);
     * 这样，就把adminMap中的键值按照键和Admin的属性进行匹配赋值，这里忽略属性和列名的大小写，最终返回了一个对象
     * @param maps 键值对
     * @param clazz 类类型
     * @param ignoreCase 是否忽略键值和属性大小写匹配
     * @param <T> 类型
     * @return 对象
     */
    public static <T> T getObjectByFieldsMap(Map<String,Object> maps,Class<T> clazz,boolean ignoreCase){
        T ret=null;
        try {
            ret=clazz.newInstance();
            Field[] fields=clazz.getDeclaredFields();
            for(Field field : fields){
                String name=field.getName();
                if(ignoreCase){
                    name=getTrueKeyIgnoreCaseInMap(maps,name);
                }

                if(name==null)
                    continue;
                if(maps.containsKey(name)==false)
                    continue;

                try{
                    field.set(ret,maps.get(name));
                }catch(Exception e){

                }

            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ret;
    }

    ///////////////////////////////////////////////////////////////////////
    //以下都是私有方法，不公开的，因此可以不用细看

    /**
     * 根据传入的key获取map中和此key忽略大小写的真实key
     * 因为你给的key，可能由于大小写不一致，导致在map中找不到键
     * 这个方法就是，根据不区分大小写的key去获得map中对应能用的key
     * @param maps 映射
     * @param key 不区分大小写的key
     * @return 真实可用的key
     */
    private static String getTrueKeyIgnoreCaseInMap(Map<String,Object> maps,String key){
        String ret=null;
        for(String pk : maps.keySet()){
            if(pk.equalsIgnoreCase(key)){
                ret=pk;
                break;
            }
        }
        return ret;
    }


    /**
     * 通过反射获取的类名，是带有包名的，完整类名
     * 但是在进行字符串比较时，一般只使用纯粹的类名
     * 作用：
     * 完整类名：java.lang.String
     * 此方法返回：String
     * @param fullName 完整类名
     * @return 最后短类名
     */
    public static String getLastClassName(String fullName){
        int indexDot=fullName.lastIndexOf(".");
        String name=fullName;
        if(indexDot>=0)
            name=fullName.substring(indexDot+1);
        return name;
    }

    /**
     * 获取指定属性的字符串值
     * 建立在toString()身上
     * 另外处理，属性值为null时，返回空串而不是null
     * @param obj 被获取属性的对象
     * @param useValue 是否使用类的属性值，不使用直接返回空串
     * @param item 属性
     * @param <T> 类型
     * @return 属性值串
     */
    private static <T> String getFieldValueString(T obj, boolean useValue, Field item) {
        String colValue = "";
        if (useValue) {
            try {
                Object probj = item.get(obj);
                if (probj != null)
                    colValue = "" + probj;
            } catch (IllegalAccessException e) {
                //e.printStackTrace();
            }
        }
        return colValue;
    }

    /**
     * 根据DBColumn注解的colToStringFunc字段指定的转换函数，转换为字符串值
     * @param obj 读取对象
     * @param colValue 原来的属性值
     * @param ann 注解
     * @param field 属性
     * @param <T> 类型
     * @return 属性串
     */
    private static<T> String getFieldValueStringAnno(T obj,String colValue,DBColumn ann,Field field){
        String annFunc=ann.colToStringFunc();
        if("".equals(colValue)){
            colValue=ann.colDefVal();
        }else{
            if("".equals(annFunc)==false){
                try {
                    colValue=""+obj.getClass().getDeclaredMethod(annFunc,Object.class).invoke(obj,field.get(obj));
                } catch (IllegalAccessException e) {
                    //e.printStackTrace();
                } catch (InvocationTargetException e) {
                    //e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    //e.printStackTrace();
                }
            }
        }
        return colValue;
    }

    /**
     * 判断类型是否在SQL语句中，作为值的时候，是否是需要单引号包含的
     * @param type
     * @return
     */
    private static boolean isStrValType(String type) {
        boolean isStrVal = false;
        if ("String".equals(type) || "Timestamp".equals(type)) {
            isStrVal = true;
        }
        return isStrVal;
    }

}

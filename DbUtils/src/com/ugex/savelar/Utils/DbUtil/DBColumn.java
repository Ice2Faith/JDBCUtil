package com.ugex.savelar.Utils.DbUtil;

import java.lang.annotation.*;

/**
 * 用于给一个Java类注解，以方便将一个Java类对象，生成其对应的SQL语句
 * 包含：建表，CRUD语句
 * 这样你可以方便的修改一下语句即可使用
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBColumn{
    //列名
    String colName() default "";
    //列类型
    String colType() default "";
    //列约束
    String colRestrict() default "";
    //不参与生成语句
    boolean colIgnore() default false;
    //属性为主键
    boolean colPrimaryKey() default false;
    //属性为默认值,值为默认值的数据
    String colDefault() default "";
    //可以设置一个将对应属性格式化的函数名，函数需要带一个Object类型的参数
    //这个参数在进行转换时会将这个属性值传给指定的函数，因此，你需要自己实现类型转换
    //函数原型：String method(Object obj);
    //注意，如果是返回对应为数据库中的字符串，那么请自行加上\'进行包裹
    String colToStringFunc() default "";
    //你可以为这一列指定一个默认值，如果对应的属性值为null时
    //不要和colDefault混淆，这个是在转换为SQL查询语句时，如果实体类该字段为null时使用此值
    String colDefVal() default "";
    //自动增长主键列，这个标识的，将不会出现在生成的insert语句中,但是其他生成语句中依然存在
    boolean colAutoIncrementPrimaryKey() default false;
    //自动增长的主键的开始值
    String colAutoIncrementBeginNumber() default "";
    //唯一约束
    boolean colUniqueRestrict() default false;
    //非空约束
    boolean colNotNUllRestrict() default false;
    //外键约束
    String colForeignKey() default "";
}

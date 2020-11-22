package com.ugex.savelar.Utils.DbUtil.SqlBuilder;

import com.ugex.savelar.Utils.DbUtil.PreparedStatementBuilder;
import com.ugex.savelar.Utils.UtilException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteBuilder {
    private StringBuilder builder=new StringBuilder();
    private List<Object> params=new ArrayList<>();

    /**
     * where条件允许为空，则表示没有删除条件，这个你自己调用需要注意，否则造成删除全表内容概不负责
     * @param tableName
     * @param where
     * @param whereParams
     */
    public DeleteBuilder(String tableName,String where,Object ... whereParams){
        builder.append("DELETE FROM ");
        builder.append(tableName);
        if(where!=null){
            builder.append(where);
            for(Object item : whereParams){
                params.add(item);
            }
        }
    }
    public String build(){
        return builder.toString();
    }
    public PreparedStatement buildStat(Connection conn) throws UtilException {
        return PreparedStatementBuilder.makeByList(conn,build(),params);
    }
}

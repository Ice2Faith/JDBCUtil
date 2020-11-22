package com.ugex.savelar.Utils.DbUtil;

import com.ugex.savelar.Utils.UtilException;

import java.sql.Connection;

public class Transaction {
    private Connection conn;

    public Transaction(Connection conn){
        this.conn=conn;
    }

    Connection getConnection(){
        return conn;
    }
    public void beginTrans() throws UtilException {
        try {
            conn.setAutoCommit(false);
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ILLEGAL_STATE_ERR,conn,
                    "begin transaction error:setAutoCommit(false)");
        }
    }
    public void commitTrans(Connection conn) throws UtilException {
        try {
            conn.commit();
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ILLEGAL_STATE_ERR,conn,
                    "commit transaction error");
        }
    }
    public void rollbackTrans(Connection conn) throws UtilException {
        try {
            conn.rollback();
        } catch (Exception e) {
            throw new UtilException(e,UtilException.ErrCode.ILLEGAL_STATE_ERR,conn,
                    "rollback transaction error");
        }
    }
}

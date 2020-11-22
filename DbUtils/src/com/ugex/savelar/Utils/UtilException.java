package com.ugex.savelar.Utils;


public class UtilException extends Exception{
    public interface ErrCode{
        int OTHER_ERR=1;

        int CONFIG_ERR=10;
        int INITIAL_ERR=11;
        int ARGUMENT_ERR=12;
        int SQL_EXEC_ERR=13;
        int SQL_CONN_ERR=14;
        int ILLEGAL_STATE_ERR=15;

        int USER_BEGIN=1000;
    }
    private int errCode;
    private Object errObj;
    public UtilException() {
        super();

    }
    public UtilException(String message) {
        super(message);

    }
    public UtilException(int errCode, String message) {
        super(message);
        this.errCode=errCode;
    }
    public UtilException(int errCode, Object errObj, String message) {
        super(message);
        this.errCode=errCode;
        this.errObj=errObj;
    }

    public UtilException(Throwable cause) {
        super(cause);
    }

    public UtilException(String message, Throwable cause) {
        super(message, cause);
    }


    public UtilException(Throwable cause, int errCode, String message) {
        super(message,cause);
        this.errCode=errCode;
    }
    public UtilException(Throwable cause, int errCode, Object errObj, String message) {
        super(message,cause);
        this.errCode=errCode;
        this.errObj=errObj;
    }

    public int getErrCode() {
        return errCode;
    }

    public Object getErrObj() {
        return errObj;
    }
}

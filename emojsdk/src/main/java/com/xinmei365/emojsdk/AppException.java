package com.xinmei365.emojsdk;

/**
 * Created by xinmei on 15/11/19.
 */
public class AppException extends Exception {

    public ExcepType mExcepType;

    public enum ExcepType{
        IOException,JSONException
    }

    public AppException() {
    }

    public AppException(ExcepType excepType,String detailMessage) {
        this(excepType,detailMessage,null);
    }

    public AppException(ExcepType excepType,String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        this.mExcepType = excepType;
    }

    public AppException(ExcepType excepType,Throwable throwable) {
        this(excepType,null,throwable);
    }
}

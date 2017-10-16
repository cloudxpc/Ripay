package com.rickxpc.ripay.web.exceptions;

public class WxPayException extends RuntimeException {
    private String returnCode;
    private String returnMsg;
    private String errorCode;
    private String errorDesc;

    public WxPayException(String message) {
        super(message);
    }

    public WxPayException(String message, Throwable cause) {
        super(message, cause);
    }

    public WxPayException(String returnCode, String returnMsg) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
    }

    public WxPayException(String returnCode, String returnMsg, String errorCode, String errorDesc) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }
}

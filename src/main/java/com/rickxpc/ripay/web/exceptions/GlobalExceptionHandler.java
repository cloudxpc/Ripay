package com.rickxpc.ripay.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(WxPayException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorModel processRuntimeException(WxPayException ex) {
        return new ErrorModel(ex.getMessage(), "Return Code: " + ex.getReturnCode() + ", Return Msg: " + ex.getReturnMsg() + ", Error Code: " + ex.getErrorCode() + ", Error Desc: " + ex.getErrorDesc());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorModel processRuntimeException(Exception ex) {
        String errorMessage = "System error occurs! Please try again later.";
        ex.printStackTrace();
        return new ErrorModel(errorMessage);
    }
}
 
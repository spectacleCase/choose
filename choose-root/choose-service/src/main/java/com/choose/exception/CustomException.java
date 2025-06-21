package com.choose.exception;


import com.choose.enums.AppHttpCodeEnum;
import lombok.Getter;

import java.util.Map;

/**
 * @author lizhentao
 */
@Getter
public class CustomException extends RuntimeException {

    private Integer code;

    private String message;

    private Map<String,Object> map;


    public CustomException(Integer code, String message,Map<String,Object> map) {
        this.code = code;
        this.message = message;
        this.map = map;
    }

    public CustomException(String message){
        this.code = AppHttpCodeEnum.SERVER_ERROR.getCode();
        this.message = message;
    }

    public CustomException(AppHttpCodeEnum appHttpCodeEnum){
        this.code = appHttpCodeEnum.getCode();
        this.message = appHttpCodeEnum.getErrorMessage();
    }

    public  CustomException(){
        this.code = AppHttpCodeEnum.SERVER_ERROR.getCode();
        this.message = AppHttpCodeEnum.SERVER_ERROR.getErrorMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }


}

package com.choose.exception;


import com.choose.enums.AppHttpCodeEnum;
import lombok.Getter;

/**
 * @author lizhentao
 */
@Getter
public class CustomException extends RuntimeException {

    private final AppHttpCodeEnum appHttpCodeEnum;

    public CustomException(AppHttpCodeEnum appHttpCodeEnum){
        this.appHttpCodeEnum = appHttpCodeEnum;
    }

}

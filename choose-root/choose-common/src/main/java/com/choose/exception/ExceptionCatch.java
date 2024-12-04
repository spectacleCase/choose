package com.choose.exception;


import com.choose.enums.AppHttpCodeEnum;
import com.choose.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 桌角的眼镜
 * 处理异常
 */
@ControllerAdvice  //控制器增强类
@Slf4j
public class ExceptionCatch {

    /**
     * 处理不可控异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result exception(Exception e){
        log.error(e.getMessage());
        log.error("catch Must not exception:{}",e.getMessage());
        return Result.error(AppHttpCodeEnum.SERVER_ERROR.getErrorMessage());
    }

    /**
     * 处理可控异常  自定义异常
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result exception(CustomException e){
        log.error("catch exception:{}",e.getMessage());
        return Result.error(e.getAppHttpCodeEnum().getErrorMessage());
    }

    /**
     * 参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result exception(MethodArgumentNotValidException e){
        log.error("argument too large:{}",e.getMessage());
        return Result.error(AppHttpCodeEnum.PARAM_INVALID.getErrorMessage());
    }
}

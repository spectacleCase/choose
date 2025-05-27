package com.choose.exception;


import com.choose.apspect.bo.SysLogBO;
import com.choose.apspect.service.C_SysLogService;
import com.choose.common.DateUtils;
import com.choose.constant.CommonConstants;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * @author 桌角的眼镜
 * 处理异常
 */
@ControllerAdvice  //控制器增强类
@Slf4j
public class ExceptionCatch {

    @Resource
    private C_SysLogService sysLogService;

    /**
     * 处理不可控异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result exception(Exception e){
        log.error(e.getMessage());
        log.error("catch Must not exception:{}",e.getMessage());
        // 异步处理
        CompletableFuture.supplyAsync(() -> {
            SysLogBO sysLogBO = new SysLogBO();
            sysLogBO.setDuration(0L);
            sysLogBO.setRemark("服务端异常");
            // 记录请求信息
            sysLogBO.setClientIp("");
            sysLogBO.setUrl("");
            sysLogBO.setUserAgent("");
            sysLogBO.setRequestType("");
            sysLogBO.setRequestTime(new Date());
            sysLogBO.setRequestContent("");

            // 记录响应信息
            sysLogBO.setResponseTime(new Date());
            sysLogBO.setResponseContent("");
            sysLogBO.setCreateDate(DateUtils.getNow());
            sysLogBO.setSuccess(CommonConstants.ResultCode.ERROR.code.toString());
            sysLogBO.setLogLevel(CommonConstants.LogLevel.ERROR);
            sysLogBO.setResponseContent(e.getMessage());
            return sysLogService.save(sysLogBO);
        });
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

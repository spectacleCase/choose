package com.choose.exception;


import com.choose.apspect.bo.SysLogBO;
import com.choose.apspect.service.C_SysLogService;
import com.choose.common.DateUtils;
import com.choose.constant.CommonConstants;
import com.choose.enums.AppHttpCodeEnum;
import com.choose.utils.Result;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.util.StringUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        // CompletableFuture.supplyAsync(() -> {
        //     SysLogBO sysLogBO = new SysLogBO();
        //     sysLogBO.setDuration(0L);
        //     sysLogBO.setRemark("服务端异常");
        //     // 记录请求信息
        //     sysLogBO.setClientIp("");
        //     sysLogBO.setUrl("");
        //     sysLogBO.setUserAgent("");
        //     sysLogBO.setRequestType("");
        //     sysLogBO.setRequestTime(new Date());
        //     sysLogBO.setRequestContent("");
        //
        //     // 记录响应信息
        //     sysLogBO.setResponseTime(new Date());
        //     sysLogBO.setResponseContent("");
        //     sysLogBO.setCreateDate(DateUtils.getNow());
        //     sysLogBO.setSuccess(CommonConstants.ResultCode.ERROR.code.toString());
        //     sysLogBO.setLogLevel(CommonConstants.LogLevel.ERROR);
        //     sysLogBO.setResponseContent(e.getMessage());
        //     return sysLogService.save(sysLogBO);
        // });
        // return Result.error(AppHttpCodeEnum.SERVER_ERROR.getErrorMessage());
        return commonException(500,e.getMessage());
    }

    /**
     * 处理可控异常  自定义异常
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result exception(CustomException e){
        log.error("catch exception:{}",e.getMessage());
        // return Result.error(e.getAppHttpCodeEnum().getErrorMessage());
        return commonException(e.getCode(),e.getMessage());
    }

    /**
     * 参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result exception(MethodArgumentNotValidException e){
        log.error("argument too large:{}",e.getMessage());
        // return Result.error(AppHttpCodeEnum.PARAM_INVALID.getErrorMessage());
        return commonException(AppHttpCodeEnum.PARAM_INVALID.getCode(),AppHttpCodeEnum.PARAM_INVALID.getErrorMessage());
    }



    private Result commonException(Integer code,String message) {
        String msg;
        Pattern pattern = Pattern.compile("^([\\w.$]+):\\s*(.*)");
        Matcher matcher = pattern.matcher(message);
        if(matcher.find()){
            msg = matcher.group(2);
        } else {
            msg = message;
        }

        if(StringUtils.hasText(msg)) {
            if(msg.contains("JWT expired at")){

                sysLogService.asyncLogSave(401,
                        "登录过期",
                        CommonConstants.LogLevel.INFO);
                return Result.error(401,"登录过期");
            }

            if(msg.contains("JWT")){
                sysLogService.asyncLogSave(401,
                        "登录过期",
                        CommonConstants.LogLevel.INFO);
                return Result.error(401,"身份认证失败，请重新登录");
            }

            sysLogService.asyncLogSave(code,
                    msg,
                    CommonConstants.LogLevel.ERROR);
            return Result.error(code, msg);
        }

        sysLogService.asyncLogSave(500,
                "未知异常，请联系开发者。",
                CommonConstants.LogLevel.ERROR);
        return  Result.error(500, "未知异常，请联系开发者。");

    }
}

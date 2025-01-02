package com.choose.apspect.aspect;

import com.choose.annotation.SysLog;
import com.choose.apspect.bo.SysLogBO;
import com.choose.apspect.service.C_SysLogService;
import com.choose.common.DateUtils;
import com.choose.common.HttpServletUtils;
import com.choose.config.UserLocalThread;
import com.choose.constant.CommonConstants;
import com.choose.josn.JsonUtil;
import com.choose.utils.common.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

;

/**
 * 系统日志切面
 * todo 需要完善
 *
 * @author 桌角的眼镜
 */
@Aspect  // 使用@Aspect注解声明一个切面
@Component
@Slf4j
public class SysLogAspect {
    @Resource
    private C_SysLogService sysLogService;

    /**
     * 这里我们使用注解的形式
     * 当然，我们也可以通过切点表达式直接指定需要拦截的package,需要拦截的class 以及 method
     * 切点表达式:   execution(...)
     * execution(public * *(..)) 任意的公共方法
     * execution（* set*（..）） 以set开头的所有的方法
     * execution（* com.LoggerApply.*（..））com.LoggerApply这个类里的所有的方法
     * execution（* com.annotation.*.*（..））com.annotation包下的所有的类的所有的方法
     * execution（* com.annotation..*.*（..））com.annotation包及子包下所有的类的所有的方法
     * execution(* com.annotation..*.*(String,?,Long)) com.annotation包及子包下所有的类的有三个参数，第一个参数为String类型，第二个参数为任意类型，第三个参数为Long类型的方法
     * execution(@annotation(com.lingyejun.annotation.Lingyejun))
     */
    @Pointcut("@annotation(com.choose.annotation.SysLog)")
    public void logPointCut() {
    }

    /**
     * 环绕通知 @Around  ， 当然也可以使用 @Before (前置通知)  @After (后置通知)
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Date requestTime = DateUtils.getNow();
        Date responseTime;
        try {
            // 执行方法
            Object result = point.proceed();
            // if(Objects.isNull(result)) {
            //     throw new Throwable();
            // }
            responseTime = DateUtils.getNow();
            this.log(point, requestTime, responseTime, result, null);
            return result;
        } catch (Throwable throwable) {
            // 记录异常日志
            responseTime = DateUtils.getNow();
            this.log(point, requestTime, responseTime, responseTime, throwable);
            throw throwable;
        }

    }

    private void log(ProceedingJoinPoint joinPoint, Date requestTime,
                     Date responseTime, Object result, Throwable throwable) {
        try {
            HttpServletRequest servletRequest = HttpServletUtils.getServletRequest();
            // 异步记录日志
            this.asyncLog(joinPoint,servletRequest,requestTime,responseTime,result,throwable);
        } catch (Throwable ex) {
            log.error("[log][ 记录操作日志时,发生异常，其中参数是 joinPoint({}) requestTime({}) responseTime({}) result({}) exception({}) ]",
                    joinPoint, requestTime, responseTime, result, ex.getMessage());
            log.error(ex.getMessage(), ex);
        }

    }

    /**
     * 异步记录
     */
    private void asyncLog(ProceedingJoinPoint point,
                          HttpServletRequest request,
                          Date requestTime,
                          Date responseTime,
                          Object result,
                          Throwable throwable) {
        final String ipAddr = CommonUtils.getIpAddr(request);
        final String requestUri = request.getRequestURI();
        String userAgent = "";
        final String requestMethod = request.getMethod();
        if(Objects.nonNull(UserLocalThread.getUser()) && Objects.nonNull(UserLocalThread.getUser().getId())) {
            userAgent  = UserLocalThread.getUser().getId();
        } else if (Objects.nonNull(UserLocalThread.getAdmin()) && Objects.nonNull(UserLocalThread.getAdmin().getId())) {
            userAgent = String.valueOf(UserLocalThread.getAdmin().getId());
        }

        // 异步记录日志
        String finalUserAgent = userAgent;
        CompletableFuture.supplyAsync(() -> {
            SysLogBO sysLogBO = new SysLogBO();

            //记录通用信息
            sysLogBO.setDuration((responseTime.getTime() - requestTime.getTime()));
            sysLogBO.setRemark(this.getRemark(point));

            // 记录请求信息
            sysLogBO.setClientIp(ipAddr);
            sysLogBO.setUrl(requestUri);
            sysLogBO.setUserAgent(finalUserAgent);
            sysLogBO.setRequestType(requestMethod);
            sysLogBO.setRequestTime(requestTime);
            sysLogBO.setRequestContent(this.getParameter(point));

            // 记录响应信息
            sysLogBO.setResponseTime(responseTime);
            sysLogBO.setResponseContent(JsonUtil.toJson(result));
            sysLogBO.setCreateDate(DateUtils.getNow());
            sysLogBO.setSuccess(CommonConstants.ResultCode.SUCCESS.code.toString());
            sysLogBO.setLogLevel(CommonConstants.LogLevel.INFO);

            //处理异常
            if(Objects.nonNull(throwable)) {
                sysLogBO.setSuccess(CommonConstants.ResultCode.ERROR.code.toString());
                sysLogBO.setResponseContent(throwable.getLocalizedMessage());
                sysLogBO.setLogLevel(CommonConstants.LogLevel.ERROR);
            }
            return sysLogService.save(sysLogBO);
        });

    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private String getParameter(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] argValues = joinPoint.getArgs();
        String[] argNames = signature.getParameterNames();

        //拼接参数
        HashMap<Object, Object> args = new HashMap<>(argValues.length);
        for (int i = 0; i < argValues.length; i++) {
            String argName = argNames[i];
            Object argValue = argValues[i];
            args.put(argName, argValue);

        }
        return JsonUtil.toJson(args);
    }

    /**
     * 获取在方法上的备注
     */
    private String getRemark(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        SysLog annotation = signature.getMethod().getAnnotation(SysLog.class);
        if(Objects.nonNull(annotation)) {
            return annotation.value();
        }
        return "";
    }

}
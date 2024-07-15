package com.atguigu.spzx.common.log.aspect;

import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.common.log.LogUtils.LogUtil;
import com.atguigu.spzx.common.log.annotation.Log;
import com.atguigu.spzx.common.log.service.AsyncOperLogService;
import com.atguigu.spzx.model.entity.system.SysOperLog;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    @Autowired
    private AsyncOperLogService asyncOperLogService;

    //环绕通知
    @Around(value = "@annotation(sysLog)")
    public  Object doAroudAdvice(ProceedingJoinPoint joinPoint, Log sysLog){
        //调用业务方法之前封装数据
        SysOperLog sysOperLog = new SysOperLog();
        LogUtil.beforeHandleLog(sysLog, joinPoint, sysOperLog);
        Object proceed = null;

        try{
            //执行业务方法
            proceed = joinPoint.proceed();
            LogUtil.afterHandlLog(sysLog, proceed, sysOperLog, 0, null);
        }catch (Throwable e){
            e.printStackTrace();
            LogUtil.afterHandlLog(sysLog, proceed, sysOperLog, 1, e.getMessage());
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        asyncOperLogService.saveSysOperLog(sysOperLog);
        return proceed;
    }
}

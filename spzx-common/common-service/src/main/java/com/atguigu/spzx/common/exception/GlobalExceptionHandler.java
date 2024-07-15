package com.atguigu.spzx.common.exception;

import com.atguigu.spzx.model.vo.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GuiguException.class)
    @ResponseBody
    public Result error(GuiguException e){
        e.printStackTrace();
        return Result.build(null , e.getResultCodeEnum()) ;
    }
}
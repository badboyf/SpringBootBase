package cn.com.fzk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cn.com.fzk.exception.RunException;
import cn.com.fzk.response.DefaultResponse;
import cn.com.fzk.util.Constant;

@ControllerAdvice
@RestController
public class HandleExceptionController {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public DefaultResponse exception(Exception e) {
    e.printStackTrace();
    return new DefaultResponse().fail(Constant.FAILURE_CODE, e.getMessage());
  }

  @ExceptionHandler(RunException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public DefaultResponse runtime(RunException e) {
    e.printStackTrace();
    return new DefaultResponse().fail(e.getCode(), e.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public DefaultResponse runtimeException(RuntimeException e) {
    e.printStackTrace();
    return new DefaultResponse().fail(Constant.FAILURE_CODE, e.getMessage());
  }
}

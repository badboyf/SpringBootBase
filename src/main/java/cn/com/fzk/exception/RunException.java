package cn.com.fzk.exception;

import cn.com.fzk.util.Constant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("serial")
@Data
public class RunException extends RuntimeException {
  private String message;
  private Integer code;

  public RunException() {}

  public RunException(String message) {
    this(message, Constant.FAILURE_CODE);
  }

  public RunException(String message, Integer code) {
    this.message = message;
    this.code = code;
  }
}

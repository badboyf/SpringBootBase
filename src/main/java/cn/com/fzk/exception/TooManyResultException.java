package cn.com.fzk.exception;

@SuppressWarnings("serial")
public class TooManyResultException extends RunException {
  private String message;
  private final Integer code = 10300;

  public TooManyResultException(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public Integer getCode() {
    return code;
  }
}

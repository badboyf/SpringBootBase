package cn.com.fzk.response;


import cn.com.fzk.util.Constant;
import lombok.Data;

@Data
public class DefaultResponse {
  private Integer code;
  private String message;
  private Boolean success;
  private Object data;

  public DefaultResponse() {
    this.success = true;
    this.code = Constant.SUCCESS_CODE;
  }

  public DefaultResponse(Object data) {
    this.data = data;
    this.success = true;
    this.code = Constant.SUCCESS_CODE;
  }

  public void succeed() {
    this.code = Constant.SUCCESS_CODE;
    this.success = true;
  }

  public void fail(String message) {
    this.success = false;
    this.message = message;
  }

  public DefaultResponse fail(Integer code, String message) {
    this.code = code;
    this.success = false;
    this.message = message;

    return this;
  }

  public DefaultResponse(Integer code, boolean success, String message, Object data) {
    this.code = code;
    this.message = message;
    this.success = success;
    this.data = data;
  }
}

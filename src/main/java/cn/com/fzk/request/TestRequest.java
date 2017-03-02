package cn.com.fzk.request;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.fzk.util.UriFormat;
import lombok.Data;

@Data
public class TestRequest {
  private String userName;
  private String password;
  private String nickName;
  private String avatar;
  private String email;
  private String phoneNumber;

  public String toUriString() {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(this, UriFormat.class).toString();
  }
}

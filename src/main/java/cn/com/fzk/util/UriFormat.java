package cn.com.fzk.util;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class UriFormat {

  private StringBuilder builder = new StringBuilder();

  @JsonAnySetter
  public void addToUri(String name, Object property) {
    if (property == null) {
      return;
    }

    if (builder.length() > 0) {
      builder.append("&");
    }
    builder.append(name).append("=").append(property);
  }

  @Override
  public String toString() {
    return builder.toString();
  }
}

package cn.com.fzk.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "db")
@Data
public class DatabaseConfiguration {
  private String driver;
  private String url;
  private String username;
  private String password;
}

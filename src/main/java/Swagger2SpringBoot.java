
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import cn.com.fzk.configuration.DatabaseConfiguration;
import cn.com.fzk.exception.JOOQToSpringExceptionTransformer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan({"cn.com.fzk.filter", "cn.com.fzk.controller", "cn.com.fzk.proxy",
    "cn.com.fzk.service", "cn.com.fzk.configuration", "cn.com.fzk.repository"})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
public class Swagger2SpringBoot {
  @Autowired
  DatabaseConfiguration databaseConfiguration;

  static final MetricRegistry metrics = new MetricRegistry();

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Swagger2SpringBoot.class);
    app.run(args);
  }

  static void startReport() {
    ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS).build();
    reporter.start(1, TimeUnit.SECONDS);
  }

  @Bean
  public Docket testApi() {
    return new Docket(DocumentationType.SWAGGER_2).groupName("group name")
        .apiInfo(new ApiInfoBuilder().title("title").description("Create by iTO").build()).select()
        .paths(regex("/test.*")).build();
  }

  @Bean
  UiConfiguration uiConfig() {
    return new UiConfiguration("validatorUrl");
  }

  @Bean(destroyMethod = "close")
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(databaseConfiguration.getUrl());
    config.setUsername(databaseConfiguration.getUsername());
    config.setPassword(databaseConfiguration.getPassword());
    config.addDataSourceProperty("characterEncoding", "utf8");
    config.addDataSourceProperty("connectionCollation", "utf8_unicode_ci");
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.setMaximumPoolSize(2);

    return new HikariDataSource(config);
  }

  @Bean
  public LazyConnectionDataSourceProxy lazyConnectionDataSource() {
    return new LazyConnectionDataSourceProxy(dataSource());
  }

  @Bean
  public TransactionAwareDataSourceProxy transactionAwareDataSource() {
    return new TransactionAwareDataSourceProxy(lazyConnectionDataSource());
  }

  @Bean
  public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(lazyConnectionDataSource());
  }

  @Bean
  public DataSourceConnectionProvider connectionProvider() {
    return new DataSourceConnectionProvider(transactionAwareDataSource());
  }

  @Bean
  public JOOQToSpringExceptionTransformer jooqToSpringExceptionTransformer() {
    return new JOOQToSpringExceptionTransformer();
  }

  @Bean
  public org.jooq.Configuration configuration() {
    DefaultConfiguration jooqConfiguration = new DefaultConfiguration();

    jooqConfiguration.set(connectionProvider());
    jooqConfiguration.set(new DefaultExecuteListenerProvider(jooqToSpringExceptionTransformer()));
    jooqConfiguration.set(SQLDialect.MYSQL);

    return jooqConfiguration;
  }

  @Bean
  public DSLContext dsl() {
    return new DefaultDSLContext(configuration());
  }

  @Bean
  public CharacterEncodingFilter characterEncodingFilter() {
    CharacterEncodingFilter filter = new CharacterEncodingFilter();
    filter.setEncoding("UTF-8");
    filter.setForceEncoding(true);

    return filter;
  }

}

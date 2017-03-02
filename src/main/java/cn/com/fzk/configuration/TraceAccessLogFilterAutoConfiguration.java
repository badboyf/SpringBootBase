/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package cn.com.fzk.configuration;

import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.boot.actuate.trace.WebRequestTraceFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import cn.com.fzk.filter.AccessLogFilter;
import cn.com.fzk.service.HttpService;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link WebRequestTraceFilter tracing}.
 *
 * @author Dave Syer
 */
@Configuration
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, ServletRegistration.class})
@AutoConfigureAfter(TraceRepositoryAutoConfiguration.class)
public class TraceAccessLogFilterAutoConfiguration {

  @Autowired
  HttpService httpService;

  @Autowired
  private TraceRepository traceRepository;

  @Autowired(required = false)
  private ErrorAttributes errorAttributes;

  @Value("${management.dump_requests:false}")
  private boolean dumpRequests;

  @Bean
  public AccessLogFilter accessLogFilter(BeanFactory beanFactory) {
    AccessLogFilter filter = new AccessLogFilter(this.traceRepository);
    filter.setDumpRequests(this.dumpRequests);
    if (this.errorAttributes != null) {
      httpService.setErrorAttributes(this.errorAttributes);
    }
    return filter;
  }

}

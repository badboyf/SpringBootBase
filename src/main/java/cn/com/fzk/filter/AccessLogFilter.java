
/*
 * Copyright 2012-2015 the original author or authors.
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
package cn.com.fzk.filter;


import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.com.fzk.service.HttpService;
import lombok.extern.log4j.Log4j2;

/**
 * Servlet {@link Filter} that logs all requests to a {@link TraceRepository}.
 *
 * @author Dave Syer
 */
@Log4j2
public class AccessLogFilter extends OncePerRequestFilter implements Ordered {

  @Autowired
  HttpService httpService;

  @SuppressWarnings("unused")
  private boolean dumpRequests = false;

  // Not LOWEST_PRECEDENCE, but near the end, so it has a good chance of catching all
  // enriched headers, but users can add stuff after this if they want to
  private int order = Ordered.LOWEST_PRECEDENCE - 10;

  private final TraceRepository traceRepository;

  /**
   * Create a new {@link AccessLogFilter} instance.
   * 
   * @param traceRepository the trace repository
   */
  public AccessLogFilter(TraceRepository traceRepository) {
    this.traceRepository = traceRepository;
  }

  /**
   * Debugging feature. If enabled, and trace logging is enabled then web request headers will be
   * logged.
   * 
   * @param dumpRequests if requests should be logged
   */
  public void setDumpRequests(boolean dumpRequests) {
    this.dumpRequests = dumpRequests;
  }

  @Override
  public int getOrder() {
    return this.order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String uri = request.getRequestURI();
    MyRequestWrapper requestWrapper = new MyRequestWrapper(request);
    MyResponseWrapper responseWrapper = new MyResponseWrapper(response);

    if (uri.contains("/sms")) {
      log.info("外部请求信息将呈现在serviceLog文件中");
      filterChain.doFilter(requestWrapper, response);
    } else {
      Map<String, Object> requestTrace = httpService.getRequestTrace(requestWrapper);
      log.info("Processing request " + request.getMethod() + " " + request.getRequestURI());

      for (String key : requestTrace.keySet()) {
        log.info("key: " + key + " value: " + requestTrace.get(key));
      }

      try {
        filterChain.doFilter(requestWrapper, responseWrapper);
      } finally {
        httpService.enhanceTrace(requestTrace, response);
        this.traceRepository.add(requestTrace);
      }

      log.info("Writing response...");

      Map<String, Object> responseHeader = httpService.getResponseHeader(responseWrapper);

      for (String key : responseHeader.keySet()) {
        log.info("key: " + key + " value: " + responseHeader.get(key));
      }

      byte[] b = responseWrapper.getResponseData();
      log.info("key: body value: " + new String(b));

      ServletOutputStream out = response.getOutputStream();
      out.write(b);
      out.flush();
      out.close();
    }
  }
}

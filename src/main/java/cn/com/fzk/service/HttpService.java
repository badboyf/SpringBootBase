package cn.com.fzk.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class HttpService {

  private ErrorAttributes errorAttributes;

  public void enhanceTrace(Map<String, Object> trace, HttpServletResponse response) {
    Map<String, String> headers = new LinkedHashMap<String, String>();
    for (String header : response.getHeaderNames()) {
      String value = response.getHeader(header);
      headers.put(header, value);
    }
    headers.put("status", "" + response.getStatus());
    @SuppressWarnings("unchecked")
    Map<String, Object> allHeaders = (Map<String, Object>) trace.get("headers");
    allHeaders.put("response", headers);
  }

  public Map<String, Object> getRequestTrace(HttpServletRequest request) {

    Map<String, Object> headers = new LinkedHashMap<String, Object>();
    Enumeration<String> names = request.getHeaderNames();

    while (names.hasMoreElements()) {
      String name = names.nextElement();
      List<String> values = Collections.list(request.getHeaders(name));
      Object value = values;
      if (values.size() == 1) {
        value = values.get(0);
      } else if (values.isEmpty()) {
        value = "";
      }
      headers.put(name, value);

    }
    Map<String, Object> trace = new LinkedHashMap<String, Object>();
    Map<String, Object> allHeaders = new LinkedHashMap<String, Object>();
    allHeaders.put("request", headers);
    trace.put("method", request.getMethod());
    trace.put("path", request.getRequestURI());
    trace.put("headers", allHeaders);
    try {
      trace.put("body", getRequestBody(request));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
    if (exception != null && this.errorAttributes != null) {
      RequestAttributes requestAttributes = new ServletRequestAttributes(request);
      Map<String, Object> error = this.errorAttributes.getErrorAttributes(requestAttributes, true);
      trace.put("error", error);
    }
    return trace;
  }

  public void setErrorAttributes(ErrorAttributes errorAttributes) {
    this.errorAttributes = errorAttributes;
  }


  public String getRequestBody(HttpServletRequest request) throws IOException {


    String body = null;
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;

    try {
      InputStream inputStream = request.getInputStream();
      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] charBuffer = new char[128];
        int bytesRead = -1;
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      } else {
        stringBuilder.append("");
      }
    } catch (IOException ex) {
      throw ex;
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException ex) {
          throw ex;
        }
      }
    }

    body = stringBuilder.toString();
    return body;
  }

  public Map<String, Object> getResponseHeader(HttpServletResponse response) {

    Map<String, Object> headers = new LinkedHashMap<String, Object>();
    Iterator<String> names = response.getHeaderNames().iterator();

    while (names.hasNext()) {
      String name = names.next();
      Object value = response.getHeader(name);
      headers.put(name, value);
    }

    Map<String, Object> responseHeader = new LinkedHashMap<String, Object>();
    Map<String, Object> allHeaders = new LinkedHashMap<String, Object>();
    allHeaders.put("response", headers);
    responseHeader.put("headers", allHeaders);

    return responseHeader;
  }

  public String getResponseBody(HttpServletResponse response) throws IOException {

    String body = null;
    StringBuilder stringBuilder = new StringBuilder();
    PrintWriter printWriter = null;

    try {
      OutputStream outputStream = response.getOutputStream();
      if (outputStream != null) {
        printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
        char[] charBuffer = new char[128];
        printWriter.write(charBuffer);
        stringBuilder.append(charBuffer);
      }
    } catch (IOException ex) {
      throw ex;
    } finally {
      if (printWriter != null) {
        printWriter.close();
      }
    }

    body = stringBuilder.toString();
    return body;
  }
}

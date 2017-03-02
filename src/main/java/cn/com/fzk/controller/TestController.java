package cn.com.fzk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.com.fzk.request.TestRequest;
import cn.com.fzk.response.DefaultResponse;
import cn.com.fzk.service.TestService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/test", headers = "Accept=application/json", produces = "application/json")
public class TestController {
  @Autowired
  TestService testService;

  @RequestMapping(value = "/{id}", method = RequestMethod.POST)
  @ApiOperation(value = "", notes = "")
  public DefaultResponse test(
      @ApiParam(value = "description", required = true) @PathVariable("id") Integer id,
      @ApiParam(value = "description", required = true) @RequestParam("param") Integer param,
      @RequestBody TestRequest registerRequest) {

    return new DefaultResponse();
  }

}

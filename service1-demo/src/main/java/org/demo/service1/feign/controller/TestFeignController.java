package org.demo.service1.feign.controller;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.demo.service1.feign.service.TestFeignService;
import org.demo.common.interceptor.UserHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/test/feign")
public class TestFeignController {

    @Resource
    private TestFeignService testFeignService;
    @GetMapping("/test")
    public String test(){
        return testFeignService.getTest();
    }

    @GetMapping("/test1")
    public String test1(){
        Long userId = UserHelper.getUserId();
        log.info("userId={}",userId);
        return "test1";
    }
}

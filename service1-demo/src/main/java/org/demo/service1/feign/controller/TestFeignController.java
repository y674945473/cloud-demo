package org.demo.service1.feign.controller;

import org.demo.service1.feign.service.TestFeignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
        return "test1";
    }
}

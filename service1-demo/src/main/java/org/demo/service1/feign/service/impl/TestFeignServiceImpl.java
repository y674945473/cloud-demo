package org.demo.service1.feign.service.impl;

import org.demo.feign.api.test.FeignTestService;
import org.demo.service1.feign.service.TestFeignService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TestFeignServiceImpl implements TestFeignService {


    @Resource
    private FeignTestService feignTestService;

    @Override
    public String getTest() {
        return feignTestService.getTest();
    }
}

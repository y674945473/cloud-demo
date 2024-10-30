package org.demo.feign.api.test;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "feignService")
public interface FeignTestService {

    /**
     * feign 调用，是通过服务名进行调用映射的，相当于把这个地址（/test/feign-service）映射到其他服务
     * @return
     */
    @GetMapping("/test/feign-service")
    String getTest();
}

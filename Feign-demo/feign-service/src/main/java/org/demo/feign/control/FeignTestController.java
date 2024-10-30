package org.demo.feign.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class FeignTestController {

    @RequestMapping("/feign-service")
    public String test() {
        return "feign-service";
    }
}

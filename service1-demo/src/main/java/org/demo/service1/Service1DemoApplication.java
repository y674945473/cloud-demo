package org.demo.service1;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "org.demo.feign.api.test")
@ComponentScan({"org.demo.common.*", "org.demo.service1.*"})
public class Service1DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(Service1DemoApplication.class, args);
    }
}

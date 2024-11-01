package org.demo.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"org.demo.common.*", "org.demo.user.*"})
public class UserAuthDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAuthDemoApplication.class, args);
    }

}

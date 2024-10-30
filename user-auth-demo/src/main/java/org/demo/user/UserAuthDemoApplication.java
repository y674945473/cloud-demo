package org.demo.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserAuthDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAuthDemoApplication.class, args);
    }

}

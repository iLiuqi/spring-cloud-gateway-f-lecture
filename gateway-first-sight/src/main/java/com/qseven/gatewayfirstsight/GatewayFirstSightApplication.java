package com.qseven.gatewayfirstsight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class GatewayFirstSightApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayFirstSightApplication.class, args);
    }

    /*
     * 自定义路由: 让"/get"请求都转发到 "http://httpbin.org/get"
     * filter: 过滤器用于对请求做各种判断和修改
     *
     * spring cloud gateway 也可以使用hystrix做服务熔断降级处理
     *
     * host 断言请求是否进入该路由，当请求路径满足"*.hystrix.com"时，回进入该router
     * */
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        String forwardTargetUri = "http://httpbin.org:80";
        return builder.routes()
                .route(p -> p.path("/get")
                        .filters(f -> f.addRequestHeader("Hello", "World"))
                        .uri(forwardTargetUri)
                )
                .route(p -> p.host("*.hystrix.com")
                        .filters(f -> f.hystrix(config -> config.setName("mycmd").setFallbackUri("forward:/fallback")))
                        .uri(forwardTargetUri)      // 外来请求头部中Host匹配上*.hystrix.com，则进入此路由进行熔断降级
                ).build();
    }

    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("i am fallback haha ...");
    }

}

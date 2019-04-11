package com.qseven.gatewayfilterdemo;

import com.qseven.gatewayfilterdemo.filter.RequestTimeFilter;
import com.qseven.gatewayfilterdemo.filter.RequestTimeGatewayFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayFilterDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayFilterDemoApplication.class, args);
    }

    @Bean
    public RouteLocator customerRouteLocator() {
        return customerRouteLocator();
    }

    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/customer/**")
                        .filters(f -> f.filter(new RequestTimeFilter())     // 添加自定义过滤器
                                .addResponseHeader("X-Response-Default-Foo", "Default-Bar"))
                        .uri("http://httpbin.org:80/get")
                        .order(0)
                        .id("customer_filter_router")
                )
                .build();
    }

    @Bean
    public RequestTimeGatewayFilterFactory initCustomerGatewayFilterFactory() {
        return new RequestTimeGatewayFilterFactory();
    }

}

package com.liuliu.webflux.learning.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class MyRouter {
    @Bean
    public RouterFunction<ServerResponse> routeCity(RouterHandler routerHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/hello"), routerHandler::helloRouter);
    }
}

package com.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;

import java.util.Objects;

@Configuration
public class RateLimiterConfig {

    // @Bean
    // public KeyResolver userKeyResolver() {
    //     return exchange -> Mono.just(
    //         Objects.requireNonNull(exchange.getRequest().getHeaders().getFirst("X-User-Id"),
    //         Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress())
    //     );
    // }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just("anonymous");
    }
    
    // @Bean
	// CommandLineRunner routes(RouteDefinitionLocator locator) {
	//     return args -> locator.getRouteDefinitions()
	//             .doOnNext(route -> System.out.println("Route Loaded: " + route.getId()))
	//             .blockLast();
	// }
}
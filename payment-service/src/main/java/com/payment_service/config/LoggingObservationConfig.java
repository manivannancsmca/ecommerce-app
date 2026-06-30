package com.payment_service.config;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingObservationConfig {

    // This handler forces Micrometer to sync with the Logback MDC Context on every thread observation
    @Bean
    public ObservationHandler<Observation.Context> tracingObservationHandler(Tracer tracer) {
        return new DefaultTracingObservationHandler(tracer);
    }
}

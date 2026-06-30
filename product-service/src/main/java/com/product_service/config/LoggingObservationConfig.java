package com.product_service.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
@Slf4j
public class LoggingObservationConfig {

    @Bean
    @ConditionalOnBean(Tracer.class) // Only initializes if Micrometer Tracing is active
    public ObservationHandler<Observation.Context> loggingTracingObservationHandler(Tracer tracer) {
        
        return new ObservationHandler<Observation.Context>() {
            
            @Override
            public void onStart(Observation.Context context) {
                if (tracer.currentSpan() != null) {
                    log.info("Observation started. Trace ID: {}, Span ID: {}", 
                            tracer.currentSpan().context().traceId(), 
                            tracer.currentSpan().context().spanId());
                }
            }

            @Override
            public void onStop(Observation.Context context) {
                if (tracer.currentSpan() != null) {
                    log.info("Observation stopped. Trace ID: {}", 
                            tracer.currentSpan().context().traceId());
                }
            }

            @Override
            public boolean supportsContext(Observation.Context context) {
                // Return true so this handler processes all types of observation contexts
                return true;
            }
        };
    }
}

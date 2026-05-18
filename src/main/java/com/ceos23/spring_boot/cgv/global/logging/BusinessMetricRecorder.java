package com.ceos23.spring_boot.cgv.global.logging;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetricRecorder {

    private final MeterRegistry meterRegistry;

    public BusinessMetricRecorder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordAuthEvent(String action, String result, long latencyMs) {
        increment("cgv.auth.events", "action", action, "result", result);
        recordTimer("cgv.auth.operation", latencyMs, "action", action);
    }

    public void recordReservationEvent(String action, String result, long latencyMs) {
        increment("cgv.reservation.events", "action", action, "result", result);
        recordTimer("cgv.reservation.operation", latencyMs, "action", action);
    }

    public void recordPaymentEvent(String action, String result, long latencyMs) {
        increment("cgv.payment.events", "action", action, "result", result);
        recordTimer("cgv.payment.operation", latencyMs, "action", action);
    }

    public void recordStorePurchaseEvent(String result, long latencyMs) {
        increment("cgv.store.purchase.events", "result", result);
        recordTimer("cgv.store.purchase.operation", latencyMs);
    }

    public void recordExpiredReservations(int count) {
        Counter.builder("cgv.reservation.expired.count")
                .register(meterRegistry)
                .increment(count);
    }

    private void increment(String name, String... tags) {
        Counter.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .increment();
    }

    private void recordTimer(String name, long latencyMs, String... tags) {
        Timer.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .record(latencyMs, TimeUnit.MILLISECONDS);
    }
}

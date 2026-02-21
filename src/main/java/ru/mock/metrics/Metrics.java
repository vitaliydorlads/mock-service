package ru.mock.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class Metrics {

    private static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    public static final Counter registerRequests = Counter.builder("requests.register.count")
            .description("Количество запросов регистрации")
            .register(registry);

    public static final Counter loginRequests = Counter.builder("requests.login.success.count")
            .description("Количество успешных логинов")
            .register(registry);

    public static final Counter failedLogins = Counter.builder("requests.login.failed.count")
            .description("Количество неуспешных логинов")
            .register(registry);

    public static final Counter userRequests = Counter.builder("requests.user.count")
            .description("Количество запросов /user")
            .register(registry);

    public static final Timer requestTimer = Timer.builder("requests.timer")
            .description("Время обработки запроса")
            .register(registry);

    public static final Timer externalTimer = Timer.builder("external.api.duration")
            .description("Время запросов к внешнему API")
            .tag("uri", "/verify")
            .publishPercentiles(0.5, 0.95, 0.99)  // ← ИСПРАВЛЕНИЕ: добавлено, чтобы гистограмма заполнялась (p50/p95/p99)
            .register(registry);

    public static final Counter externalSuccess = Counter.builder("external.api.success.count")
            .description("Успешные вызовы внешнего API")
            .register(registry);

    public static final Counter externalErrors = Counter.builder("external.api.errors.count")
            .description("Ошибки при вызове внешнего API")
            .register(registry);

    public static final Counter externalByStatus = Counter.builder("external.api.status.count")
            .description("Ответы внешнего API")
            .register(registry);

    public static PrometheusMeterRegistry getRegistry() {
        return registry;
    }
}
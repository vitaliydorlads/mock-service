package ru.mock.metrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class Metrics {

    private static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    // Счётчики
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

    // Timer для всех запросов
    public static final Timer requestTimer = Timer.builder("requests.timer")
            .description("Время обработки запроса")
            .register(registry);

    public static PrometheusMeterRegistry getRegistry() {
        return registry;
    }
}

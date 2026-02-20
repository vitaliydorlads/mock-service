package ru.mock.controller;

import com.sun.net.httpserver.HttpExchange;
import ru.mock.metrics.Metrics;

import java.io.IOException;

public class MetricsController {

    public void handle(HttpExchange exchange) throws IOException {
        String response = Metrics.getRegistry().scrape(); // <- Prometheus-совместимый формат
        byte[] bytes = response.getBytes();

        exchange.getResponseHeaders().add("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
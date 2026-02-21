package ru.mock.external;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.mock.metrics.Metrics;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;

public class ExternalApiMock {
    private static final Random rnd = new Random();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        server.createContext("/verify", ExternalApiMock::handleVerify);
        server.createContext("/metrics", ExternalApiMock::handleMetrics);

        server.start();
        System.out.println("External mock API started on 8081");
    }

    private static void handleVerify(HttpExchange exchange) {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes());
            boolean hasLogin = body.contains("\"login\"");

            int roll = rnd.nextInt(100);
            String json;
            int code;

            if (roll < 82) {
                code = 200;
                json = "{\"status\":\"ok\",\"message\":\"User verified\"}";
            } else if (roll < 90) {
                code = 400;
                json = "{\"status\":\"error\",\"message\":\"Invalid data\"}";
            } else if (roll < 95) {
                code = 429;
                json = "{\"status\":\"error\",\"message\":\"Rate limit exceeded\"}";
            } else {
                code = 503;
                json = "{\"status\":\"error\",\"message\":\"Service unavailable\"}";
            }

            byte[] bytes = json.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(code, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private static void handleMetrics(HttpExchange exchange) {
        try {
            String response = Metrics.getRegistry().scrape();
            byte[] bytes = response.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}
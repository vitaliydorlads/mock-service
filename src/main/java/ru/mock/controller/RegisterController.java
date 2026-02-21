package ru.mock.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import io.micrometer.core.instrument.Timer;
import ru.mock.model.User;
import ru.mock.storage.Storage;
import ru.mock.metrics.Metrics;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RegisterController {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void handle(HttpExchange exchange) {
        Metrics.requestTimer.record(() -> {
            try {
                InputStream body = exchange.getRequestBody();
                User user = mapper.readValue(body, User.class);

                if (user.getLogin() == null || user.getLogin().isBlank()) {
                    send(exchange, 400, "Login required");
                    return;
                }

                if (Storage.users.containsKey(user.getLogin())) {
                    send(exchange, 409, "User exists");
                    return;
                }

                String jsonBody = "{\"login\":\"" + user.getLogin() + "\"}";
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://external-mock:8081/verify"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                Timer.Sample sample = Timer.start(Metrics.getRegistry());

                try {
                    HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                    sample.stop(Metrics.externalTimer);

                    String status = String.valueOf(resp.statusCode());
                    Metrics.externalByStatus.increment();  // без тега status

                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        Metrics.externalSuccess.increment();
                        Storage.users.put(user.getLogin(), user);
                        Metrics.registerRequests.increment();
                        send(exchange, 200, "Registered");
                    } else {
                        Metrics.externalErrors.increment();
                        send(exchange, resp.statusCode(), "Verification failed: " + resp.body());
                    }
                } catch (Exception e) {
                    sample.stop(Metrics.externalTimer);
                    Metrics.externalErrors.increment();
                    send(exchange, 500, "External service error");
                }

            } catch (Exception e) {
                e.printStackTrace();
                trySend(exchange, 500, "Server error");
            } finally {
                exchange.close();
            }
        });
    }

    private void send(HttpExchange ex, int code, String msg) throws Exception {
        byte[] bytes = msg.getBytes();
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
    }

    private void trySend(HttpExchange ex, int code, String msg) {
        try {
            send(ex, code, msg);
        } catch (Exception ignored) {}
    }
}
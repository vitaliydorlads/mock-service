package ru.mock.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import ru.mock.model.User;
import ru.mock.storage.Storage;
import ru.mock.metrics.Metrics;

import java.io.InputStream;

public class RegisterController {
    private final ObjectMapper mapper = new ObjectMapper();

    public void handle(HttpExchange exchange) {
        Metrics.requestTimer.record(() -> {
            try {
                InputStream body = exchange.getRequestBody();
                User user = mapper.readValue(body, User.class);

                if (Storage.users.containsKey(user.getLogin())) {
                    String resp = "User exists";
                    byte[] respBytes = resp.getBytes();
                    exchange.sendResponseHeaders(400, respBytes.length);
                    exchange.getResponseBody().write(respBytes);

                } else {
                    Storage.users.put(user.getLogin(), user);

                    // Счётчик успешных регистраций
                    Metrics.registerRequests.increment();

                    String resp = "Registered";
                    byte[] respBytes = resp.getBytes();
                    exchange.sendResponseHeaders(200, respBytes.length);
                    exchange.getResponseBody().write(respBytes);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        });
    }
}

package ru.mock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import ru.mock.model.User;
import ru.mock.storage.Storage;
import ru.mock.metrics.Metrics;

import java.io.InputStream;

public class LoginController {

    private final ObjectMapper mapper = new ObjectMapper();

    public void handle(HttpExchange exchange) {
        Metrics.requestTimer.record(() -> {
            try {
                InputStream body = exchange.getRequestBody();
                User request = mapper.readValue(body, User.class);
                User user = Storage.users.get(request.getLogin());

                if (user == null || !user.getPassword().equals(request.getPassword())) {
                    Metrics.failedLogins.increment();
                    String resp = "Invalid login/password";
                    byte[] respBytes = resp.getBytes();
                    exchange.sendResponseHeaders(401, respBytes.length);
                    exchange.getResponseBody().write(respBytes);

                } else {
                    Metrics.loginRequests.increment();
                    String token = Storage.generateToken(user.getLogin());
                    byte[] tokenBytes = token.getBytes();
                    exchange.sendResponseHeaders(200, tokenBytes.length);
                    exchange.getResponseBody().write(tokenBytes);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        });
    }
}
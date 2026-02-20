package ru.mock.controller;
import com.sun.net.httpserver.HttpExchange;
import ru.mock.metrics.Metrics;
import ru.mock.model.User;
import ru.mock.storage.Storage;

public class UserController {
    public void handle(HttpExchange exchange) {
        Metrics.requestTimer.record(() -> {
            try {
                String token = exchange.getRequestHeaders().getFirst("Authorization");
                String login = Storage.tokens.get(token);

                if (login == null) {
                    String resp = "Invalid token";
                    byte[] respBytes = resp.getBytes();
                    exchange.sendResponseHeaders(401, respBytes.length);
                    exchange.getResponseBody().write(respBytes);

                } else {
                    // Счётчик успешных /user запросов
                    Metrics.userRequests.increment();

                    User user = Storage.users.get(login);
                    String resp = user.getFirstName() + " " + user.getLastName();
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

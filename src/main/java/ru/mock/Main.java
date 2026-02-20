package ru.mock;

import com.sun.net.httpserver.HttpServer;
import ru.mock.controller.LoginController;
import ru.mock.controller.MetricsController;
import ru.mock.controller.RegisterController;
import ru.mock.controller.UserController;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        RegisterController registerController = new RegisterController();
        LoginController loginController = new LoginController();
        UserController userController = new UserController();
        MetricsController metricsController = new MetricsController();

        server.createContext("/register", registerController::handle);
        server.createContext("/login", loginController::handle);
        server.createContext("/user", userController::handle);
        server.createContext("/metrics", metricsController::handle);

        server.start();

        System.out.println("Mock server started");
    }
}
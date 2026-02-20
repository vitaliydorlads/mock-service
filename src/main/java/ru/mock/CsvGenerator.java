package ru.mock;

import com.github.javafaker.Faker;

import java.io.FileWriter;

public class CsvGenerator {
    public static void main(String[] args) throws Exception {
        Faker faker = new Faker();
        try (FileWriter writer = new FileWriter("users.csv")) {
            writer.write("login,password,firstName,lastName\n");

            for (int i = 0; i < 600; i++) {
                String first = faker.name().firstName();
                String last = faker.name().lastName();

                writer.write(String.format(
                        "user%d,123,%s,%s\n",
                        i + 1,
                        first,
                        last
                ));
            }
        }
    }
}

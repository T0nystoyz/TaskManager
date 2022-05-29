package http;

import java.io.IOException;
// Привет, прошу прощения, проект не готов до конца, кое-что нужно дописать. В Insomnia сделал минимальные проверки,
// но осталось написать тесты и проверить все методы и эндпоинты.

public class Main {
    public static void main(String[] args) throws IOException {

        //new KVServer().start();
        new HTTPTaskServer().start();
    }
}
package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class KVClient {
    private static String key;
    private final String url;

    public KVClient(String url) {
        HttpClient client = HttpClient.newHttpClient();
        this.url = url;
        key = getKey();
    }

    public String load(String key) {
        String answer = "";
        HttpClient client = HttpClient.newHttpClient();
        URI loadUrl = URI.create(url + "load" + "/" + key + "/" + "?API_KEY=" + KVClient.key);
        HttpRequest request = HttpRequest.newBuilder().uri(loadUrl).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            answer = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + loadUrl + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return answer;
    }

    private String getKey() {
        String text = "";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(this.url + "register/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            text = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return text;
    }

    public void put(String key, String json) {
        URI url = URI.create(this.url + "save/" + key + "/?API_KEY=" + KVClient.key);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}


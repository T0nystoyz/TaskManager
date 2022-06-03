package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVClient {
    private final HttpClient client;
    private final URI url;
    private final String apikey;

    public KVClient(URI url) throws IOException, InterruptedException {
        this.url = url;
        this.client = HttpClient.newHttpClient();
        URI registerUrl = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(registerUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        apikey = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI saveUrl = URI.create(url + "/save/" + key + "?API_KEY=" + apikey);
        HttpRequest request = HttpRequest.newBuilder().uri(saveUrl)
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        URI saveUrl = URI.create(url + "/load/" + key + "?API_KEY=" + apikey);
        HttpRequest request = HttpRequest.newBuilder().uri(saveUrl).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}



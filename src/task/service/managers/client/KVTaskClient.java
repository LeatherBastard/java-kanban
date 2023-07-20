package task.service.managers.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String serverURL;
    private String accessToken;
    private HttpClient client;

    public KVTaskClient(String serverURL) {
        this.serverURL = serverURL;
        try {
            client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/register")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            accessToken = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" +
                    serverURL + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void put(String key, String json) {
        String putURL = serverURL + "/save/" + key + "?API_TOKEN=" + accessToken;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(putURL))
                    .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
            }
            if (response.statusCode() == 400) {
                System.out.println("Значение ключа или значения было пустым");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" +
                    putURL + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {
        String result = "";
        String loadURL = serverURL + "/load/" + key + "?API_TOKEN=" + accessToken;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(loadURL))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                result = response.body();
            }
            if (response.statusCode() == 400) {
                System.out.println("Значение ключа было пустым или значение не было найдено");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" +
                    loadURL + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return result;
    }
}

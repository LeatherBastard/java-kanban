package task.service.managers.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private static final String httpRequestExceptionMessage = "200 code expected but was ";
    private final String serverURL;
    private String accessToken;
    private HttpClient client;

    public KVTaskClient(String serverURL) {
        this.serverURL = serverURL;
        try {
            client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(serverURL + "/register")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            checkStatusCode(response.statusCode());
            accessToken = response.body();
        } catch (IOException | InterruptedException e) {
            queryExecutionErrorMessage(serverURL);
        } catch (HttpRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    public void put(String key, String json) throws HttpRequestException {
        String putURL = serverURL + "/save/" + key + "?API_TOKEN=" + accessToken;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(putURL))
                    .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            checkStatusCode(response.statusCode());
            System.out.println("Значение для ключа " + key + " успешно обновлено!");
        } catch (IOException | InterruptedException e) {
            queryExecutionErrorMessage(putURL);
        }
    }

    public String load(String key) throws HttpRequestException {
        String result = "";
        String loadURL = serverURL + "/load/" + key + "?API_TOKEN=" + accessToken;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(loadURL))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            checkStatusCode(response.statusCode());
            result = response.body();
        } catch (IOException | InterruptedException e) {
            queryExecutionErrorMessage(loadURL);
        }
        return result;
    }

    private void checkStatusCode(int code) throws HttpRequestException {
        if (code != 200) {
            throw new HttpRequestException(httpRequestExceptionMessage + code);
        }
    }

    private void queryExecutionErrorMessage(String url) {
        System.out.println("Во время выполнения запроса ресурса по url-адресу: '" +
                url + "' возникла ошибка.\n" +
                "Проверьте, пожалуйста, адрес и повторите попытку.");
    }
}

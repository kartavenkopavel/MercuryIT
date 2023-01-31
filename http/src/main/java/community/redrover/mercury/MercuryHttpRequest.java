package community.redrover.mercury;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class MercuryHttpRequest extends MercuryRequest {

    private HttpRequest.Builder request;
    private HttpRequest.BodyPublisher body;

    MercuryHttpRequest() {
        this.request = HttpRequest.newBuilder();
    }

    public MercuryHttpRequest uri(String uri) {
        request = request.uri(URI.create(uri));

        return this;
    }

    public MercuryHttpRequest body() {
        return body(null);
    }

    public <T> MercuryHttpRequest body(T body) {
        String bodyStr = null;
        if (body != null) {
            bodyStr = MercuryHttp.GSON.toJson(body);
        }

        return body(bodyStr);
    }

    public MercuryHttpRequest body(String body) {
        if (body != null) {
            this.body = HttpRequest.BodyPublishers.ofString(body);
        }

        return this;
    }

    @SneakyThrows
    private MercuryHttpResponse send(HttpRequest request) {
        return new MercuryHttpResponse(
                HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString()));
    }

    public MercuryHttpResponse post() {
        request = request.header("Content-Type", "application/json");
        if (body != null) {
            request = request.POST(body);
        }

        return send(request.build());
    }

    public MercuryHttpResponse get() {
        return send(request.GET().build());
    }

    public MercuryHttpResponse delete() {
        return send(request.DELETE().build());
    }
}

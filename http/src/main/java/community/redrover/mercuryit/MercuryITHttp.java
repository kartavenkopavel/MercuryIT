package community.redrover.mercuryit;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class MercuryITHttp extends MercuryITRequest<MercuryITHttp> {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_VALUE = "application/json";

    private HttpRequest.Builder request;
    private HttpRequest.BodyPublisher body;

    MercuryITHttp(MercuryITConfigHolder configHolder) {
        super(configHolder);
        this.request = HttpRequest.newBuilder();
    }

    public MercuryITHttp uri(String uri) {
        request = request.uri(URI.create(uri));

        return this;
    }

    public MercuryITHttp body() {
        return body(null);
    }

    public <T> MercuryITHttp body(T body) {
        String bodyStr = null;
        if (body != null) {
            bodyStr = config(MercuryITJsonConfig.class).toJson(body);
        }

        return body(bodyStr);
    }

    public MercuryITHttp body(String body) {
        if (body != null) {
            this.body = HttpRequest.BodyPublishers.ofString(body);
        }

        return this;
    }

    @SneakyThrows
    private MercuryITHttpResponse send(HttpRequest request) {
        return new MercuryITHttpResponse(configHolder(),
                HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString()));
    }

    public MercuryITHttpResponse post() {
        request = request.header(CONTENT_TYPE, CONTENT_VALUE);
        if (body != null) {
            request = request.POST(body);
        }

        return send(request.build());
    }

    public MercuryITHttpResponse get() {
        return send(request.GET().build());
    }

    public MercuryITHttpResponse delete() {
        return send(request.DELETE().build());
    }

    public MercuryITHttpResponse put() {
        request = request.header(CONTENT_TYPE, CONTENT_VALUE);
        if (body != null) {
            request = request.PUT(body);
        }

        return send(request.build());
    }

    public MercuryITHttpResponse patch() {
        request = request.header(CONTENT_TYPE, CONTENT_VALUE);
        if (body != null) {
            request =  request.method("PATCH", body);
        }

        return send(request.build());
    }
}

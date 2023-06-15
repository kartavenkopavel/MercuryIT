package community.redrover.mercuryit;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;


public class MercuryITHttp extends MercuryITRequest<MercuryITHttp> {

    private HttpRequest.Builder request;
    private final Map<String, String> header = new TreeMap<>();
    private HttpRequest.BodyPublisher body;

    MercuryITHttp(MercuryITConfigHolder configHolder) {
        super(configHolder);
        this.request = HttpRequest.newBuilder();
    }

    private HttpRequest build() {
        Map<String, String> headerMap = new TreeMap<>();
        headerMap.putAll(config(MercuryITHttpConfig.class).getHeader());
        headerMap.putAll(header);

        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            if (Objects.nonNull(entry.getValue())) {
                request.header(entry.getKey(), entry.getValue());
            }
        }

        return request.build();
    }

    private HttpRequest build(Supplier<HttpRequest.Builder> requestSupplier) {
        request = requestSupplier.get();
        return build();
    }

    private HttpRequest build(Function<HttpRequest.BodyPublisher, HttpRequest.Builder> requestFunction) {
        if (body == null) {
            body("");
        }

        request = requestFunction.apply(body);
        return build();
    }

    public MercuryITHttp url(String uri) {
        request = request.uri(URI.create(uri));
        return this;
    }

    public MercuryITHttp urlf(String uri, Object... args) {
        return url(String.format(uri, args));
    }

    public MercuryITHttp header(String name, String value) {
        header.put(name, value);
        return this;
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
        return new MercuryITHttpResponse(getConfigHolder().copy(),
                HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString()));
    }

    public MercuryITHttpResponse post() {
        return send(build(request::POST));
    }

    public MercuryITHttpResponse get() {
        return send(build(request::GET));
    }

    public MercuryITHttpResponse delete() {
        return send(build(request::DELETE));
    }

    public MercuryITHttpResponse put() {
        return send(build(request::PUT));
    }

    public MercuryITHttpResponse patch() {
        return send(build(bodyPublisher -> request.method("PATCH", bodyPublisher)));
    }
}

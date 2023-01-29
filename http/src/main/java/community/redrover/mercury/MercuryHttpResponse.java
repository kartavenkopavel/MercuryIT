package community.redrover.mercury;

import java.net.http.HttpResponse;
import java.util.function.Consumer;
import java.util.function.Function;


public class MercuryHttpResponse {

    private final HttpResponse<String> response;

    MercuryHttpResponse(HttpResponse<String> response) {
        this.response = response;
    }

    public int getCode() {
        return response.statusCode();
    }

    public String getBody() {
        return response.body();
    }

    public <T> T getBody(Class<T> clazz) {
        return  MercuryHttp.GSON.fromJson(getBody(), clazz);
    }

    public MercuryHttpResponse get(Consumer<MercuryHttpResponse> consumer) {
        consumer.accept(this);
        return this;
    }

    public <T> MercuryHttpResponse get(Function<MercuryHttpResponse, T> function, Consumer<T> consumer) {
        consumer.accept(function.apply(this));
        return this;
    }
}

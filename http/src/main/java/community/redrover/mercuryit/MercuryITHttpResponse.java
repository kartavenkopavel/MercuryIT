package community.redrover.mercuryit;

import java.net.http.HttpResponse;


public class MercuryITHttpResponse extends MercuryITObject<MercuryITHttpResponse> {

    private final HttpResponse<String> response;

    MercuryITHttpResponse(HttpResponse<String> response) {
        this.response = response;
    }

    public int getCode() {
        return response.statusCode();
    }

    public String getBody() {
        return response.body();
    }

    public <T> T getBody(Class<T> clazz) {
        return  config().helper().fromJson(getBody(), clazz);
    }
}

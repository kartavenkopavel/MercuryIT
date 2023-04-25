package community.redrover.mercuryit;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;


public class MercuryITHttpTest {

    @Test
    public void testGet() throws IOException, InterruptedException {
        final String uri = "http://host:8080/doc";
        final String responseBody = "{}";

        try (MockedStatic<HttpClient> httpClientClass = Mockito.mockStatic(HttpClient.class)) {
            HttpClient httpClient = Mockito.mock(HttpClient.class);
            HttpResponse<Object> httpResponse = Mockito.mock(HttpResponse.class);

            httpClientClass.when(HttpClient::newHttpClient).thenReturn(httpClient);
            Mockito.when(httpClient.send(any(HttpRequest.class), any())).thenReturn(httpResponse);
            Mockito.when(httpResponse.body()).thenReturn(responseBody);

            MercuryIT.request(MercuryITHttp.class)
                    .uri(uri)
                    .get()
                    .assertion(MercuryITHttpResponse::getBody).equalsTo(responseBody);

            Mockito.verify(httpClient).send(
                    argThat(
                            request -> uri.equals(request.uri().toString())
                                    && "GET".equals(request.method())
                                    && MercuryITHttpConfigTest.EXPECTED_HEADER_MAP.equals(
                                            request.headers().map().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0))))
                    ),
                    any());
        }
    }
}

package simplesql;

import community.redrover.mercuryit.MercuryIT;
import community.redrover.mercuryit.MercuryITHttp;
import community.redrover.mercuryit.MercuryITHttpResponse;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationTests {

    @LocalServerPort
    private int port;

    private String getUrl(String endPoint, Object... args) {
        return String.format("http://localhost:%d/api/employees/%s", port, String.format(endPoint, args));
    }

    private String getUrl(Long id) {
        return getUrl(id.toString());
    }

    private final EmployeeEntity storedEmployee = EmployeeEntity.builder()
            .name("Pavel")
            .title("QA")
            .build();

    @Test
    @Order(1)
    public void testCreateEmployee() {
        MercuryIT.request(MercuryITHttp.class)
                .uri(getUrl("create"))
                .body(storedEmployee)
                .post()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .apply(response -> {
                    EmployeeEntity actualEmployee = response.getBody(EmployeeEntity.class);
                    storedEmployee.setId(actualEmployee.getId());
                    Assertions.assertEquals(storedEmployee, actualEmployee);
                });
    }

    @Test
    @Order(2)
    public void testGetListEmployees() {
        MercuryIT.request(MercuryITHttp.class)
                .uri(getUrl("list"))
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .apply(response ->
                    Assertions.assertArrayEquals(new EmployeeEntity[]{storedEmployee},
                            response.getBody(EmployeeEntity[].class))
                );
    }

    @Test
    @Order(3)
    public void testGetEmployeeById() {
        MercuryIT.request(MercuryITHttp.class)
                .uri(getUrl(storedEmployee.getId()))
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .apply(response -> {
                    Assertions.assertEquals(storedEmployee, response.getBody(EmployeeEntity.class));
                });
    }

    @Test
    @Order(4)
    public void testDeleteEmployee() {
        MercuryIT.request(MercuryITHttp.class)
                .uri(getUrl("delete/%d", storedEmployee.getId()))
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).equalsTo("");
    }
}

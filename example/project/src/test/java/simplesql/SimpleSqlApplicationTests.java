package simplesql;

import community.redrover.mercuryit.MercuryIT;
import community.redrover.mercuryit.MercuryITHttp;
import community.redrover.mercuryit.MercuryITHttpResponse;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import simplesql.EmployeeEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimpleSqlApplicationTests {

    @LocalServerPort
    private int port;

    private String getUrl(String endPoint) {
        return String.format("http://localhost:%d/api/employees/%s", port, endPoint);
    }

    private final EmployeeEntity storedEmployee = EmployeeEntity.builder().name("Pavel").title("QA").build();

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
                .apply(response -> {
                    EmployeeEntity[] actualEmployee = response.getBody(EmployeeEntity[].class);
                    Assertions.assertEquals(1, actualEmployee.length);
                    Assertions.assertEquals(storedEmployee, actualEmployee[0]);
                });
    }

    @Test
    @Order(3)
    public void testGetEmployeeById() {
        MercuryIT.request(MercuryITHttp.class)
                .uri(getUrl(storedEmployee.getId().toString()))
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .apply(response -> {
                    EmployeeEntity actualEmployee = response.getBody(EmployeeEntity.class);
                    Assertions.assertEquals(storedEmployee, actualEmployee);
                });
    }

    @Test
    @Order(4)
    public void testDeleteEmployee() {
        MercuryIT.request(MercuryITHttp.class)
                .uri(getUrl(String.format("delete/%s", storedEmployee.getId().toString())))
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).equalsTo("");
    }
}

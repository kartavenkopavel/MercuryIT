package community.redrover.mercuryit.example.http;

import community.redrover.mercuryit.MercuryIT;
import community.redrover.mercuryit.MercuryITHttp;
import community.redrover.mercuryit.MercuryITHttpResponse;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;


@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationTests {

    private static final String EMPLOYEE_NAME = "Sergey";
    private static final String EMPLOYEE_TITLE = "Developer";

    @LocalServerPort
    private int port;

    private final EmployeeEntity storedEmployee = EmployeeEntity.builder()
            .name("Pavel")
            .title("QA")
            .build();

    @Test
    @Order(1)
    public void testCreateEmployee() {
        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/create", port)
                .body(storedEmployee)
                .post()
                .assertion(MercuryITHttpResponse::getCode)
                .equalsTo(200)
                .assertion(response -> response.getBody(EmployeeEntity.class))
                .peek(actualEmployee -> {
                    storedEmployee.setId(actualEmployee.getId());
                    Assertions.assertEquals(storedEmployee, actualEmployee);
                });
    }

    @Test
    @Order(2)
    public void testGetListEmployees() {
        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/list", port)
                .get()
                .assertion(MercuryITHttpResponse::getCode)
                .equalsTo(200)
                .peek(response ->
                    Assertions.assertArrayEquals(new EmployeeEntity[]{storedEmployee},
                            response.getBody(EmployeeEntity[].class))
                );
    }

    @Test
    @Order(3)
    public void testGetEmployeeById() {
        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/%d", port, storedEmployee.getId())
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(response -> response.getBody(EmployeeEntity.class)).equalsTo(storedEmployee);
    }

    @Test
    @Order(4)
    public void testEditEmployee() {
        EmployeeEntity expectedEmployee = EmployeeEntity.builder()
                .id(storedEmployee.getId())
                .name(EMPLOYEE_NAME)
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/edit", port)
                .body(expectedEmployee)
                .put()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(response -> response.getBody(EmployeeEntity.class)).equalsTo(expectedEmployee);
    }

    @Test
    @Order(5)
    public void testUpdateEmployee() {
        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/update/%d", port, storedEmployee.getId())
                .body(Map.of("title", EMPLOYEE_TITLE))
                .patch()
                .assertion(MercuryITHttpResponse::getCode)
                .equalsTo(200)
                .assertion(response -> response.getBody(EmployeeEntity.class))
                .peek(actualEmployeeEntity -> {
                    Assertions.assertNotNull(actualEmployeeEntity);
                    Assertions.assertEquals(EMPLOYEE_NAME,  actualEmployeeEntity.getName());
                    Assertions.assertEquals(EMPLOYEE_TITLE, actualEmployeeEntity.getTitle());
                });
    }

    @Test
    @Order(6)
    public void testDeleteEmployee() {
        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/delete/%d", port, storedEmployee.getId())
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).isEmpty();
    }
}

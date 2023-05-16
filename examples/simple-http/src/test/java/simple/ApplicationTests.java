package java.simple;

import community.redrover.mercuryit.MercuryIT;
import community.redrover.mercuryit.MercuryITHttp;
import community.redrover.mercuryit.MercuryITHttpResponse;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import simple.EmployeeEntity;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationTests {

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
                .urif("http://localhost:%d/api/employee/create", port)
                .body(storedEmployee)
                .post()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    EmployeeEntity actualEmployee = response.getBody(EmployeeEntity.class);
                    storedEmployee.setId(actualEmployee.getId());
                    Assertions.assertEquals(storedEmployee, actualEmployee);
                });
    }

    @Test
    @Order(2)
    public void testGetListEmployees() {
        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/list", port)
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                    Assertions.assertArrayEquals(new EmployeeEntity[]{storedEmployee},
                            response.getBody(EmployeeEntity[].class))
                );
    }

    @Test
    @Order(3)
    public void testGetEmployeeById() {
        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/%d", port, storedEmployee.getId())
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                    Assertions.assertEquals(storedEmployee, response.getBody(EmployeeEntity.class))
                );
    }

    @Test
    @Order(4)
    public void testEditEmployee() {
        EmployeeEntity editEmployee = EmployeeEntity.builder()
                .id(storedEmployee.getId())
                .name("Sergei")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/edit", port)
                .body(editEmployee)
                .put()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    Assertions.assertNull(response.getBody(EmployeeEntity.class).getTitle());
                    Assertions.assertEquals(editEmployee, response.getBody(EmployeeEntity.class));
                });
    }

    @Test
    @Order(5)
    public void testUpdateEmployee() {
        EmployeeEntity editEmployee = EmployeeEntity.builder()
                .title("Developer")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/update/%d", port, storedEmployee.getId())
                .body(editEmployee)
                .patch()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    Assertions.assertNotNull(response.getBody(EmployeeEntity.class).getName());
                    Assertions.assertEquals("Sergei",  response.getBody(EmployeeEntity.class).getName());
                    Assertions.assertEquals(editEmployee.getTitle(), response.getBody(EmployeeEntity.class).getTitle());
                });
    }

    @Test
    @Order(6)
    public void testDeleteEmployee() {
        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/delete/%d", port, storedEmployee.getId())
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).isEmpty();
    }
}

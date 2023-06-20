package community.redrover.mercuryit.example.mongo;

import com.mongodb.client.model.Filters;
import community.redrover.mercuryit.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationTests {

    private static final String EMPLOYEE_NAME = "Pavel";
    private static final String EMPLOYEE_TITLE = "Team Lead";

    @LocalServerPort
    private int port;

    private EmployeeEntity storedEmployee = new EmployeeEntity();

    private List<EmployeeEntity> storedEmployeesList = new ArrayList<>();

    private static Stream<Arguments> employeesData() {
        return Stream.of(
                Arguments.of("Yuliya", "SDET"),
                Arguments.of("Inna", "Developer"),
                Arguments.of("Lena", "QA Lead"),
                Arguments.of("Ira", "SDET"),
                Arguments.of("Liudmila", "PM"),
                Arguments.of("Viktoriya", "QA Engineer"),
                Arguments.of("Dmitry", "QA Engineer"),
                Arguments.of("Irina", "Project Manager")
        );
    }

    @ParameterizedTest
    @MethodSource("employeesData")
    @Order(1)
    public void testCreateEmployee(String name, String title) {
        EmployeeEntity expectedEmployee = EmployeeEntity.builder()
                .name(name)
                .title(title)
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/create", port)
                .body(expectedEmployee)
                .post()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    EmployeeEntity actualEmployee = response.getBody(EmployeeEntity.class);
                    expectedEmployee.setId(actualEmployee.getId());
                    storedEmployeesList.add(actualEmployee);

                    Assertions.assertEquals(expectedEmployee, actualEmployee);
                })

                .request(MercuryITMongo.class)
                .connection()
                .db()
                .accept(response -> {
                    List<EmployeeEntity> employeeList = response.collection(EmployeeEntity.class, Filters.eq("_id", new ObjectId(expectedEmployee.getId())));

                    Assertions.assertEquals(1, employeeList.size());
                    Assertions.assertEquals(expectedEmployee, employeeList.get(0));
                });
    }

    @Test
    @Order(2)
    public void testEmployeeList() {
        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/list", port)
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                        Assertions.assertArrayEquals(storedEmployeesList.toArray(EmployeeEntity[]::new),
                                response.getBody(EmployeeEntity[].class))
                )

                .request(MercuryITMongo.class)
                .connection()
                .db()
                .accept(response ->
                    Assertions.assertArrayEquals(storedEmployeesList.toArray(EmployeeEntity[]::new),
                            response.collection(EmployeeEntity.class).toArray(EmployeeEntity[]::new)));
    }

    @Test
    @Order(3)
    public void testGetEmployee() {
        storedEmployee = storedEmployeesList.get(3);

        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/%s", port, storedEmployee.getId())
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                        Assertions.assertEquals(storedEmployee, response.getBody(EmployeeEntity.class)));
    }

    @Test
    @Order(4)
    public void testEditEmployee() throws ClassNotFoundException {
        EmployeeEntity expectedEmployee = EmployeeEntity.builder()
                .id(storedEmployee.getId())
                .name(EMPLOYEE_NAME)
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/edit", port)
                .body(expectedEmployee)
                .put()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                    Assertions.assertEquals(expectedEmployee, response.getBody(EmployeeEntity.class))
                )

                .request(MercuryITMongo.class)
                .connection()
                .db()
                .accept(response -> {
                    List<EmployeeEntity> employeeList = response.collection(EmployeeEntity.class, Filters.eq("_id", new ObjectId(expectedEmployee.getId())));

                    Assertions.assertEquals(1, employeeList.size());
                    Assertions.assertEquals(expectedEmployee, employeeList.get(0));
                });
    }

    @Test
    @Order(5)
    public void testUpdateEmployee() throws ClassNotFoundException {
        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/update/%s", port, storedEmployee.getId())
                .body(Map.of("title", EMPLOYEE_TITLE))
                .patch()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    EmployeeEntity actualEmployeeEntity = response.getBody(EmployeeEntity.class);

                    Assertions.assertNotNull(actualEmployeeEntity);
                    Assertions.assertEquals(EMPLOYEE_NAME, actualEmployeeEntity.getName());
                    Assertions.assertEquals(EMPLOYEE_TITLE, actualEmployeeEntity.getTitle());
                })

                .request(MercuryITMongo.class)
                .connection()
                .db()
                .accept(response -> {
                    List<EmployeeEntity> employeeList = response.collection(EmployeeEntity.class, Filters.eq("_id", new ObjectId(storedEmployee.getId())));

                    Assertions.assertEquals(1, employeeList.size());
                    Assertions.assertEquals(EMPLOYEE_NAME, employeeList.get(0).getName());
                    Assertions.assertEquals(EMPLOYEE_TITLE, employeeList.get(0).getTitle());
                });
    }

    @Test
    @Order(6)
    public void testDeleteEmployee() throws ClassNotFoundException {
        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/delete/%s", port, storedEmployee.getId())
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).isEmpty()

                .request(MercuryITMongo.class)
                .connection()
                .db()
                .accept(response -> {
                    List<EmployeeEntity> employeeList = response.collection(EmployeeEntity.class, Filters.eq("_id", new ObjectId(storedEmployee.getId())));

                    Assertions.assertEquals(0, employeeList.size());
                });
    }
}
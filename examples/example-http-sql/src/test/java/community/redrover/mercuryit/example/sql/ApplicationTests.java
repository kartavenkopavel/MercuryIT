package community.redrover.mercuryit.example.sql;

import community.redrover.mercuryit.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;


@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationTests {

    private static final String EMPLOYEE_NAME = "Irina";
    private static final String EMPLOYEE_TITLE = "Project Manager";

    @LocalServerPort
    private int port;

    private static final String DB_CONNECTION_STR;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    static {
        String resourceName = "application.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();

        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {

            properties.load(resourceStream);

            DB_CONNECTION_STR = properties.getProperty("spring.datasource.url");
            DB_USER = properties.getProperty("spring.datasource.username");
            DB_PASSWORD = properties.getProperty("spring.datasource.password", "");
        } catch (IOException ioException) {
            throw new RuntimeException("Unable to load from application.properties " + ioException);
        }
    }

    private static EmployeeEntity storedEmployee = new EmployeeEntity();

    private static List<EmployeeEntity> storedEmployeesList = new ArrayList<>();

    private static Stream<Arguments> employeesData() {
        return Stream.of(
                Arguments.of("Yuliya", "SDET"),
                Arguments.of("Inna", "Developer"),
                Arguments.of("Lena", "QA Lead"),
                Arguments.of("Ira", "SDET"),
                Arguments.of("Liudmila", "PM"),
                Arguments.of("Viktoriya", "QA Engineer"),
                Arguments.of("Dmitry", "QA Engineer"),
                Arguments.of("Pavel", "Team Lead")
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

                .request(MercuryITSQL.class)
                .connection()
                .openf("select * from EMPLOYEE_ENTITY where ID = %d", expectedEmployee.getId())
                .accept(response ->
                    Assertions.assertEquals(expectedEmployee,
                            response.getNextRow(EmployeeEntity.class)));
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

                .request(MercuryITSQL.class)
                .connection()
                .open("select * from EMPLOYEE_ENTITY")
                .accept(response ->
                        Assertions.assertArrayEquals(storedEmployeesList.toArray(EmployeeEntity[]::new),
                                response.getRows(EmployeeEntity.class).toArray(EmployeeEntity[]::new)));
    }

    @Test
    @Order(3)
    public void testGetEmployee() {
        storedEmployee = storedEmployeesList.get(3);

        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/%d", port, storedEmployee.getId())
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                        Assertions.assertEquals(storedEmployee, response.getBody(EmployeeEntity.class))
                );
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

                .request(MercuryITSQL.class)
                .connection()
                .openf("select * from EMPLOYEE_ENTITY where ID = %d", expectedEmployee.getId())
                .accept(response ->
                        Assertions.assertEquals(expectedEmployee,
                                response.getNextRow(EmployeeEntity.class)));
    }

    @Test
    @Order(5)
    public void testUpdateEmployee() throws ClassNotFoundException {
        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/update/%d", port, storedEmployee.getId())
                .body(Map.of("title", EMPLOYEE_TITLE))
                .patch()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    EmployeeEntity actualEmployeeEntity = response.getBody(EmployeeEntity.class);

                    Assertions.assertNotNull(actualEmployeeEntity);
                    Assertions.assertEquals(EMPLOYEE_NAME, actualEmployeeEntity.getName());
                    Assertions.assertEquals(EMPLOYEE_TITLE, actualEmployeeEntity.getTitle());
                })

                .request(MercuryITSQL.class)
                .connection()
                .openf("select * from EMPLOYEE_ENTITY where ID = %d", storedEmployee.getId())
                .assertion(MercuryITSQLResponse::isEmpty).equalsTo(false)
                .accept(response -> {
                    EmployeeEntity actualEmployeeEntity = response.getNextRow(EmployeeEntity.class);

                    Assertions.assertNotNull(actualEmployeeEntity);
                    Assertions.assertEquals(EMPLOYEE_NAME, actualEmployeeEntity.getName());
                    Assertions.assertEquals(EMPLOYEE_TITLE, actualEmployeeEntity.getTitle());
                });
    }

    @Test
    @Order(6)
    public void testDeleteEmployee() throws ClassNotFoundException {
        MercuryIT.request(MercuryITHttp.class)
                .urlf("http://localhost:%d/api/employee/delete/%d", port, storedEmployee.getId())
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).isEmpty()

                .request(MercuryITSQL.class)
                .connection()
                .openf("select * from EMPLOYEE_ENTITY where ID = %d", storedEmployee.getId())
                .assertion(MercuryITSQLResponse::isEmpty).equalsTo(true);
    }
}
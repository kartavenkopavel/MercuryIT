package simple;

import community.redrover.mercuryit.MercuryIT;
import community.redrover.mercuryit.MercuryITHttp;
import community.redrover.mercuryit.MercuryITHttpResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationTests {

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

    private final static int PORT = 8080;
    private final static List<EmployeeEntity> LIST_OF_EMPLOYEES = new ArrayList<>();

    private static Stream<Arguments> employeesData() {
        return Stream.of(
                Arguments.of("Yuliya", "SDET"),
                Arguments.of("Inna", "DEV"),
                Arguments.of("Lena", "QA lead"),
                Arguments.of("Irina", "SDET"),
                Arguments.of("Liudmila", "PM"),
                Arguments.of("Viktoriya", "QA Engineer"),
                Arguments.of("Dmitry", "QA Engineer"),
                Arguments.of("Pavel", "Team Lead")
        );
    }

    @ParameterizedTest
    @MethodSource("employeesData")
    @Order(1)
    public void testCreateListOfEmployees(String name, String title) throws ClassNotFoundException {
        EmployeeEntity storedEmployee = EmployeeEntity.builder()
                .name(name)
                .title(title)
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/create", PORT)
                .body(storedEmployee)
                .post()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    EmployeeEntity actualEmployee = response.getBody(EmployeeEntity.class);
                    storedEmployee.setId(actualEmployee.getId());
                    Assertions.assertEquals(storedEmployee, actualEmployee);
                })
                .accept(response -> {
                    LIST_OF_EMPLOYEES.add(storedEmployee);
                    System.out.println(LIST_OF_EMPLOYEES);
                });

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = connection.createStatement().executeQuery("SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + storedEmployee.getId());
            result.next();
            EmployeeEntity sqlResultEmployee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
            result.close();

            Assertions.assertEquals(storedEmployee, sqlResultEmployee);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }

    @Test
    @Order(2)
    public void testGetListEmployees() throws ClassNotFoundException {

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/list", PORT)
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                        Assertions.assertArrayEquals(LIST_OF_EMPLOYEES.toArray(EmployeeEntity[]::new),
                                response.getBody(EmployeeEntity[].class))
                );

        EmployeeEntity sqlResultEmployee;
        List<EmployeeEntity> sqlEmployees = new ArrayList<>();

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM EMPLOYEE_ENTITY");

            while (result.next()) {
                sqlResultEmployee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
                sqlEmployees.add(sqlResultEmployee);
            }
            result.close();

            Assertions.assertEquals(LIST_OF_EMPLOYEES, sqlEmployees);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

    }

    @Test
    @Order(3)
    public void testGetEmployeeById() {

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/%d", PORT, LIST_OF_EMPLOYEES.get(3).getId())
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                        Assertions.assertEquals(LIST_OF_EMPLOYEES.get(3), response.getBody(EmployeeEntity.class))
                );
    }

    @Test
    @Order(4)
    public void testEditEmployee() throws ClassNotFoundException {

        EmployeeEntity editEmployee = EmployeeEntity.builder()
                .id(LIST_OF_EMPLOYEES.get(2).getId())
                .name("Elena")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/edit", PORT)
                .body(editEmployee)
                .put()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                    Assertions.assertEquals(editEmployee, response.getBody(EmployeeEntity.class))
                );

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = connection.createStatement().executeQuery("SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + LIST_OF_EMPLOYEES.get(2).getId());

            result.next();
            EmployeeEntity sqlResultEmployee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
            result.close();

        Assertions.assertEquals(editEmployee, sqlResultEmployee);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }

    @Test
    @Order(5)
    public void testUpdateEmployee() throws ClassNotFoundException {
        EmployeeEntity editEmployee = EmployeeEntity.builder()
                .title("Project Manager")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/update/%d", PORT, LIST_OF_EMPLOYEES.get(5).getId())
                .body(editEmployee)
                .patch()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    Assertions.assertEquals(LIST_OF_EMPLOYEES.get(5).getName(), response.getBody(EmployeeEntity.class).getName());
                    Assertions.assertEquals(LIST_OF_EMPLOYEES.get(5).getId(), response.getBody(EmployeeEntity.class).getId());
                    Assertions.assertEquals(editEmployee.getTitle(), response.getBody(EmployeeEntity.class).getTitle());
                });

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM EMPLOYEE_ENTITY  WHERE ID = " + LIST_OF_EMPLOYEES.get(5).getId());

            result.next();
            EmployeeEntity sqlResultEmployee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
            result.close();

        Assertions.assertNotNull(sqlResultEmployee.getName());
        Assertions.assertEquals(editEmployee.getTitle(), sqlResultEmployee.getTitle());

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }

    @Test
    @Order(6)
    public void testDeleteEmployeeByID() throws ClassNotFoundException {

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/delete/%d", PORT, LIST_OF_EMPLOYEES.get(3).getId())
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).isEmpty();

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = connection.createStatement().executeQuery("SELECT count(*) as numberOfEmployees FROM EMPLOYEE_ENTITY  WHERE ID = " + LIST_OF_EMPLOYEES.get(3).getId());

            result.next();
            int count = result.getInt("numberOfEmployees");
            result.close();

            Assertions.assertEquals(0, count);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

        LIST_OF_EMPLOYEES.remove(LIST_OF_EMPLOYEES.get(3));
    }

    @Test
    @Order(7)
    public void testDeleteListOfEmployees() throws ClassNotFoundException {

        for (EmployeeEntity employee : LIST_OF_EMPLOYEES) {
            MercuryIT.request(MercuryITHttp.class)
                    .urif("http://localhost:%d/api/employee/delete/%d", PORT, employee.getId())
                    .delete()
                    .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                    .assertion(MercuryITHttpResponse::getBody).isEmpty();
        }

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {
            ResultSet result = connection.createStatement().executeQuery("SELECT count(*) as numberOfEmployees FROM EMPLOYEE_ENTITY");

            result.next();
            int count = result.getInt("numberOfEmployees");
            result.close();

            Assertions.assertEquals(0, count);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }
}

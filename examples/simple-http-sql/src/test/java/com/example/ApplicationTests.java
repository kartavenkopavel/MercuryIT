package com.example;

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

    private static final int PORT = 8080;
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

    private static List<EmployeeEntity> employeesList = new ArrayList<>();

    private static Stream<Arguments> employeesData() {
        return Stream.of(
                Arguments.of("Yuliya", "SDET"),
                Arguments.of("Inna", "DEV"),
                Arguments.of("Lena", "QA lead"),
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
    public void testCreateListOfEmployees(String name, String title) throws ClassNotFoundException {
        EmployeeEntity initialEmployee = EmployeeEntity.builder()
                .name(name)
                .title(title)
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/create", PORT)
                .body(initialEmployee)
                .post()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    EmployeeEntity actualEmployee = response.getBody(EmployeeEntity.class);
                    initialEmployee.setId(actualEmployee.getId());
                    Assertions.assertEquals(initialEmployee, actualEmployee);
                })
                .accept(response ->
                    employeesList.add(initialEmployee)
                );

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM EMPLOYEE_ENTITY WHERE ID = " + initialEmployee.getId());
            result.next();
            EmployeeEntity sqlResultEmployee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
            result.close();

            Assertions.assertEquals(initialEmployee, sqlResultEmployee);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }

    @Test
    @Order(2)
    public void testGetListOfEmployees() throws ClassNotFoundException {
        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/list", PORT)
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                        Assertions.assertArrayEquals(employeesList.toArray(EmployeeEntity[]::new),
                                response.getBody(EmployeeEntity[].class))
                );

        List<EmployeeEntity> sqlResultEmployees = new ArrayList<>();

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM EMPLOYEE_ENTITY");

            while (result.next()) {
                sqlResultEmployees.add(new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title")));
            }
            result.close();

            Assertions.assertEquals(employeesList, sqlResultEmployees);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }

    @Test
    @Order(3)
    public void testGetEmployeeById() {
        storedEmployee = employeesList.get(3);

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/%d", PORT, storedEmployee.getId())
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                        Assertions.assertEquals(storedEmployee, response.getBody(EmployeeEntity.class))
                );
    }

    @Test
    @Order(4)
    public void testEditEmployee() throws ClassNotFoundException {
        EmployeeEntity initialEmployee = EmployeeEntity.builder()
                .id(storedEmployee.getId())
                .name("Irina")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/edit", PORT)
                .body(initialEmployee)
                .put()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                    Assertions.assertEquals(initialEmployee, response.getBody(EmployeeEntity.class))
                )
                .accept(response ->
                    storedEmployee.setName(initialEmployee.getName())
                );

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM EMPLOYEE_ENTITY WHERE ID = " + storedEmployee.getId());

            result.next();
            EmployeeEntity sqlResultEmployee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
            result.close();

            Assertions.assertEquals(initialEmployee, sqlResultEmployee);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }

    @Test
    @Order(5)
    public void testUpdateEmployee() throws ClassNotFoundException {
        EmployeeEntity initialEmployee = EmployeeEntity.builder()
                .title("Project Manager")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/update/%d", PORT, storedEmployee.getId())
                .body(initialEmployee)
                .patch()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    EmployeeEntity httpResponseEmployee = response.getBody(EmployeeEntity.class);
                    Assertions.assertEquals(storedEmployee.getName(), httpResponseEmployee.getName());
                    Assertions.assertEquals(initialEmployee.getTitle(), httpResponseEmployee.getTitle());
                });

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM EMPLOYEE_ENTITY WHERE ID = " + storedEmployee.getId());

            result.next();
            EmployeeEntity sqlResultEmployee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
            result.close();

            Assertions.assertEquals(storedEmployee.getName(), sqlResultEmployee.getName());
            Assertions.assertEquals(initialEmployee.getTitle(), sqlResultEmployee.getTitle());

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }

    @Test
    @Order(6)
    public void testDeleteEmployeeByID() throws ClassNotFoundException {
        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/delete/%d", PORT, storedEmployee.getId())
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).isEmpty();

        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = connection.createStatement().executeQuery("SELECT COUNT(*) AS NUMBERS_OF_EMPLOYEES FROM EMPLOYEE_ENTITY WHERE ID = " + storedEmployee.getId());

            result.next();
            int count = result.getInt("NUMBERS_OF_EMPLOYEES");
            result.close();

            Assertions.assertEquals(0, count);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }
}
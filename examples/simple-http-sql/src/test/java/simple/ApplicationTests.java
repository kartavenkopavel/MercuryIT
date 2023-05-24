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
    private static final List<EmployeeEntity> listOfEmployees = new ArrayList<>();
    private static EmployeeEntity STORED_EMPLOYEE;

    private static Stream<Arguments> sourceData() {
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
    @Order(1)
    @MethodSource("sourceData")
    public void testCreateListOfEmployees(String name, String title) throws ClassNotFoundException {

        STORED_EMPLOYEE = EmployeeEntity.builder()
                .name(name)
                .title(title)
                .build();
        listOfEmployees.add(STORED_EMPLOYEE);

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/create", PORT)
                .body(STORED_EMPLOYEE)
                .post()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    EmployeeEntity actualEmployee = response.getBody(EmployeeEntity.class);
                    STORED_EMPLOYEE.setId(actualEmployee.getId());
                    Assertions.assertEquals(STORED_EMPLOYEE, actualEmployee);
                });

        EmployeeEntity employee;

        int count;
        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = conn.createStatement().executeQuery("SELECT count(*) as cnt  FROM EMPLOYEE_ENTITY");
            result.next();
            count = result.getInt("cnt");
            result.close();

            result = conn.createStatement().executeQuery("SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + STORED_EMPLOYEE.getId());
            result.next();
            employee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
            result.close();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

        Assertions.assertEquals(listOfEmployees.size(), count);
        Assertions.assertEquals(STORED_EMPLOYEE, employee);
    }

    @Test
    @Order(2)
    public void testGetListEmployees() throws ClassNotFoundException {

        List<EmployeeEntity> sqlEmployees = new ArrayList<>();
        EmployeeEntity employee;

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = conn.createStatement().executeQuery("SELECT * FROM EMPLOYEE_ENTITY");

            while (result.next()) {
                employee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
                sqlEmployees.add(employee);
            }
            result.close();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/list", PORT)
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                        Assertions.assertArrayEquals(sqlEmployees.toArray(new EmployeeEntity[listOfEmployees.size()]),
                                response.getBody(EmployeeEntity[].class))
                );
    }

    @Test
    @Order(3)
    public void testGetEmployeeById() throws ClassNotFoundException {
        EmployeeEntity employee;

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = conn.createStatement().executeQuery("SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + listOfEmployees.get(3).getId());

            result.next();
            employee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
            result.close();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

        Assertions.assertEquals(listOfEmployees.get(3), employee);

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/%d", PORT, listOfEmployees.get(3).getId())
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                    Assertions.assertEquals(employee, response.getBody(EmployeeEntity.class))
                );
    }

    @Test
    @Order(4)
    public void testEditEmployee() throws ClassNotFoundException {
        EmployeeEntity editEmployee = EmployeeEntity.builder()
                .id(listOfEmployees.get(2).getId())
                .title("Project Manager")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/edit", PORT)
                .body(editEmployee)
                .put()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    Assertions.assertNull(response.getBody(EmployeeEntity.class).getName());
                    Assertions.assertEquals(editEmployee, response.getBody(EmployeeEntity.class));
                });

        EmployeeEntity employee;

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = conn.createStatement().executeQuery("SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + listOfEmployees.get(2).getId());

            result.next();
            employee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
            result.close();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

        Assertions.assertNull(employee.getName());
        Assertions.assertEquals(editEmployee, employee);
    }

    @Test
    @Order(5)
    public void testUpdateEmployee() throws ClassNotFoundException {
        EmployeeEntity editEmployee = EmployeeEntity.builder()
                .name("Elena")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/update/%d", PORT, listOfEmployees.get(2).getId())
                .body(editEmployee)
                .patch()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    Assertions.assertNotNull(response.getBody(EmployeeEntity.class).getTitle());
                    Assertions.assertEquals(editEmployee.getName(), response.getBody(EmployeeEntity.class).getName());
                });

        EmployeeEntity employee;

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = conn.createStatement().executeQuery("SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + listOfEmployees.get(2).getId());

            result.next();
            employee = new EmployeeEntity(result.getLong("id"), result.getString("name"), result.getString("title"));
            result.close();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

        Assertions.assertNotNull(employee.getTitle());
        Assertions.assertEquals(editEmployee.getName(), employee.getName());
    }

    @Test
    @Order(6)
    public void testDeleteEmployeeByID() throws ClassNotFoundException {

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/delete/%d", PORT, listOfEmployees.get(3).getId())
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).isEmpty();

        List<Long> sqlEmployeesIDs = new ArrayList<>();

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            ResultSet result = conn.createStatement().executeQuery("SELECT count(*) as cnt FROM EMPLOYEE_ENTITY");

            result.next();
            int count = result.getInt("cnt");
            result.close();

            Assertions.assertEquals(listOfEmployees.size() - 1, count);

            result = conn.createStatement().executeQuery("SELECT *  FROM EMPLOYEE_ENTITY");

            while (result.next()) {
                sqlEmployeesIDs.add(result.getLong("id"));
            }
            result.close();

            Assertions.assertFalse(sqlEmployeesIDs.contains(listOfEmployees.get(3).getId()));

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

        listOfEmployees.remove(listOfEmployees.get(3));
    }

    @Test
    @Order(7)
    public void testDeleteListOfEmployees() throws ClassNotFoundException {

        for (EmployeeEntity employee : listOfEmployees) {
            MercuryIT.request(MercuryITHttp.class)
                    .urif("http://localhost:%d/api/employee/delete/%d", PORT, employee.getId())
                    .delete()
                    .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                    .assertion(MercuryITHttpResponse::getBody).isEmpty();
        }

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {
            ResultSet result = conn.createStatement().executeQuery("SELECT count(*) as cnt FROM EMPLOYEE_ENTITY");

            result.next();
            int count = result.getInt("cnt");
            result.close();

            Assertions.assertEquals(0, count);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }
}

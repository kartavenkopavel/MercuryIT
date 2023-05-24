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

    private final static EmployeeEntity STORED_EMPLOYEE = EmployeeEntity.builder()
            .name("Pavel")
            .title("QA")
            .build();

    private static final List<EmployeeEntity> listOfEmployees = new ArrayList<>();
    private static EmployeeEntity STORED_EMPLOYEE_1;

    private static Stream<Arguments> getSourceData() {
        return Stream.of(
                Arguments.of("John", "SDET"),
                Arguments.of("Jane", "DEV"),
                Arguments.of("Alice", "QA lead"),
                Arguments.of("Bob", "PM"),
                Arguments.of("Charlie", "Engineer")
        );
    }

    @ParameterizedTest
    @Order(1)
    @MethodSource("getSourceData")
    public void testCreateListOfEmployees(String name, String title) throws ClassNotFoundException {

        STORED_EMPLOYEE_1 = EmployeeEntity.builder()
                .name(name)
                .title(title)
                .build();
        listOfEmployees.add(STORED_EMPLOYEE_1);

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/create", PORT)
                .body(STORED_EMPLOYEE_1)
                .post()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    EmployeeEntity actualEmployee = response.getBody(EmployeeEntity.class);
                    STORED_EMPLOYEE_1.setId(actualEmployee.getId());
                    Assertions.assertEquals(STORED_EMPLOYEE_1, actualEmployee);
                });

        EmployeeEntity employee;

        int count;
        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT count(*) as cnt  FROM EMPLOYEE_ENTITY";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            rs.next();
            count = rs.getInt("cnt");
            rs.close();

            sql = "SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + STORED_EMPLOYEE_1.getId();
            rs = conn.createStatement().executeQuery(sql);

            rs.next();
            employee = new EmployeeEntity(rs.getLong("id"), rs.getString("name"), rs.getString("title"));

            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
        Assertions.assertEquals(listOfEmployees.size(), count);
        Assertions.assertEquals(STORED_EMPLOYEE_1, employee);
    }

    @Test
    @Order(2)
    public void testDeleteListOfEmployees() throws ClassNotFoundException {

        for (EmployeeEntity employee : listOfEmployees) {
        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/delete/%d", PORT, employee.getId())
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).isEmpty();

            System.out.println("DELETED ITEMS " + employee.getId() + employee.getName());
        }

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT count(*) as cnt FROM EMPLOYEE_ENTITY";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            rs.next();
            int count = rs.getInt("cnt");
            rs.close();

            Assertions.assertEquals(0, count);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }

    @Test
    @Order(3)
    public void testCreateEmployee() throws ClassNotFoundException {
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

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT count(*) as cnt  FROM EMPLOYEE_ENTITY  WHERE ID = " + STORED_EMPLOYEE.getId();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            rs.next();
            int count = rs.getInt("cnt");
            rs.close();

            Assertions.assertEquals(1, count);

            sql = "SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + STORED_EMPLOYEE.getId();
            rs = conn.createStatement().executeQuery(sql);

            rs.next();
            employee = new EmployeeEntity(rs.getLong("id"), rs.getString("name"), rs.getString("title"));

            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

        Assertions.assertEquals(STORED_EMPLOYEE, employee);
    }

    @Test
    @Order(4)
    public void testGetListEmployees() {
        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/list", PORT)
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                        Assertions.assertArrayEquals(new EmployeeEntity[]{STORED_EMPLOYEE},
                                response.getBody(EmployeeEntity[].class))
                );
    }

    @Test
    @Order(5)
    public void testGetEmployeeById() {
        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/%d", PORT, STORED_EMPLOYEE.getId())
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response ->
                        Assertions.assertEquals(STORED_EMPLOYEE, response.getBody(EmployeeEntity.class))
                );
    }

    @Test
    @Order(6)
    public void testEditEmployee() throws ClassNotFoundException {
        EmployeeEntity editEmployee = EmployeeEntity.builder()
                .id(STORED_EMPLOYEE.getId())
                .name("Sergei")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/edit", PORT)
                .body(editEmployee)
                .put()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    Assertions.assertNull(response.getBody(EmployeeEntity.class).getTitle());
                    Assertions.assertEquals(editEmployee, response.getBody(EmployeeEntity.class));
                });

        EmployeeEntity employee;

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            String sql = "SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + STORED_EMPLOYEE.getId();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            rs.next();
            employee = new EmployeeEntity(rs.getLong("id"), rs.getString("name"), rs.getString("title"));

            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

        Assertions.assertNull(employee.getTitle());
        Assertions.assertEquals(editEmployee, employee);
    }

    @Test
    @Order(7)
    public void testUpdateEmployee() throws ClassNotFoundException {
        EmployeeEntity editEmployee = EmployeeEntity.builder()
                .title("Developer")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/update/%d", PORT, STORED_EMPLOYEE.getId())
                .body(editEmployee)
                .patch()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .accept(response -> {
                    Assertions.assertNotNull(response.getBody(EmployeeEntity.class).getName());
                    Assertions.assertEquals("Sergei", response.getBody(EmployeeEntity.class).getName());
                    Assertions.assertEquals(editEmployee.getTitle(), response.getBody(EmployeeEntity.class).getTitle());
                });

        EmployeeEntity employee;

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            String sql = "SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + STORED_EMPLOYEE.getId();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            rs.next();
            employee = new EmployeeEntity(rs.getLong("id"), rs.getString("name"), rs.getString("title"));

            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }

        Assertions.assertEquals("Sergei", employee.getName());
        Assertions.assertEquals(editEmployee.getTitle(), employee.getTitle());
    }

    @Test
    @Order(8)
    public void testDeleteEmployee() throws ClassNotFoundException {
        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/delete/%d", PORT, STORED_EMPLOYEE.getId())
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).isEmpty();

        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection(DB_CONNECTION_STR, DB_USER, DB_PASSWORD)) {

            String sql = "SELECT count(*) as cnt  FROM EMPLOYEE_ENTITY  WHERE ID = " + STORED_EMPLOYEE.getId();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            rs.next();
            int count = rs.getInt("cnt");
            rs.close();

            Assertions.assertEquals(0, count);

        } catch (SQLException e) {
            throw new RuntimeException("Unable to work with H2 database: " + e);
        }
    }
}

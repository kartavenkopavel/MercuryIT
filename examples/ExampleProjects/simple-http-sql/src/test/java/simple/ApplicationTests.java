package simple;

import community.redrover.mercuryit.MercuryIT;
import community.redrover.mercuryit.MercuryITHttp;
import community.redrover.mercuryit.MercuryITHttpResponse;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

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

        try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {

            properties.load(resourceStream);

            DB_CONNECTION_STR = properties.getProperty("spring.datasource.url");
            DB_USER = properties.getProperty("spring.datasource.username");
            DB_PASSWORD = properties.getProperty("spring.datasource.password", "");
            System.out.println(DB_CONNECTION_STR + " user:" + DB_USER + " password:[" + DB_PASSWORD + "]");
        } catch (IOException ioException) {
            throw new RuntimeException("Unable to load from application.properties " + ioException);
        }
    }

    private final static int PORT = 8080;

    private final static EmployeeEntity STORED_EMPLOYEE = EmployeeEntity.builder()
            .name("Pavel")
            .title("QA")
            .build();

    @Test
    @Order(1)
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
            String sql =  "SELECT count(*) as cnt  FROM EMPLOYEE_ENTITY  WHERE ID = "+ STORED_EMPLOYEE.getId();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            rs.next();
            int count = rs.getInt("cnt");
            rs.close();

            Assertions.assertEquals(1, count);

            sql =  "SELECT *  FROM EMPLOYEE_ENTITY  WHERE ID = " + STORED_EMPLOYEE.getId();
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
    @Order(2)
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
    @Order(3)
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
    @Order(4)
    public void testEditEmployee() {
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
    }

    @Test
    @Order(5)
    public void testUpdateEmployee() {
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
                    Assertions.assertEquals("Sergei",  response.getBody(EmployeeEntity.class).getName());
                    Assertions.assertEquals(editEmployee.getTitle(), response.getBody(EmployeeEntity.class).getTitle());
                });
    }

    @Test
    @Order(6)
    public void testDeleteEmployee() {
        MercuryIT.request(MercuryITHttp.class)
                .urif("http://localhost:%d/api/employee/delete/%d", PORT, STORED_EMPLOYEE.getId())
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).isEmpty();
    }
}

package com.example.simplesql;

import community.redrover.mercuryit.MercuryIT;
import community.redrover.mercuryit.MercuryITHttp;
import community.redrover.mercuryit.MercuryITHttpResponse;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

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

    @Test
    @Order(1)
    public void testCreateEmployee() {
        EmployeeEntity employee = EmployeeEntity.builder()
                .name("Pavel")
                .title("QA")
                .build();

        MercuryIT.request(MercuryITHttp.class)
                .uri(getUrl("create"))
                .body(employee)
                .post()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).equalsTo("{\"id\":1,\"name\":\"Pavel\",\"title\":\"QA\"}");
    }

    @Test
    @Order(2)
    public void testGetListEmployees() {
        MercuryIT.request(MercuryITHttp.class)
                .uri(getUrl("list"))
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).equalsTo("[{\"id\":1,\"name\":\"Pavel\",\"title\":\"QA\"}]");
    }

    @Test
    @Order(3)
    public void testGetEmployeeById() {
        MercuryIT.request(MercuryITHttp.class)
                .uri(getUrl("1"))
                .get()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).equalsTo("{\"id\":1,\"name\":\"Pavel\",\"title\":\"QA\"}");
    }

    @Test
    @Order(4)
    public void testDeleteEmployee() {
        MercuryIT.request(MercuryITHttp.class)
                .uri(getUrl("delete/1"))
                .delete()
                .assertion(MercuryITHttpResponse::getCode).equalsTo(200)
                .assertion(MercuryITHttpResponse::getBody).equalsTo("");
    }
}

package com.example;

import lombok.extern.java.Log;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;

@Log
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run((Application.class));
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers");
    }
}
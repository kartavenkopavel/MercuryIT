package simple;

import lombok.extern.java.Log;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.sql.SQLException;

@Log
@SpringBootApplication
@EntityScan(basePackages = "simple")
public class Application {
    public static void main(String[] args) {
        try {
            Server h2Server = Server.createTcpServer().start();
            if (h2Server.isRunning(true)) {
                log.info("H2 server was started and is running. on port " + h2Server.getPort());
            } else {
                throw new RuntimeException("Could not start H2 server.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to start H2 server: ", e);
        }
        SpringApplication.run((Application.class));
    }
}

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
// you may need this if want to connect to db from SQL IDE like DBeaver
//            Server h2Server = Server.createTcpServer("-tcpAllowOthers", "-webAllowOthers").start();
            Server h2Server = Server.createTcpServer().start();
            if (h2Server.isRunning(true)) {
                log.info("H2 server was started and is running. on port " + h2Server.getPort() +
                        "\n" + h2Server.getStatus());
            } else {
                throw new RuntimeException("Could not start H2 server.\n" + h2Server.getStatus());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to start H2 server: ", e);
        }
        SpringApplication.run((Application.class));
    }
}

/*  some usefull info about H2 server running in TCP mode
Server server = Server.createTcpServer(
     "-tcpPort", "9123", "-tcpAllowOthers").start();

Supported options are: -tcpPort, -tcpSSL, -tcpPassword, -tcpAllowOthers, -tcpDaemon, -trace, -ifExists, -ifNotExists, -baseDir, -key. See the main method for details.
If no port is specified, the default port is used if possible, and if this port is already used, a random port is used. Use getPort() or getURL() after starting to retrieve the port.
*/

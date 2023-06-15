package community.redrover.mercuryit;

import lombok.SneakyThrows;

import java.sql.DriverManager;


public class MercuryITSQL extends MercuryITRequest<MercuryITSQL> {

    MercuryITSQL(MercuryITConfigHolder configHolder) {
        super(configHolder);
    }

    public MercuryITSQLConnection connection() {
        MercuryITSQLConfig mercuryITSQLConfig = config(MercuryITSQLConfig.class);
        return connection(mercuryITSQLConfig.getDriver(), mercuryITSQLConfig.getConnect(),
                mercuryITSQLConfig.getUsername(), mercuryITSQLConfig.getPassword());
    }

    @SneakyThrows
    public MercuryITSQLConnection connection(String driver, String connect, String user, String password) {
        Class.forName(driver);
        return new MercuryITSQLConnection(getConfigHolder(), DriverManager.getConnection(connect, user, password));
    }
}

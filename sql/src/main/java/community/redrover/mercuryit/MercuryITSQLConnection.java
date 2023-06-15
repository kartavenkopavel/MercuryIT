package community.redrover.mercuryit;

import lombok.SneakyThrows;

import java.sql.Connection;


public class MercuryITSQLConnection extends MercuryITResponse<MercuryITSQLConnection> {

    private final Connection connection;

    MercuryITSQLConnection(MercuryITConfigHolder configHolder, Connection connection) {
        super(configHolder);
        this.connection = connection;
    }

    @SneakyThrows
    public MercuryITSQLResponse open(String query) {
        return new MercuryITSQLResponse(getConfigHolder(), connection.createStatement().executeQuery(query));
    }

    public MercuryITSQLResponse openf(String query, Object... args) {
        return open(String.format(query, args));
    }
}

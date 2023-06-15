package community.redrover.mercuryit;

import lombok.*;


@Getter
public class MercuryITSQLConfig extends MercuryITConfig {

    static final String CONFIG_NAME = "sql";

    private String driver;
    private String connect;
    private String username;
    private String password;

    MercuryITSQLConfig(MercuryITConfigHolder configHolder) {
        super(configHolder);
        this.driver = configuration().getString(name(APP_NAME, CONFIG_NAME, "driver"));
        this.connect = configuration().getString(name(APP_NAME, CONFIG_NAME, "connect"));
        this.username = configuration().getString(name(APP_NAME, CONFIG_NAME, "username"));
        this.password = configuration().getString(name(APP_NAME, CONFIG_NAME, "password"));
    }

    @Builder(toBuilder = true)
    MercuryITSQLConfig(MercuryITConfigHolder configHolder, String driver, String connect, String username, String password) {
        this(configHolder);
        this.driver = driver;
        this.connect = connect;
        this.username = username;
        this.password = password;
    }

    @Override
    protected MercuryITConfig copy(MercuryITConfigHolder configHolder) {
        return this.toBuilder().configHolder(configHolder).build();
    }

    public MercuryITSQLConfig driver(String driver) {
        this.driver = driver;
        return this;
    }

    public MercuryITSQLConfig connect(String connect) {
        this.connect = connect;
        return this;
    }

    public MercuryITSQLConfig username(String username) {
        this.username = username;
        return this;
    }

    public MercuryITSQLConfig password(String password) {
        this.password = password;
        return this;
    }
}

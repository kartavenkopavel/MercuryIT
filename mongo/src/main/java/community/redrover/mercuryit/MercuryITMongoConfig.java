package community.redrover.mercuryit;

import lombok.*;


@Getter
public class MercuryITMongoConfig extends MercuryITConfig {

    static final String CONFIG_NAME = "mongo";

    private String uri;
    private String database;

    MercuryITMongoConfig(MercuryITConfigHolder configHolder) {
        super(configHolder);
        this.uri = configuration().getString(name(APP_NAME, CONFIG_NAME, "uri"));
        this.database = configuration().getString(name(APP_NAME, CONFIG_NAME, "database"));
    }

    @Builder(toBuilder = true)
    MercuryITMongoConfig(MercuryITConfigHolder configHolder, String uri, String database) {
        this(configHolder);
        this.uri = uri;
        this.database = database;
    }

    @Override
    protected MercuryITConfig copy(MercuryITConfigHolder configHolder) {
        return this.toBuilder().configHolder(configHolder).build();
    }

    public MercuryITMongoConfig uri(String uri) {
        this.uri = uri;
        return this;
    }

    public MercuryITMongoConfig database(String database) {
        this.uri = database;
        return this;
    }
}

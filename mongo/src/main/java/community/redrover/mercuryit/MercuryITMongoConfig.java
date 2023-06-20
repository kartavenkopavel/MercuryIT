package community.redrover.mercuryit;

import lombok.*;


@Getter
public class MercuryITMongoConfig extends MercuryITConfig {

    static final String CONFIG_NAME = "mongo";

    private String url;
    private String database;

    MercuryITMongoConfig(MercuryITConfigHolder configHolder) {
        super(configHolder);
        this.url = configuration().getString(name(APP_NAME, CONFIG_NAME, "url"));
        this.database = configuration().getString(name(APP_NAME, CONFIG_NAME, "database"));
    }

    @Builder(toBuilder = true)
    MercuryITMongoConfig(MercuryITConfigHolder configHolder, String url, String database) {
        this(configHolder);
        this.url = url;
        this.database = database;
    }

    @Override
    protected MercuryITConfig copy(MercuryITConfigHolder configHolder) {
        return this.toBuilder().configHolder(configHolder).build();
    }

    public MercuryITMongoConfig uri(String uri) {
        this.url = uri;
        return this;
    }

    public MercuryITMongoConfig database(String database) {
        this.url = database;
        return this;
    }
}

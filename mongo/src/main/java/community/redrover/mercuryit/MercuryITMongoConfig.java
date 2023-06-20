package community.redrover.mercuryit;

import lombok.*;


@Getter
public class MercuryITMongoConfig extends MercuryITConfig {

    static final String CONFIG_NAME = "mongo";

    private String url;
    private String db;

    MercuryITMongoConfig(MercuryITConfigHolder configHolder) {
        super(configHolder);
        this.url = configuration().getString(name(APP_NAME, CONFIG_NAME, "url"));
        this.db = configuration().getString(name(APP_NAME, CONFIG_NAME, "db"));
    }

    @Builder(toBuilder = true)
    MercuryITMongoConfig(MercuryITConfigHolder configHolder, String url, String db) {
        this(configHolder);
        this.url = url;
        this.db = db;
    }

    @Override
    protected MercuryITConfig copy(MercuryITConfigHolder configHolder) {
        return this.toBuilder().configHolder(configHolder).build();
    }

    public MercuryITMongoConfig url(String url) {
        this.url = url;
        return this;
    }

    public MercuryITMongoConfig db(String db) {
        this.url = db;
        return this;
    }
}

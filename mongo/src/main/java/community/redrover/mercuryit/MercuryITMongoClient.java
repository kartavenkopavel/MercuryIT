package community.redrover.mercuryit;

import com.mongodb.client.MongoClient;


public class MercuryITMongoClient extends MercuryITResponseAutoCloseable<MercuryITMongoClient> {

    private final MongoClient mongoClient;

    MercuryITMongoClient(MercuryITConfigHolder configHolder, MongoClient mongoClient) {
        super(configHolder);
        this.mongoClient = mongoClient;

        registerAutoCloseable(mongoClient);
    }

    public MercuryITMongoResponse db() {
        MercuryITMongoConfig mercuryITMongoConfig = config(MercuryITMongoConfig.class);
        return db(mercuryITMongoConfig.getDb());
    }

    public MercuryITMongoResponse db(String dbName) {
        return new MercuryITMongoResponse(getConfigHolder(), this, mongoClient.getDatabase(dbName));
    }
}

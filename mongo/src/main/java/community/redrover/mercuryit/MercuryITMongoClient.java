package community.redrover.mercuryit;

import com.mongodb.client.MongoClient;


public class MercuryITMongoClient extends MercuryITResponseAutoCloseable<MercuryITMongoClient> {

    private final MongoClient mongoClient;

    MercuryITMongoClient(MercuryITConfigHolder configHolder, MongoClient mongoClient) {
        super(configHolder);
        this.mongoClient = mongoClient;

        registerAutoCloseable(mongoClient);
    }

    public MercuryITMongoResponse database() {
        MercuryITMongoConfig mercuryITMongoConfig = config(MercuryITMongoConfig.class);
        return database(mercuryITMongoConfig.getDatabase());
    }

    public MercuryITMongoResponse database(String database) {
        return new MercuryITMongoResponse(getConfigHolder(), this, mongoClient.getDatabase(database));
    }
}

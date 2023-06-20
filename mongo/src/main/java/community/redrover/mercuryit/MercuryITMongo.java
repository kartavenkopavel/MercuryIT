package community.redrover.mercuryit;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import community.redrover.mercuryit.utils.MercuryStringCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;


public class MercuryITMongo extends MercuryITRequest<MercuryITMongo> {

    MercuryITMongo(MercuryITConfigHolder configHolder) {
        super(configHolder);
    }

    public MercuryITMongoClient connection() {
        MercuryITMongoConfig mercuryITMongoConfig = config(MercuryITMongoConfig.class);
        return connection(mercuryITMongoConfig.getUri());
    }

    public MercuryITMongoClient connection(String uri) {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
                PojoCodecProvider.builder()
                        .automatic(true)
                        .build());

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new MercuryStringCodec()),
                MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .codecRegistry(codecRegistry)
                .build();

        return new MercuryITMongoClient(getConfigHolder(), MongoClients.create(clientSettings));
    }
}

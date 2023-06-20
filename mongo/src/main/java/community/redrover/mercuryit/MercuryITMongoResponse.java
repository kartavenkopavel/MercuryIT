package community.redrover.mercuryit;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;


public class MercuryITMongoResponse extends MercuryITResponse<MercuryITMongoResponse> {

    private final MercuryITMongoClient mongoClient;
    private final MongoDatabase db;

    MercuryITMongoResponse(MercuryITConfigHolder configHolder, MercuryITMongoClient mongoClient, MongoDatabase db) {
        super(configHolder);
        this.mongoClient = mongoClient;
        this.db = db;
    }

    protected String convertClassNameToCollectionName(Class<?> clazz) {
        return clazz.getSimpleName().substring(0, 1).toLowerCase()
                + clazz.getSimpleName().substring(1);
    }

    protected <T> List<T> collection(Class<T> clazz, String collectionName, Bson filter) {
        return db.getCollection(collectionName, clazz).find(filter).into(new ArrayList<>());
    }

    public List<Document> collection(String collectionName, Bson filter) {
        return collection(Document.class, collectionName, filter);
    }

    public List<Document> collection(String collectionName) {
        return collection(collectionName, Filters.empty());
    }

    public <T> List<T> collection(Class<T> clazz, Bson filter) {
        return collection(clazz, convertClassNameToCollectionName(clazz), filter);
    }

    public <T> List<T> collection(Class<T> clazz) {
        return collection(clazz, Filters.empty());
    }

    public MercuryITMongoClient drop() {
        db.drop();
        return mongoClient;
    }
}

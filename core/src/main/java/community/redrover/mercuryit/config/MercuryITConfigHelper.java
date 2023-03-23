package community.redrover.mercuryit.config;

public class MercuryITConfigHelper {

    private final MercuryITConfig config;

    public MercuryITConfigHelper(MercuryITConfig config) {
        this.config = config;
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        return (T)config.getFromJson().apply(json, clazz);
    }

    public String toJson(Object object) {
        return config.getToJson().apply(object);
    }
}

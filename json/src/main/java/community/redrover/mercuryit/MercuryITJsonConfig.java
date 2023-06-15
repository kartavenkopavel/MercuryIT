package community.redrover.mercuryit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.*;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;


@SuppressWarnings("unchecked")
public class MercuryITJsonConfig extends MercuryITConfig {

    private static class JsonJackson {

        private final ObjectMapper objectMapper = JsonMapper.builder()
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .build();

        public <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
            return objectMapper.convertValue(map, clazz);
        }

        @SneakyThrows
        public <T> T fromJson(String json, Class<T> clazz) {
            return objectMapper.readValue(json, clazz);
        }

        @SneakyThrows
        public String toJson(Object object) {
            return objectMapper.writeValueAsString(object);
        }
    }

    private BiFunction<Map<String, Object>, Class<?>, ?> fromMap;

    private BiFunction<String, Class<?>, ?> fromJson;

    private Function<Object, String> toJson;

    MercuryITJsonConfig(MercuryITConfigHolder configHolder) {
        super(configHolder);

        JsonJackson defaultJson = new JsonJackson();

        this.fromMap = defaultJson::fromMap;
        this.fromJson = defaultJson::fromJson;
        this.toJson = defaultJson::toJson;
    }

    @Builder(toBuilder = true)
    MercuryITJsonConfig(MercuryITConfigHolder configHolder, BiFunction<Map<String, Object>, Class<?>, ?> fromMap, BiFunction<String, Class<?>, ?> fromJson, Function<Object, String> toJson) {
        this(configHolder);

        this.fromMap = fromMap;
        this.fromJson = fromJson;
        this.toJson = toJson;
    }

    @Override
    protected MercuryITConfig copy(MercuryITConfigHolder configHolder) {
        return this.toBuilder().configHolder(configHolder).build();
    }

    public <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        return (T) fromMap.apply(map, clazz);
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        return (T) fromJson.apply(json, clazz);
    }

    public String toJson(Object object) {
        return toJson.apply(object);
    }
}

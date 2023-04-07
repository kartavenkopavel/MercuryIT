package community.redrover.mercuryit;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.function.BiFunction;
import java.util.function.Function;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class MercuryITJsonConfig extends MercuryITConfig<MercuryITJsonConfig.MercuryITJsonConfigBuilder> {

    private static final Gson gson = new Gson();

    @Builder.Default
    private BiFunction<String, Class<?>, ?> fromJson = gson::fromJson;

    @Builder.Default
    private Function<Object, String> toJson = gson::toJson;

    public <T> T fromJson(String json, Class<T> clazz) {
        return (T)fromJson.apply(json, clazz);
    }

    public String toJson(Object object) {
        return toJson.apply(object);
    }

    @Override
    public MercuryITConfig<MercuryITJsonConfigBuilder> copy() {
        return this.toBuilder().build();
    }
}

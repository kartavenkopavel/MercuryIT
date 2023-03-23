package community.redrover.mercuryit.config;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.function.BiFunction;
import java.util.function.Function;

@Getter
@Builder(toBuilder = true)
public class MercuryITConfig {
    @Builder.Default
    private BiFunction<String, Class<?>, ?> fromJson = DefaultConfig.fromJson();

    @Builder.Default
    private Function<Object, String> toJson = DefaultConfig.toJson();

    @Getter(value = AccessLevel.NONE)
    private final MercuryITConfigHelper helper = new MercuryITConfigHelper(this);

    public MercuryITConfigHelper helper() {
        return helper;
    }
}

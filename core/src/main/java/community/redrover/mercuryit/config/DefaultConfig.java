package community.redrover.mercuryit.config;

import com.google.gson.Gson;

import java.util.function.BiFunction;
import java.util.function.Function;

class DefaultConfig {

    private static final Gson gson = new Gson();

    public static BiFunction<String, Class<?>, ?> fromJson() {
        return gson::fromJson;
    }

    public static Function<Object, String> toJson() {
        return gson::toJson;
    }
}

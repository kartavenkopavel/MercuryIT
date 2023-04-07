package community.redrover.mercuryit;

import lombok.Builder;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;


public class MercuryITConfigHolder {

    private final Map<Class<? extends MercuryITConfig<?>>, MercuryITConfig<?>> configs;

    MercuryITConfigHolder() {
        this(new ConcurrentHashMap<>());
    }

    MercuryITConfigHolder(Map<Class<? extends MercuryITConfig<?>>, MercuryITConfig<?>> configs) {
        this.configs = configs;
    }

    public MercuryITConfigHolder(MercuryITConfig<?>... configs) {
        this(Arrays.stream(configs).collect(Collectors.toMap(obj -> (Class<? extends MercuryITConfig<?>>) obj.getClass(), Function.identity())));
    }

    public <Config extends MercuryITConfig<?>> Config config(Class<Config> clazz) {
        return (Config) configs.computeIfAbsent(clazz, MercuryITUtils::createConfig);
    }

    public <Config extends MercuryITConfig<?>> void set(Class<Config> clazz, Config config) {
        configs.put(clazz, config);
    }

    public MercuryITConfigHolder copy() {
        return new MercuryITConfigHolder(configs.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().copy())));
    }
}

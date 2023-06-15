package community.redrover.mercuryit;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@SuppressWarnings("unchecked")
public class MercuryITConfigHolder {

    private final Map<Class<? extends MercuryITConfig>, MercuryITConfig> configMap;

    MercuryITConfigHolder() {
        this.configMap = new ConcurrentHashMap<>();
    }

    <Config extends MercuryITConfig> void set(Class<Config> clazz, Config config) {
        configMap.put(clazz, config);
    }

    public <Config extends MercuryITConfig> Config config(Class<Config> clazz) {
        return (Config) configMap.computeIfAbsent(clazz, configClass -> MercuryITHelper.create(configClass, new Class[]{MercuryITConfigHolder.class}, new Object[]{this}));
    }

    MercuryITConfigHolder copy() {
        MercuryITConfigHolder mercuryITConfigHolder = new MercuryITConfigHolder();
        mercuryITConfigHolder.configMap.putAll(
                configMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().copy(mercuryITConfigHolder))));

        return mercuryITConfigHolder;
    }
}

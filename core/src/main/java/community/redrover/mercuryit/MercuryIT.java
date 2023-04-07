package community.redrover.mercuryit;

import java.util.function.Function;


public class MercuryIT {

    private static MercuryITConfigHolder configHolder = new MercuryITConfigHolder();

    public static void config(MercuryITConfigHolder configHolder) {
        MercuryIT.configHolder = configHolder;
    }

    public static <Config extends MercuryITConfig<?>> Config config(Class<Config> clazz) {
        return MercuryIT.configHolder.config(clazz);
    }

    public static <Config extends MercuryITConfig<?>> void config(Class<Config> clazz, Function<Config, Config> configFunction) {
        MercuryIT.configHolder.set(clazz, configFunction.apply(config(clazz)));
    }

    public static <Request extends MercuryITRequest<?>> Request request(Class<Request> clazz) {
        return MercuryITUtils.createRequest(clazz, configHolder.copy());
    }
}

package community.redrover.mercuryit;

import java.util.function.Function;


public class MercuryIT {

    private static final MercuryITConfigHolder configHolder = new MercuryITConfigHolder();

    public static <Config extends MercuryITConfig> Config config(Class<Config> clazz) {
        return MercuryIT.configHolder.config(clazz);
    }

    public static <Config extends MercuryITConfig> void config(Class<Config> clazz, Function<Config, Config> configFunction) {
        MercuryIT.configHolder.set(clazz, configFunction.apply(config(clazz)));
    }

    public static <Request extends MercuryITRequest<Request>> Request request(Class<Request> clazz) {
        return request(clazz, configHolder.copy());
    }

    static <Request extends MercuryITRequest<Request>> Request request(Class<Request> clazz, MercuryITConfigHolder configHolder) {
        return MercuryITHelper.create(clazz, new Class[]{MercuryITConfigHolder.class}, new Object[]{configHolder});
    }
}

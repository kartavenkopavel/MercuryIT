package community.redrover.mercuryit;

import java.util.function.Function;


@SuppressWarnings("unchecked")
public abstract class MercuryITObject<Self extends MercuryITObject<Self>> {

    /* field is protected because lombok bug */
    protected final MercuryITConfigHolder configHolder;

    protected MercuryITObject(MercuryITConfigHolder configHolder) {
        this.configHolder = configHolder;
    }

    protected MercuryITConfigHolder getConfigHolder() {
        return configHolder;
    }

    protected <Request extends MercuryITRequest<Request>> Request request(Class<Request> clazz) {
        return MercuryIT.request(clazz, getConfigHolder());
    }

    public <Config extends MercuryITConfig> Config config(Class<Config> clazz) {
        return getConfigHolder().config(clazz);
    }

    public <Config extends MercuryITConfig> Self config(Class<Config> clazz, Function<Config, Config> configFunction) {
        this.getConfigHolder().set(clazz, configFunction.apply(config(clazz)));
        return (Self) this;
    }
}

package community.redrover.mercuryit;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class MercuryITObject<Self extends MercuryITObject<?>> {

    private MercuryITConfigHolder configHolder;

    protected MercuryITObject(MercuryITConfigHolder configHolder) {
        this.configHolder = configHolder;
    }

    protected MercuryITConfigHolder configHolder() {
        return configHolder.copy();
    }

    protected <Config extends MercuryITConfig<?>> Config config(Class<Config> clazz) {
        return configHolder.config(clazz);
    }

    public Self config(MercuryITConfigHolder configHolder) {
        this.configHolder = configHolder;
        return (Self)this;
    }

    public <Config extends MercuryITConfig<?>> Self config(Class<Config> clazz, Function<Config, Config> configFunction) {
        this.configHolder.set(clazz, configFunction.apply(config(clazz)));
        return (Self)this;
    }

    public Self apply(Consumer<Self> actual) {
        actual.accept((Self)this);
        return (Self)this;
    }

    public <Value> AssertionValue<Self, Value> assertion(Function<Self, Value> actual) {
        return new AssertionValue<>((Self)this, actual.apply((Self)this));
    }

    public Assertion<Self> assertion() {
        return new Assertion<>((Self)this);
    }
}

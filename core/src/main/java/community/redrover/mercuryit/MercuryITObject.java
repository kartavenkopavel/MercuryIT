package community.redrover.mercuryit;

import java.util.function.Consumer;
import java.util.function.Function;


@SuppressWarnings("unchecked")
public abstract class MercuryITObject<Self extends MercuryITObject<?>> {

    private final MercuryITConfigHolder configHolder;

    protected MercuryITObject(MercuryITConfigHolder configHolder) {
        this.configHolder = configHolder;
    }

    protected MercuryITConfigHolder copyOfConfigHolder() {
        return configHolder.copy();
    }

    protected <Config extends MercuryITConfig<?>> Config config(Class<Config> clazz) {
        return configHolder.config(clazz);
    }

    public <Config extends MercuryITConfig<?>> Self config(Class<Config> clazz, Function<Config, Config> configFunction) {
        this.configHolder.set(clazz, configFunction.apply(config(clazz)));
        return (Self)this;
    }

    public <Result> Result apply(Function<Self, Result> apply) {
        return apply.apply((Self)this);
    }

    public Self accept(Consumer<Self> actual) {
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

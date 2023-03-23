package community.redrover.mercuryit;

import community.redrover.mercuryit.config.MercuryITConfig;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class MercuryITObject<Self extends MercuryITObject<?>> {

    private MercuryITConfig config;

    protected Self config(MercuryITConfig config) {
        this.config = config;
        return (Self)this;
    }

    protected MercuryITConfig config() {
        return config;
    }

    public Self config(Function<MercuryITConfig, MercuryITConfig> configFunction) {
        this.config = configFunction.apply(this.config);
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

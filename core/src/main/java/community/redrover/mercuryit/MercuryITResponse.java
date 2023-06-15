package community.redrover.mercuryit;


import java.util.function.Consumer;
import java.util.function.Function;


@SuppressWarnings("unchecked")
public abstract class MercuryITResponse<Self extends MercuryITResponse<Self>> extends MercuryITObject<Self> {

    protected MercuryITResponse(MercuryITConfigHolder configHolder) {
        super(configHolder);
    }

    @Override
    public <Request extends MercuryITRequest<Request>> Request request(Class<Request> clazz) {
        return super.request(clazz);
    }

    public <Result> Result apply(Function<Self, Result> apply) {
        return apply.apply((Self) this);
    }

    public Self accept(Consumer<Self> actual) {
        actual.accept((Self) this);
        return (Self) this;
    }

    public <Value> AssertionValue<Self, Value> assertion(Function<Self, Value> actual) {
        return new AssertionValue<>((Self) this, actual.apply((Self) this));
    }

    public Assertion<Self> assertion() {
        return new Assertion<>((Self) this);
    }
}

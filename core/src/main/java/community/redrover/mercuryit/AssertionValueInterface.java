package community.redrover.mercuryit;


import java.util.function.Consumer;
import java.util.function.Function;


public interface AssertionValueInterface<Self extends MercuryITResponse<Self>, Value> {

    Self getSelf();

    Value getValue();

    default <Result> Result apply(Function<Value, Result> apply) {
        return apply.apply(getValue());
    }

    default Self peek(Consumer<Value> actual) {
        actual.accept(getValue());
        return getSelf();
    }

    default <Result> AssertionValue<Self, Result> assertion(Function<Value, Result> actual) {
        return new AssertionValue<>(getSelf(), actual.apply(getValue()));
    }

    default Assertion<Self> assertion() {
        return new Assertion<>(getSelf());
    }
}

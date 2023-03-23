package community.redrover.mercuryit;

import org.junit.jupiter.api.Assertions;

public class AssertionValue<Self, Value> {

    private final Self self;
    private final Value value;

    AssertionValue(Self self, Value value) {
        this.self = self;
        this.value = value;
    }

    public Self equalsTo(Object expected, String message) {
        Assertions.assertEquals(expected, this.value, message);
        return this.self;
    }

    public Self equalsTo(Object expected) {
        return equalsTo(expected, null);
    }
}

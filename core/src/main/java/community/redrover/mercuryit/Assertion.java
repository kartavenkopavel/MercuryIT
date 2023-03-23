package community.redrover.mercuryit;

import org.junit.jupiter.api.Assertions;

public class Assertion<Self> {

    private final Self self;

    Assertion(Self self) {
        this.self = self;
    }

    public Self equalsTo(Object actual, Object expected, String message) {
        Assertions.assertEquals(expected, actual, message);
        return this.self;
    }

    public Self equalsTo(Object actual, Object expected) {
        return equalsTo(actual, expected, null);
    }
}

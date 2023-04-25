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

    public Self notNull(Object actual, String message) {
        Assertions.assertNotNull(actual, message);
        return self;
    }

    public Self notNull(Object actual) {
        Assertions.assertNotNull(actual);
        return self;
    }

    public Self isNull(Object actual, String message) {
        Assertions.assertNull(actual, message);
        return self;
    }

    public Self isNull(Object actual) {
        Assertions.assertNull(actual);
        return self;
    }

}

package community.redrover.mercuryit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AssertionTest {

    private final Assertion<Object> ASSERTION = new Assertion<>(new Object());

    @Test
    void testEqualsTo() {
        ASSERTION.equalsTo(123, 123);
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class, () -> ASSERTION.equalsTo(123, 456));
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class, () -> ASSERTION.equalsTo(123, null));


        final String message = "failed assertion message";
        ASSERTION.equalsTo(123, 123, message);
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class, () -> ASSERTION.equalsTo(123, 456, message));
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class, () -> ASSERTION.equalsTo(123, null, message));
    }

    @Test
    void testNotNull() {
        ASSERTION.notNull(123);
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class, () -> ASSERTION.notNull(null));

        final String message = "failed assertion message";
        ASSERTION.notNull(123, message);
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class, () -> ASSERTION.notNull(null), message);
    }

    @Test
    void testIsNull() {
        ASSERTION.isNull(null);
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class, () -> ASSERTION.isNull(123));

        final String message = "failed assertion message";
        ASSERTION.isNull(null, message);
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class, () -> ASSERTION.isNull(123), message);
    }
}
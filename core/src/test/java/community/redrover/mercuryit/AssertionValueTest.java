package community.redrover.mercuryit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AssertionValueTest {

    private static final String VALUE_NULL = null;
    private static final String VALUE_EMPTY = "";
    private static final String VALUE_123 = "123";
    private static final String ERROR_MESSAGE = "Assertion error message";

    @Test
    void testEqualsTo() {
        Object self = new Object();

        final AssertionValue<Object, String> assertionValue123 = new AssertionValue<>(self, VALUE_123);
        assertionValue123.equalsTo(VALUE_123);
        assertionValue123.equalsTo(VALUE_123, ERROR_MESSAGE);

        final AssertionValue<Object, String> assertionValueNull = new AssertionValue<>(self, VALUE_NULL);
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class,
                () -> assertionValueNull.equalsTo(VALUE_123));
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class,
                () -> assertionValueNull.equalsTo(VALUE_123, ERROR_MESSAGE));

        final AssertionValue<Object, String> assertionValueEmpty = new AssertionValue<>(self, VALUE_EMPTY);
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class,
                () -> assertionValueEmpty.equalsTo(VALUE_123));
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class,
                () -> assertionValueEmpty.equalsTo(VALUE_123, ERROR_MESSAGE));
    }

    @Test
    void testIsEmpty() {
        Object self = new Object();

        final AssertionValue<Object, String> assertionValueEmpty = new AssertionValue<>(self, VALUE_EMPTY);
        assertionValueEmpty.isEmpty(ERROR_MESSAGE);
        assertionValueEmpty.isEmpty();

        final AssertionValue<Object, String> assertionValueNull = new AssertionValue<>(self, VALUE_NULL);
        assertionValueNull.isEmpty(ERROR_MESSAGE);
        assertionValueNull.isEmpty();

        final AssertionValue<Object, String> assertionValue123 = new AssertionValue<>(self, VALUE_123);
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class, () -> assertionValue123.isEmpty(ERROR_MESSAGE));
        Assertions.assertThrows(org.opentest4j.AssertionFailedError.class, assertionValue123::isEmpty);
    }
}
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

    /**
     * For String check if value is an empty string "". For other types check if null
     * @param message error message to show if Assertion is failed
     * @return self
     */
    public Self isEmpty(String message) {
        if (this.value instanceof String) {
            Assertions.assertEquals("", this.value, message);
        } else {
            Assertions.assertNull(this.value, message);
        }
        return this.self;
    }

    /**
     * For String check if value is an empty string "". For other types check if null
     * @return self
     */
    public Self isEmpty() {
        return isEmpty(null);
    }
}

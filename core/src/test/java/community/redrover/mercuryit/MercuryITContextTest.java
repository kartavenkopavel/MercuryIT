package community.redrover.mercuryit;

import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class MercuryITContextTest {

    public static class MercuryITTestRequest extends MercuryITRequest<MercuryITTestRequest> {

        MercuryITTestRequest(MercuryITConfigHolder configHolder) {
            super(configHolder);
        }
    }

    @Getter
    public static class MercuryITTestConfig extends MercuryITConfig {

        private String value;

        protected MercuryITTestConfig(MercuryITConfigHolder configHolder) {
            super(configHolder);
        }

        @Builder(toBuilder = true)
        protected MercuryITTestConfig(MercuryITConfigHolder configHolder, String value) {
            this(configHolder);
            this.value = value;
        }

        @Override
        protected MercuryITConfig copy(MercuryITConfigHolder configHolder) {
            return this.toBuilder().configHolder(configHolder).build();
        }

        public MercuryITTestConfig value(String value) {
            this.value = value;
            return this;
        }
    }

    private static final String GLOBAL_VALUE = "global_value";

    @BeforeEach
    public void beforeTest() {
        System.out.println("before");
        MercuryIT.config(MercuryITTestConfig.class)
                .value(GLOBAL_VALUE);
    }

    @Test
    public void testContext() {
        final String local_value = "local_value";

        String local_result = MercuryIT.request(MercuryITContext.class)
                .config(MercuryITTestConfig.class)
                .value(local_value)
                .request(MercuryITTestRequest.class)
                .config(MercuryITTestConfig.class)
                .getValue();

        String global_result = MercuryIT.config(MercuryITTestConfig.class)
                .getValue();

        Assertions.assertEquals(local_value, local_result);
        System.out.println("Assert");
        Assertions.assertEquals(GLOBAL_VALUE, global_result);
    }
}

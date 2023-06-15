package community.redrover.mercuryit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;


public class MercuryITHttpConfigTest {

    public static final Map<String, String> EXPECTED_HEADER_MAP = Map.of(
            "Content-Type", "application/json",
            "token", "123456789"
    );

    @Test
    public void testConfigFromFile() {
        Assertions.assertEquals(
                EXPECTED_HEADER_MAP,
                MercuryIT.config(MercuryITHttpConfig.class).getHeader());
    }
}

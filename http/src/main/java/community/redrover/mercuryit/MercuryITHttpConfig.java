package community.redrover.mercuryit;

import lombok.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;


@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class MercuryITHttpConfig extends MercuryITConfig<MercuryITHttpConfig.MercuryITHttpConfigBuilder> {

    public static final String CONFIG_NAME = "http";

    public static final String HEADER_NAME = "header";

    @Singular("header")
    private Map<String, String> header;

    MercuryITHttpConfig() {
        this.header = new TreeMap<>(
                configuration().getList(name(APP_NAME, CONFIG_NAME, HEADER_NAME)).stream()
                        .map(Object::toString)
                        .map(line -> line.split("="))
                        .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1])));
    }

    public MercuryITHttpConfig header(String name, String value) {
        header.put(name, value);
        return this;
    }

    public MercuryITHttpConfig header(Function<Map<String, String>, Map<String, String>> headerFunction) {
        header = headerFunction.apply(header);
        return this;
    }

    public Map<String, String> header() {
        return header;
    }

    @Override
    public MercuryITConfig<MercuryITHttpConfigBuilder> copy() {
        return this.toBuilder().build();
    }
}

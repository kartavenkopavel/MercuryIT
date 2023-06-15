package community.redrover.mercuryit;

import lombok.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Getter
public class MercuryITHttpConfig extends MercuryITConfig {

    static final String CONFIG_NAME = "http";

    private Map<String, String> header;

    MercuryITHttpConfig(MercuryITConfigHolder configHolder) {
        super(configHolder);
        this.header = new TreeMap<>(
                configuration().getList(name(APP_NAME, CONFIG_NAME, "header")).stream()
                        .map(Object::toString)
                        .map(line -> line.split("="))
                        .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1])));
    }

    @Builder(toBuilder = true)
    MercuryITHttpConfig(MercuryITConfigHolder configHolder, @Singular("header") Map<String, String> header) {
        this(configHolder);
        this.header = header;
    }

    @Override
    protected MercuryITConfig copy(MercuryITConfigHolder configHolder) {
        return this.toBuilder().configHolder(configHolder).build();
    }

    public MercuryITHttpConfig header(String name, String value) {
        header.put(name, value);
        return this;
    }
}

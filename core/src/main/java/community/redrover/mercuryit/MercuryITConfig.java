package community.redrover.mercuryit;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.TreeMap;


public abstract class MercuryITConfig extends MercuryITObject<MercuryITConfig> {

    private static Configuration configuration;

    protected static Configuration configuration() {
        if (configuration == null) {
            Configurations configurations = new Configurations();
            try {
                configuration = configurations.fileBased(YAMLConfiguration.class, APP_NAME + ".yaml");
            } catch (ConfigurationException yamlException) {
                try {
                    configuration = configurations.fileBased(PropertiesConfiguration.class, APP_NAME + ".properties");
                } catch (ConfigurationException propertiesException) {
                    configuration = new MapConfiguration(new TreeMap<>());
                }
            }
        }

        return configuration;
    }

    public static final String APP_NAME = "mercuryit";

    public static String name(String... names) {
        StringBuilder fullName = new StringBuilder();
        for (String name : names) {
            fullName.append(name).append('.');
        }

        if (fullName.length() > 0) {
            fullName.deleteCharAt(fullName.length() - 1);
        }

        return fullName.toString();
    }

    protected MercuryITConfig(MercuryITConfigHolder configHolder) {
        super(configHolder);
    }

    protected abstract MercuryITConfig copy(MercuryITConfigHolder configHolder);

    @Override
    public <Request extends MercuryITRequest<Request>> Request request(Class<Request> clazz) {
        return super.request(clazz);
    }
}

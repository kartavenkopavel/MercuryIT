package community.redrover.mercuryit;

import community.redrover.mercuryit.config.MercuryITConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class MercuryIT {

    private static MercuryITConfig config = MercuryITConfig.builder().build();

    public static void config(Function<MercuryITConfig, MercuryITConfig> configFunction) {
        config = configFunction.apply(config);
    }

    public static <Request extends MercuryITRequest<?>> Request request(Class<Request> clazz) {
        Constructor<Request> defaultConstructor;
        try {
           defaultConstructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new DefaultConstructorNotFoundException(e);
        }

        try {
            return (Request) defaultConstructor.newInstance()
                    .config(config);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CannotCreateMercuryITRequestException(e);
        }
    }
}

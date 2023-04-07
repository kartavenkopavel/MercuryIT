package community.redrover.mercuryit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MercuryITUtils {

    public static class CannotCreateRequestException extends RuntimeException {

        public CannotCreateRequestException(Throwable cause, String className) {
            super(String.format("Cannot create instance of \"%s\".", className), cause);
        }
    }

    public static class DefaultConstructorNotFoundException extends RuntimeException {

        public DefaultConstructorNotFoundException(Throwable cause) {
            super("Default constructor is not found.", cause);
        }
    }

    private static <Result> Result create(Class<Result> clazz, Class<?>[] classes, Object[] objects) {
        Constructor<Result> defaultConstructor;
        try {
            defaultConstructor = clazz.getDeclaredConstructor(classes);
        } catch (NoSuchMethodException e) {
            throw new DefaultConstructorNotFoundException(e);
        }

        try {
            return defaultConstructor.newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CannotCreateRequestException(e, clazz.getSimpleName());
        }
    }


    static <Request extends MercuryITRequest<?>> Request createRequest(Class<Request> clazz, MercuryITConfigHolder configHolder) {
        return create(clazz, new Class[]{MercuryITConfigHolder.class}, new Object[]{configHolder});
    }

    static <Config extends MercuryITConfig<?>> Config createConfig(Class<Config> clazz) {
        return create(clazz, new Class[]{}, new Object[]{});
    }
}

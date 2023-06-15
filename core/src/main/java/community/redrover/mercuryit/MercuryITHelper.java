package community.redrover.mercuryit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class MercuryITHelper {

    public static class CannotCreateInstanceException extends RuntimeException {

        public CannotCreateInstanceException(Throwable cause, String className) {
            super(String.format("Cannot create instance of \"%s\".", className), cause);
        }
    }

    public static class DefaultConstructorNotFoundException extends RuntimeException {

        public DefaultConstructorNotFoundException(Throwable cause) {
            super("Default constructor is not found.", cause);
        }
    }

    static <Result> Result create(Class<Result> clazz, Class<?>[] classes, Object[] objects) {
        Constructor<Result> defaultConstructor;
        try {
            defaultConstructor = clazz.getDeclaredConstructor(classes);
        } catch (NoSuchMethodException e) {
            throw new DefaultConstructorNotFoundException(e);
        }

        try {
            return defaultConstructor.newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CannotCreateInstanceException(e, clazz.getSimpleName());
        }
    }
}

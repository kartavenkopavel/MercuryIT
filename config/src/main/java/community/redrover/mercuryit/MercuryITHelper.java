package community.redrover.mercuryit;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;


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

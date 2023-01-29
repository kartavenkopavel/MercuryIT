package community.redrover.mercury;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Mercury {

    public static <REQUEST extends MercuryRequest> REQUEST request(Class<REQUEST> clazz) {
        Constructor<REQUEST> defaultConstructor = null;
        try {
           defaultConstructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new DefaultConstructorNotFoundException(e);
        }

        try {
            return defaultConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CannotCreateMercuryRequestException(e);
        }
    }
}

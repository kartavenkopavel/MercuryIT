package community.redrover.mercuryit;


import lombok.SneakyThrows;

import java.lang.ref.Cleaner;


public abstract class MercuryITResponseAutoCloseable<Self extends MercuryITResponseAutoCloseable<Self>> extends MercuryITResponse<Self> {

    private static class AutoCloseableObjectHolder {

        private final AutoCloseable object;

        public AutoCloseableObjectHolder(AutoCloseable object) {
            this.object = object;
        }

        @SneakyThrows
        protected void close() {
            object.close();
        }
    }

    protected void registerAutoCloseable(AutoCloseable object) {
        Cleaner cleaner = Cleaner.create();
        cleaner.register(this, new AutoCloseableObjectHolder(object)::close);
    }

    protected MercuryITResponseAutoCloseable(MercuryITConfigHolder configHolder) {
        super(configHolder);
    }
}

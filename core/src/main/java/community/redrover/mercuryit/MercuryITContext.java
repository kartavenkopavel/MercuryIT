package community.redrover.mercuryit;


public final class MercuryITContext extends MercuryITRequest<MercuryITContext> {

    protected MercuryITContext(MercuryITConfigHolder configHolder) {
        super(configHolder);
    }

    @Override
    public <Request extends MercuryITRequest<Request>> Request request(Class<Request> clazz) {
        return super.request(clazz);
    }
}

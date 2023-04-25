package community.redrover.mercuryit;


public abstract class MercuryITRequest<Self extends MercuryITRequest<?>> extends MercuryITObject<Self> {

    public MercuryITRequest(MercuryITConfigHolder configHolder) {
        super(configHolder);
    }

    public <Request extends MercuryITRequest<?>> Request mercuryIT(Class<Request> clazz) {
        return MercuryIT.request(clazz, copyOfConfigHolder());
    }
}

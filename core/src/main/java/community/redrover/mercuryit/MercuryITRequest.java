package community.redrover.mercuryit;


public abstract class MercuryITRequest<Self extends MercuryITRequest<?>> extends MercuryITObject<Self> {

    public <Request extends MercuryITRequest<?>> Request mercuryRequest(Class<Request> clazz) {
        return MercuryIT.request(clazz);
    }
}

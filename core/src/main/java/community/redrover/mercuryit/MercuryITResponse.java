package community.redrover.mercuryit;


@SuppressWarnings("unchecked")
public abstract class MercuryITResponse<Self extends MercuryITResponse<Self>> extends MercuryITObject<Self> implements AssertionValueInterface<Self, Self> {

    protected MercuryITResponse(MercuryITConfigHolder configHolder) {
        super(configHolder);
    }

    @Override
    public <Request extends MercuryITRequest<Request>> Request request(Class<Request> clazz) {
        return super.request(clazz);
    }

    @Override
    public Self getSelf() {
        return (Self) this;
    }

    @Override
    public Self getValue() {
        return (Self) this;
    }
}

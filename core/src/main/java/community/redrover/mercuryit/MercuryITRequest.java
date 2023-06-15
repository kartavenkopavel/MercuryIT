package community.redrover.mercuryit;


public abstract class MercuryITRequest<Self extends MercuryITRequest<Self>> extends MercuryITObject<Self> {

    protected MercuryITRequest(MercuryITConfigHolder configHolder) {
        super(configHolder);
    }
}

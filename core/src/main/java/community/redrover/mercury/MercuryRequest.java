package community.redrover.mercury;

public abstract class MercuryRequest {

    public <REQUEST extends MercuryRequest> REQUEST mercuryRequest(Class<REQUEST> clazz) {
        return Mercury.request(clazz);
    }
}

package community.redrover.mercuryit;

public class CannotCreateMercuryITRequestException extends RuntimeException {

    public CannotCreateMercuryITRequestException(Throwable cause) {
        super("Cannot create MercuryIT request.", cause);
    }
}

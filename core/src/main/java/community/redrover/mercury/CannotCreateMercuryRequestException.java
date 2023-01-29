package community.redrover.mercury;

public class CannotCreateMercuryRequestException extends RuntimeException {

    public CannotCreateMercuryRequestException(Throwable cause) {
        super("Cannot create Mercury request.", cause);
    }
}

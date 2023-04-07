package community.redrover.mercuryit;

public abstract class MercuryITConfig<Builder> {

    public abstract Builder toBuilder();

    public abstract MercuryITConfig<Builder> copy();
}

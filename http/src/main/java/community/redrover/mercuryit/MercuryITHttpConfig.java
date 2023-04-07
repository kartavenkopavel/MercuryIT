package community.redrover.mercuryit;

import lombok.*;


@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class MercuryITHttpConfig extends MercuryITConfig<MercuryITHttpConfig.MercuryITHttpConfigBuilder> {

    @Override
    public MercuryITConfig<MercuryITHttpConfigBuilder> copy() {
        return this.toBuilder().build();
    }
}

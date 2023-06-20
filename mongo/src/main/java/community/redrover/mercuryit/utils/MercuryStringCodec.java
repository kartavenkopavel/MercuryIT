package community.redrover.mercuryit.utils;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.StringCodec;

public class MercuryStringCodec extends StringCodec {

    private Codec<String> stringCodec;

    @Override
    public String decode(BsonReader reader, DecoderContext decoderContext) {
        if (getRepresentation() == BsonType.STRING
                && reader.getCurrentBsonType() == BsonType.OBJECT_ID)
        {
            if (stringCodec == null) {
                stringCodec = withRepresentation(BsonType.OBJECT_ID);
            }

            return stringCodec.decode(reader, decoderContext);
        } else {
            return super.decode(reader, decoderContext);
        }
    }
}

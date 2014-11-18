package server.server.datatypes.locations;

import server.server.packets.StarboundBufferReader;
import io.netty.buffer.ByteBuf;

import java.util.UUID;


public class AnyLocationType {

    /**
     * UniqueWorldId
     * CelestialWorldId
     * ClientShipWorldId
     *
     *
     *
     */
    private Object value;

    public AnyLocationType() {}

    public AnyLocationType(Object value) throws Exception {
        if (!(value == null ||
                value instanceof String ||
                value instanceof CelestialCoordinates ||
                value instanceof UUID
                )) {
            throw new Exception("Variants are unable to represent " + value.getClass().getName() + ".");
        }
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public AnyLocationType readFromByteBuffer(ByteBuf in) throws Exception {
        AnyLocationType anyLocationType = new AnyLocationType();
        byte type = StarboundBufferReader.readUnsignedByte(in);
        switch (type) {
            case 1:
                anyLocationType.value = StarboundBufferReader.readStringVLQ(in);
                break;
            case 2:
                //TODO
//                anyType.value = new CelestialCoordinates();
                break;
            case 3:
                anyLocationType.value = StarboundBufferReader.readUUID(in);
                break;
            default:
                System.err.println("Unknown Type: "+type);
                throw new Exception("Unknown type");
        }
        return anyLocationType;
    }

    public void writeToByteBuffer(ByteBuf out) {
        if (value instanceof String) {
            out.writeByte(1);
            writeStringVLQ(out, (String) value);
        } else if (value instanceof CelestialCoordinates) {
            out.writeByte(2);
            //TODO
        } else if (value instanceof UUID) {
            out.writeByte(3);
            writeUUID(out, (UUID) value);
        }
    }

}

package starnubserver.events.packet;

import starbounddata.packets.Packet;
import starbounddata.packets.Packets;
import utilities.events.EventRouter;

/**
 * Represents StarNubs PacketEventRouter to be used with {@link starnubserver.events.packet.PacketEventSubscription} and
 * {@link starnubserver.events.packet.PacketEventHandler}
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class PacketEventRouter extends EventRouter<Class<? extends Packet>, Packet, Packet> {

    /**
     * This is instantiated to build out the Packet Event Router
     */
    private static final PacketEventRouter INSTANCE = new PacketEventRouter();

    /**
     * This is instantiated to build out the Packets enum at start up
     */
    private static final Packets PACKETS_INSTANCE = Packets.PROTOCOLVERSION;

    private PacketEventRouter() {
        super();
    }

    public static PacketEventRouter getInstance() {
        return INSTANCE;
    }

    /**
     * This was implemented inline with the Packet Decoder.
     *
     * @param packet Packet representing a packet
     */
    @Override
    public void eventNotify(Packet packet) {
    }

    /**
     * This was implemented inline with the Packet Decoder.
     *
     * @param packet Packet representing a packet
     */
    @Override
    public void handleEvent(Packet packet){

    }
}

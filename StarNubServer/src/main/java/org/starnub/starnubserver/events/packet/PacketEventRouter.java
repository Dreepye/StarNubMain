package org.starnub.starnubserver.events.packet;

import org.starnub.starbounddata.packets.Packet;
import org.starnub.starbounddata.packets.Packets;
import org.starnub.utilities.events.EventRouter;

/**
 * Represents StarNubs PacketEventRouter to be used with {@link PacketEventSubscription} and
 * {@link PacketEventHandler}
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
     * This is instantiated to build out the Packets enum at startUDPServer up
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
     * This is not used
     *
     * @param event T2 events data of some type
     */
    @Override
    public void eventNotifyNullCheck(Packet event) {
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

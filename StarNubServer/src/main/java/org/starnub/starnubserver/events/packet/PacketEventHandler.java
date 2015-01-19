package org.starnub.starnubserver.events.packet;

import org.starnub.starbounddata.packets.Packet;
import org.starnub.utilities.events.EventHandler;

/**
 * Represents StarNubs PacketEventHandler that handles {@link org.starnub.starbounddata.packets.Packet} the
 * onEvent method is overridden so that you may conduct logic with the Packet
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public interface PacketEventHandler extends EventHandler<Packet> {

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: When your {@link PacketEventSubscription} is called this EventHandler
     * will be called and the onEvent() method invoked.
     *
     * NOTE: - YOU MUST CAST THE PACKET TO THE SPECIFIC PACKET TYPE YOU WANT. YOU CAN RETURN THE PACKET ANYWAY
     *              {
     *                  ChatSentPacket chatSentPacket = (ChatSentPacket) eventData;
     *                  //Do Stuff, chatSentPacket.getMessage();
     *                  //Do Stuff, chatSentPacket.setMessage();
     *              }
     *       - YOUR CAN RECYCLE THE PACKET IN ORDER TO STOP ITS ROUTING BY USING packet.recycle();
     *
     * @param eventData Packet representing the packet being routed
     */
    @Override
    public abstract void onEvent(Packet eventData);

}

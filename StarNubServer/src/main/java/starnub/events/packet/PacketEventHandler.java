package starnub.events.packet;

import starbounddata.packets.Packet;
import utilities.events.EventHandler;

/**
 * Represents StarNubs PacketEventHandler that handles {@link starbounddata.packets.Packet} the
 * onEvent method is overridden so that you may conduct logic with the Packet
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public abstract class PacketEventHandler extends EventHandler<Packet> {

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: When your {@link starnub.events.packet.PacketEventSubscription} is called this EventHandler
     * will be called and the onEvent() method invoked.
     *
     * NOTE: - YOU MUST CAST THE PACKET TO THE SPECIFIC PACKET TYPE YOU WANT. YOU CAN RETURN THE PACKET ANYWAY
     *              {
     *                  ChatSentPacket chatSentPacket = (ChatSentPacket) eventData;
     *                  //Do Stuff, chatSentPacket.getMessage();
     *                  //Do Stuff, chatSentPacket.setMessage();
     *                  return chatSentPacket;
     *              }
     *       - YOU MUST RETURN THE PACKET OR YOU WILL CAUSE THE STREAM TO FAIL.
     *       - YOUR CAN RECYCLE THE PACKET IN ORDER TO STOP ITS ROUTING BY USING packet.recycle();
     *
     * @param eventData Packet representing the packet being routed
     * @return Packet any class representing packet can be returned
     */
    @Override
    public abstract Packet onEvent(Packet eventData);

}

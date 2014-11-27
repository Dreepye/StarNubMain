package utilities.connectivity.connection;

import io.netty.channel.ChannelHandlerContext;
import utilities.events.EventRouter;
import utilities.events.types.ObjectEvent;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Represents Netty Abstract Connection Class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class Connection {

    private final ConnectionStatus DISCONNECTED;
    private final ConnectionStatus PENDING;
    private final ConnectionStatus CONNECTED;

    private volatile ConnectionStatus connectionStatus;
    private final ChannelHandlerContext CLIENT_CTX;
    private final long CONNECTION_START_TIME;

    protected final EventRouter EVENT_ROUTER;
    protected final boolean EVENT_MESSAGE;

    public Connection(EventRouter EVENT_ROUTER, ChannelHandlerContext CLIENT_CTX) {
        DISCONNECTED = new Disconnected(this);
        PENDING = new Pending(this);
        CONNECTED = new Connected(this);
        this.CLIENT_CTX = CLIENT_CTX;
        this.EVENT_ROUTER = EVENT_ROUTER;
        this.EVENT_MESSAGE = EVENT_ROUTER != null;
        this.CONNECTION_START_TIME = System.currentTimeMillis();
        this.connectionStatus = PENDING;
    }

    protected ConnectionStatus getPENDING() {
        return PENDING;
    }

    protected ConnectionStatus getCONNECTED() {
        return CONNECTED;
    }

    protected ConnectionStatus getDISCONNECTED() {
        return DISCONNECTED;
    }

    protected ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public ChannelHandlerContext getCLIENT_CTX() {
        return CLIENT_CTX;
    }

    public long getCONNECTION_START_TIME() {
        return CONNECTION_START_TIME;
    }

    public boolean isConnected() {
        return connectionStatus.isConnected();
    }

    public boolean disconnect() {
        return connectionStatus.disconnect();
    }

    public InetAddress getClientIP() {
        return ((InetSocketAddress) CLIENT_CTX.channel().remoteAddress()).getAddress();
    }

    public int getClientSocket() {
        return ((InetSocketAddress) CLIENT_CTX.channel().remoteAddress()).getPort();
    }

    public String getClientHostName() {
        return ((InetSocketAddress) CLIENT_CTX.channel().remoteAddress()).getHostName();
    }

    public String getClientHostString(){
        return ((InetSocketAddress) CLIENT_CTX.channel().remoteAddress()).getHostString();
    }

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This will either print to console or send events
     *
     * @param eventKey String representing the event key or console leading name
     * @param eventData Object representing the data
     */
    @SuppressWarnings("unchecked")
    protected void printOrEvent(String eventKey, Object eventData){
        if (EVENT_MESSAGE) {
            EVENT_ROUTER.eventNotify(new ObjectEvent(eventKey, eventData));
        } else {
            System.out.println(eventKey + ": " +eventData);
        }
    }
}



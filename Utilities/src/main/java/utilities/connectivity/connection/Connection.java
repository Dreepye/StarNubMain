package utilities.connectivity.connection;

import io.netty.channel.ChannelHandlerContext;

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

    public Connection(ChannelHandlerContext CLIENT_CTX) {
        DISCONNECTED = new Disconnected(this);
        PENDING = new Pending(this);
        CONNECTED = new Connected(this);
        connectionStatus = PENDING;
        this.CLIENT_CTX = CLIENT_CTX;
        this.CONNECTION_START_TIME = System.currentTimeMillis();
    }

    public ConnectionStatus getPENDING() {
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

    public boolean connect() {
        return connectionStatus.connect();
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

}



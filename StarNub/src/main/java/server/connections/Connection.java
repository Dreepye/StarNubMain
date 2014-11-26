package server.connections;

import io.netty.channel.ChannelHandlerContext;
import server.connections.states.connectionstatus.*;

/**
 * Represents StarNubs Abstract Connection Class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public abstract class Connection {

    private Status initializingConnection;
    private Status pendingConnection;
    private Status connected;
    private Status disconnected;

    private volatile Status status;
    private final ChannelHandlerContext CLIENT_CTX;
    private final long CONNECTION_START_TIME;

    public Connection(ChannelHandlerContext CLIENT_CTX) {
        disconnected = new Disconnected(this);
        initializingConnection = new InitializingConnection(this);
        pendingConnection = new PendingConnection(this);
        connected = new Connected(this);
        status = initializingConnection;
        this.CLIENT_CTX = CLIENT_CTX;
        this.CONNECTION_START_TIME = System.currentTimeMillis();
    }

    public Status getInitializingConnection() {
        return initializingConnection;
    }

    public Status getPendingConnection() {
        return pendingConnection;
    }

    public Status getConnected() {
        return connected;
    }

    public Status getDisconnected() {
        return disconnected;
    }

    public Status getStatus() {
        return status;
    }

    public ChannelHandlerContext getCLIENT_CTX() {
        return CLIENT_CTX;
    }

    public long getCONNECTION_START_TIME() {
        return CONNECTION_START_TIME;
    }

    public void initializingConnect(){
        status.initializeConnection();
    }

    public void pendingConnection(){
        status.pendingConnection();
    }

    public void connected(){
        status.connect();
    }

    public void disconnected(){
        status.disconnect();
    }
}



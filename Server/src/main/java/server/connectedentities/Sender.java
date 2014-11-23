package server.connectedentities;

import io.netty.channel.ChannelHandlerContext;

public abstract class Sender {


    private volatile ChannelHandlerContext SENDER_CTX;
    private volatile long CONNECTION_TIME;

    public Sender(){}

    public Sender(ChannelHandlerContext SENDER_CTX) {
        this.SENDER_CTX = SENDER_CTX;
        this.CONNECTION_TIME = System.currentTimeMillis();
    }

    public ChannelHandlerContext getSENDER_CTX() {
        return SENDER_CTX;
    }

    public long getCONNECTION_TIME() {
        return CONNECTION_TIME;
    }
}

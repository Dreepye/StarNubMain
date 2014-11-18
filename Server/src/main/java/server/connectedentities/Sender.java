package server.connectedentities;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

public abstract class Sender {

    @Getter
    private volatile ChannelHandlerContext SENDER_CTX;

    @Getter
    private volatile long CONNECTION_TIME;

    public Sender(){}

    public Sender(ChannelHandlerContext SENDER_CTX) {
        this.SENDER_CTX = SENDER_CTX;
        this.CONNECTION_TIME = System.currentTimeMillis();
    }
}

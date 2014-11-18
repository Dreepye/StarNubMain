package server.cache.objects;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class CtxCache extends AbstractCache {

    @Getter @Setter
    private volatile ChannelHandlerContext ctx;

}

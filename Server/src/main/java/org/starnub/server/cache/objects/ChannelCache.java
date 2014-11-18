
package org.starnub.server.cache.objects;


import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ChannelCache extends AbstractCache {

    @Getter @Setter
    private volatile Channel channel;

}
package org.starnub.server.cache.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class BooleanCache extends AbstractCache {

    @Getter @Setter
    private volatile boolean bool;

}

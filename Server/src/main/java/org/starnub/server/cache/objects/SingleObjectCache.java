package org.starnub.server.cache.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class SingleObjectCache<T1> extends AbstractCache{

    @Getter @Setter
    private volatile T1 cacheObject1;

}

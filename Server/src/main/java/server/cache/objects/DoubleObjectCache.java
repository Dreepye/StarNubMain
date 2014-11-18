package server.cache.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class DoubleObjectCache<T1, T2> extends AbstractCache{

    @Getter @Setter
    private volatile T1 cacheObject1;
    @Getter @Setter
    private volatile T2 cacheObject2;

}

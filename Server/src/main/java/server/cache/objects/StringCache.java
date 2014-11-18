package server.cache.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class StringCache extends AbstractCache {

    @Getter @Setter
    private volatile String string;
}

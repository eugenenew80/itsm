package itdesign.entity.util;

import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class PreventUpdateAndRemove {
    @PreUpdate
    void onPreUpdate(Object o) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @PreRemove
    void onPreRemove(Object o) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
}

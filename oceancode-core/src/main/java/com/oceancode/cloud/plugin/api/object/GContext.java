package com.oceancode.cloud.plugin.api.object;

import java.util.List;

public interface GContext {
    default <T extends GObject> List<T> findAll(Class<T> gClass) {
        return findAll(gClass.getName())
                .stream().filter(gClass::isInstance)
                .map(gClass::cast)
                .toList();
    }



    List<GObject> findAll(String gClassName);

    default <T extends GObject> T findBySourceId(Class<T> gClass, Long id) {
        GObject target = findBySourceId(gClass.getName(), id);
        if (gClass.isInstance(target)) {
            return gClass.cast(target);
        }
        return null;
    }

    default <T extends GObject> T findByOId(Class<T> gClass, String id) {
        GObject target = findByOidId(gClass.getName(), id);
        if (gClass.isInstance(target)) {
            return gClass.cast(target);
        }
        return null;
    }

    GObject findBySourceId(String gClassName, Long id);

    default GObject findByOidId(String gClassName, String id) {
        return null;
    }
}

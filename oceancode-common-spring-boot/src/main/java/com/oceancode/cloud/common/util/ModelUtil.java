package com.oceancode.cloud.common.util;


import com.oceancode.cloud.api.NotifierType;
import com.oceancode.cloud.api.cache.CacheKey;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class ModelUtil {
    private static List<com.oceancode.cloud.api.Notifier> getNotifiers(Class<?> modelTypeClass) {
        return ComponentUtil.getNotifiers(modelTypeClass.getName());
    }

    public static class Notifier {
        public static <T> void deleteById(Class<T> modelClass, Long value) {
            notifier(modelClass, NotifierType.DELETE_BY_ID, null, value);
        }

        public static <T> void delete(Class<T> modelClass, T value) {
            notifier(modelClass, NotifierType.DELETE, null, value);
        }

        public static <T> void update(Class<T> modelClass, T value) {
            notifier(modelClass, NotifierType.UPDATE, null, value);
        }

        public static <T> void update(Class<T> modelClass, T newVal, T oldVal) {
            notifier(modelClass, NotifierType.UPDATE, newVal, oldVal);
        }

        public static <T> void updateById(Class<T> modelClass, Long value) {
            notifier(modelClass, NotifierType.UPDATE_BY_ID, null, value);
        }

        public static <T> void add(Class<T> modelClass, T value) {
            notifier(modelClass, NotifierType.ADD, value, null);
        }


        public static <T> void notifier(Class<T> modelCalss, NotifierType type, Object newVal, Object oldVal) {
            getNotifiers(modelCalss).forEach(e -> e.notifier(type, oldVal, newVal));
        }
    }

    public static class Data{
        public static <T>T query(CacheKey key, Class<T> modelClass, Supplier<T> supplier){
            T entity = CacheUtil.getCache().getEntity(key,modelClass);
            if(Objects.nonNull(entity)){
                return entity;
            }
            entity = supplier.get();
            CacheUtil.getCache().setEntity(key,entity);
            return entity;
        }

        public static <T> void add(CacheKey key,Supplier<Boolean> supplier){

        }
    }
}

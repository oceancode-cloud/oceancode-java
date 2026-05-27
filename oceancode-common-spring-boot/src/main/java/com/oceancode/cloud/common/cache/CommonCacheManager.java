package com.oceancode.cloud.common.cache;

import com.oceancode.cloud.api.cache.DataCacheManager;
import com.oceancode.cloud.api.cache.DataLoader;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public abstract class CommonCacheManager implements DataCacheManager {
    private static Map<String, DataCacheManager> cacheManagerMap;
    private static CommonConfig commonConfig;

    protected String getEmptyValue() {
        return "nil";
    }


    protected int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    protected int randomInt() {
        int num = ThreadLocalRandom.current().nextInt(0, 10);
        if (num % 2 == 0) {
            return randomInt(1, 1000);
        }
        return -randomInt(1, 1000);
    }

    public long getExpire(String cacheId) {
        int offset = randomInt();
        long expire = 60 * 60 * 1000L;
        if (ValueUtil.isNotEmpty(cacheId)) {
            return expire + offset;
        }
        return ComponentUtil.getBean(CommonConfig.class).getValueAsLong("oc.cache." + cacheId + ".expire", expire) + offset;
    }

    public static DataCacheManager getCacheManager() {
        return cacheManagerMap.get("master");
    }

    public static DataCacheManager getCacheManager(String source) {
        return cacheManagerMap.get(source);
    }

    public static synchronized Map<String, DataCacheManager> getAllCacheManager() {
        cacheManagerMap = new HashMap<>();
        commonConfig = ComponentUtil.getBean(CommonConfig.class);
        Map<String, DataCacheManager> map = ComponentUtil.getBeans(DataCacheManager.class);
        DataCacheManager cacheManager = null;
        for (DataCacheManager value : map.values()) {
            cacheManagerMap.put(value.getSource(), value);
            if ("master".equals(value.getSource())) {
                cacheManager = value;
            }
        }
        if (Objects.isNull(cacheManager)) {
            if (!map.isEmpty()) {
                DataCacheManager first = map.values().stream().toList().getFirst();
                cacheManagerMap.put("master", first);
            }
        }
        return cacheManagerMap;
    }

    public static String buildKey(String cacheId, String express) {
        String prefix = commonConfig.getValue("oc.cache.prefix", "data");
        return cacheId + "::" + prefix + ":" + express;
    }

    @Override
    public <T> T load(String key, Class<T> dataType, DataLoader<T> loader) {
        Map<String, Object> map = getObject(key);
        if (Objects.isNull(map)) {
            T t = loader.get();
            map = JsonUtil.beanToMap(t);
            setObject(key, map);
            return t;
        }
        if (map.isEmpty()) {
            return null;
        }
        return JsonUtil.mapToBean(map, dataType);
    }

    @Override
    public <T> List<T> loadList(String key, Class<T> dataType, DataLoader<List<T>> loader) {
        List<T> list = getObjectList(key, dataType);
        if (Objects.isNull(list)) {
            list = loader.get();
            setObjectList(key, list);
        }
        return list;
    }

    public String[] keyParts(String key) {
        if (!key.contains("::")) {
            return null;
        }
        return key.split("::");
    }

    public String getCacheId(String[] parts) {
        if (Objects.isNull(parts)) {
            return null;
        }
        if (parts.length < 2) {
            return null;
        }
        return parts[0];
    }

    public String getExpress(String[] parts, String defaultExpress) {
        if (Objects.isNull(parts)) {
            return defaultExpress;
        }
        if (parts.length < 2) {
            return defaultExpress;
        }
        return parts[1];
    }
}

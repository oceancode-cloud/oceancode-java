package com.oceancode.cloud.x.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Context {
    private Map<String, String> packageMapping = new HashMap<>();
    private Map<String, String> classNameMapping = new HashMap<>();

    private String randomVar(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        char randomChar = (char) (ThreadLocalRandom.current().nextInt(95) + 32);
        sb.append(randomChar);
        while (true) {
            if (map.containsKey(sb.toString())) {
                String str = (char) (ThreadLocalRandom.current().nextInt(95) + 32) + "" + ThreadLocalRandom.current().nextInt();
                sb.append(str);
            } else {
                return sb.toString();
            }
        }
    }

    public String replacePackageName(String rawPackageName) {
        return packageMapping.computeIfAbsent(rawPackageName, key -> randomVar(packageMapping));
    }

    public String replaceClassName(String packageName, String className) {
        String name = classNameMapping.computeIfAbsent(packageName + "." + className, key -> randomVar(classNameMapping));
        if (name.length() == 1) {
            return name.toUpperCase();
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}

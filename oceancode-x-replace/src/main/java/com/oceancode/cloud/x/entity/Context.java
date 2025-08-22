package com.oceancode.cloud.x.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Context {
    private Map<String, String> packageMapping = new HashMap<>();
    private Map<String, String> methodMapping = new HashMap<>();
    private Map<String, String> datasourceIdMapping = new HashMap<>();
    private Map<String, String> classNameMapping = new HashMap<>();
    private Map<String, Map<String, String>> variableMapping = new HashMap<>();
    private List<Runnable> funCallbacks = new ArrayList<>();
    private List<Runnable> writeCodeCallbacks = new ArrayList<>();

    private String randomVar(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        char randomChar = (char) ThreadLocalRandom.current().nextInt(97, 122);
        sb.append(randomChar);
        while (true) {
            if (map.values().contains(sb.toString())) {
                String str = ((char) ThreadLocalRandom.current().nextInt(97, 122)) + "" + (ThreadLocalRandom.current().nextInt(0, 9));
                str = str.replace("-", "");
                sb.append(str);
            } else {
                return sb.toString();
            }
        }
    }

    public String replacePackageName(String packageName) {
        return packageMapping.computeIfAbsent(packageName, key -> randomVar(packageMapping));
    }

    public String replaceMethodName(String fullPackageName, String name) {
        return methodMapping.computeIfAbsent(fullPackageName + ":" + name, key -> randomVar(methodMapping));
    }

    public String replaceDatasourceId(String id) {
        return datasourceIdMapping.computeIfAbsent(id, key -> randomVar(datasourceIdMapping));
    }

    public String replaceClassName(String packageName, String className) {
        if (!packageName.endsWith(className)) {
            packageName = packageName + "." + className;
        }
        String name = classNameMapping.computeIfAbsent(packageName, key -> randomVar(classNameMapping));
        return name;
    }

    public String replaceVariable(String scope, String name) {
        if (!variableMapping.containsKey(scope)) {
            variableMapping.put(scope, new HashMap<>());
        }
        Map<String, String> map = variableMapping.get(scope);
        return map.computeIfAbsent(name, key -> {
            while (true) {
                String v = randomVar(map);
                if (v.equals(classNameMapping.get(scope))) {
                    v = randomVar(map);
                } else {
                    return v;
                }
            }
        });
    }

    public void addCallback(Runnable runnable) {
        funCallbacks.add(runnable);
    }

    public List<Runnable> getCallbacks() {
        return funCallbacks;
    }

    public void addCodeCallback(Runnable runnable) {
        writeCodeCallbacks.add(runnable);
    }

    public List<Runnable> getWriteCodeCallbacks() {
        return writeCodeCallbacks;
    }
}

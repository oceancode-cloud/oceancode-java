package com.oceancode.cloud.x.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Context {
    private Map<String, String> packageMapping = new HashMap<>();
    private Map<String, Map<String, String>> methodMapping = new HashMap<>();
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
        return packageMapping.computeIfAbsent(packageName, key -> {
            String prefix = "";
            Set<String> classNames = new HashSet<>();
            for (Map.Entry<String, String> entry : classNameMapping.entrySet()) {
                if (entry.getKey().startsWith(packageName + ".")) {
                    classNames.add(entry.getValue());
                }
            }
            while (true) {
                String pkg = prefix + randomVar(packageMapping);
                if (classNames.contains(pkg)) {
                    prefix = pkg;
                    continue;
                }
                return pkg;
            }
        });
    }

    public String replaceMethodName(String fullPackageName, String name) {
        if (!methodMapping.containsKey(fullPackageName)) {
            methodMapping.put(fullPackageName, new HashMap<>());
        }
        Map<String, String> map = methodMapping.get(fullPackageName);
        return map.computeIfAbsent(name, key -> randomVar(map));
    }

    public String replaceDatasourceId(String id) {
        return datasourceIdMapping.computeIfAbsent(id, key -> randomVar(datasourceIdMapping));
    }

    public String replaceClassName(String packageName, String className) {
        if (!packageName.endsWith(className)) {
            packageName = packageName + "." + className;
        }
        String name = classNameMapping.computeIfAbsent(packageName, key -> {
            String prefix = "";
            while (true) {
                String cName = prefix + randomVar(classNameMapping);
                if (packageMapping.containsKey(cName)) {
                    prefix = cName;
                    continue;
                }
                return cName;
            }
        });
        return name;
    }

    public String replaceVariable(String scope, String name) {
        return replaceVariable(Collections.emptySet(), scope, name);
    }

    public String replaceVariable(String methodScope, String scope, String name) {
        return replaceVariable(Collections.singleton(methodScope), scope, name);
    }

    public String replaceVariable(Set<String> scopes, String scope, String name) {
        if (!variableMapping.containsKey(scope)) {
            variableMapping.put(scope, new HashMap<>());
        }
        Map<String, String> map = variableMapping.get(scope);
        return map.computeIfAbsent(name, key -> {
            String prefix = "";
            while (true) {
                String v = prefix + randomVar(map);
                if (Objects.nonNull(scopes)) {
                    boolean isExists = false;
                    for (String it : scopes) {
                        Map<String, String> targetMap = variableMapping.get(it);
                        if (Objects.nonNull(targetMap) && targetMap.values().contains(v)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (isExists) {
                        prefix = v;
                        continue;
                    }
                }
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

package com.oceancode.cloud.x.util;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.oceancode.cloud.x.entity.Context;
import com.oceancode.cloud.x.wrapper.ProjectWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class XUtil {
    private final static ThreadLocal<Context> CONTEXT = new ThreadLocal<>();

    private XUtil() {
    }

    public static void init() {
        CONTEXT.set(new Context());
    }

    public static void remove() {
        CONTEXT.remove();
    }

    public static Context getContext() {
        return CONTEXT.get();
    }

    public static boolean isJavaSourceFile(File file) {
        return file.isFile() && isJavaSourceFile(file.getName());
    }

    public static boolean isJavaSourceFile(String name) {
        return name.toLowerCase().endsWith(".java");
    }


    public static void copyFile(String sourceFile, String target) {
        try {
            Files.copy(Path.of(sourceFile), Path.of(target));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeFile(String filePath, String content) {
        try {
            Files.write(Path.of(filePath), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canReplaced(NodeList<AnnotationExpr> annotations) {
        if (hasAnnotation(annotations, "PrivateScope")) {
            return true;
        }
        return !hasAnnotation(annotations, "Resource", "Autowired", "RawScope", "PublicApi", "Override");
    }

    public static boolean hasAnnotation(NodeList<AnnotationExpr> annotations, String... overrides) {
        Set<String> set = Arrays.stream(overrides).collect(Collectors.toSet());
        return annotations.stream().anyMatch(it -> set.contains(it.getNameAsString()));
    }

    public static void replaceAll(List<ProjectWrapper> list) {
        init();
        try {
            for (ProjectWrapper projectWrapper : list) {
                projectWrapper.replaceAll();
            }
            for (Runnable callback : getContext().getCallbacks()) {
                callback.run();
            }

            for (Runnable writeCodeCallback : getContext().getWriteCodeCallbacks()) {
                writeCodeCallback.run();
            }
        } finally {
            remove();
        }
    }

    public static File findFirstFileFromDir(File dir) {
        if (dir.isFile()) {
            return dir;
        }
        File[] files = dir.listFiles();
        if (Objects.isNull(files)) {
            return null;
        }
        for (File file : files) {
            if (file.isFile()) {
                return file;
            }
            File f = findFirstFileFromDir(file);
            if (Objects.nonNull(f)) {
                return f;
            }
        }
        return null;
    }
}

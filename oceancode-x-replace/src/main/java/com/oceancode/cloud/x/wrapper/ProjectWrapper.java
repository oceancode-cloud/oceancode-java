package com.oceancode.cloud.x.wrapper;

import java.io.File;
import java.util.Objects;

public class ProjectWrapper {
    private File dir;

    public ProjectWrapper(String dir) {
        this.dir = new File(dir);
    }

    public FileWrapper getPackageFile() {
        return findRootPackageFile(dir);
    }

    private FileWrapper findRootPackageFile(File dir) {
        if (!dir.exists()) {
            return null;
        }

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (Objects.isNull(files) || files.length == 0) {
                return null;
            }
            for (File file : files) {
                FileWrapper rootPackageFile = findRootPackageFile(file);
                if (Objects.nonNull(rootPackageFile)) {
                    return rootPackageFile;
                }
            }
        }

        if (dir.getName().toLowerCase().endsWith(".java")) {
            return new FileWrapper(dir.getParentFile(), this);
        }

        return null;
    }

    public File getFile() {
        return dir;
    }
}

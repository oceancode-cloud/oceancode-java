package com.oceancode.cloud.x;

import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.ProjectWrapper;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        String dir = "C:\\Users\\qinjiawang\\Downloads\\test\\repository\\123\\code";
        String outDir = "C:\\Users\\qinjiawang\\Downloads\\test\\123";
        ProjectWrapper apiProject = new ProjectWrapper(dir, "test-api", outDir);
        apiProject.addExcludePath("src");

        ProjectWrapper coreProject = new ProjectWrapper(dir, "test-core", outDir);
//        coreProject.addBasePackages("com.test.common.config.handler");

        XUtil.replaceAll(Arrays.asList(apiProject, coreProject));
    }
}

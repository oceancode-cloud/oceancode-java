package com.oceancode.cloud.x;

import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.ProjectWrapper;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        String dir = "D:\\qinjiawang\\project\\code-cloud\\server\\ccp-server-lib\\ccp-admin\\server_releases\\demo\\code-platform\\templates\\code-platform-core\\projects\\ocean-platform\\";
        String outDir = "C:\\Users\\qinjiawang\\Downloads\\test\\xreplace";
        ProjectWrapper coreProject = new ProjectWrapper(dir + "ocean-platform-core", outDir);
        coreProject.addBasePackages("com.ocean.platform.mapper.master.account");

        ProjectWrapper apiProject = new ProjectWrapper(dir + "ocean-platform-api", outDir);
        apiProject.addBasePackages("com.ocean.platform.function.account");
        XUtil.replaceAll(Arrays.asList(coreProject));
    }
}

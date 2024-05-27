package com.oceancode.cloud.common.web;

import com.oceancode.cloud.common.FullModelBeanNameGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = {
        PackageInfo.class,
        com.oceancode.cloud.common.PackageInfo.class,
}, nameGenerator = FullModelBeanNameGenerator.class)
@SpringBootApplication
public class Application {
}

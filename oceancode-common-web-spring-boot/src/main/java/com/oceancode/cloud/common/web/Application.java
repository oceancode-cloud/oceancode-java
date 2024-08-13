package com.oceancode.cloud.common.web;

import com.oceancode.cloud.PackageInfo;
import com.oceancode.cloud.common.FullModelBeanNameGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = {
        PackageInfo.class
}, nameGenerator = FullModelBeanNameGenerator.class)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {
}

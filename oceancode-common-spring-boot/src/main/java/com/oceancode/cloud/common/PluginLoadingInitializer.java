package com.oceancode.cloud.common;

import com.oceancode.cloud.common.util.SystemUtil;
import com.oceancode.cloud.function.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoadingInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private final static Logger LOGGER = LoggerFactory.getLogger(PluginLoadingInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        boolean enabled = Boolean.parseBoolean(environment.getProperty("oc.plugin.enabled", "false"));
        if (!enabled) {
            return;
        }
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        String pluginPath = SystemUtil.parsePath(environment.getProperty("oc.plugin.dir", "../plugins"));
        File pluginDir = new File(pluginPath);
        loadDirJars(pluginDir, 0, registry);
    }

    private void loadDirJars(File dir, int deep, BeanDefinitionRegistry registry) {
        if (deep > 100) {
            return;
        }
        if (!dir.exists()) {
            return;
        }
        if (!dir.isDirectory() || Objects.isNull(dir.listFiles())) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
                loadJar(file.getAbsolutePath(), registry);
            } else if (file.isDirectory()) {
                loadDirJars(file, deep + 1, registry);
            }
        }
    }

    private void loadJar(String path, BeanDefinitionRegistry registry) {
        try {
            URL jarUrlObj = new URL("jar:file:" + path + "!/");
            URLConnection urlConnection = jarUrlObj.openConnection();
            JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();
            URLClassLoader loader = new URLClassLoader(new URL[]{jarUrlObj});
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName().replace("/", ".");
                    className = className.substring(0, className.lastIndexOf("."));
                    Class<?> clazz = loader.loadClass(className);
                    if (clazz.getInterfaces().length == 0 || !Plugin.class.isAssignableFrom(clazz)) {
                        continue;
                    }
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> it : interfaces) {
                        if (processRegisterBean(clazz, it, registry)) {
                            LOGGER.info("load plugin[" + className + " - " + it.getName() + "] successful - " + path);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("plugin load error", e);
        }
    }


    private boolean processRegisterBean(Class<?> clazz, Class<?> func, BeanDefinitionRegistry registry) {
        if (func.equals(Plugin.class)) {
            return false;
        }
        registry.registerBeanDefinition(func.getName(), BeanDefinitionBuilder.genericBeanDefinition(clazz).getBeanDefinition());
        return true;
    }
}

/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.Notifier;
import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.api.strategy.StrategyAdaptor;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.function.BaseFunction;
import com.oceancode.cloud.common.function.ClientFunction;
import com.oceancode.cloud.common.function.LocalFunction;
import com.oceancode.cloud.common.function.RemoteFunction;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

public final class ComponentUtil {
    private static ApplicationContext applicationContext;

    private ComponentUtil() {
    }

    public static <T> Map<String, T> getBeans(Class<T> beanTypeClassType) {
        return applicationContext.getBeansOfType(beanTypeClassType);
    }

    public static <T> T getBean(Class<T> beanTypeClassType) {
        return applicationContext.getBean(beanTypeClassType);
    }

    public static <T> T getBean(String key, Class<T> returnClassType) {
        return applicationContext.getBean(key, returnClassType);
    }

    public static <T> T getBean(Class<T> beanTypeClassType, Function<T, Boolean> function) {
        Map<String, T> map = getBeans(beanTypeClassType);
        if (map == null || map.isEmpty()) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, beanTypeClassType.getName() + " not found.");
        }
        for (T value : map.values()) {
            Boolean target = function.apply(value);
            if (Objects.nonNull(target) && target) {
                return value;
            }
        }

        return null;
    }

    public static <T> T getStrategyBean(Class<T> typeClass, TypeEnum<?> type) {
        return getStrategyBean0(typeClass, type);
    }

    private static <T> T getStrategyBean0(Class<T> typeClass, Object type) {
        T bean = getBean(typeClass, service -> StrategyAdaptor.class.isAssignableFrom(service.getClass()) && ((StrategyAdaptor) service).isSupport(type));
        if (Objects.isNull(bean)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, typeClass.getName() + "[" + type + "] not found");
        }
        return bean;
    }

    public static List<Notifier> getNotifiers(String name) {
        Map<String, Notifier> map = getBeans(Notifier.class);
        if (map == null || map.isEmpty()) {
            return Collections.emptyList();
        }
        List<Notifier> list = new ArrayList<>(map.size());
        for (Notifier value : map.values()) {
            if (value.getResourceId().equals(name)) {
                list.add(value);
            }
        }

        return list;
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }


    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getLocalFunction(Class<T> functionClass) {
        return getBean(functionClass, e -> {
            if (e instanceof LocalFunction) {
                return true;
            }
            if (e instanceof RemoteFunction || e instanceof ClientFunction) {
                return false;
            }
            return !(e instanceof BaseFunction<?>);
        });
    }

    public static <T> T getRemoteFunction(Class<T> functionClass, boolean onlyGetRemoteFunction) {
        List<T> clientFunction = new ArrayList<>();
        List<T> remoteFunction = new ArrayList<>();
        for (T value : getBeans(functionClass).values()) {
            if (ClientFunction.class.isInstance(value)) {
                clientFunction.add(value);
            } else if (RemoteFunction.class.isInstance(value)) {
                remoteFunction.add(value);
            }
        }
        clientFunction.sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                ClientFunction c1 = (ClientFunction) o1;
                ClientFunction c2 = (ClientFunction) o2;
                return c1.getType().getOrder() - c2.getType().getOrder();
            }
        });
        if (onlyGetRemoteFunction) {
            return clientFunction.isEmpty() ? null : clientFunction.get(0);
        }
        if (!remoteFunction.isEmpty() && !clientFunction.isEmpty()) {
            return getLocalFunction(functionClass);
        }
        if (clientFunction.isEmpty()) {
            return getLocalFunction(functionClass);
        }
        return clientFunction.get(0);
    }
}

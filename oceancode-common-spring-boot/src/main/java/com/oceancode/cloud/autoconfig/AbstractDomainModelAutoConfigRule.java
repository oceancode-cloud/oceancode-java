package com.oceancode.cloud.autoconfig;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDomainModelAutoConfigRule<ADD, UPDATE, INFO> extends AbstractModelAutoConfigRule<Long, ADD, UPDATE, INFO> {
    @Override
    protected List<Class<?>> getTypeClasses() {
        List<Class<?>> typeClasses = super.getTypeClasses();
        List<Class<?>> list = new ArrayList<>();
        list.add(Long.class);
        list.addAll(typeClasses);
        return list;
    }
}

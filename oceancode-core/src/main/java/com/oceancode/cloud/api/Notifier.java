package com.oceancode.cloud.api;

public interface Notifier<T1, T2> {

    void notifier(NotifierType notifierType, T1 oldValue, T2 newValue);

    String getResourceId();
}

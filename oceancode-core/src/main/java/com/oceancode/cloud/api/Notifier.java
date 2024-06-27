package com.oceancode.cloud.api;

public interface Notifier {

    void notifier(NotifierType notifierType, Object oldValue, Object newValue);

    String getResourceId();
}

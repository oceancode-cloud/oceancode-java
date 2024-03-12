package com.oceancode.cloud.api;

import java.util.Map;

public interface MapTransfer extends Transferable {

    Map<String, Object> toMap();
}

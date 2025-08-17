package com.oceancode.cloud.api.model;


import java.util.List;

public interface PersistModelService {
    String MODEL = "model";
    String MODEL_FIELD = "field";
    String MODEL_FIELDS = "fields";
    String MODEL_GROUP = "group";
    String BUSINESS_PREFIX = "business:";
    String MODEL_ALL_PREFIX = "all:";

    String getScope();

    MObject findObjectById(String id, String versionId);

    List<MObject> findObjects(String id, String versionId);

    List<MObject> findAllObjects();

    void deleteObject(String id, String versionId);

    void saveObject(MObject object, boolean throwEx);
}

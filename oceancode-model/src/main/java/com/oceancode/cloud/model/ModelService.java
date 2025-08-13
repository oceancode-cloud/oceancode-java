package com.oceancode.cloud.model;

import com.oceancode.cloud.api.MFieldObject;
import com.oceancode.cloud.api.MObject;
import com.oceancode.cloud.api.Result;

import java.util.List;

public interface ModelService {
    Result<MObject> findById(String id);

    Result<List<MFieldObject>> findFields(String modelId, boolean allFields);

    Result<List<MFieldObject>> findFields(String modelId);

    Result<MFieldObject> findFieldById(String id);

    Result<MFieldObject> findFieldByField(String modelId, String field);

    void addModelField(MFieldObject fieldObject);

    void addModelFields(List<MFieldObject> fieldObjects);

    void updateModelField(MFieldObject fieldObject);

    void deleteModelField(String modelId, String field);

    void deleteModelFieldById(String id);

    void update(MObject mObject);

    void add(MObject mObject);

    void deleteById(String id);

    void deleteById(String id, String field);

    boolean existsField(String modelId, String... fields);

    Class<?> getClass(String className);

}

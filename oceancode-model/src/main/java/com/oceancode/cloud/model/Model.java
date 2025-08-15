package com.oceancode.cloud.model;

import com.oceancode.cloud.api.MFieldObject;
import com.oceancode.cloud.api.MObject;
import com.oceancode.cloud.api.UnSerializable;

import java.util.List;

public interface Model extends UnSerializable {

    MObject object();

    void addField(ModelField modelField);

    void addField(MFieldObject object);

    List<Model> findReferenced();

    String id();

    String versionId();

    Model parent();

    List<Model> parents();

    ModelField field(String field);

    MFieldObject fieldObject(String field);

    ModelField findFieldById(String fieldId, String versionId);

    ModelField findFieldById(String fieldId);

    List<ModelField> fields(boolean allFields);

    List<ModelField> fields();

    boolean hasParent();

    boolean isPersist();

    boolean isEnum();

    List<Model> refModels(boolean isList);
}

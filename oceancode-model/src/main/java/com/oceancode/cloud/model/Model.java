package com.oceancode.cloud.model;

import com.oceancode.cloud.api.model.MFieldObject;
import com.oceancode.cloud.api.model.MObject;

import java.util.List;

public interface Model extends BaseObject<MObject> {

    void addField(ModelField modelField);

    void addField(MFieldObject object);

    List<Model> findReferenced();

    Model parent();

    List<Model> parents();

    ModelField field(String field);

    MFieldObject fieldObject(String field);

    ModelField findFieldById(String fieldId, String versionId);

    ModelField findFieldById(String fieldId);

    List<ModelField> fields(boolean allFields);

    List<ModelField> fields();

    boolean hasParent();

    boolean isEnum();


    List<Model> refModels(boolean isList);

    ModelGroup group();

    String path();
}

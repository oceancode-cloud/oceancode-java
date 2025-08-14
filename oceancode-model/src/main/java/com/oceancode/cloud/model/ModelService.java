package com.oceancode.cloud.model;

import com.oceancode.cloud.api.MFieldObject;
import com.oceancode.cloud.api.MObject;

public interface ModelService {
    Class<?> getClass(String className);

    Model findByModelId(String modelId);

    Model findByModelId(String modelId, String versionId);

    ModelField findFieldById(String fieldId, String versionId);

    ModelField findFieldById(String fieldId);

    void save(Model model);

    void save(MObject model);

    void save(ModelField modelField);

    void save(MFieldObject modelField);

    Model createModel(MObject object);

    ModelField createModelField(MFieldObject object);

    void deleteByModelId(String modelId);

    void deleteByModelId(String modelId, String versionId);

    void deleteByFieldId(String fieldId, String versionId);

    void deleteByFieldId(String fieldId);
}

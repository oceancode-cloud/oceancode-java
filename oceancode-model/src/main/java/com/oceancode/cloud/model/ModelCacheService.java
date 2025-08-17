package com.oceancode.cloud.model;

import com.oceancode.cloud.api.model.MObject;

import java.util.List;

public interface ModelCacheService {
    MObject findMObjectById(String id, String versionId, String scope);

    default MObject findMObjectById(Long id, Long versionId, String scope) {
        return findMObjectById(ModelUtil.toStr(id), ModelUtil.toStr(versionId), scope);
    }

    MObject findMObjectById(String id, String scope);
    void saveMObjects(List<MObject> objects);

    default MObject findMObjectById(Long id, String scope) {
        return findMObjectById(ModelUtil.toStr(id), scope);
    }

    void deleteMObjectById(String id, String versionId, String scope);

    default void deleteMObjectById(Long id, Long versionId, String scope) {
        deleteMObjectById(ModelUtil.toStr(id), ModelUtil.toStr(versionId), scope);
    }

    void deleteMObjectById(String id, String scope);

    default void deleteMObjectById(Long id, String scope) {
        deleteMObjectById(ModelUtil.toStr(id), scope);
    }

    Model findModelById(String modelId, String versionId);

    default Model findModelById(Long modelId, Long versionId) {
        return findModelById(ModelUtil.toStr(modelId), ModelUtil.toStr(versionId));
    }

    ModelField findModelFieldById(String fieldId, String versionId);

    default ModelField findModelFieldById(Long fieldId, Long versionId) {
        return findModelFieldById(ModelUtil.toStr(fieldId), ModelUtil.toStr(versionId));
    }

    List<ModelField> findModelFields(String modelId, String versionId);

    default List<ModelField> findModelFields(Long modelId, Long versionId) {
        return findModelFields(ModelUtil.toStr(modelId), ModelUtil.toStr(versionId));
    }

    ModelGroup findModelGroupById(String groupId, String versionId);

    default ModelGroup findModelGroupById(Long groupId, Long versionId) {
        return findModelGroupById(ModelUtil.toStr(groupId), ModelUtil.toStr(versionId));
    }

    void deleteModelById(String id, String versionId);

    default void deleteModelById(Long id, Long versionId) {
        deleteModelById(ModelUtil.toStr(id), ModelUtil.toStr(versionId));
    }

    void deleteModelFieldById(String fieldId, String versionId);

    default void deleteModelFieldById(Long fieldId, Long versionId) {
        deleteModelFieldById(ModelUtil.toStr(fieldId), ModelUtil.toStr(versionId));
    }

    void deleteModelGroupById(String groupId, String versionId);

    default void deleteModelGroupById(Long groupId, Long versionId) {
        deleteModelGroupById(ModelUtil.toStr(groupId), ModelUtil.toStr(versionId));
    }

    void save(MObject object, boolean throwEx);

    void save(MObject object);

    Class<?> getClass(String className);

    Model findModelById(String modelId);

    default Model findModelById(Long modelId) {
        return findModelById(ModelUtil.toStr(modelId));
    }

    ModelField findModelFieldById(String fieldId);

    default ModelField findModelFieldById(Long fieldId) {
        return findModelFieldById(ModelUtil.toStr(fieldId));
    }

    List<ModelField> findModelFields(String modelId);

    default List<ModelField> findModelFields(Long modelId) {
        return findModelFields(ModelUtil.toStr(modelId));
    }

    ModelGroup findModelGroupById(String groupId);

    default ModelGroup findModelGroupById(Long groupId) {
        return findModelGroupById(ModelUtil.toStr(groupId));
    }

    void deleteModelById(String id);

    default void deleteModelById(Long id) {
        deleteModelById(ModelUtil.toStr(id));
    }

    void deleteModelFieldById(String fieldId);

    default void deleteModelFieldById(Long fieldId) {
        deleteModelFieldById(ModelUtil.toStr(fieldId));
    }

    void deleteModelGroupById(String groupId);

    default void deleteModelGroupById(Long groupId) {
        deleteModelGroupById(ModelUtil.toStr(groupId));
    }

    Object create(MObject object);
}

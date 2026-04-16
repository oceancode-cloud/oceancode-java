package com.oceancode.cloud.plugin.api.object.model;

public enum ModifyType {
    UPDATE(3),
    UPDATE_MANY(4),
    REMOVE(5),
    REMOVE_MANY(6),
    FMOVE(7),
    NMOVE(8),
    ADD(1),
    ADD_MANY(2),
    CUSTOM(-1);

    private int type;

    ModifyType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public boolean isRemove() {
        return REMOVE.equals(this) || REMOVE_MANY.equals(this);
    }

    public boolean isUpdate() {
        return UPDATE.equals(this) || UPDATE_MANY.equals(this);
    }

    public boolean isAdd() {
        return ADD.equals(this) || ADD_MANY.equals(this);
    }

    public boolean isMany() {
        return REMOVE_MANY.equals(this) || ADD_MANY.equals(this) || UPDATE_MANY.equals(this);
    }
}

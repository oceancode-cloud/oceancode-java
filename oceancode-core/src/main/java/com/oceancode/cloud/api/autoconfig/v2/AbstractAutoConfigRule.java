package com.oceancode.cloud.api.autoconfig.v2;

import com.oceancode.cloud.api.autoconfig.AutoConfigType;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractAutoConfigRule implements AutoConfigRule {

    @Override
    public String getProperty() {
        return "";
    }

    @Override
    public boolean support(AutoConfigContext context, AutoConfig item, List<AutoConfig> groups) {
        return true;
    }

    @Override
    public boolean isIgnore(AutoConfig item) {
        return false;
    }

    @Override
    public void apply(AutoConfigContext context, AutoConfigResult result, AutoConfig item, List<AutoConfig> groups) {
        List<AutoConfig> list = groups.stream().filter(it -> !this.isIgnore(it)).toList();
        if (Objects.isNull(item)) {
            if (groups.size() == 1) {
                item = groups.getFirst();
            }
        }
        if (doUpdateProperties(context, result, groups)) {
            for (AutoConfig group : groups) {
                if (AutoConfigType.UPDATE.getValue().equals(group.getType())) {
                    context.done(group);
                }
            }
            list = list.stream().filter(context::isApplied).toList();
        }
        if (Objects.nonNull(item)) {
            if (isIgnore(item)) {
                return;
            }
            result.setSourceId(item.getSourceId());
            result.setNotifier(item.getNotifier());
            result.setGroup(item.getGroup());
            result.setProperty(item.getProperty());
            if (AutoConfigType.ADD.getValue().equals(item.getType())) {
                if (this.doApplyAdd(context, result, item, list)) {
                    context.done(item);
                    if (ValueUtil.isEmpty(result.getNewValue())) {
                        result.setNewValue(item.getNewValue());
                    }
                    return;
                }
            } else if (AutoConfigType.ADD_MANY.getValue().equals(item.getType())) {
                if (this.doApplyAddMany(context, result, item, list)) {
                    if (ValueUtil.isEmpty(result.getNewValue())) {
                        result.setNewValue(item.getNewValue());
                    }
                    return;
                }
            } else if (AutoConfigType.UPDATE.getValue().equals(item.getType())) {
                if (this.doApplyUpdate(context, result, item, list)) {
                    if (ValueUtil.isEmpty(result.getNewValue())) {
                        result.setNewValue(item.getNewValue());
                    }
                    return;
                }
            } else if (AutoConfigType.UPDATE_MANY.getValue().equals(item.getType())) {
                if (this.doApplyUpdateMany(context, result, item, list)) {
                    return;
                }
            } else if (AutoConfigType.REMOVE.getValue().equals(item.getType())) {
                if (this.doApplyDelete(context, result, item, list)) {
                    return;
                }
            } else if (AutoConfigType.MOVE.getValue().equals(item.getType())) {
                if (this.doApplyMove(context, result, item, list)) {
                    return;
                }
            }

            if (context.isApplied(item)) {
                return;
            }
        }
        this.doApply(context, result, item, list);
    }

    protected void doApply(AutoConfigContext context, AutoConfigResult result, AutoConfig item, List<AutoConfig> groups) {
    }

    protected boolean doApplyUpdate(AutoConfigContext context, AutoConfigResult result, AutoConfig item, List<AutoConfig> groups) {
        return false;
    }

    protected boolean doApplyUpdateMany(AutoConfigContext context, AutoConfigResult result, AutoConfig item, List<AutoConfig> groups) {
        return false;
    }

    protected boolean doApplyAdd(AutoConfigContext context, AutoConfigResult result, AutoConfig item, List<AutoConfig> groups) {
        return false;
    }

    protected boolean doApplyAddMany(AutoConfigContext context, AutoConfigResult result, AutoConfig item, List<AutoConfig> groups) {
        return false;
    }

    protected boolean doApplyDelete(AutoConfigContext context, AutoConfigResult result, AutoConfig item, List<AutoConfig> groups) {
        return false;
    }

    protected boolean doApplyMove(AutoConfigContext context, AutoConfigResult result, AutoConfig item, List<AutoConfig> groups) {
        return false;
    }

    protected boolean doUpdateProperties(AutoConfigContext context, AutoConfigResult result, List<AutoConfig> groups) {
        if (ValueUtil.isEmpty(groups)) {
            return true;
        }
        Map<String, Object> map = new HashMap<>();
        List<AutoConfig> updateList = new ArrayList<>();
        for (AutoConfig group : groups) {
            if (!AutoConfigType.UPDATE.getValue().equals(group.getType())) {
                continue;
            }
            updateList.add(group);
            context.done(group);
            map.put(group.getProperty(), group.getNewValue());
            if (ValueUtil.isNotEmpty(group.getVersionId())) {
                map.put("versionId", group.getVersionId());
            }
        }
        if (map.isEmpty()) {
            return true;
        }

        boolean ret = doUpdateProperties(context, result, map, updateList);
        if (ret) {
            result.setNotifier(updateList.getFirst().getNotifier());
        }
        return ret;
    }

    protected boolean doUpdateProperties(AutoConfigContext context, AutoConfigResult result, Map<String, Object> map, List<AutoConfig> groups) {
        return false;
    }
}

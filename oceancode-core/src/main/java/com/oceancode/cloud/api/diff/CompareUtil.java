package com.oceancode.cloud.api.diff;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class CompareUtil {
    private CompareUtil() {
    }

    public static boolean checkNull(List<DiffDetail> list, Object newVal, Object oldVal) {
        if (Objects.isNull(newVal)) {
            if (Objects.isNull(oldVal)) {
                return true;
            }
            list.add(new DiffDetail(newVal, oldVal, ValueStatus.UNSET));
            return true;
        } else {
            if (Objects.isNull(oldVal)) {
                list.add(new DiffDetail(newVal, oldVal, ValueStatus.SET));
                return true;
            }
        }
        return false;
    }


    public static void compare(List<DiffDetail> list, String newVal, String oldVal) {
        if (checkNull(list, newVal, oldVal)) {
            return;
        }
        if (!newVal.equals(oldVal)) {
            list.add(new DiffDetail(newVal, oldVal, ValueStatus.UPDATE));
        }
    }

    public static void compare(List<DiffDetail> list, Long newVal, Long oldVal) {
        if (checkNull(list, newVal, oldVal)) {
            return;
        }
        if (!newVal.equals(oldVal)) {
            list.add(new DiffDetail(newVal, oldVal, ValueStatus.UPDATE));
        }
    }

    public static void compare(List<DiffDetail> list, Integer newVal, Integer oldVal) {
        if (checkNull(list, newVal, oldVal)) {
            return;
        }
        if (!newVal.equals(oldVal)) {
            list.add(new DiffDetail(newVal, oldVal, ValueStatus.UPDATE));
        }
    }

    public static void compare(List<DiffDetail> list, Float newVal, Float oldVal) {
        if (checkNull(list, newVal, oldVal)) {
            return;
        }
        if (!newVal.equals(oldVal)) {
            list.add(new DiffDetail(newVal, oldVal, ValueStatus.UPDATE));
        }
    }

    public static void compare(List<DiffDetail> list, Double newVal, Double oldVal) {
        if (checkNull(list, newVal, oldVal)) {
            return;
        }
        if (!newVal.equals(oldVal)) {
            list.add(new DiffDetail(newVal, oldVal, ValueStatus.UPDATE));
        }
    }

    public static void compare(List<DiffDetail> list, BigDecimal newVal, BigDecimal oldVal) {
        if (checkNull(list, newVal, oldVal)) {
            return;
        }
        if (newVal.compareTo(oldVal) != 0) {
            list.add(new DiffDetail(newVal, oldVal, ValueStatus.UPDATE));
        }
    }

    public static void compare(List<DiffDetail> list, Boolean newVal, Boolean oldVal) {
        if (checkNull(list, newVal, oldVal)) {
            return;
        }
        if (!newVal.equals(oldVal)) {
            list.add(new DiffDetail(newVal, oldVal, ValueStatus.UPDATE));
        }
    }

    public static void compare(List<DiffDetail> list, DiffCompare newVal, Object oldVal) {
        if (checkNull(list, newVal, oldVal)) {
            return;
        }
        List<DiffDetail> childrenList = newVal.diff(oldVal);
        DiffDetail diffDetail = new DiffDetail(newVal, oldVal, null);
        diffDetail.setChildren(childrenList);
        list.add(diffDetail);
    }
}

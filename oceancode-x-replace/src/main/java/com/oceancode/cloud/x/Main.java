package com.oceancode.cloud.x;

import com.oceancode.cloud.x.util.XUtil;

public class Main {

    public static void main(String[] args) {
        try {
            XUtil.init();
        } finally {
            XUtil.remove();
        }
    }
}

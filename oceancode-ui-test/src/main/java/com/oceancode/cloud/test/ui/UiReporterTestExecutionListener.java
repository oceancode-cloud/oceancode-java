package com.oceancode.cloud.test.ui;

import com.oceancode.cloud.common.util.SystemUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.test.reporter.ReporterTestExecutionListener;
import com.oceancode.cloud.test.ui.container.UiUtil;

public class UiReporterTestExecutionListener extends ReporterTestExecutionListener {
    @Override
    protected String getOutputPath() {
        String dataDir = UiUtil.getProperties().getProperty(SystemUtil.OUTPUT_DIR_CONFIG_KEY);
        if (ValueUtil.isEmpty(dataDir)) {
            dataDir = System.getProperty("user.dir") + "/output";
        }

        return dataDir;
    }
}

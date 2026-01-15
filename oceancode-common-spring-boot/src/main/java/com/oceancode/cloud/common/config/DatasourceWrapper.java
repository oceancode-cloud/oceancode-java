package com.oceancode.cloud.common.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceWrapper;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;

public class DatasourceWrapper extends DruidDataSourceWrapper {
    private String datasourceId;

    public DatasourceWrapper(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    @Override
    public void setPassword(String password) {
        CommonConfig commonConfig = ComponentUtil.getBean(CommonConfig.class);
        String publicKey = commonConfig.getValue("spring.datasource." + datasourceId + ".publicKey");
        if (ValueUtil.isEmpty(publicKey)) {
            super.setPassword(password);
            return;
        }
        Rsa2CryptoService rsa2CryptoService = ComponentUtil.getBean(Rsa2CryptoService.class);
        String pass = rsa2CryptoService.decryptByPublicKey(super.getPassword(), publicKey);
        super.setPassword(pass);
    }
}

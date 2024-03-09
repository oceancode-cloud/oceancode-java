package com.oceancode.cloud.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;

public class DatasourceWrapper extends DruidDataSource {

    private Rsa2CryptoService rsa2CryptoService;
    private String datasourceId;

    public DatasourceWrapper(Rsa2CryptoService rsa2CryptoService, String datasourceId) {
        this.rsa2CryptoService = rsa2CryptoService;
        this.datasourceId = datasourceId;
    }

    @Override
    public String getPassword() {
        CommonConfig commonConfig = ComponentUtil.getBean(CommonConfig.class);
        String publicKey = commonConfig.getValue("spring.datasource." + datasourceId + ".publicKey");
        if (ValueUtil.isEmpty(publicKey)) {
            return super.getPassword();
        }
        return rsa2CryptoService.decryptByPublicKey(super.getPassword(), publicKey);
    }
}

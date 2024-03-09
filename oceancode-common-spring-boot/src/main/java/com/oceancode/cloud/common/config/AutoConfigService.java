package com.oceancode.cloud.common.config;

import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.security.Rsa2Crypto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AutoConfigService {

    @Bean
    @ConditionalOnMissingBean(BCryptPasswordEncoder.class)
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(Rsa2CryptoService.class)
    public Rsa2CryptoService cryptoService() {
        return new Rsa2Crypto();
    }
}

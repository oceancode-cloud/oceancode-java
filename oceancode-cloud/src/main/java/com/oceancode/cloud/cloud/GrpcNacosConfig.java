package com.oceancode.cloud.cloud;

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.PostConstruct;
import net.devh.boot.grpc.common.util.GrpcUtils;
import net.devh.boot.grpc.server.config.GrpcServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({NacosRegistration.class, GrpcServerProperties.class})
public class GrpcNacosConfig {
    @Autowired(required = false)
    private NacosRegistration nacosRegistration;

    @Autowired(required = false)
    private GrpcServerProperties grpcServerProperties;

    @PostConstruct
    public void init() {
        if (nacosRegistration != null) {
            int port = grpcServerProperties.getPort();
            if (GrpcUtils.INTER_PROCESS_DISABLE != port) {
                if (ValueUtil.isEmpty(nacosRegistration.getMetadata().get(GrpcUtils.CLOUD_DISCOVERY_METADATA_PORT))) {
                    nacosRegistration.getMetadata().put(GrpcUtils.CLOUD_DISCOVERY_METADATA_PORT, Integer.toString(port));
                }
            }
        }
    }
}

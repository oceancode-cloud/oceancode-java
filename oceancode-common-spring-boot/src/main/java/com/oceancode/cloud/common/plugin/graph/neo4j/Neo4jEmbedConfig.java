package com.oceancode.cloud.common.plugin.graph.neo4j;


import com.oceancode.cloud.common.util.SystemUtil;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.config.Setting;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass(DatabaseManagementService.class)
public class Neo4jEmbedConfig {

    @Bean
    @ConditionalOnExpression(value = "'${oc.graph.type}'=='embed'")
    public GraphDatabaseService graphDatabaseService() {
        String dbPath = SystemUtil.dataDir() + File.separator + "graph/neo4j.db";
        Map<Setting<?>, Object> config = new HashMap<>();
//        config.put(GraphDatabaseSettings.DEFAULT_DATABASE_NAME, "neo4j"); // 设置数据库名称
        config.put(BoltConnector.enabled, true); // 启用Bolt协议连接器
        config.put(BoltConnector.listen_address, new SocketAddress("localhost", 7687)); // 设置Bolt端口号和地址
//        config.put(GraphDatabaseSettings.pagecache_memory, "1G"); // 设置页面缓存大小限制
        config.put(GraphDatabaseSettings.data_directory, Path.of(SystemUtil.dataDir() + File.separator + "graph/neo4j")); // 设置数据存储目录
        DatabaseManagementService managementService = new DatabaseManagementServiceBuilder(Path.of(dbPath))
                .setConfig(config)
                .build();
        GraphDatabaseService database = managementService.database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME);
        registerShutdownHook(managementService, GraphDatabaseSettings.DEFAULT_DATABASE_NAME);
        return database;
    }

    private static void registerShutdownHook(final DatabaseManagementService graphDb, String database) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdownDatabase(database);
                graphDb.shutdown();
            }
        });
    }
}

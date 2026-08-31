package com.consistenthashing.config;

import com.consistenthashing.consistenthash.ConsistentHashRing;
import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.service.DistributionService;
import com.consistenthashing.service.NodeService;
import com.consistenthashing.service.StudentService;
import com.consistenthashing.storage.MongoClientProvider;
import com.consistenthashing.storage.MongoStudentStore;
import com.consistenthashing.storage.StudentStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConsistentHashingConfig {

    @Value("${app.nodes:localhost:27017,localhost:27018,localhost:27019}")
    private String nodesProperty;

    @Value("${app.virtual-nodes:150}")
    private int virtualNodes;

    @Bean
    public MongoClientProvider mongoClientProvider() {
        return new MongoClientProvider();
    }

    @Bean
    public StudentStore studentStore(MongoClientProvider clientProvider) {
        return new MongoStudentStore(clientProvider);
    }

    @Bean
    public ConsistentHashRing hashRing() {
        List<StorageNode> nodes = new ArrayList<>();
        if (nodesProperty != null && !nodesProperty.trim().isEmpty()) {
            String[] parts = nodesProperty.split(",");
            for (String part : parts) {
                String[] hostPort = part.trim().split(":");
                if (hostPort.length == 2) {
                    nodes.add(new StorageNode(hostPort[0], Integer.parseInt(hostPort[1])));
                }
            }
        }
        return new ConsistentHashRing(virtualNodes, nodes);
    }

    @Bean
    public DistributionService distributionService(ConsistentHashRing hashRing, StudentStore studentStore) {
        return new DistributionService(hashRing, studentStore);
    }

    @Bean
    public StudentService studentService(ConsistentHashRing hashRing, StudentStore studentStore) {
        return new StudentService(hashRing, studentStore);
    }

    @Bean
    public NodeService nodeService(ConsistentHashRing hashRing, StudentStore studentStore, DistributionService distributionService) {
        return new NodeService(hashRing, studentStore, distributionService);
    }
}

package com.consistenthashing.storage;

import com.consistenthashing.consistenthash.StorageNode;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MongoClientProvider {

    private final Map<StorageNode, MongoClient> clientCache = new ConcurrentHashMap<>();

    public MongoClient getClient(StorageNode node) {
        return clientCache.computeIfAbsent(node, n -> {
            String connectionString = String.format("mongodb://%s:%d/?serverSelectionTimeoutMS=3000&directConnection=true",
                    n.getHost(), n.getPort());
            return MongoClients.create(connectionString);
        });
    }

    public void closeAll() {
        for (MongoClient client : clientCache.values()) {
            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
        clientCache.clear();
    }
}

# Exercise 4 — MongoDB Horizontal Sharding

## Directory to Run
```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-4-sharding
```

## Manual Execution Steps

### Step 1: Start Docker Containers
```bash
docker compose up -d
```

### Step 2: Initialize Config Server and Shard Replica Sets
```bash
docker exec -i configsvr mongosh --port 27019 --eval 'rs.initiate({_id: "configReplSet", configsvr: true, members: [{_id: 0, host: "configsvr:27019"}]});'
```
```bash
docker exec -i shard1 mongosh --port 27018 --eval 'rs.initiate({_id: "shard1RS", members: [{_id: 0, host: "shard1:27018"}]});'
```
```bash
docker exec -i shard2 mongosh --port 27028 --eval 'rs.initiate({_id: "shard2RS", members: [{_id: 0, host: "shard2:27028"}]});'
```

### Step 3: Add Shards and Enable Sharding
```bash
docker exec -i mongos mongosh --port 27017 --eval 'sh.addShard("shard1RS/shard1:27018"); sh.addShard("shard2RS/shard2:27028"); sh.enableSharding("College");'
```

### Step 4: Shard Collection and Split/Move Chunks
```bash
docker exec -i mongos mongosh --port 27017 --eval 'db.getSiblingDB("College").Student.createIndex({ RollNo: 1 }); sh.shardCollection("College.Student", { RollNo: 1 }); sh.splitAt("College.Student", { RollNo: 10 }); sh.moveChunk("College.Student", { RollNo: 11 }, "shard2RS");'
```

### Step 5: Insert Student Records
```bash
docker exec -i mongos mongosh --port 27017 --eval 'db.getSiblingDB("College").Student.insertMany([{ RollNo: 1, Name: "Student_1", Department: "Computer Science", Year: 1, CGPA: 8.5 }, { RollNo: 2, Name: "Student_2", Department: "Mechanical", Year: 2, CGPA: 7.8 }, { RollNo: 3, Name: "Student_3", Department: "Civil", Year: 4, CGPA: 9.1 }, { RollNo: 9, Name: "Student_9", Department: "Information Tech", Year: 3, CGPA: 8.0 }, { RollNo: 10, Name: "Student_10", Department: "Computer Science", Year: 1, CGPA: 8.2 }, { RollNo: 15, Name: "Student_15", Department: "Electrical", Year: 3, CGPA: 7.9 }, { RollNo: 20, Name: "Student_20", Department: "Civil", Year: 2, CGPA: 9.0 }]);'
```

### Step 6: Check Cluster Status
```bash
docker exec -i mongos mongosh --port 27017 --eval 'sh.status();'
```

### Step 7: View Data Distribution Across Shards
```bash
docker exec -i mongos mongosh --port 27017 --eval 'db.getSiblingDB("College").Student.getShardDistribution();'
```

### Step 8: Targeted Single-Shard Query Explain Plan
```bash
docker exec -i mongos mongosh --port 27017 --eval 'db.getSiblingDB("College").Student.find({ RollNo: { $gte: 1, $lte: 5 } }).explain("executionStats");'
```

### Step 9: Multi-Shard Broadcast Query Explain Plan
```bash
docker exec -i mongos mongosh --port 27017 --eval 'db.getSiblingDB("College").Student.find({ Department: "Computer Science" }).explain("executionStats");'
```

### Step 10: Clean Up / Stop Docker Containers
```bash
docker compose down -v
```

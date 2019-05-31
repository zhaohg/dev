# Mongodb 3.4.6 Sharding 

标签： mongodb sharding 3.4.6

---

### sharding由三部分组成
    shard: 每个shard包括一个所有shard数据的子集. 每个shard能够部署成replica set
    mongos: 做为查询路由, 提供client与sharded cluster的接口
    config servers: 存储群集的metadata和配置，必须部署为replica set
> 以下配置不要使用localhost或127.0.0.1，sharding会报错 

### 建立数据目录
```
mkdir -p /Users/com.zhaohg/mongodb/rs{0,1,2}/{s{0,1,2},config,log}
mkdir -p /Users/com.zhaohg/mongodb/mongos
```

### 配置 shard server

```
mongod --port 20011 --shardsvr --replSet shard1 --dbpath /Users/com.zhaohg/mongodb/rs0/s0 --fork --logpath /Users/com.zhaohg/mongodb/rs0/log/s0.log
    
mongod --port 20021 --shardsvr --replSet shard2 --dbpath /Users/com.zhaohg/mongodb/rs0/s1 --fork --logpath /Users/com.zhaohg/mongodb/rs0/log/s1.log
    
mongod --port 20031 --shardsvr --replSet shard3 --dbpath /Users/com.zhaohg/mongodb/rs0/s2 --fork --logpath /Users/com.zhaohg/mongodb/rs0/log/s2.log

----------

mongod --port 20012 --shardsvr --replSet shard1 --dbpath /Users/com.zhaohg/mongodb/rs1/s0 --fork --logpath /Users/com.zhaohg/mongodb/rs1/log/s0.log
    
mongod --port 20022 --shardsvr --replSet shard2 --dbpath /Users/com.zhaohg/mongodb/rs1/s1 --fork --logpath /Users/com.zhaohg/mongodb/rs1/log/s1.log
    
mongod --port 20032 --shardsvr --replSet shard3 --dbpath /Users/com.zhaohg/mongodb/rs1/s2 --fork --logpath /Users/com.zhaohg/mongodb/rs1/log/s2.log

----------

mongod --port 20013 --shardsvr --replSet shard1 --dbpath /Users/com.zhaohg/mongodb/rs2/s0 --fork --logpath /Users/com.zhaohg/mongodb/rs2/log/s0.log
    
mongod --port 20023 --shardsvr --replSet shard2 --dbpath /Users/com.zhaohg/mongodb/rs2/s1 --fork --logpath /Users/com.zhaohg/mongodb/rs2/log/s1.log
    
mongod --port 20033 --shardsvr --replSet shard3 --dbpath /Users/com.zhaohg/mongodb/rs2/s2 --fork --logpath /Users/com.zhaohg/mongodb/rs2/log/s2.log

# 测试用以上方法，生产环境要用配置文件,如：mongod_20011.conf
# /usr/local/etc/mongodb/mongod_20011.conf
systemLog:
  destination: file
  path: /usr/local/var/log/mongodb/20011.log
  logAppend: true

storage:
  dbPath: /usr/local/var/mongodb/20011
  journal:
    enabled: true
  syncPeriodSecs: 60
  engine: wiredTiger 
  wiredTiger:
    engineConfig:
      cacheSizeGB: 1
      statisticsLogDelaySecs: 0
      journalCompressor: snappy
    collectionConfig:
      blockCompressor: snappy
    indexConfig:
      prefixCompression: true
operationProfiling:
  slowOpThresholdMs: 200
  mode: slowOp
   
net:
  port: 20011

processManagement:
  fork: true
  pidFilePath: /usr/local/var/run/mongodb/10001.pid

#security:
  #keyFile: /data/mongodb/config/data/keyfile
  #authorization: enabled

replication:
  oplogSizeMB: 2048
  replSetName: shard1

sharding:
  clusterRole:  shardsvr
  
  # 启动实例
  mongod --config /usr/local/etc/mongodb/mongod_20011.conf
  # 其他实例同20011实例
```
### 配置 config server
 
```   
mongod --port 30001 --configsvr --replSet cfg1 --dbpath /Users/com.zhaohg/mongodb/rs0/config --fork --logpath /Users/com.zhaohg/mongodb/rs0/log/config.log

# 初始化配置服务
rs.initiate()
    
mongod --port 30002 --configsvr --replSet cfg2 --dbpath /Users/com.zhaohg/mongodb/rs1/config --fork --logpath /Users/com.zhaohg/mongodb/rs1/log/config.log
# 初始化配置服务
rs.initiate()
    
mongod --port 30003 --configsvr --replSet cfg3 --dbpath /Users/com.zhaohg/mongodb/rs2/config --fork --logpath /Users/com.zhaohg/mongodb/rs2/log/config.log
# 初始化配置服务
rs.initiate()


# 测试用以上方法，生产环境要用配置文件,如：mongod_20011.conf
# /usr/local/etc/mongodb/mongod_30000.conf
systemLog:
  destination: file
  path: /usr/local/var/log/mongodb/30000.log
  logAppend: true

storage:
  dbPath: /usr/local/var/mongodb/30000
  journal:
    enabled: true
  syncPeriodSecs: 60
  engine: wiredTiger 
  wiredTiger:
    engineConfig:
      cacheSizeGB: 1
      statisticsLogDelaySecs: 0
      journalCompressor: snappy
    collectionConfig:
      blockCompressor: snappy
    indexConfig:
      prefixCompression: true

processManagement:
  fork: true
  pidFilePath: /usr/local/var/run/mongodb/30000.pid

net:
  port: 30000

#security:
  #keyFile: /data/mongodb/config/data/keyfile
  #authorization: enabled

operationProfiling:
  slowOpThresholdMs: 200
  mode: slowOp

replication:
   oplogSizeMB: 2048
   replSetName: cfg1

sharding:
  clusterRole: configsvr

# 启动 configsvr
mongod --config /usr/local/etc/mongodb/mongod_30000.conf
```
> 连接到任意一台shard服务器上 创建配置服务器副本集 

    mongo 20011.mongo.com:20011/admin
    rs.initiate({
        "_id":"shard1",     
        "members":[{"_id":0,"host":"20011.mongo.com:20011"},
                   {"_id":1,"host":"20012.mongo.com:20012"},
                   {"_id":2,"host":"20013.mongo.com:20013"}]
    })

    mongo 20021.mongo.com:20021/admin
    rs.initiate({
        "_id":"shard2",     
        "members":[{"_id":0,"host":"20021.mongo.com:20021"},
                {"_id":1,"host":"20022.mongo.com:20022"},
                {"_id":2,"host":"20023.mongo.com:20023"}]
    })
    
    mongo 20031.mongo.com:20021/admin
    rs.initiate({
        "_id":"shard3", 
        "members":[{"_id":0,"host":"20031.mongo.com:20031"},
                   {"_id":1,"host":"20032.mongo.com:20032"},
                   {"_id":2,"host":"20033.mongo.com:20033"}]
    })

### 配置 mongos server
```
mongos --port 40000 --configdb cfg1/30001.mongo.com:30001 --fork --logpath /Users/com.zhaohg/mongodb/mongos/route.log

#mongos.conf
systemLog:
  destination: file
  path: /usr/local/var/log/mongodb/40000.log
  logAppend: true
processManagement:
  fork: true
  pidFilePath: /usr/local/var/run/mongodb/40000.pid
net:
  port: 40000
  
#security:
  #keyFile: /data/mongodb/config/data/keyfile
  #authorization: enabled
sharding:
  configDB: shard1/20011.mongo.com:20011,...:.,...:..



# 使用MongoDB Shell登录到mongos，添加Shard节点
mongo admin --port 40000 #此操作需要连接admin库

# 添加分片
mongos> sh.addShard("shard1/20011.mongo.com:20011,20012.mongo.com:20012,20013.mongo.com:20013")
mongos> sh.addShard('shard2/20021.mongo.com:20021')
mongos> sh.addShard('shard2/20022.mongo.com:20022')
mongos> sh.addShard('shard2/20023.mongo.com:20023')

# 建立超级用户
use admin

db.createUser(
  {
    user: 'admin',
    pwd: '@admin',
    roles: [ { role: 'root', db: 'admin' } ]
  }
);

# 认证登录
db.auth('admin', '@admin') 

# app数据库user表插入100000条数据
use app
for (var i = 1; i < 100000; i++) db.user.insert({id: i, name: 'ken'})

WriteResult({ "nInserted" : 1 })

```

```
## 查看分片状态
sh.status();
--- Sharding Status --- 
  sharding version: {
    "_id" : 1,
    "minCompatibleVersion" : 5,
    "currentVersion" : 6,
    "clusterId" : ObjectId("59261bca37ceff575b36ef09")
}
  shards:
    {  "_id" : "shard1",  "host" : "shard1/20011.mongo.com:20011,20012.mongo.com:20012,20013.mongo.com:20013",  "state" : 1 }
    {  "_id" : "shard2",  "host" : "shard2/20021.mongo.com:20021,20022.mongo.com:20022,20023.mongo.com:20023",  "state" : 1 }
  active mongoses:
    "3.4.6" : 1
 autosplit:
    Currently enabled: yes
  balancer:
    Currently enabled:  yes
    Currently running:  no
        Balancer lock taken at Thu May 25 2018 07:48:42 GMT+0800 (CST) by ConfigServer:Balancer
    Failed balancer rounds in last 5 attempts:  0
    Migration Results for the last 24 hours: 
        No recent migrations
  databases:
    {  "_id" : "app",  "primary" : "shard1",  "partitioned" : false }

# 结果显示没有分区，记录插在shard1服务器
```

### 配置分片
```  
# 允许分片的数据库
sh.enableSharding('app') 
```

```
# 为用做shard key的字段建立索引，实际中可以用uuid字段
use app
db.user.createIndex({id: 1})
{
    "raw" : {
        "shard1/20011.mongo.com:20011,20012.mongo.com:20012,20013.mongo.com:20013" : {
            "createdCollectionAutomatically" : false,
            "numIndexesBefore" : 1,
            "numIndexesAfter" : 2,
            "ok" : 1,
            "$gleStats" : {
                "lastOpTime" : {
                    "ts" : Timestamp(1495675335, 1),
                    "t" : NumberLong(1)
                },
                "electionId" : ObjectId("7fffffff0000000000000001")
            }
        }
    },
    "ok" : 1
}

# 允许分片的collection，指定shard key的字段
sh.shardCollection('app.user', { id : 'hashed' })
{ "collectionsharded" : "app.user", "ok" : 1 }


# 插入数据
for (var i = 1; i < 10000; i++) db.user.insert({id: i, name: 'ken'})
WriteResult({ "nInserted" : 1 })

# 查看状态，可以看到db与collection的分片状态
sh.status()
--- Sharding Status --- 
  sharding version: {
    "_id" : 1,
    "minCompatibleVersion" : 5,
    "currentVersion" : 6,
    "clusterId" : ObjectId("59261bca37ceff575b36ef09")
}
  shards:
     {  "_id" : "shard1",  "host" : "shard1/20011.mongo.com:20011,20012.mongo.com:20012,20013.mongo.com:20013",  "state" : 1 }
     {  "_id" : "shard2",  "host" : "shard2/20021.mongo.com:20021,20022.mongo.com:20022,20023.mongo.com:20023",  "state" : 1 }
  active mongoses:
    "3.4.6" : 1
 autosplit:
    Currently enabled: yes
  balancer:
    Currently enabled:  yes
    Currently running:  no
        Balancer lock taken at Thu May 25 2018 07:48:42 GMT+0800 (CST) by ConfigServer:Balancer
    Failed balancer rounds in last 5 attempts:  0
    Migration Results for the last 24 hours: 
        1 : Success
  databases:
    {  "_id" : "app",  "primary" : "shard1",  "partitioned" : true }
        app.user
            shard key: { "id" : "hashed" }
            unique: false
            balancing: true
            chunks:
                replSet1  2
                replSet2  2
            { "id" : { "$minKey" : 1 } } -->> { "id" : NumberLong("-4611686018427387902") } on : shard1 Timestamp(2, 2) 
            { "id" : NumberLong("-4611686018427387902") } -->> { "id" : NumberLong(0) } on : shard1 Timestamp(2, 3) 
            { "id" : NumberLong(0) } -->> { "id" : NumberLong("4611686018427387902") } on : shard2 Timestamp(2, 4) 
            { "id" : NumberLong("4611686018427387902") } -->> { "id" : { "$maxKey" : 1 } } on : shard2 Timestamp(2, 5)
```   

``` 
# shard1
db.user.find().count() 
4992  
```

```
# shard2
db.user.find().count()
5007
```

```
# 如果在允许colletion前user有数据，执行以下命令时会报错。
sh.shardCollection('app.user', { id : 'hashed' })
{
    "proposedKey" : {
        "id" : "hashed"
    },
    "curIndexes" : [
        {
            "v" : 2,
            "key" : {
                "_id" : 1
            },
            "name" : "_id_",
            "ns" : "app.user"
        },
        {
            "v" : 2,
            "key" : {
                "id" : 1
            },
            "name" : "id_1",
            "ns" : "app.user"
        }
    ],
    "ok" : 0,
    "errmsg" : "please create an index that starts with the shard key before sharding."
}
做法是将collection删除，重建索引。
```
> chunksize默认是64M，如果分片过慢的话，可以适当调大，如512M。mongos的配置文件里。







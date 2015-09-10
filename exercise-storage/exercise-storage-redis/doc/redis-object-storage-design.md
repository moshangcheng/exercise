# Redis对象存储的设计

## 方案一

使用`string`存储序列化为JSON字符串的对象

```
INCR id:users
SET user:{id} '{"name":"Fred","age":25}'
SADD users {id}
```

优点

- 能快速访问对象的所有属性

缺点

- 无法访问对象的单个属性

## 方案二

使用`hash`存储未序列化的对象

```
INCR id:users
HMSET user:{id} name "Fred" age 25
SADD users {id}
```

优点

- 能快速访问对象的单个属性

缺点

- 访问对象所有属性时，相比方案一，对Redis的CPU负载很大，有数量级的性能差距
- 无法存储嵌套对象

## 方案三

将所有对象序列化存储到一个`hash`中

```
INCR id:users
HMSET users {id} '{"name":"Fred","age":25}'
```

该方案相比方案二没有优势，缺点有：

- 无法设置单个对象的TTL
- 如果对象数量庞大，无法做sharding

## 方案四

使用`string`存储单个对象的属性

```
INCR id:users
SET user:{id}:name "Fred"
SET user:{id}:age 25
SADD users {id}
```

该方案较为灵活，但是需要占据最多的存储空间

优点

- 可以设置单个属性的TTL
- 可以同时获取多个对象的属性

## 参考资料

- [Redis strings vs Redis hashes to represent JSON: efficiency?](http://stackoverflow.com/questions/16375188/redis-strings-vs-redis-hashes-to-represent-json-efficiency)
# 批量操作Redis的实例

批量操作Redis时，注意使用`SCAN`接口，而不是`KEYS`接口

## 实例

将所有未设置TTL的key设置TTL

```bash
#!/bin/bash

shopt -s extglob

if [ $# -ne 3 ]
then
  echo "set TTL for keys from Redis matching a pattern using SCAN & EXPIRE"
  echo "Usage: $0 <host> <port> <ttl>"
  exit 1
fi

cursor=-1
keys=""

while [ $cursor -ne 0 ]; do

  if [ $cursor -eq -1 ]; then
    cursor=0
  fi

  iter=0
  while read LINE; do
    if [ $iter -eq 0 ]; then
      cursor=$LINE
    else
      ttl=`redis-cli -h $1 -p $2 ttl $LINE`
      if [ $ttl -eq -1 ]; then
        echo $LINE `redis-cli -h $1 -p $2 expire $LINE $3`
      fi
    fi
    iter=$((iter+1))
  done < <(redis-cli -h $1 -p $2 SCAN $cursor)
done

```

参考资料

- [Redis的SCAN接口](http://redis.io/commands/scan)
- [How to atomically delete keys matching a pattern using Redis](http://stackoverflow.com/a/23399125)



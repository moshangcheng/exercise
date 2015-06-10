# Spark工具类

## 内容

对于Hadoop 2.x

- 缺少 `winutils.exe`
    - [Win32预编译版本](https://code.google.com/p/rrd-hadoop-win32)
    - [Win64预编译版本](https://github.com/srccodes/hadoop-common-2.2.0-bin)

对于Hadoop 1.x

- 文件无法设置权限
    - [Failed to set permissions](https://github.com/congainc/patch-hadoop_7682-1.0.x-win)

Spark 1.1.0使用的是Hadoop 1.x，Spark1.3.0使用的是Hadoop 2.x
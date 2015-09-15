# Python的编码问题

Python中用`str`类型表示原始字节，用`unicode`类型表示字符

- `str.decode`将`str`转换为`unicode`
- `unicode.encode`将`unicode`转换为`str`
- `str`中的编码类型由调用者决定
- 执行`str('a') + u'b'`，会对`str('a')`自动转换为`unicode`类型，并且使用默认的`ASCII`编码进行解码
- 因此`str('中国') + u'b'`会执行失败，因为`'中国'`不是ASCII字符

## 场景一

将如下代码：

```python
print repr('中国'[0])
```

保存为文件`x.py`，运行`python x.py`，可能的有哪些情况呢？

- 保存为`UTF-8`编码
	- `x.py`为`UTF-8`编码，运行出错`SyntaxError: Non-ASCII character '\xe4' in file x.py `
	- `x.py`为带BOM的`UTF-8`编码，运行结果为`'\xe4'`
	- `x.py`为`UTF-8`编码，并加上`# -*- coding: utf-8 -*-`声明，运行结果为`'\xe4'`
- 保存为`ASCII`编码，实际是native编码，由系统决定
	- `x.py`为`ASCII`编码，运行出错`SyntaxError: Non-ASCII character '\xd6' in file x.py`
	- `x.py`为`ASCII`编码，并加上`# -*- coding: utf-8 -*-`声明，运行结果为`'\xd6'`
	- `x.py`为`ASCII`编码，并加上`# -*- coding: gbk -*-`声明，运行结果为`'\xd6'`

结论

- 对于`str`类型的字符串字面量，python解释器看到字符与源文件编码相关
- 对于看到的字符，python解释器根据`# -*- coding: gbk -*-`或BOM头解释看到的字符
- 如果都没有指定编码，则使用`ASCII`字符集进行解释，遇到非法字符就抛出错误

## 场景二

将如下代码：

```python
print repr(u'中国'[0])
```

保存为文件`x.py`，运行`python x.py`，可能的有哪些情况呢？

- 保存为`UTF-8`编码
	- `x.py`为`UTF-8`编码，运行出错`SyntaxError: Non-ASCII character '\xe4' in file x.py `
	- `x.py`为带BOM的`UTF-8`编码，运行结果为`u'\u4e2d'`
	- `x.py`为`UTF-8`编码，并加上`# -*- coding: utf-8 -*-`声明，运行结果为`u'\u4e2d'`
- 保存为`ASCII`编码，实际是native编码，由系统决定
	- `x.py`为`ASCII`编码，运行出错`SyntaxError: Non-ASCII character '\xd6' in file x.py`
	- `x.py`为`ASCII`编码，并加上`# -*- coding: utf-8 -*-`声明，运行出错`SyntaxError: (unicode error) 'utf8' codec can't decode byte 0xd6`
	- `x.py`为`ASCII`编码，并加上`# -*- coding: gbk -*-`声明，运行结果为`u'\u4e2d'`

结论，除了与`str`类型字面量相似外，python解释器会尝试将源文件中其它编码类型的`unicode`类型字面量字符串转化为unicode编码


## 场景三

将如下代码：

```python
print '中国'
```

保存为文件`x.py`，运行`python x.py`，可能的有哪些情况呢？

- 保存为`UTF-8`编码
	- `x.py`为`UTF-8`编码，运行出错`SyntaxError: Non-ASCII character '\xe4' in file x.py `
	- `x.py`为带BOM的`UTF-8`编码，windows上运行结果为`涓浗`，Linux上运行结果为`中国`
	- `x.py`为`UTF-8`编码，并加上`# -*- coding: utf-8 -*-`声明，windows上运行结果为`涓浗`，Linux上运行结果为`中国`
- 保存为`ASCII`编码，实际是native编码，由系统决定
	- `x.py`为`ASCII`编码，运行出错`SyntaxError: Non-ASCII character '\xd6' in file x.py`
	- `x.py`为`ASCII`编码，并加上`# -*- coding: utf-8 -*-`声明，运行结果为`中国`
	- `x.py`为`ASCII`编码，并加上`# -*- coding: gbk -*-`声明，运行结果为`中国`

结论

- windows上的console不支持输出UTF-8字符

## 场景四

```python
print u'中国'
```

保存为文件`x.py`，运行`python x.py`，可能的有哪些情况呢？

- 保存为`UTF-8`编码
	- `x.py`为`UTF-8`编码，运行出错`SyntaxError: Non-ASCII character '\xe4' in file x.py `
	- `x.py`为带BOM的`UTF-8`编码，运行结果为`中国`
	- `x.py`为`UTF-8`编码，并加上`# -*- coding: utf-8 -*-`声明，运行结果为`中国`
- 保存为`ASCII`编码，实际是native编码，由系统决定
	- `x.py`为`ASCII`编码，运行出错`SyntaxError: Non-ASCII character '\xd6' in file x.py`
	- `x.py`为`ASCII`编码，并加上`# -*- coding: utf-8 -*-`声明，运行出错`SyntaxError: (unicode error) 'utf8' codec can't decode byte 0xd6 `
	- `x.py`为`ASCII`编码，并加上`# -*- coding: gbk -*-`声明，运行结果为`中国`

结论

- 对于`unicode`类型字符串字面量，不管在windows上还是Linux上都能正确运行和输出
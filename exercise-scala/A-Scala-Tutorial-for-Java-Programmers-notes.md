## [A Scala Tutorial for Java Programmers](http://docs.scala-lang.org/tutorials/scala-for-java-programmers.html)笔记

### HelloWorld程序

	package me.shu.exercise.scala.basic

	/**
	 * My first scala application
	 */
	object HelloWorld {
	  def main(args: Array[String]) = println("Hi!")
	}

- Scala没有静态成员、方法，它通过`object`关键字声明一个单例对象来达到同样的目的。
- 导入package时，Scala使用`_`代替`*`，因为在Scala中`*`是一个标识符的合法组成部分。

### 类

	class Complex(real: Double, imaginary: Double) {
	  def re() = real
	  def im() = imaginary
	}

- Scala的类有参数，可以当作构造函数使用。
- 因为`Unit`类型只有一个实例，所以返回值为`Unit`类型的函数相当于java里的`void`函数

### Pattern Matching

`case class`的样例

	abstract class Tree
    case class Sum(l: Tree, r: Tree) extends Tree
    case class Var(n: String) extends Tree
    case class Const(v: Int) extends Tree

- 创建`case class`的实例时，可以省略`new`关键字
- 构建器的参数会自动生成`getter`
- 会根据`case class`的结构自动创建`hashCode`和`equals`方法
- 默认的`toString`方法会提供`case class`的实例的字符串描述
- `case class`的实例可以在`pattern matching`中被分解

`pattern matching`的样例

	def eval(t: Tree, env: Environment): Int = t match {
    	case Sum(l, r) => eval(l, env) + eval(r, env)
	    case Var(n) => env(n)
	    case Const(v) => v
    }

使用`method`和使用`pattern matching`的比较：
- 当使用`method`时，如果需要添加新的操作符，需要修改所有已有的子类；如果添加新的节点，只需要添加一个子类。
- 当使用`pattern matching`时，如果需要添加新的操作符，只需要添加一个新的函数；如果添加新的节点，需要修改所有已有的函数。

### Traits

`trait`类似java里包含代码的`interface`。

`trait`的样例：

    trait Ord {
	    def < (that: Any): Boolean
	    def <=(that: Any): Boolean = (this < that) || (this == that)
	    def > (that: Any): Boolean = !(this <= that)
	    def >=(that: Any): Boolean = !(this < that)
    }

- `Any`是Scala里所有类型的父类
- `isInstanceOf`方法相当于java里的`instanceOf`操作符
- `asInstanceOf`方法相当于java里的转型操作符

### Generics

`Generic`的样例：

    class Reference[T] {
	    private var contents: T = _
	    def set(value: T) { contents = value }
	    def get: T = contents
    }

- `_`表示默认值，对于数值类型，它是`0`；对于对象类型，它是`null`；对于`Boolean`，它是`false`；对于`Unit`类型，它是`()`。
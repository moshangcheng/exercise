# 推荐系统练习

使用scala实现一个简单的推荐系统，数据集使用开源的数据集，算法使用基于物品的协同过滤算法，相似度选用Pearson相似度，并对推荐结果使用RMSE进行评估。

## 预备知识

- Scala基础知识
- 数据挖掘的基础知识
	- 训练数据集、测试数据集的概念
- 推荐系统的基本知识
	- 基于内容的推荐算法、基于协同过滤的推荐算法等
	- 冷启动问题
	- 推荐系统的评价指标
	- 参考文献
		- [推荐系统调研报告及综述](http://yongfeng.me/attach/rs-survey-zhang.pdf)
		- [推荐系统实践](http://book.douban.com/subject/10769749/)
- 协同过滤算法
	- 基于物品的协同过滤算法、基于用户的协同过滤算法
	- 不同相似度的定义
        - [协同过滤推荐及相似性度量](http://my.oschina.net/dillan/blog/164263)

## 基本流程

1. 根据Pearson相似度公式，计算训练数据集中物品的相似度，参考类为`SimilarityCalculator`
2. 使用物品的相似度文件，预测用户对未购买商品的评分，参考类为`Predictor`
3. 对比预测评分和测试集的用户评分数据，计算两者的RMSE；计算RMSE时，只考虑测试集中出现的评分项，参考类为`Evaluator`

## 数据集说明

- 使用[MovieLens](http://grouplens.org/datasets/movielens/)的数据作为训练数据集和测试数据集
- 使用最小的100k数据集
- 数据的格式为用户ID、物品ID、评分等，详细的格式参考网站文档
- 输入数据、中间结果都在当前目录的`data`子目录中

## 其它

- 工程使用`sbt`进行管理，在命令行里运行`sbt compile`进行编译
- 推荐使用`intellij IDEA`进行开发，最新版的`intellij IDEA`免费版安装Scala插件后可以直接导入`sbt`工程
- `SimilarityCalculator`、`Predictor`和`Evaluator`都是可以运行的，具体的实现待填充
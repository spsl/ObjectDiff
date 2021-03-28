# ObjectDiff

---
对比两个java对象的差异，支持原生类型，自定义类型，以及集合类型，采用Javassist动态字节码增强技术，动态生成比较类，使性能远高于反射方式比较。


## 目前已经实现的功能
1. 基础类型比较
2. 属性自定义类型递归比较   
3. 集合类型和数组类型比较
4. 生成的Differ类循环依赖问题
5. 对象循环依赖

## TODOs
1. 属性过滤
2. 属性黑名单和白名单过滤模式
3 自定义集合迭代和比较策略

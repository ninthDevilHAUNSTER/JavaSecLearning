# JAVA RMI 简介

 Java RMI，即 远程方法调用(Remote Method Invocation)，一种用于实现远程过程调用(RPC)(Remote procedure call)的Java API， 能直接传输序列化后的Java对象和分布式垃圾收集。它的实现依赖于Java虚拟机(JVM)，因此它仅支持从一个JVM到另一个JVM的调用。


> Impl 对应 implent ，实现的意思


![](https://upload-images.jianshu.io/upload_images/12696746-f390c4f4bc0e3949.jpg)

Registry(注册表)是放置所有服务器对象的命名空间。用接口来实现

每次服务端创建一个对象时，它都会使用bind()或rebind()方法注册该对象。
这些是使用称为绑定名称的唯一名称注册的。

要调用远程对象，客户端需要该对象的引用,如(RMIInterface)。
即通过服务端绑定的名称(RMIInterface)从注册表中获取对象(lookup()方法)。


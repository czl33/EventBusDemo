## 1.概述

![](https://img-blog.csdnimg.cn/20181208105518324.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0OTAyNTIy,size_16,color_FFFFFF,t_70)

EventBus是一个利用发布订阅模式做到模块组件间通信的第三方库，以往的通信方式常常选择 handle 以及 广播方式或是Intent Bundle传递，但是通过它们往往得增加大量的代码，组件间还容易产生耦合。


EventBus的优势：
>* 简化了组件间交流的方式
>* 对事件通信双方进行解耦
>* 可以灵活方便的指定工作线程，通过ThreadMode
>* 速度快，性能好
>* 能跨模块通信，但得保证bus是同一辆。

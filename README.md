# EventBus

## 1.概述

![](https://img-blog.csdnimg.cn/20181208105518324.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0OTAyNTIy,size_16,color_FFFFFF,t_70)

EventBus是一个利用发布订阅模式做到模块组件间通信的第三方库，以往的通信方式常常选择 handle 以及 广播方式或是Intent Bundle传递，但是通过它们往往得增加大量的代码，组件间还容易产生耦合。


EventBus的优势：
>* 简化了组件间交流的方式
>* 对事件通信双方进行解耦
>* 可以灵活方便的指定工作线程，通过ThreadMode
>* 速度快，性能好
>* 能跨模块通信，但得保证bus是同一辆。

## 2.使用

### EvenntBus使用主要理解3个角色：
> 1.**subscriber（订阅者）**：对消息进行订阅的角色

> 2.**Event(消息)**  ： 传输的内容，bus上的乘客

> 3.**publisher(发布者)** ：发送消息的角色

------------------------
### OnEvent方法的注意事项

>* 该方法有且只有一个参数。
>* 该方法必须是public修饰符修饰，不能用static关键字修饰，不能是抽象的（abstract）
>* 该方法需要用@Subscribe注解进行修饰。

------------------------

### 使用中的第一种情况（发送方以及接收方都已注册）：

从oneActivity将数据通过EventBus 传送到各个注册了EventBus的组件，并触发对应的onEvent(Message message)方法

***注意：***
>* *1.接收方一定要注册EventBus，但也只限于第一种情况*
>* *2.注册完了，在不用的时候千万别忘了unregister。*
>* *3.不能重复注册。注册之后，没有unregister，然后又注册了一次*
>* *4.register与unregister的时间根据实际需求来把控，官方的例子是在onStart（）回调方法进行注册，onStop()回调方法进行unregister（）*
>* *5.发送方不一定要注册EventBus*
>* *6.传输的消息一定对应，也就是OnEvent方法所入的形参*
    
![](http://newczl.cn:8098/1.jpg)
--------------------------------------
### 使用中的第二种情况 (发送方需要发送给一个未注册的接收方)

***使用场景：***
我们要把一个Event发送到一个还没有初始化的Activity/Fragment，即尚未订阅事件。那么如果只是简单的post一个事件，那么是无法收到的，这时候，你需要用到粘性事件,它可以帮你解决这类问题

从oneActivity 通过发送一个 粘性消息给到TwoActivity 可以实现发送消息到一个延迟注册的组件中。

![](http://newczl.cn:8098/2.jpg)


***注意：***
>* *粘性消息暂存内存中，如果某些组件需要如果认为这个粘性消息已使用完，请进行remove，如未进行删除，以免它在传递下去*

-------------------------
### 使用中的第三种情况 （接收方需要按照优先级来进行接收）

***使用场景：***
如果应用程序在前台，事件可能会触发一些UI逻辑，但如果应用程序当前对用户不可见，则会做出不同的反应。

通常优先级被高优先级的处理事件方法接收，高优先级接收到消息后 做一些判断 来确认是否需要继续传递下去，如若不需要则 cancelEventDelivery(Object event) 来取消事件交付过程。

![](http://newczl.cn:8098/3.jpg)

***注意：***
>* *1.在同一个交付线程(ThreadMode)中，优先级较高的订阅者将先于优先级较低的订阅者接收事件*
>* *2.默认优先级为0，"priority" 的值越大，优先级越高。*


---------------------------------------------------------------------------

## 3.编码实现


1.首先gradle引入,并建立一个注解名为@BindEventBus的注解，方便标识需要绑定的Activity，因为一般都是动态注册EventBus，基类直接注册可能会有各种问题。
```java
  implementation 'org.greenrobot:eventbus:3.2.0'
  //建立注解
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface BindEventBus {}
```
2.为了方便，这里建立一个基类BaseActivity，并通过查询是否注解来动态注册EventBus
```java
/**
 * @desc Activity基类
 * @author czl 
 * @date 2020-7-20
*/
abstract class BaseActivity extends AppCompatActivity {

    protected TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewId());
        textView = findViewById(R.id.textView);
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBus.getDefault().unregister(this);
        }
    }

    abstract int getViewId();

    protected void showToast(String toast){
        Toast.makeText(this,toast,Toast.LENGTH_LONG).show();
    }

}
```
3.建立通信的消息类型，可以多建几个来标识 不同的事件。
```
public class Message1 {
    private long time;
    private String message;

    public Message1(long time, String message) {
        this.time = time;
        this.message = message;
    }
    public Message1( String message) {
        this.time = System.currentTimeMillis();
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat f = new SimpleDateFormat( "HH:mm:ss");
        return String.format(Locale.CHINA,"消息：%s,时间：%s",message,f.format(new Date(time)));
    }
}
```
4.在入口建立一个接受message1的消息事件,既然用到了@Subscribe注解，那就详细说下
```
/**
 * @desc MainActivity
 * @author czl 
 * @date 2020-7-20
*/
@BindEventBus
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    int getViewId() {
        return R.layout.activity_main;
    }
    
    //1.注解中可以包含3种参数，分别是threadMode（工作线程），sticky（是否粘性），priority（优先级）
    //  分别对应了我的3种情况，每个参数都可以不填，即有默认值 threadMode 默认POSTING 模式 即post在哪个
    //  线程，工作就在哪个线程啊,stickey默认false，优先级默认0.
    //2.工作线程包含5种，MAIN，MAIN_ORDERED，BACKGROUND，ASYNC，POSTING，MAIN_ORDERED执行是非阻塞的。
    //  ASYNC和BACKGROUND都代表在子线程中执行
    //3.优先级的话越大优先级越高.

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage1(Message1 message1){
        textView.setText(message1.toString());
    }
    
    public void postSticky(View view) {
        showToast("发送粘性消息");
        EventBus.getDefault().postSticky(new Message2("我是粘性消息"));
    }
}

```
5.第一个情况的实现很简单，只需要在OneActivity中post事件Message1 MainActivity即可响应
第二种情况 当MainActivity发送粘性消息 在OneActivity注册时  也能收到这个粘性消息并做出处理
```java
OneActivity.class 

@BindEventBus
public class OneActivity extends BaseActivity {

    public void post(View view) {
        EventBus.getDefault().post(new Message1("默认消息已发送"));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getStickyMessage(Message2 myStickyMessage){
        textView.setText(myStickyMessage.toString());
        //收到粘性消息，可以对粘性消息进行清除，可以根据项目情况进行针对性处理，可以取消也可保留
         1.EventBus.getDefault().removeAllStickyEvents();
         2. EventBus.getDefault().removeStickyEvent(myStickyMessage);
         3. EventBus.getDefault().removeStickyEvent(Message2.class);
        //避免下一次进入重复收到或让其他注册相同事件的函数进行处理
        /
    }

    @Override
    int getViewId() {
        return R.layout.activity_one;
    }
}
```
6.第三种情况，即优先级策略，高优先级可以取消同ThreadModes的订阅者的事件，注意事项见代码
```java
@BindEventBus
public class TwoActivity extends BaseActivity {
    TextView textView2;
    TextView textView3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
    }

    public void post(View view) {
        EventBus.getDefault().post(new Message3("我是优先级消息啊"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getStickyMessage(Message2 myStickyMessage){
        textView.setText(myStickyMessage.toString());
        EventBus.getDefault().removeAllStickyEvents();
    }

    @Subscribe(threadMode = ThreadMode.POSTING,priority = 2)
    public void getPriorityMessageHigh(Message3 myMessage){
        textView2.setText(myMessage.toString());
        //只有接收在POSTING下才可以使用取消事件传递
        //priority只会影响同线程下的传递顺序
        //优先级不会影响具有不同ThreadModes的订阅者的传递顺序！
        EventBus.getDefault().cancelEventDelivery(myMessage);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 1)
    public void getPriorityMessage(Message3 myMessage){
        textView3.setText(myMessage.toString());
    }

    @Override
    int getViewId() {
        return R.layout.activity_two;
    }
}
```
源码见：https://github.com/czl33/EventBusDemo

EventBus设计图：

![](https://img-blog.csdnimg.cn/20181215135411964.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0OTAyNTIy,size_16,color_FFFFFF,t_70)


## 4..参考

> 源码解析 ：https://blog.csdn.net/qq_34902522/article/details/85013185

# learn-netty
## 笔记入门01 - 什么是NIO
#### 一、什么是NIO

- Java NIO全称java non-blocking IO， 是指JDK提供的新API。从JDK1.4开始，Java提供了一系列改进的输入/输出的新特性，被统称为NIO(即New IO)，是同步非阻塞的

-  NIO有三大核心部分: **Channel(通道)， Buffer(缓冲区),Selector(选择器)**

- NIO是面向缓冲区，或者面向块编程的。数据读取到一个它稍后处理的缓冲区，需要时可在缓冲区中前后移动，这就增加了处理过程中的灵活性，使用它可以提供非阻塞式的高伸缩性网络。

#### 二、NIO 与BIO 模型对比

> BIO 是同步阻塞IO,服务器的模式是一个线程处理一个请求，当无响应时，会阻塞线程

> NIO 同步非阻塞IO,会有一个Selector管理多个线程，当有事件发生后，进行处理、不会发生阻塞

![image-20210502094415803](https://gitee.com/liuzihao169/pic/raw/master/image/20210502094420.png)

#### 三、NIO 与BIO的差异

1、BIO 以流的方式处理数据,而NIO以块的方式处理数据,块I/O 的效率比流I/O高很多
2、BIO 是阻塞的，NIO则是非阻塞的
3、 BIO基 于字节流和字符流进行操作，而NIO 基于Channel(通道)和Buffer(缓冲区)进行操作，数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中。Selector(选择器)用于监听多个通道的事件(比如:连接请求，数据到达等)，因此使用单个线程就可以监听多个客户端通道

#### 四、什么是Buffer(缓冲区)

**缓冲区(Buffer)**  ： 缓冲区本质上是一个可以读写数据的内存块，可以理解成是一个容器对象(含数组)，该对象提供了一组方法，可以更轻松地使用内存块，，缓冲区对内置了一些机制，能够跟踪和记录缓冲区的状态变化情况。Channel 提供从文件、网络读取数据的渠道，但是读取或写入的数据都必须经由Buffer。

##### 在buffer类中都有4个属性

| Capacity | 容量，即可以容纳的最大数据量                                 |
| -------- | ------------------------------------------------------------ |
| Limit    | 表示缓冲区的当前终点，不能对缓冲区超过极限的位置进行读写操作。且极限是可以修改的 |
| Position | 位置，下一个要被读或写的元素的索引，                         |
| Mark     | 标记                                                         |

#### 五、什么是Channel(通道)

NIO的通道类似与流但是又有区别：通道可以进行读写，而流只能读或者写；通道可以支持异步读写。

#### 六、什么是Selector(选择器)

Selector能够检测多个注册的通道上是否有事件发生(**多个Channel以事件的方式可以注册到同一个Selector**)

- 如果有事件发生，便获取事件(通过selectKey)然后针对每个事件进行相应的处理。这样就可以只用一个单线程去管理多个通道，也就是管理多个连接和请求。

- 只有在连接真正有读写事件发生时，才会进行读写，就大大地减少了系统开销，并且不必为每个连接都创建一个线程， 不用去维护多个线程避免了多线程之间的上下文切换导致的开销

**核心流程：**

1、注册通道  2、监听通道 3、获取通道 执行业务逻辑

#### 案例

**服务端**

```java
public class NIOServer {

    public static void main(String[] args) throws Exception{

        final int port = 8888;

        // 相当于 ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 获得selector
        Selector selector = Selector.open();

        // serverSocketChannel 注册到selector当中 OP_ACCEPT连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 循环等待连接处理
        while (true){

            if (0 == selector.select(1000)){
                System.out.println("等待连接....");
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 获取到所有的注册key
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();

            if (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();

                // 连接事件处理
                if (selectionKey.isAcceptable()) {
                    // 获得通道
                    SocketChannel accept = serverSocketChannel.accept();
                    // 设置非阻塞
                    accept.configureBlocking(false);
                    // 注册到selector中，并设置为读事件
                    accept.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                    System.out.println("客户端连接成功....生成socketChannel" + accept.hashCode());
                }

                // 读事件处理
                if (selectionKey.isReadable()) {


                    SocketChannel channel = (SocketChannel)selectionKey.channel();
                    System.out.println("读事件发生....."+channel.hashCode());
                    ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                    channel.read(buffer);

                    System.out.println("读取到客户端到数据...."+new String(buffer.array()));
                }
                // key移除操作
                selectionKeyIterator.remove();
            }

        }



    }
}
```



**客户端**

```java
public class NIOClient {

    public static void main(String[] args) throws Exception{
        // 获得一个Channel
        SocketChannel socketChannel = SocketChannel.open();

        if (!socketChannel.connect(new InetSocketAddress("127.0.0.1",8888))) {
            System.out.println("连接中.....");
        }

        if (!socketChannel.finishConnect()){
            System.out.println("等待连接...");
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap("hello world....".getBytes());

        socketChannel.write(byteBuffer);

        System.out.println("客户端发送完成...");

    }
}
    
```


#### Demo1 - 缓冲区读写

```java
  // 输入流获取管道
        FileInputStream fileInputStream = new FileInputStream(new File("/Users/liuzihao/Downloads/niofile.txt"));
        FileChannel channel = fileInputStream.getChannel();
        
        // 输出流获取管道
        FileOutputStream fileOutputStream = new FileOutputStream(new File("/Users/liuzihao/Downloads/niofile1.txt"));
        FileChannel outChannel = fileOutputStream.getChannel();
        
        // 缓冲区
        ByteBuffer allocate = ByteBuffer.allocate(1);
        
        while (true) {
            // 清除标记位置
            allocate.clear();
            // 将输入流通道里数据 读取到缓冲区
            int read = channel.read(allocate);
            // 数据读取完 跳出
            if (read<= -1){
                break;
            }
            // 转换读写模式
            allocate.flip();
            // 写到输出通道中
            outChannel.write(allocate);
        }
        
        // 关闭流操作
        fileInputStream.close();
        fileOutputStream.close();
```




## 笔记入门02 - 什么是Netty

### 一、Netty是什么

Netty 是一个利用 Java 的高级网络的能力，隐藏其背后的复杂性而提供一个易于使用的 API 的客户端/服务器框架。

> 本质：网络应用程序框架
>
> 实现：异步、事件驱动
>
> 特性：高性能、可维护、快熟开发
>
> 用途：开发服务器和客户端。
>
> 框架：dubbo

#### 1.1为什么不使用NIO

> 支持常用应用层协议
>
> 解决传输的问题：粘包、半包现象
>
> 支持流量整形
>
> 完善的断连、ldle等异常处理

#### 1.2 netty 为什么删除已经支持的AIO

#### 1.3 I/o模型

io模型的理解：就是用什么样的通道进行数据发送和接收，很大程度上决定了通信性能。

BIo: 阻塞io 模型，一个连接进行一个服务的处理，如果没有结束会一直等待

Nio: 非阻塞io模型，一个线程可以处理多个请求io,客户端发送的请求会注册到多路复用器上，多路复用就一个线程检查连接的状态

AIo: 异步i o 服务端装备好数据后，会主动通知客户端拿获取数据

#### 1.4 NIO 存在的问题

1、NIO的类库和API繁杂，使用麻烦:需要熟练掌握Selector、 ServerSocketChannel、SocketChannel、 ByteBuffer

2、需要具备其他的额外技能:要熟悉Java多线程编程，因为NIO编程涉及到Reactor 模式，你必须对多线程.和网络编程非常熟悉，才能编写出高质量的NIO程序。

3、开发工作量和难度都非常大:例如客户端面临断连重连、网络闪断、半包读写、失败缓存、网络拥塞和异常流的处理等等。

4、JDKNIO 的Bug: 例如臭名昭著的Epoll Bug,它会导致Selector 空轮询,最终导致CPU 100%。直到JDK 1.7
版本该问题仍旧存在，没有被根本解决。

### 二、线程模型

#### 2.1 传统I/O 阻塞模型

特点：

一个请求过来，一个线程连接处理；采用阻塞式IO获取输入数据；

如图：

![image-20210509085934194](https://gitee.com/liuzihao169/pic/raw/master/image/20210509085937.png)

缺点：线程数多，服务器压力大，造成资源浪费；阻塞式io，线程利用率不高；

#### 2.2 Reactor模式

基于传统i/o 模型的缺点，有两种解决方案：

1、基于IO复用模型：多个连接共用一个阻塞对象，应用程序只需要在**一个阻塞对象等待**，无需阻塞等待所有连接。当某个连接有新的数据可以处理时，操作系统通知应用程序，线程从阻塞状态返回，开始进行业务处理。

2、基于线程池复用模型：不必再为每个连接创建线程，将连接完成后的业务处理任务分配给线程进行处理，
一个线程可以处理多个连接的业务。

**Reactor模式就是： I/O复用 + 线程池**模型 如下图：

![image-20210509094517688](https://gitee.com/liuzihao169/pic/raw/master/image/20210509094522.png)

Reactor模式是基于事件驱动，应用程序收到不同的事件后，分发给不同的事件进行处理；**Reactor模式采用I/O复用监听事件，然后分发处理，这是netty高性能的关键**

- （转发handler）reactor 负责分发 
- handler 负责处理

##### 2.2.1 根据reactor模式的分类

a.单reactor 单线程模式  b.单reactor多线程模式。c.主从reactor多线程模式

###### a.单reactor单线程

![image-20210509110037496](https://gitee.com/liuzihao169/pic/raw/master/image/20210509110040.png)

流程说明：

如果是建立连接请求：则交给Acceptor来进行连接处理；否者创建一个handler进行处理；

缺点：单个线程性能比较差



###### b.单reactor多线程

![image-20210509110143213](https://gitee.com/liuzihao169/pic/raw/master/image/20210509110149.png)

流程说明：

如果是建立连接请求：则交给Acceptor来进行连接处理；否者创建一个handler进行处理,但是handler不进行业务处理，只负责接收和响应，真正的业务操作会交给Worka线程来进行处理。

缺点：所有的响应和发送都是Reactor进行，在高并发场景容易出现性能瓶颈



###### c.多线程多reactor模式

![image-20210509110226226](https://gitee.com/liuzihao169/pic/raw/master/image/20210509110229.png)

流程说明：

**主要区别就是 主reactor负责连接，从reactor负责请求处理，然后分发；**

1) Reactor 主线程MainReactor 对象通过select监听连接事件,收到事件后，通过Acceptor处理连接事件
2)当Acceptor处理连接事件后，MainReactor将连接分配给SubReactor
3)
subreactor将连接加入到连接队列进行监听，并创建handler进行各种事件处理
4)当有新事件发生时，subreactor就会调用对应的handler处理
5) handler 通过read读取数据，分发给后面的worker线程处理
6)worker线程池分配独立的worker线程进行业务处理，并返回结果

#### 2.3 小结 reactor模式的优点

- 响应快，不必为单个同步时间所阻塞，虽然Reactor本身依然是同步的
- 可以最大程度的避免复 杂的多线程及同步问题，并且避免了多线程/进程的切换开销
- 扩展性好，可以方便的通过增加Reactor 实例个数来充分利用CPU资源
- 复用性好， Reactor 模型本身与具体事件处理逻辑无关，具有很高的复用性

### 三、Netty模型

netty模型主要是基于 主从多线程reactor模型，简易模型如下：

![image-20210509113032203](https://gitee.com/liuzihao169/pic/raw/master/image/20210509113041.png)

**上图工作流程**

1、BossGroup 线程维护Selector，只关注Accecpt
2、当 接收到Accept事件，获取到对应的SocketChannel,封装成NIOScoketChannel 并注册到Worker线程(事件循环)，并进行维护
3、当Worker线程监听到selector中通道发生自己感兴趣的事件后，就进行处理(就由handler)，注意handler已经加入到通道

#### 3.1 netty的Reactor模式图解

![image-20210509123810418](https://gitee.com/liuzihao169/pic/raw/master/image/20210509123816.png)

1.Netty有两组线程池**BossGroup**专门负责接收客户端的连接，**WorkerGroup**专门负责网络的读写。类型都是NioEventIoopGroup
2.NioEventLoopGroup 相当于一一个事件循环组，这个组中含有多个事件循环，每一个事件循环是NioEventLoop .
3.NioEventLoop 表示一个不断循环的执行处理任务的线程，每个NioEventLoop都有一个selector,用于监听绑定在其上的socket的网络通讯

Boss NioEventLoop循环执行的步骤有3步：

- 轮询accept 事件、
- 处理accept事件，与 client建立连接，生 成NioScocketChannel，并将其注册到某个worker NIOEventLoop、
- 处理任务队列的任务，即runAllTasks

Worker NIOEventLoop循环也是3步骤：

- 轮询read, write事件、

- 处理i/o事件，即 read，write事件，在对应NioScocketChannel处理
- 处理任务队列的任务，即runAllTasks

4.每个Worker NIOEventLoop处 理业务时，会使用pipeline(管道), pipeline中包含了channel ，即通过pipeline
可以获取到对应通道，管道中维护了很多的处理器.

### 四、netty Demo 实践

代码介绍：

- NettyServer.java 服务器端
- NettyServerHandler.java 服务器端处理器
- NettyClient.java 客户端
- NettyClientHandler.java 客户端处理器



**NettyServer.java**

```java
/**
 * @author liuzihao
 * @create 2021-05-09-15:24
 * netty客户端
 */
public class NettyServer {

    public static void main(String[] args) throws Exception{

        /**
         * 创建两个线程组合 bossGroup workGroup 如上图所介绍
         * bossGroup; 只负责连接请求
         * workGroup; 真正和客户段业务处理
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workGroup = new NioEventLoopGroup();

        // 创建服务器的启动参数配置类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {

            serverBootstrap.group(bossGroup, workGroup)
                    // 指定类型为 NioServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列连接个数 108
                    .option(ChannelOption.SO_BACKLOG, 108)
                    // 设置保持连接活动状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 初始化业务handler 如图处理业务到handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 往管道加入业务处理handler
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            System.out.println("服务器端初始化完成 ....");

            // 绑定端口 获取异步对象
            ChannelFuture sync = serverBootstrap.bind(8888).sync();

            // 异步监听关闭通道
            sync.channel().closeFuture().sync();

        }finally {
            // 发生异常优雅关闭
            bossGroup.shutdownGracefully();
        }
    }
}

```

**NettyServerHandler.java**

```java
/**
 * @author liuzihao
 * @create 2021-05-09-15:24
 * 服务端业务处理
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 从通道中读取数据；获取客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(" 服务端接收到客户端的消息>>>>" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println(" 服务端接收到客户端的地址为>>>>" + ctx.channel().remoteAddress());


    }

    /**
     * 获取数据完成
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println(" 服务端 处理完客户端的消息>>>>并进行回复");
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello im doing ", CharsetUtil.UTF_8));
    }

    /**
     * 发生异常之后 直接关闭
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
```

**NettyClient.java**

```java
/**
 * @author liuzihao
 * @create 2021-05-09-15:24
 * netty 客户端
 */
public class NettyClinet {

    public static void main(String[] args) throws Exception{

        // 客户端只需要一个 group
        EventLoopGroup group = new NioEventLoopGroup();

        // 设置相关属性
        Bootstrap bootstrap = new Bootstrap();
        try {

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClinetHandler());
                        }
                    });

            System.out.println("客户端 准备好了...");

            ChannelFuture sync = bootstrap.connect("127.0.0.1", 8888).sync();

            // 异步监听关闭通道
            sync.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }
    }
}
```

**NettyClinetHandler**

```java
/**
 * @author liuzihao
 * @create 2021-05-09-15:25
 * 客户端 handler
 */
public class NettyClinetHandler extends ChannelInboundHandlerAdapter {

    /**
     * 通道连接之后
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("客户端发送端数据>>>>>>hello server !!!!", CharsetUtil.UTF_8));
    }

    /**
     * 读取通道的数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(" 客户端接收服务端的消息>>>>" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println(" 客户端接收服务端的消息>>>>" + ctx.channel().remoteAddress());
    }

    /**
     * 发生异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

```






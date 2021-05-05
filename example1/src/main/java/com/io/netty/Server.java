package com.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author liuzihao
 * @create 2021-04-29-20:10
 */
public class Server {

    public static final String HOST = "127.0.0.1";
    public static final int PORT=8080;

    public void run() throws Exception {
        // 配置服务端的NIO线程组
        // 主线程组, 用于接受客户端的连接，但是不做任何具体业务处理，像老板一样，负责接待客户，不具体服务客户
        EventLoopGroup boss = new NioEventLoopGroup(1);
        // 工作线程组, 老板线程组会把任务丢给他，让手下线程组去做任务，服务客户
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(boss, worker)  //配置boss和worker
                    .channel(NioServerSocketChannel.class)   //配置Server通道
                    .childHandler(new ChildChannelHandler());  //配置通道的ChannelPipeline

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = server.bind(PORT).sync();

            System.out.println("服务器启动完成，监听端口： " + PORT);

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            channel.pipeline().addLast(new ServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        new Server().run();
    }
}

package com.io.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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

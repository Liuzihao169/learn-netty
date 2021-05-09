package com.io.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

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

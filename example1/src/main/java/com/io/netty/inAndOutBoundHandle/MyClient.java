package com.io.netty.inAndOutBoundHandle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyClient {
    public static void main(String[] args)  throws  Exception{

        EventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入一个出站的handler 对数据进行一个编码
                            pipeline.addLast(new MyLongToByteEncoder());
                            //这时一个入站的解码器(入站handler )
                            pipeline.addLast(new MyByteToLongDecoder());
                            //加入一个自定义的handler ， 处理业务
                            pipeline.addLast(new MyClientHandler());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("localhost", 8888).sync();

            channelFuture.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }
    }
}

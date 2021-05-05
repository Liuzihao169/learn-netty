package com.io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @author liuzihao
 * @create 2021-04-29-20:09
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for (int i = 0; i < 3; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("这是一个Netty示例程序！\n", CharsetUtil.UTF_8));
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        System.out.println("客户端接收到消息： " + in.toString(CharsetUtil.UTF_8));
        ctx.write(in.toString(CharsetUtil.UTF_8) );
        System.out.println("客户端...channelRead0..");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("客户端...exceptionCaught..");
        cause.printStackTrace();
        ctx.close();
    }
}

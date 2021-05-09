package com.io.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

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

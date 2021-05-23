package com.io.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

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
       // 传递到下一个handler
        ctx.fireChannelRead(msg);
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

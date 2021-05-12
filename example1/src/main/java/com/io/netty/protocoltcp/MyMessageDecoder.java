package com.io.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 解码器
 */
public class MyMessageDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyMessageDecoder decode 被调用; 入站解码 ");
        //需要将得到二进制字节码-> MessageProtocol 数据包(对象)
        int i = in.readableBytes();
        System.out.println("ByteBuf可处理字节："+ i );
        int length = in.readInt();
        System.out.println("readInt可处理长度："+ length );
        System.out.println("readInt后可处理字节" + in.readableBytes());
        byte[] content = new byte[length];
        in.readBytes(content);
        System.out.println("写入content后剩余字节" + in.readableBytes());
        //封装成 MessageProtocol 对象，放入 out， 传递下一个handler业务处理
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setLen(length);
        messageProtocol.setContent(content);

        out.add(messageProtocol);

    }
}

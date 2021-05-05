package com.io.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author liuzihao
 * @create 2021-05-05-10:10
 * nio客户端
 */
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

        System.in.read();


    }
}

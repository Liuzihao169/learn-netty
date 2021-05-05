package com.io.netty.nio;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author liuzihao
 * @create 2021-05-05-16:07
 */
public class NIOCopyServer {

    public static void main(String[] args) throws Exception{

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.socket().bind(new InetSocketAddress(8888));

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println(Thread.currentThread().getName()+"线程....");
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            socketChannel.read(allocate);
            System.out.println(new String(allocate.array()));
        }



    }
}

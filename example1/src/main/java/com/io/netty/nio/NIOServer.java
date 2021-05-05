package com.io.netty.nio;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author liuzihao
 * @create 2021-05-05-09:26
 * nio服务端
 */
public class NIOServer {

    public static void main(String[] args) throws Exception{

        final int port = 8888;

        // 相当于 ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 获得selector
        Selector selector = Selector.open();

        // serverSocketChannel 注册到selector当中 OP_ACCEPT连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 循环等待连接处理
        while (true){

            if (0 == selector.select(1000)){
                System.out.println("等待连接....");
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 获取到所有的注册key
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();

            if (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();

                // 连接事件处理
                if (selectionKey.isAcceptable()) {
                    // 获得通道
                    SocketChannel accept = serverSocketChannel.accept();
                    // 设置非阻塞
                    accept.configureBlocking(false);
                    // 注册到selector中，并设置为读事件
                    accept.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                    System.out.println("客户端连接成功....生成socketChannel" + accept.hashCode());
                }

                // 读事件处理
                if (selectionKey.isReadable()) {


                    SocketChannel channel = (SocketChannel)selectionKey.channel();
                    System.out.println("读事件发生....."+channel.hashCode());
                    ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                    channel.read(buffer);

                    System.out.println("读取到客户端到数据...."+new String(buffer.array()));
                }
                // key移除操作
                selectionKeyIterator.remove();
            }

        }



    }
}

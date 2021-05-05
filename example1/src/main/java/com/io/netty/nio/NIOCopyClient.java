package com.io.netty.nio;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @author liuzihao
 * @create 2021-05-05-16:07
 */
public class NIOCopyClient {
    public static void main(String[] args) throws Exception{

        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.connect(new InetSocketAddress("127.0.0.1",8888));
        File file = new File("/Users/liuzihao/Downloads/niofile.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        FileChannel channel = fileInputStream.getChannel();
        channel.transferTo(0, channel.size(), socketChannel);


    }
}

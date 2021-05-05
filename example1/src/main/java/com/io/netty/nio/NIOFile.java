package com.io.netty.nio;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author liuzihao
 * @create 2021-05-01-10:57
 */
public class NIOFile {
    public static void main(String[] args) throws Exception{
        // 指定输出位置
        FileOutputStream fileOutputStream = new FileOutputStream(new File("/Users/liuzihao/Downloads/niofile.txt"));

        // 获得通道
        FileChannel channel = fileOutputStream.getChannel();

        /**
         * 创建缓冲区，并输入数据
         */
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        allocate.put("hello world".getBytes());

        // 读写模式反转
        allocate.flip();

        // 将缓冲区的数据 写入通道
        channel.write(allocate);

        // 关闭流
        fileOutputStream.close();
    }
}

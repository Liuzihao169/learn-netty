package com.io.netty.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author liuzihao
 * @create 2021-05-01-11:15
 */
public class NIOFile2 {
    public static void main(String[] args) throws Exception{

        // 输入流获取管道
        FileInputStream fileInputStream = new FileInputStream(new File("/Users/liuzihao/Downloads/niofile.txt"));
        FileChannel channel = fileInputStream.getChannel();

        // 输出流获取管道
        FileOutputStream fileOutputStream = new FileOutputStream(new File("/Users/liuzihao/Downloads/niofile1.txt"));
        FileChannel outChannel = fileOutputStream.getChannel();

        // 缓冲区
        ByteBuffer allocate = ByteBuffer.allocate(1);

        while (true) {
            // 清除标记位置
            allocate.clear();
            // 将输入流通道里数据 读取到缓冲区
            int read = channel.read(allocate);
            // 数据读取完 跳出
            if (read<= -1){
                break;
            }
            // 转换读写模式
            allocate.flip();
            // 写到输出通道中
            outChannel.write(allocate);
        }

        // 关闭流操作
        fileInputStream.close();
        fileOutputStream.close();
    }
}

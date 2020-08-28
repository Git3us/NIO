/**
 * @Auther : CoderEus
 * @Date : 2020/8/22 15:50:06
 * @Description : //TODO
 */

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 一、缓冲区（buffer）：在 JAVA NIO 中负责数据的存取，缓冲区的底层就是数组用于存储不同数据类型的数据
 * <p>
 * 所以根据数据类型的不同，提供了对应类型的缓冲区（Boolean 除外）
 * 如：ByteBuffer、CharBuffer、ShortBuffer、IntBuffer、LongBuffer、FlotBuffer、DoubleBuffer
 * <p>
 * 上述缓冲区的管理方式几乎一致，都是通过 allocate() 来获取一个缓冲区，其中最常用的是 ByteBuffer
 * <p>
 * 二、缓冲区存取数据的两个核心方法：
 * put()：存入数据到缓冲区中
 * get()：获取缓冲区中的数据
 */
public class TestBuffer {

    @Test
    public void test02() {
        //分配直接缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

    }

    @Test
    public void test01() {

        //1.分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        System.out.println("-------------------------------");
        System.out.println(buf.position());     //0
        System.out.println(buf.limit());        //1024
        System.out.println(buf.capacity());     //1024

        //2.利用 put() 存入数据到缓存区中
        String str = "abcde";
        buf.put(str.getBytes());

        System.out.println("-------------------------------");
        System.out.println(buf.position());     //5
        System.out.println(buf.limit());        //1024
        System.out.println(buf.capacity());     //1024

        buf.flip();
        System.out.println("-------------------------------");
        System.out.println(buf.position());     //0
        System.out.println(buf.limit());        //5
        System.out.println(buf.capacity());     //1024


        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
        System.out.println("-------------------------------");
        System.out.println(new String(bytes, 0, bytes.length));
        System.out.println(buf.position());     //5
        System.out.println(buf.limit());        //5
        System.out.println(buf.capacity());     //1024
    }
}

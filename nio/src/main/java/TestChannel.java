import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

/**
 * @Auther : CoderEus
 * @Date : 2020/8/26 23:48:05
 * @Description : //TODO
 */
public class TestChannel {
    //利用通道完成文件的复制
    @Test
    public void test01() throws IOException {
        FileInputStream fis = new FileInputStream("1.jpg");
        FileOutputStream fos = new FileOutputStream("2.jpg");

        //获取通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        //分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //将通道中的数据存入缓冲区中
        while (inChannel.read(buf) != -1) {
            //要记得将缓冲区切换成读模式
            buf.flip();
            //将缓冲区中的数据写入通道中
            outChannel.read(buf);
            buf.clear();    //清空缓冲区
        }

        //操作完以后要记得关闭通道
        outChannel.close();
        inChannel.close();
        fis.close();
        fos.close();
    }

    @Test
    public void test02() throws IOException {
        //第一个参数 path，即文件的路径（文件路径可以使用逗号隔开，表示对路径进行拼接）
        //第二个参数 options，即想要对文件进行的操作
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);

        //该方法原理与 allocateDirect() 方法一样，只不过获取方式不一样而已
        MappedByteBuffer inMapBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMapBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, outChannel.size());

        //直接对缓冲区进行数据的读写操作
        byte[] dst = new byte[inMapBuffer.limit()];
        inMapBuffer.get(dst);
        outMapBuffer.put(dst);

        inChannel.close();
        outChannel.close();
    }

    @Test
    public void test03() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("4.jpg"), StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);

        inChannel.transferTo(0, inChannel.size(), outChannel);

        inChannel.close();
        outChannel.close();
    }

    @Test
    public void test04() throws IOException {

        //分散读取
        RandomAccessFile raf1 = new RandomAccessFile("1.txt", "rw");

        //获取通道
        FileChannel channel = raf1.getChannel();

        //分配指定大小的缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);

        //分散读取
        ByteBuffer[] bufs = {buf1, buf2};
        channel.read(bufs);

        for (ByteBuffer buf : bufs) {
            buf.flip();
        }

        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
        System.out.println("--------------------------------------------------------");
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

        //聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("2.txt", "rw");
        FileChannel channel2 = raf2.getChannel();

        channel2.write(bufs);

        channel.close();
        channel2.close();

    }

    @Test
    public void test05() {
        Map<String, Charset> map = Charset.availableCharsets();
        Set<Map.Entry<String, Charset>> entries = map.entrySet();

        for (Map.Entry<String, Charset> entry : entries) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    @Test
    public void test06() throws CharacterCodingException {

        Charset gbk = Charset.forName("GBK");

        //获取编码器与解码器
        //编码器
        CharsetEncoder charsetEncoder = gbk.newEncoder();

        //解码器
        CharsetDecoder charsetDecoder = gbk.newDecoder();

        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("你好世界");
        cBuf.flip();

        //编码
        ByteBuffer bBuf = charsetEncoder.encode(cBuf);

        for (int i = 0; i < 8; i++) {
            System.out.println(bBuf.get());
        }

        //解码
        bBuf.flip();
        CharBuffer cBuff = charsetDecoder.decode(bBuf);
        System.out.println(cBuff.toString());

        System.out.println("------------------------------------------");

        //使用 UTF-8 解 GBK 的编码集
        Charset UTF8 = Charset.forName("UTF-8");
        bBuf.flip();
        CharBuffer decode = UTF8.decode(bBuf);
        System.out.println(decode.toString());

    }
}

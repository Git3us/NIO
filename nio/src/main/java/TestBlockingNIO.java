import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


/**
 * @Auther : CoderEus
 * @Date : 2020/8/27 16:22:35
 * @Description : //TODO
 */
public class TestBlockingNIO {

    //客户端
    @Test
    public void client() throws IOException {
        //1.获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        //用于读取本地文件的Channel
        FileChannel inChannel = FileChannel.open(Paths.get("001.jpg"), StandardOpenOption.READ);

        //2.分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //3.读取本地文件，并发送数据到服务端
        while (inChannel.read(buf) != -1) {
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        //4.关闭通道
        inChannel.close();
        sChannel.close();
    }

    //服务端
    @Test
    public void server() throws IOException {
        //1.获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        FileChannel outChannel = FileChannel.open(Paths.get("002.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //2.绑定连接端口号
        ServerSocketChannel bind = ssChannel.bind(new InetSocketAddress("127.0.0.1", 9898));

        //3.获取客户端连接的通道得到 sChannel
        SocketChannel sChannel = ssChannel.accept();

        //分配一个指定大小的缓冲区来接收数据
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //4.接收客户端数据并保存到本地（保存到本地也需要一个FileChannel）
        while (sChannel.read(buf) != -1) {
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        outChannel.close();
        sChannel.close();
    }
}

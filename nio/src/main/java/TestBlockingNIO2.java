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
 * @Date : 2020/8/27 17:03:14
 * @Description : //TODO
 */
public class TestBlockingNIO2 {

    @Test
    public void Client() throws IOException {
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        FileChannel inChannel = FileChannel.open(Paths.get("002.jpg"), StandardOpenOption.READ);
        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (inChannel.read(buf) != -1) {
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        sChannel.shutdownOutput();

        //接收服务端的反馈
        int len = 0;
        while ((len = sChannel.read(buf)) != -1) {
            buf.flip();
            System.out.println(new String(buf.array(), 0, len));
            buf.clear();
        }

        inChannel.close();
        sChannel.close();
    }

    @Test
    public void Server() throws IOException {
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ServerSocketChannel bind = ssChannel.bind(new InetSocketAddress("127.0.0.1", 9898));

        SocketChannel sChannel = ssChannel.accept();
        FileChannel outChannel = FileChannel.open(Paths.get("003.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (sChannel.read(buf) != -1) {
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        sChannel.shutdownInput();

        //发送反馈给客户端
        buf.put("服务端接收客户端数据成功".getBytes());
        buf.flip();
        sChannel.write(buf);

        ssChannel.close();
        sChannel.close();
        outChannel.close();
    }
}

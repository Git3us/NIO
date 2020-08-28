import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Auther : CoderEus
 * @Date : 2020/8/27 18:12:54
 * @Description : //TODO
 */
public class TestNonBlockingNIO {

    //客户端
    @Test
    public void client() throws IOException {
        //1.获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        //2.切换成非阻塞模式
        sChannel.configureBlocking(false);

        //3.分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //4.发送数据给服务端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String str = scanner.next();
            buf.put((LocalDateTime.now().toString() + "\n" + str).getBytes());
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        //5.关闭通道
        sChannel.close();
    }

    //服务端
    @Test
    public void server() throws IOException {
        //1.获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        //2.切换成非阻塞模式
        ssChannel.configureBlocking(false);

        //3.绑定连接
        ssChannel.bind(new InetSocketAddress("127.0.0.1", 9898));

        //4.获取选择器
        Selector selector = Selector.open();

        //5.把通道注册到选择器上，并且指定监听"接收事件"
        SelectionKey register = ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        //6.轮询式地获取选择器上已经准备就绪的事件
        //当 selector.select() 的返回值大于 0 则代表当前已经至少有一个准备就绪的通道
        while (selector.select() > 0) {
            //7.获取当前选择器中所有注册的"选择键"（已就绪的监听事件）
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            //8.迭代获取
            while (iterator.hasNext()) {
                SelectionKey sKey = iterator.next();    //获取准备就绪的事件

                //9.判断具体是什么事件准备就绪了
                if (sKey.isAcceptable()) {
                    //如果接收状态就绪，则获取客户端连接
                    SocketChannel sChannel = ssChannel.accept();

                    //10.记得要把客户端通道也切换成非阻塞
                    sChannel.configureBlocking(false);

                    //11.将该通道也注册到选择器上并监控他的"读数据状态"
                    sChannel.register(selector, SelectionKey.OP_READ);

                } else if (sKey.isReadable()) {
                    //12.如果是读就绪状态，则要获取当前选择器上"读就绪"的通道
                    SocketChannel sChannel = (SocketChannel) sKey.channel();

                    //13.读取数据
                    ByteBuffer buf = ByteBuffer.allocate(1024);

                    int len = 0;
                    while ((len = sChannel.read(buf)) > 0) {
                        buf.flip();
                        System.out.println(new String(buf.array(), 0, len));
                        buf.clear();
                    }
                }

                //14.SelectionKey使用完毕后要记得取消，不取消的话就会一直有效
                //取消则通过迭代器的 remove() 方法迭代删除即可
                iterator.remove();
            }
        }
    }
}

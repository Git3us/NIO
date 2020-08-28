import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * @Auther : CoderEus
 * @Date : 2020/8/27 20:55:04
 * @Description : //TODO
 */
public class TestPipe {

    @Test
    public void test01() throws IOException {
        //获取管道
        Pipe pipe = Pipe.open();

        //2.将缓冲区中的数据写入管道
        ByteBuffer buf = ByteBuffer.allocate(1024);

        Pipe.SinkChannel sink = pipe.sink();
        buf.put("通过单向管道发送信息".getBytes());
        buf.flip();
        sink.write(buf);

        //3.读取缓冲区中的数据
        Pipe.SourceChannel source = pipe.source();
        buf.flip();
        source.read(buf);
        System.out.println(new String(buf.array(), 0, buf.limit()));

        source.close();
        sink.close();
    }
}

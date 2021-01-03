import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChannelServer {

    public static void main(String[] args) throws IOException {
        // チャネルの作成
        var serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(1234));
        serverSocketChannel.configureBlocking(false);

        // 引数
        // corePoolSize: IDLEでもプールに保持されるスレッド数
        // maximumPoolSize: プール中のスレッド最大数
        // keepAliveTime: corePoolSize個を超えたスレッドが終了されるまでの待ち時間
        // unit
        // workQueue: 実行前タスクを保持するキュー
        // ※NIO自体とは無関係
        var executor = new ThreadPoolExecutor(3, 10, 1000, // ?
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100));

        while (true) {
            // 接続をacceptする
            // blocking modeならブロックする
            // non-blocking modeなら即nullリターンしうる
            var socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                executor.submit(new ChannelServerThread(socketChannel));
            }
        }
    }
}

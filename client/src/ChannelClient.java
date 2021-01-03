import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChannelClient {
    public static void main(String[] args) throws IOException {
        var socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(1234));
        while (true) {
            var sc = new Scanner(System.in); // Scannerとは?
            var next = sc.next();
            sendMessage(socketChannel, next);
        }
    }

    public static void sendMessage(SocketChannel socketChannel, String mes) throws IOException {
        if (mes == null || mes.isEmpty()) {
            return;
        }
        var bytes = mes.getBytes(StandardCharsets.UTF_8);
        int size = bytes.length;
        var buffer = ByteBuffer.allocate(size);
        var sizeBuffer = ByteBuffer.allocate(4);

        sizeBuffer.putInt(size);
        buffer.put(bytes);

        buffer.flip();
        sizeBuffer.flip();
        ByteBuffer dest[] = {sizeBuffer, buffer};
        while (sizeBuffer.hasRemaining() || buffer.hasRemaining()) {
            socketChannel.write(dest); // 配列を渡すもの?
        }
    }
}

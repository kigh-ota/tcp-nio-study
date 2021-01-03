import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

class ChannelServerThread implements Runnable {
    private final SocketChannel socketChannel;
    private String remoteName;

    ChannelServerThread(SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        remoteName = socketChannel.getRemoteAddress().toString();
        System.out.println("client:" + remoteName + " access successfully!");
    }

    @Override
    public void run() {
        var buffer = ByteBuffer.allocate(1024);
        var sizeBuffer = ByteBuffer.allocate(4);
        var sb = new StringBuffer();
        byte b[];
        while (true) {
            try {
                sizeBuffer.clear();
                int read = socketChannel.read(sizeBuffer); // ? まず長さを返すという契約？
                if (read != -1) {
                    sb.setLength(0);
                    sizeBuffer.flip(); // ?
                    int size = sizeBuffer.getInt();
                    int readCount = 0;
                    b = new byte[1024];
                    while (readCount < size) {
                        buffer.clear(); // ?
                        read = socketChannel.read(buffer); // ?  -1とは？
                        if (read != -1) {
                            readCount += read;
                            buffer.flip();
                            int index = 0;
                            while (buffer.hasRemaining()) {
                                b[index++] = buffer.get(); // ?
                                if (index >= b.length) {
                                    index = 0;
                                    sb.append(new String(b, StandardCharsets.UTF_8));
                                }
                            }
                            if (index > 0) {
                                sb.append(new String(b, StandardCharsets.UTF_8));
                            }
                        }
                        System.out.println(remoteName +  ":" + sb.toString());
                    }
                }
            } catch (Exception e) {
                System.out.println(remoteName + "access closed");
                try {
                    socketChannel.close();
                } catch (IOException ex) {
                }
                break;
            }
        }
    }
}

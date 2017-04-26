import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class WebServer {

    private static byte[] data = new byte[255];

    public static void log(String message) {
        if (message != null) {
            System.out.println(message);
        } else {
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < data.length; i++)
            data[i] = (byte) i;
        if (args.length == 0) {
            log("Usage: java WebServer <port1> <port2> ...");
            return;
        }
        Selector selector = Selector.open();
        for (String portValue : args) {
            try {
                int port = Integer.parseInt(portValue);
                ServerSocketChannel server = ServerSocketChannel.open();
                server.configureBlocking(false);
                server.socket().bind(new InetSocketAddress(port));
                server.register(selector, SelectionKey.OP_ACCEPT);
            } catch (NumberFormatException exception) {
                log("Skipping invalid port " + portValue);
            }
        }

        while (true) {
            selector.select();
            Set readyKeys = selector.selectedKeys();
            Iterator iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
                    log("Accepted connection from " + client);
                    client.configureBlocking(false);
                    ByteBuffer source = ByteBuffer.wrap("HELLO ".getBytes());
                    SelectionKey key2 = client.register(selector, SelectionKey.OP_WRITE);
                    key2.attach(source);
                } else if (key.isWritable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer output = (ByteBuffer) key.attachment();
                    if (!output.hasRemaining()) {
                        output.rewind();
                    }
                    client.write(output);
                    client.close();
                }
            }
        }
    }
}

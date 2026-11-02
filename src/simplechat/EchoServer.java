package simplechat;

import java.io.IOException;
import com.lloseng.ocsf.server.AbstractServer;
import com.lloseng.ocsf.server.ConnectionToClient;

public class EchoServer extends AbstractServer {
    public static final int DEFAULT_PORT = 5555;
    private static final String LOGIN_KEY = "login";

    public EchoServer(int port) {
        super(port);
    }

    @Override
    protected synchronized void handleMessageFromClient(
            Object message, ConnectionToClient client) {
        String text = String.valueOf(message).trim();
        Object loginId = client.getInfo(LOGIN_KEY);

        // Chưa có login ID thì thông điệp đầu tiên phải là #login <tên>.
        if (loginId == null) {
            if (text.matches("(?i)^#login\\s+\\S+$")) {
                String name = text.replaceFirst("(?i)^#login\\s+", "").trim();
                client.setInfo(LOGIN_KEY, name);
                reply(client, "Đăng nhập thành công: " + name);
            } else {
                reply(client, "Lỗi: lệnh đầu tiên phải là #login <loginID>.");
                try {
                    client.close();
                } catch (IOException ignored) {
                }
            }
            return;
        }

        // Đã đăng nhập rồi thì không cho gửi lại lệnh #login.
        if (text.regionMatches(true, 0, "#login", 0, 6)) {
            reply(client, "Lỗi: #login chỉ được gửi làm lệnh đầu tiên.");
            return;
        }

        // Tin nhắn thường được gửi tới tất cả client kèm login ID.
        sendToAllClients(loginId + ": " + text);
    }

    private void reply(ConnectionToClient client, String message) {
        try {
            client.sendToClient(message);
        } catch (IOException e) {
            System.out.println("Không gửi được phản hồi: " + e.getMessage());
        }
    }

    @Override
    protected void serverStarted() {
        System.out.println(
                "Server đang lắng nghe ở cổng " + getPort());
    }

    @Override
    protected void serverStopped() {
        System.out.println("Server đã dừng nhận kết nối mới.");
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("Client vừa kết nối: " + client);
    }

    @Override
    protected synchronized void clientDisconnected(
            ConnectionToClient client) {
        System.out.println("Client đã ngắt kết nối: " + client);
    }

    public static void main(String[] args) {
        ServerConsole.main(args);
    }
}
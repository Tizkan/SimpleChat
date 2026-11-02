package simplechat;

import java.io.IOException;
import com.lloseng.ocsf.client.AbstractClient;

public class ChatClient extends AbstractClient {
    private final ChatIF clientUI;
    private final String loginId;
    private boolean userRequestedDisconnect;

    public ChatClient(String host, int port, String loginId, ChatIF clientUI) {
        super(host, port);
        this.loginId = loginId;
        this.clientUI = clientUI;
        // E50: không tự kết nối; người dùng nhập #login để kết nối.
    }

    @Override
    protected void handleMessageFromServer(Object message) {
        clientUI.display(String.valueOf(message));
    }

    @Override
    protected void connectionClosed() {
        if (!userRequestedDisconnect) {
            clientUI.display("Server đã tắt. Client sẽ kết thúc.");
            quit();
        }
        userRequestedDisconnect = false;
    }

    @Override
    protected void connectionException(Exception exception) {
        if (!userRequestedDisconnect) {
            clientUI.display("Kết nối bị lỗi: " + exception.getMessage());
            quit();
        }
    }
    
    @Override
    protected void connectionEstablished() {
        try {
            sendToServer("#login " + loginId);
        } catch (IOException e) {
            clientUI.display("Không gửi được lệnh đăng nhập: " + e.getMessage());
            quit();
        }
    }

    public void login() throws IOException {
        if (isConnected()) {
            clientUI.display("Client đã kết nối rồi.");
            return;
        }

        userRequestedDisconnect = false;
        openConnection();
    }

    public void logoff() {
        if (!isConnected()) {
            clientUI.display("Client hiện chưa kết nối.");
            return;
        }

        userRequestedDisconnect = true;
        try {
            closeConnection();
            clientUI.display("Đã ngắt kết nối.");
        } catch (IOException e) {
            clientUI.display("Lỗi khi ngắt kết nối: " + e.getMessage());
        }
    }

    public void handleMessageFromClientUI(String message) {
        try {
            sendToServer(message);
        } catch (IOException e) {
            clientUI.display("Không gửi được tin nhắn: " + e.getMessage());
        }
    }

    public void quit() {
        if (isConnected()) {
            userRequestedDisconnect = true;
            try {
                closeConnection();
            } catch (IOException ignored) {
            }
        }
        System.exit(0);
    }
}
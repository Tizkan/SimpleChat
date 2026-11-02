package simplechat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientConsole implements ChatIF {
    private static final int DEFAULT_PORT = 5555;
    private final ChatClient client;

    public ClientConsole(String loginId, String host, int port) {
    	client = new ChatClient(host, port, loginId, this);
    }

    @Override
    public void display(String message) {
        System.out.println("> " + message);
    }

    public void accept() {
        try {
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(System.in));

            String message;
            while ((message = input.readLine()) != null) {
                if (!processMessage(message.trim())) {
                    break;
                }
            }
        } catch (IOException e) {
            display("Lỗi khi đọc nội dung nhập vào.");
        }
    }

    private boolean processMessage(String message) {
        if (!message.startsWith("#")) {
            if (client.isConnected()) {
                client.handleMessageFromClientUI(message);
            } else {
                display("Chưa kết nối. Hãy nhập #login trước.");
            }
            return true;
        }

        String[] parts = message.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
            case "#quit":
                client.quit();
                return false;

            case "#logoff":
                client.logoff();
                break;

            case "#login":
                try {
                    client.login();
                } catch (IOException e) {
                    display("Không kết nối được: " + e.getMessage());
                }
                break;

            case "#sethost":
                if (client.isConnected()) {
                    display("#sethost chỉ được dùng khi đã logoff.");
                } else if (argument.isEmpty()) {
                    display("Cách dùng: #sethost <host>");
                } else {
                    client.setHost(argument);
                    display("Host hiện tại: " + client.getHost());
                }
                break;

            case "#setport":
                if (client.isConnected()) {
                    display("#setport chỉ được dùng khi đã logoff.");
                } else if (argument.isEmpty()) {
                    display("Cách dùng: #setport <port>");
                } else {
                    try {
                        int port = Integer.parseInt(argument);
                        if (port < 1 || port > 65535) {
                            display("Port phải nằm trong khoảng 1–65535.");
                        } else {
                            client.setPort(port);
                            display("Port hiện tại: " + client.getPort());
                        }
                    } catch (NumberFormatException e) {
                        display("Port phải là số.");
                    }
                }
                break;

            case "#gethost":
                display("Host hiện tại: " + client.getHost());
                break;

            case "#getport":
                display("Port hiện tại: " + client.getPort());
                break;

            default:
                display("Lệnh không hợp lệ: " + command);
        }

        return true;
    }

    public static void main(String[] args) {
        if (args.length == 0 || args[0].trim().isEmpty()) {
            System.out.println(
                    "Thiếu login ID. Cách chạy: ClientConsole <loginID> [host] [port]");
            return;
        }

        String loginId = args[0];
        String host = args.length > 1 ? args[1] : "localhost";
        int port = DEFAULT_PORT;

        if (args.length > 2) {
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("Port phải là số.");
                return;
            }
        }

        ClientConsole console = new ClientConsole(loginId, host, port);
        console.display("Nhập #login để kết nối.");
        console.accept();
    }
}
package simplechat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConsole implements ChatIF {
    private final EchoServer server;
    private boolean serverClosed;

    public ServerConsole(int port) {
        server = new EchoServer(port);
    }

    @Override
    public void display(String message) {
        System.out.println(message);
    }

    public void accept() {
        try {
            server.listen();
            display("Lệnh server: #stop, #start, #close, "
                    + "#setport <port>, #getport, #quit");

            BufferedReader input =
                    new BufferedReader(new InputStreamReader(System.in));

            String message;
            while ((message = input.readLine()) != null) {
                if (!processMessage(message.trim())) {
                    break;
                }
            }
        } catch (IOException e) {
            display("Lỗi server: " + e.getMessage());
        }
    }

    private boolean processMessage(String message) {
        if (!message.startsWith("#")) {
            String serverMessage = "SERVER MSG> " + message;
            display(serverMessage);
            server.sendToAllClients(serverMessage);
            return true;
        }

        String[] parts = message.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        try {
            switch (command) {
                case "#quit":
                    server.close();
                    return false;

                case "#stop":
                    if (server.isListening()) {
                        server.stopListening();
                        display("Server đã dừng nhận kết nối mới.");
                    } else {
                        display("Server hiện không lắng nghe.");
                    }
                    break;

                case "#close":
                    server.close();
                    serverClosed = true;
                    display("Server đã đóng và ngắt các client.");
                    break;

                case "#setport":
                    if (!serverClosed) {
                        display("#setport chỉ dùng sau khi nhập #close.");
                    } else if (argument.isEmpty()) {
                        display("Cách dùng: #setport <port>");
                    } else {
                        int port = Integer.parseInt(argument);
                        if (port < 1 || port > 65535) {
                            display("Port phải nằm trong khoảng 1–65535.");
                        } else {
                            server.setPort(port);
                            display("Port mới: " + port);
                        }
                    }
                    break;

                case "#start":
                    if (server.isListening()) {
                        display("Server đang lắng nghe rồi.");
                    } else {
                        server.listen();
                        serverClosed = false;
                        display("Đã yêu cầu server bắt đầu lắng nghe.");
                    }
                    break;

                case "#getport":
                    display("Port hiện tại: " + server.getPort());
                    break;

                default:
                    display("Lệnh không hợp lệ: " + command);
            }
        } catch (IOException e) {
            display("Không thực hiện được lệnh: " + e.getMessage());
        } catch (NumberFormatException e) {
            display("Port phải là số.");
        }

        return true;
    }

    public static void main(String[] args) {
        int port = args.length > 0
                ? Integer.parseInt(args[0])
                : EchoServer.DEFAULT_PORT;

        new ServerConsole(port).accept();
    }
}
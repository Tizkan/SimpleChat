# SimpleChat dùng OCSF

SimpleChat là ứng dụng chat nhiều client qua một server. Chương trình dùng OCSF để quản lý kết nối và trao đổi tin nhắn. Phần mã ứng dụng ghi ở các lớp `ChatClient`, `EchoServer`, `ClientConsole` và `ServerConsole`.

Trong bài này, OCSF là **Object Client-Server Framework** trong bộ mã Lloseng. Đây không phải Open Cybersecurity Schema Framework.

## Yêu cầu

- JDK 8 trở lên
- Eclipse IDE hoặc công cụ Java tương đương
- Mã nguồn OCSF từ thư mục `V2Code/com/lloseng/ocsf` trong [kho Lloseng trên GitHub](https://github.com/TimLethbridge/Lloseng/tree/master)

## Cấu trúc trong Eclipse

Tạo một Java Project, ví dụ `SimpleChat`. Đặt các gói OCSF và ứng dụng bên trong thư mục `src` như sau:

```text
src/
├── com/lloseng/ocsf/
│   ├── client/
│   └── server/
└── simplechat/
    ├── ChatIF.java
    ├── ChatClient.java
    ├── ClientConsole.java
    ├── EchoServer.java
    └── ServerConsole.java
```

Chép cả hai gói `client` và `server` từ OCSF vào `src/com/lloseng/ocsf`. Chép năm file trong thư mục `simplechat` của project này vào `src/simplechat`. Không đổi dòng `package` đầu các file và không chép đè mã OCSF bằng mã ứng dụng.

## Chạy trong Eclipse

1. Trong Package Explorer, mở `SimpleChat > src > simplechat`.
2. Nhấp phải `ServerConsole.java`, chọn **Run As > Java Application**. Server mặc định lắng nghe ở port `5555`.
3. Nhấp phải `ClientConsole.java`, chọn **Run As > Java Application**.
4. Mở **Run Configurations > Java Application > ClientConsole > Arguments**. Điền **Program arguments**:

   ```text
   alice localhost 5555
   ```

   `alice` là login ID; `localhost` là host; `5555` là port. Host và port có thể bỏ qua, nhưng login ID là bắt buộc.
5. Chuyển sang Console của client, nhập `#login` rồi nhấn Enter. Client sẽ kết nối và tự gửi login ID đã khai báo trong Program arguments.
6. Tạo thêm các lần chạy `ClientConsole` với login ID khác, chẳng hạn `bob localhost 5555`, để thử chat giữa nhiều client.

Khi Eclipse mở nhiều Console, chọn đúng Console bằng nút **Display Selected Console**. Khởi động server trước client. Các client phải dùng cùng host và port với server.

## Lệnh ở client

| Lệnh | Tác dụng |
|---|---|
| `#login` | Kết nối và đăng nhập bằng login ID trong Program arguments |
| `#logoff` | Ngắt kết nối; muốn chat tiếp thì nhập `#login` |
| `#sethost <host>` | Đổi host khi đã logoff |
| `#setport <port>` | Đổi port khi đã logoff |
| `#gethost` | Xem host hiện tại |
| `#getport` | Xem port hiện tại |
| `#quit` | Thoát client |

Sau khi đăng nhập, nhập nội dung bình thường rồi nhấn Enter để gửi. Server phát tin theo dạng `<loginID>: <nội dung>`. Không cần gõ `#login alice`; chỉ gõ `#login`, vì tên đăng nhập đã được đưa vào Program arguments.

## Lệnh ở server

| Lệnh | Tác dụng |
|---|---|
| `#stop` | Dừng nhận kết nối mới; client đang kết nối vẫn giữ phiên |
| `#start` | Bắt đầu nhận kết nối trở lại |
| `#close` | Đóng server và ngắt các client |
| `#setport <port>` | Đổi port sau khi đã dùng `#close` |
| `#getport` | Xem port hiện tại |
| `#quit` | Đóng server và thoát |

Nhập văn bản không bắt đầu bằng `#` ở Console server để phát thông báo đến các client. Tin nhắn có tiền tố `SERVER MSG>`.

## Nếu gặp lỗi kết nối

- **`Address already in use: bind`**: đã có server khác dùng port đó. Dừng server cũ bằng nút đỏ **Terminate** trong Eclipse, hoặc đóng server rồi đổi port.
- **`Connection refused`**: server chưa chạy hoặc client đang dùng host/port khác với server. Kiểm tra server đã lắng nghe trước khi nhập `#login`.
- **Không thấy phản hồi ở client**: kiểm tra đang nhập lệnh trong đúng Console và client đã đăng nhập.

## Kiểm thử theo E52

Thử ít nhất hai client: đăng nhập, gửi tin và xác nhận các client đều nhận được. Kiểm tra thêm `#logoff`, đăng nhập lại, thay đổi host/port sau khi logoff, các lệnh quản lý server và xử lý khi server dừng. E52 còn yêu cầu chạy các ca kiểm thử Phase 1 được chọn và thử chéo với một bản client hoặc server Phase 2 khác. Chỉ ghi vào báo cáo những kết quả đã thực sự quan sát; README này không khẳng định các ca đó đã chạy.

## Mã nguồn tham khảo

- [Kho Lloseng trên GitHub](https://github.com/TimLethbridge/Lloseng/tree/master)
- Các lớp OCSF dùng trong project nằm dưới `V2Code/com/lloseng/ocsf/client` và `V2Code/com/lloseng/ocsf/server`.

# Tidit Tracker - Ghi Nhật Ký Quẹt Cửa Tự Động Cho Ứng Dụng Tidit

Ứng dụng Android chạy ngầm giúp **tự động theo dõi và ghi lại thời gian (ngày, giờ, phút, giây)** mỗi khi bạn sử dụng ứng dụng **Tidit (LG CNS)** trên điện thoại để quẹt qua cửa/cổng an ninh.

---

## 📥 Tải Về Cài Đặt Ngay (Download APK)

👉 **[Tải file APK bản mới nhất: TiditTracker-v1.0.0.apk](release/TiditTracker-v1.0.0.apk)**

*(Hoặc vào thư mục `release/` trong repository để tải file `TiditTracker-v1.0.0.apk` về điện thoại và nhấn Cài đặt).*

---

## 🎯 Tính Năng Nổi Bật

- **Tự động nhận diện quẹt thẻ ngầm qua NFC**: Chỉ cần bật sáng màn hình và chạm vào đầu đọc ở cửa công ty, ứng dụng tự động nhận diện đúng sóng NFC của Tidit và ghi lại thời gian.
- **Phân biệt chính xác - Không ghi nhầm thẻ ngân hàng**: Ứng dụng lọc nghiêm ngặt theo mã định danh AID độc quyền của Tidit (`D410000039533153494944` - Thẻ S1 & `D4106429200010` - Thẻ IDP). Tuyệt đối **không** ghi nhầm khi bạn quẹt thanh toán Google Pay, thẻ Visa, Mastercard hay Napas.
- **Gom nhóm theo Ngày - Tháng - Năm**: Danh sách nhật ký được gom gọn gàng theo từng ngày, hiển thị thứ trong tuần và tổng số lượt quẹt của ngày hôm đó (ví dụ: `Hôm nay • 18/11/2026 - 2 lượt`).
- **Xóa linh hoạt bằng Checkbox**: Tích chọn những mục cần xóa hoặc chọn xóa nhanh cả ngày / toàn bộ lịch sử.
- **Xuất bảng tính Excel / CSV chuẩn UTF-8**: Dễ dàng xuất toàn bộ lịch sử ra file CSV để gửi qua Zalo, Email, Drive đối chiếu chấm công giờ vào - ra.
- **Hoạt động 100% Offline & Bảo mật**: Toàn bộ dữ liệu lưu trữ trực tiếp trên máy của bạn (Room Database SQLite), không gửi dữ liệu ra bên ngoài.

---

## ⚙️ Cơ Chế Hoạt Động (Background Monitor)

1. **NFC Transaction Receiver (Bộ nhận diện giao dịch NFC ngầm)**:
   - Lắng nghe broadcast sự kiện NFC của hệ thống Android (`android.nfc.action.TRANSACTION_DETECTED`).
   - Kiểm tra mã AID: Chỉ kích hoạt khi AID thuộc hệ thống thẻ cửa Tidit.
2. **Notification Listener Service (Trình bắt thông báo ngầm)**:
   - Tự động bắt thông báo phát sinh từ ứng dụng `com.lgcns.didid` khi thẻ được xác thực qua cửa.
3. **Accessibility Service (Dịch vụ trợ năng)**:
   - Tự động bắt sự kiện khi người dùng mở trực tiếp ứng dụng Tidit trên màn hình.
4. **Cơ chế chống ghi lặp (Debounce 15s)**:
   - Tránh việc ghi trùng nhiều bản ghi trong cùng một lần quẹt cửa.

---

## 🚀 Hướng Dẫn Cài Đặt (Cho Người Dùng)

1. Tải file **[TiditTracker-v1.0.0.apk](release/TiditTracker-v1.0.0.apk)** về điện thoại và bấm cài đặt.
2. Mở ứng dụng **Tidit Tracker** lên.
3. Bấm vào nút: **"Bật quyền giám sát trong Cài đặt"**:
   - Điện thoại sẽ chuyển đến mục **Trợ năng (Accessibility / Hỗ trợ tiếp cận)**.
   - Chọn **Tidit Auto Logger** và gạt công tắc sang **BẬT (ON)**.
4. *(Khuyên dùng)* Cấp quyền **Truy cập thông báo (Notification Access)** và tắt **Tiết kiệm pin** cho ứng dụng trong phần Cài đặt ứng dụng của máy (đặc biệt là điện thoại Xiaomi / HyperOS) để ứng dụng không bị hệ thống tắt khi chạy nền.
5. Xong! Từ nay mỗi khi bạn đến cổng công ty quẹt thẻ qua cửa, ứng dụng sẽ tự động ghi lại lịch sử chính xác.

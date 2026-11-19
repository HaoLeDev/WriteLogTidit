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

## 🚀 Hướng Dẫn Cài Đặt (Cho Người Dùng - 100% Chạy Ngầm Tự Động)

1. Tải file **[TiditTracker-v1.0.0.apk](release/TiditTracker-v1.0.0.apk)** về điện thoại và bấm cài đặt (hoặc cài đè lên bản cũ).
2. Mở ứng dụng **Tidit Tracker** lên.
3. Trên bảng điều khiển **"CHẠY NGẦM TỰ ĐỘNG"**:
   - **Mục 1 - Trợ năng (Accessibility)**: Bấm **Bật** -> Tìm **Tidit Auto Logger** -> Gạt sang **BẬT (ON)** (Dùng để bắt Toast thông báo & giao diện).
   - **Mục 2 - Quyền đọc thông báo (Notification Access)**: Bấm **Cấp quyền** -> Tìm **Tidit Tracker** -> Gạt sang **CHO PHÉP** (Dùng để bắt thông báo xác thực quẹt ngầm).
   - **Mục 3 - Tiết kiệm pin**: Bấm **Bỏ giới hạn** -> Chọn **Không tối ưu / Không hạn chế** (để hệ thống không tắt ngầm app).
4. **Đảm bảo thông báo trong Tidit đang BẬT**:
   - Bấm nút **Cài đặt Tidit** ngay trên ứng dụng -> vào mục **Thông báo (Notifications)** -> Đảm bảo thông báo của Tidit đang **BẬT**.
5. **Xong!** Bạn không cần bấm bất kỳ nút nào khác. Khi đến cổng công ty, đúng theo thói quen thường ngày: **chỉ cần bật sáng màn hình và đưa điện thoại vào đầu đọc thẻ ở cửa**, ứng dụng sẽ tự động ghi lại thời gian chuẩn từng giây!

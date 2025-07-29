# 📚 English Learning Platform

Ứng dụng học từ vựng tiếng Anh với hỗ trợ AI và WebSocket.

## ✨ Tính năng chính

### 🎯 Học từ vựng theo cấp độ

- **A1 (Cơ bản)**: Từ vựng cơ bản hàng ngày
- **A2 (Sơ cấp)**: Từ vựng về công việc, sở thích
- **B1 (Trung cấp)**: Từ vựng về giáo dục, du lịch
- **B2 (Trung cao)**: Từ vựng học thuật, phức tạp

### 🤖 Tích hợp AI

- Sử dụng **Google Gemini 1.5 Flash** để tạo từ vựng động
- 30 từ vựng mới cho mỗi level
- Phiên âm IPA chính xác
- Nghĩa tiếng Việt phù hợp

### 🎮 Giao diện tương tác

- Dashboard hiện đại với Tailwind CSS
- Trang chọn level trực quan
- Bài tập nhập nghĩa tương tác
- Theo dõi tiến độ realtime

## 🚀 Cách chạy ứng dụng

### Yêu cầu hệ thống

- Java 17+
- Maven 3.6+
- Spring Boot 3.5+

### Cài đặt

```bash
# Clone repository
git clone <repository-url>
cd LTSocket

# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

### Truy cập ứng dụng

- **URL**: http://localhost:8080
- **Dashboard**: Trang chủ với các tùy chọn học tập
- **Vocabulary**: Bài tập từ vựng theo level

## ⚙️ Cấu hình Gemini AI (Tùy chọn)

Để sử dụng AI tạo từ vựng thay vì dữ liệu mẫu:

1. **Lấy API key**: https://makersuite.google.com/app/apikey
2. **Cấu hình**: Sửa file `src/main/resources/gemini.properties`
3. **Chi tiết**: Xem file `GEMINI_SETUP.md`

> 💡 **Lưu ý**: Nếu không cấu hình API, ứng dụng vẫn hoạt động với 30 từ vựng mẫu cho mỗi level!

## 🏗️ Kiến trúc ứng dụng

### Backend

- **Spring Boot 3.5**: Framework chính
- **Spring Security**: Xác thực và phân quyền
- **WebSocket**: Giao tiếp realtime
- **Thymeleaf**: Template engine
- **MySQL**: Cơ sở dữ liệu

### Frontend

- **Tailwind CSS**: Styling framework
- **JavaScript**: Tương tác động
- **WebSocket Client**: Kết nối realtime

### AI Integration

- **Google Gemini 1.5 Flash**: Tạo nội dung từ vựng
- **OkHttp**: HTTP client cho API calls
- **JSON Processing**: Xử lý response

## 📁 Cấu trúc dự án

```
src/
├── main/
│   ├── java/uth/edu/
│   │   ├── controllers/          # REST Controllers
│   │   ├── Services/            # Business Logic
│   │   ├── Models/              # Data Models
│   │   ├── Configs/             # Configuration
│   │   └── Repositories/        # Data Access
│   └── resources/
│       ├── templates/           # Thymeleaf Templates
│       ├── static/              # CSS, JS, Images
│       └── *.properties         # Configuration Files
```

## 🛠️ Tech Stack

| Công nghệ        | Phiên bản | Mục đích                |
| ---------------- | --------- | ----------------------- |
| Spring Boot      | 3.5.4     | Backend Framework       |
| Spring Security  | 6.5.2     | Authentication          |
| Thymeleaf        | -         | Template Engine         |
| Tailwind CSS     | 3.x       | UI Framework            |
| MySQL            | 8.x       | Database                |
| Gemini 1.5 Flash | v1beta    | AI Content              |
| WebSocket        | -         | Real-time Communication |

## 🤝 Đóng góp

1. Fork repository
2. Tạo feature branch
3. Commit changes
4. Push và tạo Pull Request

## 📄 License

Dự án này được phát hành dưới MIT License.

---

🎓 **Phát triển bởi**: Nhóm phát triển LapTrinhMang  
📧 **Liên hệ**: [email]  
🌟 **GitHub**: [repository-url]

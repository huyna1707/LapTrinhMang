# 🤖 Hướng dẫn cấu hình Gemini API

## ✨ Tại sao cần cấu hình?

- **Hiện tại**: Ứng dụng đang sử dụng 30 từ vựng mẫu cho mỗi level
- **Sau khi cấu hình**: Ứng dụng sẽ tự động tạo từ vựng mới từ Gemini AI (Model: gemini-2.0-flash)

## 🔧 Bước 1: Lấy API Key từ Google

1. **Truy cập**: https://makersuite.google.com/app/apikey
2. **Đăng nhập** bằng tài khoản Google của bạn
3. **Click "Create API Key"**
4. **Copy API key** được tạo (dạng: `AIzaSyB...`)

## ⚙️ Bước 2: Cấu hình trong ứng dụng

1. **Mở file**: `src/main/resources/gemini.properties`
2. **Thay thế** dòng:

   ```properties
   gemini.api.key=YOUR_GEMINI_API_KEY
   ```

   **Thành**:

   ```properties
   gemini.api.key=AIzaSyBxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   ```

## 🚀 Bước 3: Restart ứng dụng

Sau khi cấu hình xong, restart ứng dụng để áp dụng thay đổi.

## ℹ️ Lưu ý quan trọng:

- ✅ **Miễn phí**: Gemini API có quota miễn phí hàng ngày
- 🔒 **Bảo mật**: Không chia sẻ API key công khai
- 📊 **Fallback**: Nếu API lỗi, ứng dụng tự động dùng dữ liệu mẫu
- 🔄 **Thay đổi**: Mỗi lần chọn level sẽ tạo từ vựng mới từ AI
- 🤖 **Model**: Sử dụng Gemini 2.0 Flash (model mới nhất, nhanh và hiệu quả)

## 🎯 Kết quả sau khi cấu hình:

- Từ vựng được tạo tự động theo level CEFR
- Phiên âm IPA chính xác
- Nghĩa tiếng Việt phù hợp
- Nội dung đa dạng, không lặp lại

## 🔄 Cập nhật gần đây:

- **29/07/2025**: Chuyển từ `gemini-pro` sang `gemini-1.5-flash` do Google deprecate model cũ

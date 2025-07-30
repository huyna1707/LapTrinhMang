package uth.edu.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.PropertySource;
import uth.edu.Models.Vocabulary;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@PropertySource(value = "classpath:gemini.properties", encoding = "UTF-8")
public class VocabularyService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    /**
     * Lấy 30 từ vựng realtime từ Gemini AI cho level được chỉ định
     * Mỗi lần gọi sẽ tạo ra 30 từ vựng mới hoàn toàn khác nhau
     */
    public List<Vocabulary> getVocabularyFromAI(String level) {
        System.out.println("🚀 [" + LocalDateTime.now() + "] Bắt đầu gọi Gemini API realtime cho level: " + level);
        
        // Kiểm tra API key
        if (apiKey == null || apiKey.trim().isEmpty() || "YOUR_GEMINI_API_KEY".equals(apiKey)) {
            System.out.println("⚠️ API key chưa được cấu hình, sử dụng dữ liệu mẫu");
            return generateSampleVocabulary(level);
        }

        try {
            return fetchVocabularyFromGemini(level);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gọi Gemini API: " + e.getMessage());
            System.out.println("🔄 Fallback: Sử dụng dữ liệu mẫu cho level " + level);
            return generateSampleVocabulary(level);
        }
    }

    /**
     * Gọi Gemini API để lấy từ vựng realtime
     */
    private List<Vocabulary> fetchVocabularyFromGemini(String level) throws IOException {
        System.out.println("🤖 Đang gọi Gemini API với level: " + level);
        
        // Tạo prompt với timestamp để đảm bảo kết quả khác nhau mỗi lần
        String timestamp = String.valueOf(System.currentTimeMillis());
        String prompt = String.format(
                "Generate exactly 30 DIFFERENT English words at %s level (CEFR) for vocabulary learning. " +
                "Use timestamp %s to ensure uniqueness. " +
                "For each word, provide the word, phonetic transcription (IPA), and meaning in Vietnamese. " +
                "Return ONLY a valid JSON array in this exact format: " +
                "[{\"word\": \"apple\", \"phonetic\": \"/ˈæpl/\", \"meaning\": \"quả táo\"}, " +
                "{\"word\": \"book\", \"phonetic\": \"/bʊk/\", \"meaning\": \"sách\"}, ...] " +
                "Make sure it's exactly 30 words and valid JSON format. " +
                "Do not include common words like hello, goodbye, yes, no.",
                level, timestamp
        );

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .build();

            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put("text", prompt));
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contents);

            Request request = new Request.Builder()
                    .url(GEMINI_API_URL)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-goog-api-key", apiKey)
                    .post(RequestBody.create(
                            requestBody.toString(),
                            MediaType.parse("application/json")
                    ))
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody responseBodyObj = response.body();
            if (responseBodyObj == null) {
                throw new IOException("Empty response body from Gemini API");
            }
            String responseBody = responseBodyObj.string();

            JSONObject json = new JSONObject(responseBody);

            if (json.has("error")) {
                JSONObject error = json.getJSONObject("error");
                String errorMessage = error.getString("message");
                throw new IOException("Gemini API Error: " + errorMessage);
            }

            if (!json.has("candidates")) {
                throw new IOException("Invalid Gemini API response format");
            }

            String content_text = json
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            // Loại bỏ markdown code blocks nếu có
            content_text = content_text.trim();
            if (content_text.startsWith("```json")) {
                content_text = content_text.substring(7);
            }
            if (content_text.startsWith("```")) {
                content_text = content_text.substring(3);
            }
            if (content_text.endsWith("```")) {
                content_text = content_text.substring(0, content_text.length() - 3);
            }
            content_text = content_text.trim();

            JSONArray array = new JSONArray(content_text);
            List<Vocabulary> vocabularyData = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String word = obj.getString("word");
                String phonetic = obj.getString("phonetic");
                String meaning = obj.getString("meaning");
                vocabularyData.add(new Vocabulary(word, phonetic, meaning, level));
            }

            System.out.println("✅ Gemini API trả về " + vocabularyData.size() + " từ vựng cho level " + level);
            return vocabularyData;
        }

        /**
         * Tạo dữ liệu mẫu khi API không khả dụng
         * Mỗi lần gọi sẽ random từ pool để tạo sự đa dạng
         */
        private List<Vocabulary> generateSampleVocabulary(String level) {
            System.out.println("📝 Tạo dữ liệu mẫu cho level: " + level);
            
            List<Vocabulary> allWords = new ArrayList<>();
            Random random = new Random();
            
            switch (level.toUpperCase()) {
                case "A1":
                    allWords = Arrays.asList(
                        new Vocabulary("apple", "/ˈæpl/", "quả táo", level),
                        new Vocabulary("book", "/bʊk/", "sách", level),
                        new Vocabulary("cat", "/kæt/", "con mèo", level),
                        new Vocabulary("dog", "/dɔːɡ/", "con chó", level),
                        new Vocabulary("house", "/haʊs/", "ngôi nhà", level),
                        new Vocabulary("water", "/ˈwɔːtər/", "nước", level),
                        new Vocabulary("food", "/fuːd/", "thức ăn", level),
                        new Vocabulary("car", "/kɑːr/", "xe hơi", level),
                        new Vocabulary("tree", "/triː/", "cây", level),
                        new Vocabulary("sun", "/sʌn/", "mặt trời", level),
                        new Vocabulary("moon", "/muːn/", "mặt trăng", level),
                        new Vocabulary("star", "/stɑːr/", "ngôi sao", level),
                        new Vocabulary("flower", "/ˈflaʊər/", "hoa", level),
                        new Vocabulary("bird", "/bɜːrd/", "con chim", level),
                        new Vocabulary("fish", "/fɪʃ/", "con cá", level),
                        new Vocabulary("table", "/ˈteɪbl/", "cái bàn", level),
                        new Vocabulary("chair", "/tʃer/", "cái ghế", level),
                        new Vocabulary("window", "/ˈwɪndoʊ/", "cửa sổ", level),
                        new Vocabulary("door", "/dɔːr/", "cửa ra vào", level),
                        new Vocabulary("pen", "/pen/", "cái bút", level),
                        new Vocabulary("paper", "/ˈpeɪpər/", "giấy", level),
                        new Vocabulary("school", "/skuːl/", "trường học", level),
                        new Vocabulary("teacher", "/ˈtiːtʃər/", "giáo viên", level),
                        new Vocabulary("student", "/ˈstuːdnt/", "học sinh", level),
                        new Vocabulary("friend", "/frend/", "bạn bè", level),
                        new Vocabulary("family", "/ˈfæməli/", "gia đình", level),
                        new Vocabulary("mother", "/ˈmʌðər/", "mẹ", level),
                        new Vocabulary("father", "/ˈfɑːðər/", "bố", level),
                        new Vocabulary("brother", "/ˈbrʌðər/", "anh/em trai", level),
                        new Vocabulary("sister", "/ˈsɪstər/", "chị/em gái", level),
                        new Vocabulary("hand", "/hænd/", "bàn tay", level),
                        new Vocabulary("foot", "/fʊt/", "bàn chân", level),
                        new Vocabulary("head", "/hed/", "đầu", level),
                        new Vocabulary("eye", "/aɪ/", "mắt", level),
                        new Vocabulary("nose", "/noʊz/", "mũi", level),
                        new Vocabulary("mouth", "/maʊθ/", "miệng", level),
                        new Vocabulary("ear", "/ɪr/", "tai", level),
                        new Vocabulary("hair", "/her/", "tóc", level),
                        new Vocabulary("leg", "/leɡ/", "chân", level),
                        new Vocabulary("arm", "/ɑːrm/", "cánh tay", level)
                    );
                    break;
                    
                case "A2":
                    allWords = Arrays.asList(
                        new Vocabulary("computer", "/kəmˈpjuːtər/", "máy tính", level),
                        new Vocabulary("telephone", "/ˈteləfoʊn/", "điện thoại", level),
                        new Vocabulary("television", "/ˈteləvɪʒn/", "tivi", level),
                        new Vocabulary("kitchen", "/ˈkɪtʃən/", "nhà bếp", level),
                        new Vocabulary("bedroom", "/ˈbedruːm/", "phòng ngủ", level),
                        new Vocabulary("bathroom", "/ˈbæθruːm/", "phòng tắm", level),
                        new Vocabulary("garden", "/ˈɡɑːrdn/", "khu vườn", level),
                        new Vocabulary("restaurant", "/ˈrestrɑːnt/", "nhà hàng", level),
                        new Vocabulary("hospital", "/ˈhɑːspɪtl/", "bệnh viện", level),
                        new Vocabulary("library", "/ˈlaɪbreri/", "thư viện", level),
                        new Vocabulary("museum", "/mjuˈziːəm/", "bảo tàng", level),
                        new Vocabulary("market", "/ˈmɑːrkɪt/", "chợ", level),
                        new Vocabulary("office", "/ˈɔːfɪs/", "văn phòng", level),
                        new Vocabulary("factory", "/ˈfæktri/", "nhà máy", level),
                        new Vocabulary("station", "/ˈsteɪʃn/", "ga", level),
                        new Vocabulary("airport", "/ˈerpɔːrt/", "sân bay", level),
                        new Vocabulary("hotel", "/hoʊˈtel/", "khách sạn", level),
                        new Vocabulary("cinema", "/ˈsɪnəmə/", "rạp chiếu phim", level),
                        new Vocabulary("shopping", "/ˈʃɑːpɪŋ/", "mua sắm", level),
                        new Vocabulary("money", "/ˈmʌni/", "tiền", level),
                        new Vocabulary("ticket", "/ˈtɪkɪt/", "vé", level),
                        new Vocabulary("passport", "/ˈpæspɔːrt/", "hộ chiếu", level),
                        new Vocabulary("vacation", "/vəˈkeɪʃn/", "kỳ nghỉ", level),
                        new Vocabulary("weather", "/ˈweðər/", "thời tiết", level),
                        new Vocabulary("season", "/ˈsiːzn/", "mùa", level),
                        new Vocabulary("spring", "/sprɪŋ/", "mùa xuân", level),
                        new Vocabulary("summer", "/ˈsʌmər/", "mùa hè", level),
                        new Vocabulary("autumn", "/ˈɔːtəm/", "mùa thu", level),
                        new Vocabulary("winter", "/ˈwɪntər/", "mùa đông", level),
                        new Vocabulary("temperature", "/ˈtemprətʃər/", "nhiệt độ", level),
                        new Vocabulary("clothes", "/kloʊðz/", "quần áo", level),
                        new Vocabulary("shirt", "/ʃɜːrt/", "áo sơ mi", level),
                        new Vocabulary("pants", "/pænts/", "quần dài", level),
                        new Vocabulary("shoes", "/ʃuːz/", "giày", level),
                        new Vocabulary("jacket", "/ˈdʒækɪt/", "áo khoác", level),
                        new Vocabulary("dress", "/dres/", "váy", level),
                        new Vocabulary("hat", "/hæt/", "mũ", level),
                        new Vocabulary("watch", "/wɑːtʃ/", "đồng hồ", level),
                        new Vocabulary("glasses", "/ˈɡlæsɪz/", "kính", level),
                        new Vocabulary("bag", "/bæɡ/", "túi xách", level)
                    );
                    break;
                    
                default: // B1, B2, C1, C2
                    allWords = Arrays.asList(
                        new Vocabulary("achievement", "/əˈtʃiːvmənt/", "thành tựu", level),
                        new Vocabulary("opportunity", "/ˌɑːpərˈtuːnəti/", "cơ hội", level),
                        new Vocabulary("challenge", "/ˈtʃælɪndʒ/", "thử thách", level),
                        new Vocabulary("experience", "/ɪkˈspɪriəns/", "kinh nghiệm", level),
                        new Vocabulary("knowledge", "/ˈnɑːlɪdʒ/", "kiến thức", level),
                        new Vocabulary("research", "/rɪˈsɜːrtʃ/", "nghiên cứu", level),
                        new Vocabulary("development", "/dɪˈveləpmənt/", "phát triển", level),
                        new Vocabulary("environment", "/ɪnˈvaɪrənmənt/", "môi trường", level),
                        new Vocabulary("technology", "/tekˈnɑːlədʒi/", "công nghệ", level),
                        new Vocabulary("communication", "/kəˌmjuːnɪˈkeɪʃn/", "giao tiếp", level),
                        new Vocabulary("relationship", "/rɪˈleɪʃnʃɪp/", "mối quan hệ", level),
                        new Vocabulary("responsibility", "/rɪˌspɑːnsəˈbɪləti/", "trách nhiệm", level),
                        new Vocabulary("organization", "/ˌɔːrɡənəˈzeɪʃn/", "tổ chức", level),
                        new Vocabulary("management", "/ˈmænɪdʒmənt/", "quản lý", level),
                        new Vocabulary("leadership", "/ˈliːdərʃɪp/", "lãnh đạo", level),
                        new Vocabulary("creativity", "/ˌkriːeɪˈtɪvəti/", "sáng tạo", level),
                        new Vocabulary("innovation", "/ˌɪnəˈveɪʃn/", "đổi mới", level),
                        new Vocabulary("collaboration", "/kəˌlæbəˈreɪʃn/", "hợp tác", level),
                        new Vocabulary("competition", "/ˌkɑːmpəˈtɪʃn/", "cạnh tranh", level),
                        new Vocabulary("strategy", "/ˈstrætədʒi/", "chiến lược", level),
                        new Vocabulary("analysis", "/əˈnæləsɪs/", "phân tích", level),
                        new Vocabulary("solution", "/səˈluːʃn/", "giải pháp", level),
                        new Vocabulary("decision", "/dɪˈsɪʒn/", "quyết định", level),
                        new Vocabulary("priority", "/praɪˈɔːrəti/", "ưu tiên", level),
                        new Vocabulary("efficiency", "/ɪˈfɪʃnsi/", "hiệu quả", level),
                        new Vocabulary("improvement", "/ɪmˈpruːvmənt/", "cải tiến", level),
                        new Vocabulary("performance", "/pərˈfɔːrməns/", "hiệu suất", level),
                        new Vocabulary("success", "/səkˈses/", "thành công", level),
                        new Vocabulary("failure", "/ˈfeɪljər/", "thất bại", level),
                        new Vocabulary("progress", "/ˈprɑːɡres/", "tiến bộ", level),
                        new Vocabulary("adventure", "/ədˈventʃər/", "cuộc phiêu lưu", level),
                        new Vocabulary("discovery", "/dɪˈskʌvəri/", "khám phá", level),
                        new Vocabulary("investigation", "/ɪnˌvestɪˈɡeɪʃn/", "điều tra", level),
                        new Vocabulary("exploration", "/ˌekspləˈreɪʃn/", "thám hiểm", level),
                        new Vocabulary("observation", "/ˌɑːbzərˈveɪʃn/", "quan sát", level),
                        new Vocabulary("conclusion", "/kənˈkluːʒn/", "kết luận", level),
                        new Vocabulary("recommendation", "/ˌrekəmenˈdeɪʃn/", "khuyến nghị", level),
                        new Vocabulary("consideration", "/kənˌsɪdəˈreɪʃn/", "cân nhắc", level),
                        new Vocabulary("transformation", "/ˌtrænsfərˈmeɪʃn/", "biến đổi", level),
                        new Vocabulary("implementation", "/ˌɪmplɪmenˈteɪʃn/", "thực hiện", level)
                    );
                    break;
            }
            
            // Random chọn 30 từ từ pool
            List<Vocabulary> selectedWords = new ArrayList<>();
            List<Vocabulary> tempList = new ArrayList<>(allWords);
            
            for (int i = 0; i < Math.min(30, tempList.size()); i++) {
                int randomIndex = random.nextInt(tempList.size());
                selectedWords.add(tempList.remove(randomIndex));
            }
            
            // Nếu không đủ 30 từ, lặp lại
            while (selectedWords.size() < 30 && !allWords.isEmpty()) {
                int randomIndex = random.nextInt(allWords.size());
                selectedWords.add(allWords.get(randomIndex));
            }
            
            System.out.println("✅ Tạo thành công " + selectedWords.size() + " từ vựng mẫu cho level " + level);
            return selectedWords;
        }
    }
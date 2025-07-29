package uth.edu.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.PropertySource;
import uth.edu.Models.Vocabulary;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource(value = "classpath:gemini.properties", encoding = "UTF-8")
public class VocabularyService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public List<Vocabulary> getVocabularyFromAI(String level) throws IOException {
        // Kiểm tra API key
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IOException("Gemini API key không được cấu hình trong gemini.properties");
        }

        try {
            String prompt = String.format(
                    "Generate exactly 30 English words at %s level (CEFR) for vocabulary learning. " +
                            "For each word, provide the word, phonetic transcription (IPA), and meaning in Vietnamese. " +
                            "Return ONLY a valid JSON array in this exact format: " +
                            "[{\"word\": \"apple\", \"phonetic\": \"/ˈæpl/\", \"meaning\": \"quả táo\"}, " +
                            "{\"word\": \"book\", \"phonetic\": \"/bʊk/\", \"meaning\": \"sách\"}, ...] " +
                            "Make sure it's exactly 30 words and valid JSON format.",
                    level
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

            if (vocabularyData.size() != 30) {
                throw new IOException("Gemini API returned " + vocabularyData.size() + " words instead of 30");
            }

            return vocabularyData;

        } catch (Exception e) {
            throw new IOException("Failed to fetch vocabulary from Gemini API: " + e.getMessage(), e);
        }
    }
}
package uth.edu.Configs;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class EnglishLearningSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Khi client vừa kết nối, gửi 30 từ vựng (giả lập hoặc từ AI)
        List<Map<String, String>> vocabularyData = getVocabularySample();
        String json = objectMapper.writeValueAsString(vocabularyData);
        session.sendMessage(new TextMessage(json));
    }

    private List<Map<String, String>> getVocabularySample() {
        List<Map<String, String>> list = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            Map<String, String> word = new HashMap<>();
            word.put("word", "word" + i);
            word.put("phonetic", "/ˈwɜːd" + i + "/");
            word.put("meaning", "Nghĩa số " + i);
            // Cấp độ ngẫu nhiên: A1, A2, B1, B2
            String[] levels = {"A1", "A2", "B1", "B2"};
            word.put("level", levels[new Random().nextInt(levels.length)]);
            list.add(word);
        }

        return list;
    }
}

package uth.edu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uth.edu.Models.Vocabulary;
import uth.edu.Services.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
public class VocabularyController {

    @Autowired
    private VocabularyService vocabularyService;

    @GetMapping("/vocabulary-level")
    public String showLevelSelection() {
        return "vocabulary_level_selection"; // Trang chọn level
    }

    @GetMapping("/vocabulary")
    public String getVocabulary(@RequestParam(defaultValue = "A1") String level, Model model) {
        List<Vocabulary> vocabularies = vocabularyService.getVocabularyFromAI(level);
        model.addAttribute("vocabularies", vocabularies);
        model.addAttribute("selectedLevel", level);
        return "vocabulary"; // Tên template HTML (vocabulary.html)
    }
}
package uth.edu.Models;

import jakarta.persistence.Entity;


public class Vocabulary {
    private String word;
    private String phonetic;
    private String meaning;
    private String level; // A1, A2, B1, B2

    // Default constructor
    public Vocabulary() {
    }

    // Constructor with 3 parameters (for backward compatibility)
    public Vocabulary(String word, String phonetic, String meaning) {
        this.word = word;
        this.phonetic = phonetic;
        this.meaning = meaning;
    }

    // Constructor with 4 parameters
    public Vocabulary(String word, String phonetic, String meaning, String level) {
        this.word = word;
        this.phonetic = phonetic;
        this.meaning = meaning;
        this.level = level;
    }

    // Getters & Setters

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }
}
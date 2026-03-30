package com.chatbot.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NLPProcessor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "is", "it", "in", "on", "at", "to", "for",
            "of", "and", "or", "but", "was", "are", "be", "been", "being"
    ));
    
    public List<String> tokenize(String input) {
        if (input == null || input.isBlank()) return new ArrayList<>();
        String cleaned = input.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "").trim();
        return Arrays.stream(cleaned.split("\\s+"))
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    public String stem(String word) {
        if (word == null) throw new IllegalArgumentException("Word must not be null.");
        if (word.length() <= 3) return word;
        if (word.endsWith("ing") && word.length() > 6) return word.substring(0, word.length() - 3);
        if (word.endsWith("ed")  && word.length() > 4) return word.substring(0, word.length() - 2);
        if (word.endsWith("ly")  && word.length() > 4) return word.substring(0, word.length() - 2);
        if (word.endsWith("es")  && word.length() > 4) return word.substring(0, word.length() - 2);
        if (word.endsWith("s")   && word.length() > 3) return word.substring(0, word.length() - 1);
        return word;
    }

    public List<String> removeStopWords(List<String> tokens) {
        return tokens.stream()
                .filter(t -> !STOP_WORDS.contains(t))
                .collect(Collectors.toList());
    }

    public String processInput(String input) {
        if (input == null || input.isBlank()) return "";
        List<String> tokens    = tokenize(input);
        List<String> filtered  = removeStopWords(tokens);
        List<String> stemmed   = filtered.stream().map(this::stem).collect(Collectors.toList());
        return String.join(" ", stemmed);
    }

    public String understandQuery(String query) {
        if (query == null || query.isBlank()) return "Please enter a message.";
        String processed = processInput(query);
        if (processed.contains("hello") || processed.contains("hi"))
            return "greeting";
        if (processed.contains("bye") || processed.contains("goodbye"))
            return "farewell";
        if (processed.contains("help"))
            return "help";
        if (processed.contains("password") || processed.contains("reset"))
            return "password_reset";
        return processed;
    }
}

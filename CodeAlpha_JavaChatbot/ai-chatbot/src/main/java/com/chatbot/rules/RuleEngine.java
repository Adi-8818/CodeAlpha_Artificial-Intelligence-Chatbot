package com.chatbot.rules;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class RuleEngine {
    
    private final Map<String, String> rules;

    public RuleEngine() {
        rules = new LinkedHashMap<>();
        initializeRules();
    }

    private void initializeRules() {
        rules.put("password",   "You can reset your password by clicking 'Forgot Password' on the login page.");
        rules.put("reset",      "To reset your account, please visit the settings page.");
        rules.put("hello",      "Hi there! How can I assist you today?");
        rules.put("hi",         "Hello! What can I help you with?");
        rules.put("bye",        "Goodbye! Have a great day!");
        rules.put("goodbye",    "See you later! Feel free to return if you need help.");
        rules.put("help",       "Sure! What do you need help with?");
        rules.put("support",    "Our support team is available 24/7. Visit the Help section for details.");
        rules.put("name",       "I am an AI-powered chatbot here to assist you.");
        rules.put("thank",      "You're welcome! Is there anything else I can help you with?");
    }

    public String getResponse(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return "Please enter a valid message.";
        }

        String cleaned = userInput.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "").trim();

        return rules.keySet().stream()
                .filter(key -> cleaned.matches(".*\\b" + key + "\\b.*"))
                .max(Comparator.comparingInt(String::length))
                .map(rules::get)
                .orElse(null);
    }

    public void addRule(String keyword, String response) {
        if (keyword == null || keyword.isBlank() || response == null || response.isBlank()) {
            throw new IllegalArgumentException("Keyword and response must not be null or empty.");
        }
        rules.put(keyword.toLowerCase().trim(), response.trim());
    }

    public void removeRule(String keyword) {
        if (keyword != null) rules.remove(keyword.toLowerCase().trim());
    }
}

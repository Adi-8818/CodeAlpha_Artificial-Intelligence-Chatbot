package com.chatbot.faq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FAQTrainer {

    private List<FAQEntry> faqData;

    public FAQTrainer() {
        this.faqData = new ArrayList<>();
    }

    public void loadFAQData() {
        faqData.clear();
        faqData.add(new FAQEntry("What is your name?",               "I am an AI chatbot built to assist you."));
        faqData.add(new FAQEntry("How can I reset my password?",     "Click 'Forgot Password' on the login page and follow the instructions."));
        faqData.add(new FAQEntry("What can you do?",                 "I can answer FAQs, assist with common queries, and learn from examples."));
        faqData.add(new FAQEntry("How do I contact support?",        "You can contact support via the Help section in your account settings."));
        faqData.add(new FAQEntry("What are your working hours?",     "Our support team is available 24/7."));
        faqData.add(new FAQEntry("How do I update my profile?",      "Go to Settings > Profile and click Edit."));
        faqData.add(new FAQEntry("What is your privacy policy?",     "We take your privacy seriously. Please review our privacy policy for details."));
    }

    public List<FAQEntry> getFAQData() {
        return Collections.unmodifiableList(faqData);
    }

    public void addFAQEntry(String question, String answer) {
        if (question == null || question.isBlank() || answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("Question and answer must not be null or empty.");
        }
        // Prevent exact duplicate questions
        boolean duplicate = faqData.stream()
                .anyMatch(e -> e.getQuestion().equalsIgnoreCase(question.trim()));
        if (duplicate) {
            throw new IllegalArgumentException("A FAQ entry with this question already exists.");
        }
        faqData.add(new FAQEntry(question.trim(), answer.trim()));
    }

    public static class FAQEntry {
        private final String question;
        private final String answer;

        public FAQEntry(String question, String answer) {
            this.question = question;
            this.answer   = answer;
        }

        public String getQuestion() { return question; }
        public String getAnswer()   { return answer;   }

        @Override
        public String toString() {
            return "FAQEntry{question='" + question + "', answer='" + answer + "'}";
        }
    }
}

package com.chatbot.ml;

import com.chatbot.faq.FAQTrainer.FAQEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MLModel {
    
    private Map<String, String> trainingData;
    private boolean isTrained = false;

    public MLModel() {
        this.trainingData = new HashMap<>();
    }

    public void trainModel(List<FAQEntry> faqData) {
        if (faqData == null || faqData.isEmpty()) {
            System.err.println("Warning: Training data is empty. Model will not be trained.");
            return;
        }
        trainingData.clear();
        for (FAQEntry entry : faqData) {
            String normalizedQuestion = normalize(entry.getQuestion());
            trainingData.put(normalizedQuestion, entry.getAnswer());
        }
        isTrained = true;
        System.out.println("MLModel trained with " + trainingData.size() + " entries.");
    }

    public String predictResponse(String userInput) {
        if (!isTrained || trainingData.isEmpty()) {
            return "I haven't been trained yet. Please load FAQ data first.";
        }
        if (userInput == null || userInput.isBlank()) {
            return "Please provide a valid input.";
        }

        String normalizedInput = normalize(userInput);

        if (trainingData.containsKey(normalizedInput)) {
            return trainingData.get(normalizedInput);
        }

        String bestMatch = null;
        double bestScore = 0.0;

        String[] inputTokens = normalizedInput.split("\\s+");

        for (Map.Entry<String, String> entry : trainingData.entrySet()) {
            double score = similarityScore(inputTokens, entry.getKey().split("\\s+"));
            if (score > bestScore) {
                bestScore = score;
                bestMatch = entry.getValue();
            }
        }

        if (bestScore >= 0.3 && bestMatch != null) {
            return bestMatch;
        }

        return null;
    }

    private double similarityScore(String[] inputTokens, String[] questionTokens) {
        int matches = 0;
        for (String token : inputTokens) {
            for (String qToken : questionTokens) {
                if (token.equals(qToken)) {
                    matches++;
                    break;
                }
            }
        }
        int maxLen = Math.max(inputTokens.length, questionTokens.length);
        return maxLen == 0 ? 0 : (double) matches / maxLen;
    }

    private String normalize(String text) {
        return text.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "").trim();
    }

    public boolean isTrained() {
        return isTrained;
    }
}

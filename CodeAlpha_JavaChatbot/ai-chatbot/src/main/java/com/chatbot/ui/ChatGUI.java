package com.chatbot.ui;

import com.chatbot.ml.MLModel;
import com.chatbot.nlp.NLPProcessor;
import com.chatbot.rules.RuleEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatGUI {

    private JFrame frame;
    private JTextArea chatArea;
    private JTextField userInputField;
    private JButton sendButton;

    private final NLPProcessor nlpProcessor;
    private final MLModel mlModel;
    private final RuleEngine ruleEngine;

    public ChatGUI(NLPProcessor nlpProcessor, MLModel mlModel, RuleEngine ruleEngine) {
        this.nlpProcessor = nlpProcessor;
        this.mlModel      = mlModel;
        this.ruleEngine   = ruleEngine;
        
    }

    public void initialize() {
        frame = new JFrame("AI Chatbot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 650);
        frame.setLayout(new BorderLayout(5, 5));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        userInputField = new JTextField();
        userInputField.setFont(new Font("Arial", Font.PLAIN, 14));
        sendButton = new JButton("Send");

        inputPanel.add(userInputField, BorderLayout.CENTER);
        inputPanel.add(sendButton,     BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        frame.add(inputPanel, BorderLayout.SOUTH);

        chatArea.append("Bot: Hello! How can I assist you today?\n\n");

        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUserInput();
            }
        };
        userInputField.addActionListener(sendAction);
        sendButton.addActionListener(sendAction);

        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }

    private void handleUserInput() {
        String userInput = userInputField.getText().trim();
        if (userInput.isEmpty()) return;

        chatArea.append("You: " + userInput + "\n");
        userInputField.setText("");

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return getResponse(userInput);
            }

            @Override
            protected void done() {
                try {
                    String response = get();
                    chatArea.append("Bot: " + response + "\n\n");
                    // Auto-scroll to latest message
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                } catch (Exception ex) {
                    chatArea.append("Bot: Sorry, an error occurred.\n\n");
                }
            }
        };
        worker.execute();
    }

    private String getResponse(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return "Please enter a message.";
        }

        String processedInput = nlpProcessor.processInput(userInput);

        String response = ruleEngine.getResponse(processedInput);

        if (response == null) {
            response = mlModel.predictResponse(processedInput);
        }

        if (response == null || response.isBlank()) {
            response = "I'm sorry, I don't understand that. Could you rephrase?";
        }

        return response;
    }
}

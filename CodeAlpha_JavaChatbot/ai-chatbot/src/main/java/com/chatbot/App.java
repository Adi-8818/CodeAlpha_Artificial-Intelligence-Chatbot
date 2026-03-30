package com.chatbot;

import com.chatbot.faq.FAQTrainer;
import com.chatbot.ml.MLModel;
import com.chatbot.nlp.NLPProcessor;
import com.chatbot.rules.RuleEngine;
import com.chatbot.ui.ChatGUI;
import com.chatbot.ui.WebInterface;

import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        
        NLPProcessor nlpProcessor = new NLPProcessor();
        RuleEngine   ruleEngine   = new RuleEngine();
        MLModel      mlModel      = new MLModel();
        FAQTrainer   faqTrainer   = new FAQTrainer();

        faqTrainer.loadFAQData();
        if (faqTrainer.getFAQData().isEmpty()) {
            System.err.println("Warning: No FAQ data loaded. Chatbot responses may be limited.");
        }

        mlModel.trainModel(faqTrainer.getFAQData());

        WebInterface webInterface = new WebInterface(nlpProcessor, mlModel, ruleEngine);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            webInterface.stopServer();
        }));

        String mode = args.length > 0 ? args[0].toLowerCase() : "--gui";

        switch (mode) {
            case "--web":
                System.out.println("Starting in Web mode...");
                webInterface.startServer();
                break;

            case "--both":
                System.out.println("Starting in GUI + Web mode...");
                new Thread(webInterface::startServer, "WebServer-Thread").start();
                launchGUI(nlpProcessor, mlModel, ruleEngine);
                break;

            case "--gui":
            default:
                System.out.println("Starting in GUI mode...");
                launchGUI(nlpProcessor, mlModel, ruleEngine);
                break;
        }
    }

    private static void launchGUI(NLPProcessor nlpProcessor, MLModel mlModel, RuleEngine ruleEngine) {
        SwingUtilities.invokeLater(() -> {
            ChatGUI chatGUI = new ChatGUI(nlpProcessor, mlModel, ruleEngine);
            chatGUI.initialize();
        });
    }
}

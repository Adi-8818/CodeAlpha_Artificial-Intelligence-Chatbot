package com.chatbot.ui;

import com.chatbot.ml.MLModel;
import com.chatbot.nlp.NLPProcessor;
import com.chatbot.rules.RuleEngine;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import spark.Spark;

public class WebInterface {

    private final NLPProcessor nlpProcessor;
    private final MLModel mlModel;
    private final RuleEngine ruleEngine;
    private final Gson gson = new Gson();

    public WebInterface(NLPProcessor nlpProcessor, MLModel mlModel, RuleEngine ruleEngine) {
        this.nlpProcessor = nlpProcessor;
        this.mlModel      = mlModel;
        this.ruleEngine   = ruleEngine;
    }
    
    public void startServer() {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "4567"));
        Spark.port(port);

        Spark.before((req, res) -> {
            res.header("Access-Control-Allow-Origin",  "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.type("application/json");
        });

        Spark.options("/*", (req, res) -> {
            res.status(200);
            return "OK";
        });

        Spark.get("/health", (req, res) -> {
            JsonObject json = new JsonObject();
            json.addProperty("status", "running");
            return json.toString();
        });

        Spark.post("/chat", (req, res) -> {
            try {
                JsonObject body = gson.fromJson(req.body(), JsonObject.class);

                if (body == null || !body.has("message")) {
                    res.status(400);
                    return errorJson("Request body must contain a 'message' field.");
                }

                String userInput = body.get("message").getAsString().trim();
                if (userInput.isEmpty()) {
                    res.status(400);
                    return errorJson("'message' must not be empty.");
                }

                String response = getResponse(userInput);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("response", response);
                return jsonResponse.toString();

            } catch (Exception e) {
                res.status(500);
                return errorJson("Internal server error: " + e.getMessage());
            }
        });

        Spark.exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.type("application/json");
            res.body(errorJson("Unexpected error: " + e.getMessage()));
        });

        Spark.awaitInitialization();
        System.out.println("Web server started on port " + port);
        System.out.println("POST /chat  — send { \"message\": \"your text\" }");
        System.out.println("GET  /health — check server status");
    }

    private String getResponse(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return "Please provide a valid message.";
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

    private String errorJson(String message) {
        JsonObject json = new JsonObject();
        json.addProperty("error", message);
        return json.toString();
    }

    public void stopServer() {
        Spark.stop();
    }
}

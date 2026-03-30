# 🤖 AI Chatbot — Java NLP & Machine Learning

A Java-based chatbot with a three-layer response pipeline: **Rule Engine → NLP Processor → ML Model**, available as both a Swing desktop GUI and a Spark Java REST API.

---

## 📁 Project Structure

```
ai-chatbot/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── chatbot/
                    ├── App.java                  # Entry point
                    ├── faq/
                    │   └── FAQTrainer.java        # FAQ data loader & store
                    ├── ml/
                    │   └── MLModel.java           # Token similarity ML model
                    ├── nlp/
                    │   └── NLPProcessor.java      # Tokenizer, stemmer, pipeline
                    ├── rules/
                    │   └── RuleEngine.java        # Keyword rule matching
                    └── ui/
                        ├── ChatGUI.java           # Swing desktop interface
                        └── WebInterface.java      # Spark REST API interface
```

---

## ⚙️ Prerequisites

| Tool | Minimum Version |
|------|----------------|
| Java (JDK) | 17+ |
| Maven | 3.8+ |

---

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/your-username/ai-chatbot.git
cd ai-chatbot
```

### 2. Build the project
```bash
mvn clean package
```
This produces a fat JAR at `target/ai-chatbot-1.0-SNAPSHOT.jar` with all dependencies included.

### 3. Run the chatbot

**GUI mode (default):**
```bash
java -jar target/ai-chatbot-1.0-SNAPSHOT.jar --gui
```

**Web API mode:**
```bash
java -jar target/ai-chatbot-1.0-SNAPSHOT.jar --web
```

**Both simultaneously:**
```bash
java -jar target/ai-chatbot-1.0-SNAPSHOT.jar --both
```

---

## 🖥️ GUI Mode

A Swing desktop window opens with a chat area and input field. Type a message and press **Enter** or click **Send**.

- Responses are generated on a background thread (no UI freezing)
- Chat auto-scrolls to the latest message
- Close the window to exit

---

## 🌐 Web API Mode

The REST server starts on port **4567** by default.

### Override the port
```bash
export PORT=8080
java -jar target/ai-chatbot-1.0-SNAPSHOT.jar --web
```

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/chat`  | Send a message, receive a response |
| `GET`  | `/health`| Check if the server is running |

### Example: Send a message
```bash
curl -X POST http://localhost:4567/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "How do I reset my password?"}'
```

**Response:**
```json
{
  "response": "Click 'Forgot Password' on the login page and follow the instructions."
}
```

### Example: Health check
```bash
curl http://localhost:4567/health
```
**Response:**
```json
{ "status": "running" }
```

---

## 🧠 How the Response Pipeline Works

Every user message passes through three layers in order:

```
User Input
    │
    ▼
┌─────────────────┐
│  NLPProcessor   │  tokenize → remove stop words → stem → rejoin
└────────┬────────┘
         │ processed text
         ▼
┌─────────────────┐
│   RuleEngine    │  keyword + word-boundary matching (deterministic)
└────────┬────────┘
         │ null if no rule matched
         ▼
┌─────────────────┐
│    MLModel      │  Jaccard token-similarity scoring against FAQ data
└────────┬────────┘
         │ null if score < 0.3
         ▼
   Default fallback: "I'm sorry, I don't understand that."
```

---

## 📚 Adding FAQ Data

Edit `FAQTrainer.java` to add static entries:
```java
faqData.add(new FAQEntry("How do I cancel my subscription?",
                          "Go to Settings > Billing > Cancel Subscription."));
```

Or add entries dynamically at runtime:
```java
faqTrainer.addFAQEntry("What is your refund policy?",
                        "We offer a 30-day money-back guarantee.");
```

---

## ➕ Adding Rules

Edit `RuleEngine.java` to add static keyword rules:
```java
rules.put("refund", "We offer a 30-day money-back guarantee.");
```

Or add rules dynamically:
```java
ruleEngine.addRule("shipping", "Standard shipping takes 3–5 business days.");
```

---

## 🧪 Running Tests

```bash
mvn test
```

Tests use **JUnit 5** and **Mockito**. Place test classes under:
```
src/test/java/com/chatbot/
```

---

## 📦 Dependencies

| Library | Purpose |
|---------|---------|
| Spark Java 2.9.4 | Lightweight REST web server |
| Gson 2.10.1 | JSON parsing for web requests/responses |
| TensorFlow Core 0.4.0 | ML foundation (extensible) |
| Apache Commons Lang 3.14.0 | String utilities |
| JUnit 5 | Unit testing |
| Mockito 5 | Mocking for tests |

---

## 🐛 Known Limitations

- The ML model uses lightweight token-similarity matching, not a trained neural network. Extend `MLModel.java` to integrate TensorFlow for deep learning.
- Stemming is rule-based and basic. Replace with Porter Stemmer or Apache OpenNLP for production use.
- FAQ data is currently hard-coded. Extend `FAQTrainer.loadFAQData()` to read from a JSON file or database.

---

## 🔮 Roadmap

- [ ] Load FAQ data from JSON / database
- [ ] Integrate TensorFlow model for intent classification
- [ ] Add conversation history / context tracking
- [ ] Add user session management in Web API
- [ ] Docker support
- [ ] CI/CD pipeline with GitHub Actions

---

## 📄 License

MIT License — free to use, modify, and distribute. 

User → Spring Security → Controller → Service → Gemini API
                                      ↓
                                   MySQL DB

Flow:

1: User registers & logs in
2: User sends a message
3: Chat history is fetched from database
4: History + new message is sent to Gemini
5: AI response is stored in DB
6: Updated conversation is rendered in UI

Project Structure :

com.example.demo
│
├── config
│   └── SecurityConfig.java
│
├── controller
│   ├── AuthController.java
│   └── ChatController.java
│
├── entity
│   ├── User.java
│   └── ChatMessage.java
│
├── repository
│   ├── UserRepository.java
│   └── ChatMessageRepository.java
│
├── service
│   └── ChatService.java
│
└── templates
    ├── login.html
    ├── register.html
    └── chat.html



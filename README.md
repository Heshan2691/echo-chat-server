# Echo Chat Server

A simple, real-time web chat app built with Java to explore network programming. Connect multiple users, share messages instantly, and see usernames in action—all via sockets and WebSockets.

**Built with ❤️ in October 2025** | [Live Demo Repo](https://github.com/Heshan2691/echo-chat-server)

## 🌟 Quick Features
- **Live Chat**: Messages broadcast to everyone in real-time.
- **Usernames**: Enter a name on join—messages show as "Alice: Hi!".
- **Smooth UI**: See your own messages right away (no waiting).
- **Join & Leave Alerts**: Know when friends connect or go.
- **Multi-Device**: Works in browser tabs or Java console.
- **Scalable**: Handles many users with efficient code.

## 🛠 Tech Highlights
- **Java 17+**: Sockets, NIO, and multi-threading for the backend.
- **WebSockets**: For browser-friendly real-time magic.
- **HTML/JS**: Clean frontend—no fancy frameworks.
- **Libraries**: Java-WebSocket & SLF4J (lightweight).

## 🚀 Get Started

### 1. Clone the Project
```bash
git clone https://github.com/Heshan2691/echo-chat-server.git
cd echo-chat-server
```

### 2. Grab Dependencies
Download these JARs to your folder:
- [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket/releases) (`Java-WebSocket-1.6.0.jar`).
- [SLF4J API](https://www.slf4j.org/download.html) (`slf4j-api-2.0.9.jar`).

### 3. Build & Run
```bash
# Compile
javac -cp ".;Java-WebSocket-1.6.0.jar;slf4j-api-2.0.9.jar" ServerWebSocket.java

# Start Server
java -cp ".;Java-WebSocket-1.6.0.jar;slf4j-api-2.0.9.jar" ServerWebSocket
```
- Server runs on port 1234: "WebSocket Server started!"

### 4. Open the Chat
- Double-click `index.html` in your browser.
- Or serve it: `python -m http.server 8000` → Visit `localhost:8000`.
- Open a few tabs, enter usernames, and start chatting!

**Pro Tip**: For console fun, run `java Server` and `java Client` in separate terminals.

## 💬 How It Works
1. Connect → Get username prompt.
2. Join: Everyone sees "Alice joined!".
3. Chat: Type "Hello" → You see "You: Hello" (instant); others see "Alice: Hello".
4. Leave: Type "/exit" → "Alice left." pops up.

**Sample Chat Flow**:
```
Alice: Hey team!
You: What's up?  (your view)
Bob: All good!   (broadcast)
```

## 🔧 Challenges & Wins
- **Real-Time Feel**: Solved echo issues with local JS updates.
- **Concurrency**: Used thread-safe maps to avoid mix-ups.
- **Browser Limits**: WebSockets bridged Java sockets seamlessly.
- **Efficiency**: NIO keeps it snappy for 10+ users.

## 🤝 Let's Improve It
- Fork the repo.
- Add a feature (e.g., emojis?).
- Submit a pull request—happy to chat!

## 📄 License
MIT License—use, tweak, share freely. See [LICENSE](LICENSE).

## 👋 About Me
Built by [Heshan](https://github.com/Heshan2691) for fun and learning. Got questions? Open an issue! 

*Star if you like it* ⭐

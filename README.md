# R-D-SMP (Roll & Death) - High-Performance Game Engine

## 🚀 Executive Summary
**Roll & Death** is a large-scale, custom-engineered Minecraft Survival Multiplayer (SMP) platform serving a community of **800+ active users**. As the Lead Developer, I architected and built a robust server-side plugin that transforms the base game into a complex RPG with roguelike elements.

The project is deployed in partnership with **Teramont**, leveraging enterprise-grade hosting to support high-concurrency gameplay, custom physics simulations, and persistent state management.

## 🏆 Key Technical Achievements

### 🔧 Custom Domain-Specific Language (DSL) & Scripting Engine
To enable rapid content iteration without code deployment, I designed and implemented a **bespoke scripting engine** capable of parsing and executing complex game logic defined in YAML.
*   **Interpreter Design**: Built a recursive interpreter using the **Command Pattern** and **Java Reflection** to dynamically map configuration keys to executable Java `Function` interfaces.
*   **Performance**: Optimized for O(1) lookup times using `ConcurrentHashMap` registries (`ActionRegistrar`), ensuring zero latency impact during gameplay ticks (50ms budget).
*   **Scope Management**: Implemented a `ScopeRegistry` to handle variable persistence and state transfer between execution contexts (e.g., passing damage data from a projectile event to a player status effect).

### ⚡ High-Concurrency & Asynchronous Systems
Engineered a non-blocking architecture to maintain a stable 20 TPS (Ticks Per Second) under heavy load.
*   **Embedded Microservice**: Integrated a lightweight **HTTP Server** (`com.sun.net.httpserver`) directly within the plugin process. It runs on a dedicated thread pool (`ExecutorService`) to serve real-time JSON game state to an external React frontend without blocking the main game loop.
*   **Async I/O Integration**: Developed a `DiscordWebhookService` using Java 11's `HttpClient` and `CompletableFuture` to handle cross-platform chat synchronization and event logging asynchronously.
*   **Thread Safety**: Applied rigorous concurrency controls using `ConcurrentHashMap` and atomic operations to manage shared state between the async web workers and the synchronous game thread.

### 🏗️ Scalable Software Architecture
Adopted industry-standard design patterns to ensure maintainability and testability of the codebase.
*   **Dependency Injection (DI)**: Utilized a manual DI approach with a **Service Locator** pattern (`Manager` classes) to decouple core systems (`LifeManager`, `TeamManager`, `GameManager`).
*   **Polymorphism**: Designed a flexible `RoleManager` system where player classes (Vampire, Engineer, etc.) share a common interface but implement unique behaviors, adhering to the **Open/Closed Principle**.
*   **Fluent Builders**: Created custom Builder classes for constructing complex JSON payloads and UI components, improving code readability and reducing boilerplate.

## 🎮 Complex Gameplay Systems
*   **Daily RNG Mechanics**: A centralized "Roll" system that procedurally generates daily modifiers, altering global physics (gravity, solar damage) and player attributes.
*   **Permadeath Lifecycle**: A strict state machine managing player lives, bans, and "reanimation" rituals, requiring persistent data storage and edge-case handling.
*   **Custom Physics**: Implemented vector-based projectile logic and particle systems (`ScriptedProjectileService`) to create custom spells and weapons not native to the game engine.

## 🛠️ Technology Stack

| Category | Technologies |
|----------|--------------|
| **Languages** | **Java 17+** (Records, Pattern Matching, Streams API) |
| **Build Tool** | **Gradle** (Kotlin DSL) for dependency management and build automation |
| **Frameworks** | **Spigot/Paper API** (Server Backend), **Netty** (Networking) |
| **Libraries** | `GSON` (Serialization), `Adventure` (UI/Components), `Lombok` |
| **Concepts** | Multithreading, Reflection, REST API Design, Event-Driven Architecture |
| **Infrastructure** | Linux (Deployment), Git (Version Control) |

---
*Designed and Developed by [Your Name]*
*Hosted in partnership with Teramont*

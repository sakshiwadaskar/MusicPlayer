# 🎵 MusicHub — Design Pattern–Driven Music Player

##  Software Design Patterns  

---

## 🚀 Project Overview

**JAMSYNC** is a console-based, Spotify-inspired music player that demonstrates the application of **10 fundamental software design patterns** and core **object-oriented programming principles**.

Designed with **scalability, modularity, and real-world constraints** in mind, JAMSYNC supports role-based access (Standard vs Premium users), intelligent API rate limiting, playlist management, and playback — all simulated via CSV-based data ingestion.

---

## 🧠 Problem Statement

JAMSYNC addresses the challenge of building a realistic and maintainable music player that:

- Supports **browsing, playback, and playlist management**
- Enforces **intelligent rate limiting**:
  - Standard users → Fixed Window strategy
  - Premium users → Token Bucket strategy
- Processes **CSV input** to simulate real-world API usage
- Implements a **scalable architecture** using:
  - 10 classical **Design Patterns**
  - Clean, extensible **OOP principles**

---

## 🧱 OOP Concepts Applied

- **Encapsulation:** Commands, Player APIs, and RateLimiter isolate responsibilities.
- **Abstraction:** Interfaces used for Strategy, Command, State, Player modules.
- **Inheritance:** Shared behaviors extended in Decorators, Commands, States.
- **Polymorphism:** Dynamic behavior via Strategy, State, Decorator switching.
- **Composition:** Playlists contain tracks; proxies wrap players.
- **SOLID Principles:**
  - *Single Responsibility Principle (SRP)*
  - *Open-Closed Principle (OCP)*

---

## 🧩 Design Patterns Implemented

| Pattern Type   | Pattern        | Purpose |
|----------------|----------------|---------|
| Behavioral     | **Strategy**   | Rate limiting (Fixed Window / Token Bucket) |
| Behavioral     | **Observer**   | Notifications when user exceeds rate limit |
| Behavioral     | **Command**    | Encapsulates actions (Play, Pause, Next, etc.) |
| Behavioral     | **State**      | Handles user interaction states (Browsing, Playing, Paused) |
| Creational     | **Factory**    | Creates player types |
| Creational     | **Singleton**  | Ensures one global instance of player |
| Creational     | **Builder**    | Constructs playlists and music metadata |
| Structural     | **Decorator**  | Enhances player output ("Now Playing", EQ) |
| Structural     | **Proxy**      | Controls access to premium features |
| Structural     | **Flyweight**  | Reuses shared song metadata for memory efficiency |

---

## 🏗️ High-Level Architecture

Modules:
- **Rate Limiting** — Strategy + Observer
- **Playback Engine** — Factory + Singleton + Decorator + Proxy
- **User Actions** — Command pattern
- **User States** — State pattern
- **Metadata Optimization** — Flyweight
- **Playlist Creation** — Builder
- **CSV Ingestion** — FileUtility → Builder + Flyweight

---

## 🔄 System Flow (N-notation Summary)

```text
N1: Launch app
N2: GUI loads CSV data via Builder + Flyweight
N3: Display songs, playlists, UI controls
N4: User action → CommandInvoker → MusicCommand
N5: Command → PlayerContext (State check) → MusicPlayerAPI
N6: Decorator wraps playback (EQ, NowPlaying)
N7: UI updates, State transitions
N8: RateLimiter applies Strategy, notifies via Observer
N9: PlaylistFactory manages playlists
N10: Loop continues


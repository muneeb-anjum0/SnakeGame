# Java Swing Snake Game

A polished **Snake game built with Java Swing**, featuring two game modes, smooth controls, dynamic difficulty, visual effects, and persistent high scores.

## Features

- **Normal Mode** with 5 progressive levels
- **Endless Mode** with edge wrapping
- Smooth **Arrow Keys / WASD** controls
- Pause, restart, win, and game-over screens
- Dynamic speed progression
- Separate high scores for each mode
- File-based score persistence
- Custom Java2D graphics and animations
- Clean OOP and event-driven architecture

## Controls

| Action | Key |
|---|---|
| Move | Arrow Keys / WASD |
| Pause | `P` |
| Restart | `R` |
| Menu / Back | `Esc` |

## Run Locally

### Requirements

- JDK 8 or newer

### Compile

```bash
javac -d out src/*.java
```

### Run

```bash
java -cp out Main
```

## Game Modes

### Normal

Complete 5 increasingly difficult levels. Walls and self-collision end the game.

### Endless

Play continuously with edge wrapping and gradual speed increases. Only self-collision ends the run.

## Architecture

The project uses:

- **Inheritance** through `GameObject` and `BasePanel`
- **Polymorphism** through custom `draw()` implementations
- **Encapsulation** for game state, score, and movement logic
- **Abstraction** for reusable UI and game components
- **Event-driven programming** with Swing timers, key bindings, and listeners

## Core Classes

```text
Main
└── GameFrame
    ├── MenuPanel
    ├── GamePanel
    │   ├── Snake
    │   ├── Food
    │   └── ScoreManager
    │       └── HighScoreManager
    ├── InstructionsPanel
    ├── GameOverPanel
    └── WinPanel
```

## Tech Stack

`Java` `Swing` `Java2D` `OOP` `Event-Driven Programming` `File I/O`

## Future Ideas

Sound effects, power-ups, obstacles, themes, leaderboards, difficulty presets, and multiplayer.

## Authors

**Muneeb & Mustafa**

---

Built as a Java OOP and GUI project.

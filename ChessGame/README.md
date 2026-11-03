# Dorsa Chess Game

A Java Swing chess game built for an OOP course project.

## Requirements

- Java JDK 17 or newer (JDK 20+ recommended)

## Quick start (Windows)

Double-click **`run.bat`**, or run in PowerShell:

```powershell
.\run.bat
```

A desktop window titled **"Dorsa Chess Game"** opens with the board on the left and game info on the right.

## How to play

1. On launch, choose a **rule variant / scenario** from the dialog (see below).
2. **White moves first** — click one of your pieces, then click the destination square.
3. Click the same piece again to deselect it.
4. Valid moves are highlighted when a piece is selected.
5. The game detects **check**, **checkmate**, and **stalemate**.
6. When a pawn reaches the last rank, choose a promotion piece in the dialog.
7. Restart the app to play again after game over.

## Rule variants / scenarios

Chosen at startup from a dialog, implemented as pluggable `GameRule` and
`SpecialSquare` implementations (see `src/main/rules` and `src/main/hazards`):

- **Standard Chess** — the classic rules, unchanged.
- **King of the Hill** — standard setup, but marching your own king onto one
  of the four central squares (d4/d5/e4/e5) wins instantly.
- **Chess960 (Fischer Random)** — the back-rank order is randomized every
  game (bishops on opposite-colored squares, king between the rooks).
- **Hazard Arena** — standard setup plus a few obstacle squares (block
  movement, shown darkened with an "X") and a shield power-up (shown green
  with an "S") that grants one ply of capture-immunity to whichever piece
  picks it up.

## Run from command line

```powershell
cd "path\to\ChessGame"
javac -d out -sourcepath src src\main\main.java src\main\*.java src\main\rules\*.java src\main\hazards\*.java src\piece\*.java
java -cp out main.main
```

## Run with Maven (any IDE)

If you have Maven installed:

```powershell
mvn compile exec:java
```

## Open in an IDE

| IDE | Steps |
|-----|-------|
| **IntelliJ IDEA** | File → Open → select this folder. Run `main.main` in `src/main/main.java`. |
| **VS Code** | Install "Extension Pack for Java". Open this folder. Run using `.vscode/launch.json`. |
| **Eclipse** | File → New → Java Project → uncheck default locations → set source folder to `src`. Run `main.main`. |
| **NetBeans** | File → Open Project → select this folder. Main class: `main.main`. |

**Important:** The source root is `src/` (not `src/main/java`). Main class is `main.main`.

## Save your version

### Option 1 — Copy the folder
Copy the entire `ChessGame` folder to a backup location or USB drive.

### Option 2 — Zip it
Right-click the folder → **Send to → Compressed (zipped) folder**.

### Option 3 — Git (recommended for version history)

```powershell
cd "path\to\ChessGame"
git init
git add .
git commit -m "Dorsa chess game - updated version"
```

## Project structure

```
ChessGame/
├── src/
│   ├── main/          # Game logic, UI, demos
│   │   ├── rules/     # GameRule interface + variants (customization layer)
│   │   └── hazards/   # SpecialSquare interface + obstacle/power-up tiles
│   └── piece/         # Chess piece classes
├── out/               # Compiled classes (generated)
├── run.bat            # Windows one-click launcher
├── pom.xml            # Maven build (optional)
└── dorsa.iml          # IntelliJ module file
```

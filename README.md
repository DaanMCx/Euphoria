# Euphoria Mod

Welcome to the **Euphoria** development repository. This mod is currently focused on the Minecraft 1.12.2 ecosystem using Forge.

---

## 🛠 Setup & Development (Branch: 1.12)

To get the development environment running on your local machine, follow these steps.

### 1. Prerequisites
- **Java 17**: Required to run the Gradle build system. 
  - **Download here:** [Oracle JDK 17 Archive](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### 2. Initializing the Workspace
- **Visual Studio Code**: The repo is pre-configured for VS Code. Just follow these steps to get the workspace ready:
1.  Open the folder and install the recommended extensions.
2.  It might not load correctly the first time, as some background stuff take a while to install. Just give it some time and restart VSCode on errors.
3.  When you finally see `☕Java: Ready` in the bottom left, run the following command in the terminal:

```
./gradlew prepareRuns
```

- **IntelliJ IDEA**: If you prefer IntelliJ, run the following command in the terminal after importing the project:

```
./gradlew genIntellijRuns
```

### 3. Server Configuration & First Run
To prepare the local testing server, you must follow this exact order:
1.  **Initialize**: Run the `runServer` configuration once. It will immediately close.
2.  **Accept EULA**: Navigate to `run/server/eula.txt` and set `eula=true`.
3.  **Start the server again**: Run the `runServer` configuration a second time and let the server fully start. Once it has finished loading, stop/kill the server process.
4.  **Configure Properties**: Open `run/server/server.properties` and set the following values. Note that only the first setting is required for local debugging — the others are optional conveniences.
  * `online-mode=false` — Required for local debug connections (prevents Mojang auth checks).
  * `gamemode=1` (optional) — Starts the player in Creative mode.
  * `level-seed=700` (optional) — For a nice Plains environment.
5.  **Delete world folder**: If you changed the `level-seed` in the previous step, make sure to delete the `run/server/world` folder that was already generated.
5.  **Launch**: You can now use the **"Debug Fullstack"** configuration for rigorous testing.

### 4. Connecting
Once both instances are loaded, join the server from the client using the IP address:
`localhost`

---

## 💡 Troubleshooting

### `Unable to read a class file correctly` / `There was a problem reading the entry module-info.class in the jar (blah blah)` / `probably a corrupt zip` when starting the game

Something on the classpath is compiled to a classfile format newer than the one used in Java 8. Because it breaks the loading process, classes in that jar will not be visible to mods, but it's otherwise harmless.

To me, this happens to a bunch of `asm-6.x`-related jars, which seem to be a transitive dependency of MergeTool. That's fine though, we don't need those, because ForgeGradle provides its own perfectly fine copy of ASM 5.2!
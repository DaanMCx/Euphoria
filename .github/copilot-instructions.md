# Euphoria - Minecraft Forge 1.12.2 Mod

## Project Overview
A Minecraft mod adding realistic drug mechanics and effects, inspired by Psychedelicraft. Built with Forge 1.12.2 using ForgeGradle 5.1 and Java 8.

## Architecture

### Core Components
- **Main Mod Class**: [Euphoria.java](../src/main/java/nl/daanmc/euphoria/Euphoria.java) - Central entry point using `@Mod` and `@EventBusSubscriber` annotations
- **Content Registration**: Nested `Content` class with `@GameRegistry.ObjectHolder` for static access to registered objects
- **Custom Registry System**: `Drug` extends `IForgeRegistryEntry.Impl<Drug>` with custom registry event in `onNewRegistry()`

### Registration Pattern
All content (blocks, items, drugs) is registered via `@SubscribeEvent` methods on registry events:
```java
@SubscribeEvent
public static void onItemRegister(RegistryEvent.Register<Item> event)
```
Objects are stored in both static `@GameRegistry.ObjectHolder` fields AND `ArrayList<>` collections for iteration.

### Client-Server Architecture
- **Sided Proxies**: `ClientProxy` / `ServerProxy` via `@SidedProxy` annotation
- **Networking**: Custom packet system in `util/NetworkHandler.java` using `SimpleNetworkWrapper`
  - `MsgSyncPDCap`: Syncs player drug capability server→client
  - `MsgReqConfPDCap`: Client requests capability sync/confirmation
  - `MsgDrugPresence`: Handles drug effect application

### Capability System
Player drug state tracked via `IPlayerDrugsCap` capability ([PlayerDrugsCap.java](../src/main/java/nl/daanmc/euphoria/util/capabilities/PlayerDrugsCap.java)):
- Client-side tick scheduling via `ConcurrentHashMap<Long, ArrayList<ITask>>`
- Drug breakdown mechanics with S-curve calculations
- `DrugPresence` system for timed effects (comeUp, peak, comeDown)
- Must sync between client/server using network messages

### Drug System
- **Drug Registry**: Custom Forge registry created in `onNewRegistry()` with `RegistryBuilder`
- **Drug Items**: Implement `IDrug` interface (smokable, edible, drinkable, rollable variants)
- **Effects**: Applied via `DrugPresence` attached to items, scheduled as `ITask` instances
- **Breakdown**: Time-based degradation (e.g., THC: 6000 ticks) with configurable curves

### World Generation
- **Surface Generators**: `EuphoriaSurfaceGenerator<T extends Block & ISurfaceGen>` for spawning plants
- **Chunk-based**: Spawns controlled by `chunkSpawnRate` and `maxGroupSize` parameters
- Cannabis plants spawn naturally in overworld with biome-aware foliage coloring

## Development Workflows

### Build & Run
```powershell
./gradlew build              # Initial setup (takes ~6 minutes)
./gradlew runClient          # Launch game client
./gradlew runServer          # Launch dedicated server
```

### IDE Setup (IntelliJ)
```powershell
./gradlew genIntellijRuns    # Generate run configurations
```
**CRITICAL**: Manually set JDK 8 in run configs (`Edit Configurations...` → `JDK or JRE`). Gradle provisions Java 8 toolchain but doesn't auto-configure IDE runs.

### Resource Quirk
Resources MUST be in `build/classes/java/main/` NOT `build/resources/main/` due to Forge 1.12 classpath expectations. Handled by:
```gradle
sourceSets.all { it.output.resourcesDir = it.output.classesDirs.getFiles().iterator().next() }
```

## Code Conventions

### Package Structure
```
nl.daanmc.euphoria/
├── block/          # Block implementations
├── item/           # Item implementations with drug-specific variants
├── tileentity/     # TileEntity classes (DryingTable, CannabisStrain)
├── util/           # Utilities, capabilities, networking, tasks
├── client/         # Client-only rendering code
└── worldgen/       # World generation features
```

### Item Patterns
- Drug items extend `Item` and implement `IDrug` interface
- Use `setMaxDamage()` for durability-based consumption tracking
- `onUsingTick()` / `onItemUseFinish()` handle consumption with multipliers
- Attach `DrugPresence` via `attachDrugPresence()` in `postInit()`

### Block Patterns
- Plants extend `BlockBush` and implement `ISurfaceGen` for worldgen
- Large plants use multi-block structures (see `BlockLargeDrugPlant`)
- Functional blocks use TileEntities (`BlockDryingTable` → `TileEntityDryingTable`)

### Event Handling
- Main events in `util/EventHandler.java` registered via `@Mod.EventBusSubscriber`
- Client tick handling for capability updates and task execution
- Player login/logout for capability attachment and syncing

### Resource Naming
- Registry names match translation keys: `"cannabis_bud"` → `euphoria:cannabis_bud`
- Textures: `assets/euphoria/textures/{blocks,items,gui}/`
- Models: JSON in `assets/euphoria/models/{block,item}/`
- Blockstates: `assets/euphoria/blockstates/{block_name}.json`

## Common Issues

### Java Version
Minecraft 1.12.2 requires Java 8. Using Java 9+ causes `ClassCastException` on launch.

### ForgeGradle Daemon
If setup fails with `ProjectScopeServices has been closed`, run:
```powershell
./gradlew stop
```

### Missing Resources
If `mcmod.info` or assets don't load, verify `sourceSets.all` configuration in [build.gradle](../build.gradle).

## Key Files
- [Euphoria.java](../src/main/java/nl/daanmc/euphoria/Euphoria.java) - Mod initialization and registration
- [build.gradle](../build.gradle) - Forge 1.12.2 setup with Java 8 toolchain
- [EventHandler.java](../src/main/java/nl/daanmc/euphoria/util/EventHandler.java) - Client/server event handling
- [PlayerDrugsCap.java](../src/main/java/nl/daanmc/euphoria/util/capabilities/PlayerDrugsCap.java) - Player drug state capability
- [NetworkHandler.java](../src/main/java/nl/daanmc/euphoria/util/NetworkHandler.java) - Client-server packet handling

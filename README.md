# NoobGuard

NoobGuard is a **server-side only** Minecraft NeoForge mod (1.21.x) designed to provide a "peace mode" for specific players, allowing them to explore and interact with the world without facing hostile mobs when they are alone or in the presence of other "guarded" players. This is especially useful for players who prefer a less challenging experience or for younger players.

## Features

- **Guarded Players**: Prevents hostile mobs from targeting or damaging specific players when they are alone or only in the company of other guarded players.
- **Hostile Entity Management**:
    - Prevents hostile mobs from targeting or damaging guarded players.
    - Prevents hostile mobs from spawning near guarded players if configured.
    - Allows the option to drop loot from hostile mobs killed by the plugin, or simply removes them without loot.
- **Configurable Alone Distance**: Set a configurable distance in blocks around a player, defining how far they need to be from other non-guarded players to trigger peace mode.

## Installation

1. Install Minecraft NeoForge.
2. Download the NoobGuard mod file.
3. Place the NoobGuard mod file into the `mods` folder of your Minecraft directory.
4. You will find the configuration file `noobguard-common.toml` in the `config` folder of your Minecraft Server directory.

## Usage

1. After installation, launch the game.
2. Add the UUIDs of guarded players in the `noobguard-common.toml` config file, or edit via the config screen in-game if supported.

## Configuration Options

NoobGuard offers the following customizable options:

- **preventSpawning**: Boolean (`false` by default). Set to `true` to prevent hostile entities from spawning near guarded players.
- **dropLoot**: Boolean (`false` by default). Set to `true` to enable loot drops from hostile mobs removed by the mod.
- **aloneDistance**: Integer (default is `32` blocks). Defines the distance around a guarded player to check for other players.
- **guarded-users**: A list of UUIDs for players who should be protected. Only these players will be shielded from hostile entities.

### Example Configuration

In `noobguard.toml`:

```toml
preventSpawning = true
dropLoot = true
aloneDistance = 40
guarded-users = ["uuid1", "uuid2", "uuid3"]
```
Replace uuid1, uuid2, and uuid3 with the actual UUIDs of the players you wish to guard.

## Events

NoobGuard leverages the following events:

- LivingChangeTargetEvent: Prevents hostile mobs from targeting guarded players when peace mode is active.
- EnderManAngerEvent: Stops Enderman from targeting guarded players.
- FinalizeSpawnEvent: Prevents hostile mob spawning near guarded players.
- LivingIncomingDamageEvent: Cancels incoming damage to guarded players from hostile entities.

## Logging

NoobGuard logs actions and events such as target prevention, entity removal, and spawn prevention for debugging and monitoring purposes under the DEBUG level.

## License

This project is licensed under the MIT License. See LICENSE for details.
# Item Pickup

A Minecraft mod for **Fabric** and **NeoForge** (Minecraft 1.21.1 – 26.3) that lets players choose how they pick up
items lying on the ground.

- **Vanilla mode** – unchanged Minecraft: everything near the player is picked up automatically.
- **Manual mode** – walking over items does nothing. Look at an item and press the **pick up key** (right-click by
  default) to pick it up. Holding the key keeps picking up whatever item you look at, so a pile of drops can be
  collected by sweeping over it.
  - The item that would be picked up is **outlined** like a glowing entity and its **name** (and count) is shown in
    small text below the crosshair.
  - Items can be picked up within a configurable range, by default 2 blocks around the player (twice the area in
    which vanilla picks up items automatically). Solid blocks and entities in between hide them, grass and flowers
    do not.
  - A click on an item picks it up and does not also use the item in your hand, so you do not place a block on the
    floor by accident. Everywhere else right-click works as usual.
  - Full inventories, items that belong to another player and items that can never be picked up behave like in
    vanilla. Only the short pickup delay of freshly dropped items is skipped.
- **Toggle key** (`K` by default) switches between both modes while playing. The new mode is shown above the hotbar.

## Installation

The mod is needed on the **client** to use manual mode and on the **server** (or in singleplayer, which runs both)
for manual mode to work, since the server decides what a player picks up. Either side alone is harmless:

- A client with the mod on a server without it stays in vanilla mode and says so when toggling.
- Players without the mod can join a server with it and pick up items like in vanilla.

## Configuration

`config/itempickup.properties` is created on first start. Changes are applied automatically while the game is running.
It can also be edited in game: on NeoForge in *Mods → Item Pickup → Config*, on Fabric through
[Mod Menu](https://modrinth.com/mod/modmenu).
`range` is decided by the server: in multiplayer the server's value applies and is sent to the players. All other
settings only affect the client.

| Key           | Default   | Description                                                                                                                                                                                                    |
|---------------|-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `defaultMode` | `vanilla` | Mode when the game starts: `vanilla` or `manual`. Changing the value switches to it immediately.                                                                                                              |
| `range`       | `2.0`     | How far items can be picked up manually, in blocks around the player's body: sideways the full value, up and down half of it. `1.0` is the area in which vanilla picks up items automatically. `0` to `16`. |
| `outline`     | `true`    | Outline the item that the pick up key would pick up in manual mode.                                                                                                                                            |
| `showName`    | `true`    | Show the name of that item below the crosshair.                                                                                                                                                                |

The **toggle key** and the **pick up key** are changed in Minecraft's own key binds: *Options → Controls → Key Binds
→ Gameplay* ("Toggle Item Pickup Mode" and "Pick Up Item"). They are saved in `options.txt` like every other key.
By default the pick up key shares right-click with "Use Item/Place Block"; Minecraft may show the two as conflicting,
which is intended and can be ignored.

## Building

The project uses [Stonecutter](https://stonecutter.kikugie.dev/) to build every Minecraft version and loader from one
source tree. Each node in `settings.gradle.kts` targets one Minecraft version and declares the compatible range in
`stonecutter.properties.toml`.

```sh
./gradlew build                          # build all versions and loaders
./gradlew buildAndCollect                # copy all jars to build/libs/<mod version>/
./gradlew :1.21.1-fabric:runClient       # run a specific version (Fabric)
./gradlew :26.3-neoforge:runClient       # run a specific version (NeoForge)
```

Every push to `main` builds all versions on GitHub Actions; the jars can be downloaded from the run's
artifacts.

On **NixOS**, run the game inside the provided Nix shell so Minecraft finds its native libraries
(otherwise it crashes with `GLFW error during init: Failed to detect any supported platform`):

```sh
nix-shell --run './gradlew :1.21.1-fabric:runClient'
```

Version-specific code uses Stonecutter comments (`//? if >=1.21.9 {`). The source tree is checked in for the
`1.21.1-fabric` node; switch the active version with the `Set active project to …` Gradle tasks before editing code
for another version, and switch back to `1.21.1-fabric` (`Reset active project`) before committing.

## License

[MIT](LICENSE)

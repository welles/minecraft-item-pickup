# Changelog

All notable changes to this mod are documented in this file. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the mod follows [Semantic Versioning](https://semver.org/).

## [1.2.0] - 2026-11-03

### Added

- A pixel art icon, shown in Mod Menu, in NeoForge's mod list and at the top of the README.

## [1.1.0] - 2026-11-02

### Added

- The config screen checks every value while typing: numbers outside their range or unknown pickup modes are marked
  red, and Done stays disabled until every value is valid, listing the invalid ones in its tooltip.
- Every value in the config screen can be reset to its default with the ↺ button next to it, or all at once with
  Reset all. Nothing is saved until Done.

## [1.0.0] - 2026-10-26

First release, for Fabric and NeoForge on Minecraft 1.21.1 – 26.3.

### Added

- Two pickup modes: vanilla, and manual, where walking over items does nothing and the pick up key (right-click by
  default) picks up the item you look at. Holding the key sweeps up a pile of drops.
- The toggle key (`K` by default) switches between the modes while playing.
- In manual mode the targeted item is outlined, and its name and count are shown below the crosshair.
- Items can be picked up within a configurable range, 2 blocks by default; in multiplayer the server decides it.
- `config/itempickup.properties`, reloaded automatically and editable in game through NeoForge's mod list or
  Mod Menu.

[1.2.0]: https://github.com/welles/minecraft-item-pickup/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/welles/minecraft-item-pickup/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/welles/minecraft-item-pickup/releases/tag/v1.0.0

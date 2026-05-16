# Candy Dispenser

**A handheld candy dispenser that automatically feeds you candy when hungry, providing healing boosts when damaged. Available on Fabric and NeoForge for Minecraft 26.1.x.**

## About This Mod

The Candy Dispenser adds a smart automatic feeding system to Minecraft. Craft a candy dispenser, load it with candy, and keep it in your hotbar — it will automatically feed you when your hunger drops and provide extra saturation boosts when you take damage. Perfect for long mining sessions, exploration, or any time you want to focus on gameplay without worrying about hunger management.

The dispenser holds up to 512 pieces of candy. Candy level is shown as a durability bar that color-codes green / orange / red as the dispenser empties.

## Getting Started

### Crafting Recipe Chain
1. **Candy Mix**: Sugar + Sweet Berries (shapeless crafting, yields 2)
2. **Candy**: Smelt Candy Mix in a furnace or smoker
3. **Candy Dispenser**: Shaped recipe — iron, glass, dispenser, redstone, and candy

### How to Use
1. Craft the dispenser (starts empty)
2. With candy in your inventory, right-click while holding the dispenser to load it (up to 512 pieces)
3. Keep the loaded dispenser anywhere in your hotbar
4. The mod automatically feeds you when hungry and boosts saturation when damaged

### Configuration

**Fabric:** Open the in-game config screen via [ModMenu](https://modrinth.com/mod/modmenu) (requires [Cloth Config](https://modrinth.com/mod/cloth-config)) — toggle enabled, damage-boost, recipe availability, healing targets.

**NeoForge:** Edit `config/candy_dispenser-common.toml` in your world / instance directory. No in-game UI in this release.

### Feeding rules
- Checked once per second (20 ticks).
- Maintenance mode: when hunger < 20 and no recent damage, feeds up to 3 candies per cycle until full.
- Healing mode: triggers for 30 seconds after taking damage. Boosts saturation to 2.0 once every 5 seconds (the threshold for natural regeneration). Blocks maintenance feeding during the damage window so the dispenser prioritizes healing.

## Requirements

- Minecraft **26.1, 26.1.1, or 26.1.2**
- Java **25+**
- One of:
  - **Fabric:** Fabric Loader 0.18.4+, Fabric API 0.149.0+26.1.2
  - **NeoForge:** 26.1.x

**Optional (Fabric only):** [Cloth Config](https://modrinth.com/mod/cloth-config) and [ModMenu](https://modrinth.com/mod/modmenu) for the config GUI.

## Installation

**Fabric:**
1. Install [Fabric Loader](https://fabricmc.net/use/) for MC 26.1.x
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. (Optional) Install Cloth Config + ModMenu for the config GUI
4. Drop `candy-dispenser-fabric-<version>.jar` into your `mods/` folder

**NeoForge:**
1. Install [NeoForge 26.1.x](https://neoforged.net/)
2. Drop `candy-dispenser-neoforge-<version>.jar` into your `mods/` folder

## Building from Source

```bash
./gradlew buildAll              # builds both Fabric and NeoForge jars
./gradlew :fabric:build         # Fabric only
./gradlew :neoforge:build       # NeoForge only
./gradlew :fabric:runClient     # launch a dev client
./gradlew :neoforge:runClient   # launch a dev client
```

Output jars land in `fabric/build/libs/` and `neoforge/build/libs/`.

## License

CC0-1.0.

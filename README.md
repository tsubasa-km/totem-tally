# Totem Tally

A small Fabric mod for Minecraft 26.3 that makes Totems of Undying stack up to 64, and shows how many you carry on the HUD.

## Features

- **Totems stack everywhere.** The Totem of Undying's default max stack size is raised to 64, so totems stack in your inventory, in chests, in hoppers and as dropped items. Totems collected by a hopper from a mob farm stack too.
- **One totem per save.** When a stack of totems saves you, only one totem is used up, just like vanilla.
- **Works with existing totems.** Totems that a datapack gave an explicit `max_stack_size` of 64 lose that extra data when they are loaded, so they stack with all other totems.
- **Totem counter.** A totem icon next to the hotbar shows the total number of totems in your inventory, hotbar and offhand.

## Installation

- Requires [Fabric Loader](https://fabricmc.net/) and [Fabric API](https://modrinth.com/mod/fabric-api).
- Install it on **both the server and every client.** The stack size is part of the item itself, so a client without the mod would not know that totems stack.
- Replaces stack-size datapacks such as Stackable Totems and HUD mods such as Totem Bar. Remove those when you install this mod.

## Configuration

The counter is configured in `config/totem_tally.json` on the client:

| Option | Default | Description |
|---|---|---|
| `showHud` | `true` | Show the counter |
| `showWhenEmpty` | `false` | Keep the counter visible when you carry no totems |
| `side` | `"left"` | Side of the hotbar: `"left"` or `"right"`. The counter moves outward when the offhand slot is on the same side |
| `xOffset`, `yOffset` | `0` | Extra offset in GUI pixels |

## Building

```
./gradlew build
```

The jar is written to `build/libs/`. `./gradlew runServer` starts a test server in `run/`.

## License

MIT

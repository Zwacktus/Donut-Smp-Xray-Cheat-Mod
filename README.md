# Donut Searcher

Client-side Fabric mod for Minecraft 1.21.11. Press **H** to configure the target block, world seed, and search radius. Defaults are `minecraft:deepslate_coal_ore`, seed `6608149111735331168`, and 250 blocks. Settings are stored in `config/donut-searcher.json`. The settings screen shows how many already-loaded chunks are being scanned in the current radius.

The mod checks the seed `6608149111735331168` in singleplayer. Multiplayer servers do not send their seed to clients, so there the mod scans the actual blocks in chunks already loaded by the client without being able to verify the seed. It draws green outlines around matches and does not request or load chunks from the server.

The configured seed is used to check a singleplayer world; it does not predict ore positions. On Paper servers with Anti-Xray enabled, hidden ores may arrive at the client as fake stone, so this client scanner cannot outline them. If you administer the server and want the scanner to receive real blocks for your account, configure `anticheat.anti-xray.use-permission: true` for that world, grant yourself operator status or `paper.antixray.bypass`, and restart the server. See the [Paper Anti-Xray guide](https://docs.papermc.io/paper/anti-xray/).

Use a block registry ID such as `minecraft:diamond_ore` to select a different target. Radius can be adjusted from 16 to 512 blocks in 16-block increments.

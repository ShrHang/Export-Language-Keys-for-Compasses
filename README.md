# Export Language Keys for Compasses

![Icon](.idea/large_icon.png)

A Minecraft mod for exporting biome and structure language keys for use with ***Nature's Compass*** and ***Explorer's Compass***.

So that you can easily translate the mod's language keys into your own language.

Just run the command to generate a JSON file containing the requested language keys:
```text
/elkfc export all missing
```

Then you can find the generated files in your Minecraft game directory under `export_language_keys_for_compasses/`.

See the [wiki](https://github.com/ShrHang/Export-Language-Keys-for-Compasses/wiki) for details.

## Requirements

- In multiplayer, the mod must be installed on both **the server** and **the player running the command**.
- ***Nature's Compass*** and ***Explorer's Compass*** are **OPTIONAL**. When installed, their configured blacklists are applied to the exported entries.

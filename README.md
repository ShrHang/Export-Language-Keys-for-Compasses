# Export Language Keys for Compasses

A Minecraft mod for exporting biome and structure language keys for use with Nature's Compass and Explorer's Compass.

So that you can easily translate the mod's language keys into your own language.

Just run the command to generate a JSON file containing the requested language keys:
```text
/elkfc export all missing
```

You can find the generated files in your Minecraft game directory under `export_language_keys_for_compasses/`.

## Requirements

- In multiplayer, the mod must be installed on both the server and the player running the command.
- Nature's Compass and Explorer's Compass are optional. When installed, their configured blacklists are applied to the exported entries.

## Command

```text
/elkfc export <target> <mode> [lang]
```

The command has no permission-level requirement, but it can only be run by a player.

- `<target>`:
  - `biome` — exports biome language keys.
  - `structure` — exports structure and structure-group language keys.
  - `all` — exports both biome and structure language keys.
- `<mode>`:
  - `missing` — exports only keys missing from the selected language.
  - `all` — exports every collected language key.
- `[lang]`:
  - An optional language code such as `en_us`, `zh_cn`, or `ja_jp`.
  - If omitted, the client configuration option `defaultLanguage` is used. Its default value, `current`, follows the language currently selected in Minecraft.

Examples:

```text
/elkfc export biome missing
/elkfc export structure all fr_fr
/elkfc export all missing zh_cn
```

## Output

Files are written to:
```text
<game directory>/export_language_keys_for_compasses/
```

The filename format is:
```text
<lang>-<target>-<mode>-<timestamp>.json
```

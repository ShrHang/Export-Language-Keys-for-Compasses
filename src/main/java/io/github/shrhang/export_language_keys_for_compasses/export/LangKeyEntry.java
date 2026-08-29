package io.github.shrhang.export_language_keys_for_compasses.export;

import net.minecraft.resources.ResourceLocation;

public record LangKeyEntry(
        String key,
        String fallback,
        EntryKind kind,
        ResourceLocation sourceId,
        String sourceModId
) {
}

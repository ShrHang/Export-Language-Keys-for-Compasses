package io.github.shrhang.export_language_keys_for_compasses.collector;

import io.github.shrhang.export_language_keys_for_compasses.export.EntryKind;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyEntry;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyFormatter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class BiomeRegistryCollector {
    public static List<LangKeyEntry> collect(
            ServerLevel level,
            Predicate<ResourceLocation> biomeFilter
    ) {
        Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
        List<LangKeyEntry> entries = new ArrayList<>();

        for (ResourceLocation biomeId : biomeRegistry.keySet()) {
            if (!biomeFilter.test(biomeId)) {
                continue;
            }

            entries.add(new LangKeyEntry(
                    LangKeyFormatter.makeLangKey("biome", biomeId),
                    LangKeyFormatter.humanize(biomeId),
                    EntryKind.BIOME,
                    biomeId,
                    biomeId.getNamespace()
            ));
        }

        return entries;
    }
}

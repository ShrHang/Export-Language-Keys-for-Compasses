package io.github.shrhang.export_language_keys_for_compasses.collector;

import io.github.shrhang.export_language_keys_for_compasses.compat.OptionalCompassCompat;
import io.github.shrhang.export_language_keys_for_compasses.export.LangKeyEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public final class LanguageKeyCollector {

    public static List<LangKeyEntry> collect(ServerLevel level, String target) {
        List<LangKeyEntry> entries = new ArrayList<>();

        if ("biome".equals(target) || "all".equals(target)) {
            entries.addAll(collectBiomes(level));
        }
        if ("structure".equals(target) || "all".equals(target)) {
            entries.addAll(collectStructures(level));
        }

        return entries;
    }

    private static List<LangKeyEntry> collectBiomes(ServerLevel level) {
        Optional<Set<ResourceLocation>> allowedBiomeIds = OptionalCompassCompat.getAllowedBiomeIds(level);
        Predicate<ResourceLocation> biomeFilter = allowedBiomeIds
                .<Predicate<ResourceLocation>>map(allowedIds -> allowedIds::contains)
                .orElse(biomeId -> true);
        return BiomeRegistryCollector.collect(level, biomeFilter);
    }

    private static List<LangKeyEntry> collectStructures(ServerLevel level) {
        Optional<Set<ResourceLocation>> allowedStructureIds = OptionalCompassCompat.getAllowedStructureIds(level);
        Optional<Map<ResourceLocation, ResourceLocation>> structureGroups = OptionalCompassCompat.getStructureGroups(level);
        Predicate<ResourceLocation> structureFilter = allowedStructureIds
                .<Predicate<ResourceLocation>>map(allowedIds -> allowedIds::contains)
                .orElse(structureId -> true);
        return StructureRegistryCollector.collect(
                level,
                structureFilter,
                structureGroups.orElseGet(Map::of)
        );
    }
}
